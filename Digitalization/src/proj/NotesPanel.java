package proj;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class NotesPanel extends JPanel {
    private JTextArea noteTextArea;
    private JList<String> notesList;
    private DefaultListModel<String> listModel;
    private JButton saveButton;
    private JButton newNoteButton;
    private JButton deleteButton;
    private JButton logoutButton; 
    private JButton downloadButton; 
    private int selectedNoteId = -1; 
    private List<Note> notes;
    private String username;
    private JFrame parentFrame;

    public NotesPanel(JFrame parentFrame, String username) {
        this.username = username;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        
        listModel = new DefaultListModel<>();
        notesList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(notesList);
        listScrollPane.setPreferredSize(new Dimension(200, 0)); // Set preferred size for the list panel

        noteTextArea = new JTextArea();
        noteTextArea.setWrapStyleWord(true);
        noteTextArea.setLineWrap(true);
        JScrollPane textScrollPane = new JScrollPane(noteTextArea);

        saveButton = new JButton("Save");
        newNoteButton = new JButton("New Note");
        deleteButton = new JButton("Delete Note");
        logoutButton = new JButton("Logout"); 
        downloadButton = new JButton("Download"); 

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(newNoteButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton); 
        buttonPanel.add(downloadButton); 

        // Add components to panel
        add(listScrollPane, BorderLayout.WEST);
        add(textScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        
        loadNotes();

        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNote();
            }
        });

        newNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newNote();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteNote();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadNotes();
            }
        });

        notesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = notesList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        Note selectedNote = notes.get(selectedIndex);
                        selectedNoteId = selectedNote.getId();
                        noteTextArea.setText(selectedNote.getContent());
                    }
                }
            }
        });
    }


    private void loadNotes() {
        try {
            notes = Database.getNotesByUsername(username);
            listModel.clear();
            for (Note note : notes) {
                listModel.addElement("Note ID: " + note.getId() + " - " + note.getContent().substring(0, Math.min(30, note.getContent().length()))); // Show preview
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading notes");
        }
    }


    private void saveNote() {
        String content = noteTextArea.getText();
        if (selectedNoteId == -1) { 
            try {
                Database.saveNoteForUser(username, content);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving note");
            }
        } else { 
            try {
                Database.updateNoteContent(selectedNoteId, content);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating note");
            }
        }
        loadNotes(); 
        noteTextArea.setText("");
        selectedNoteId = -1;     }

    
    private void newNote() {
        noteTextArea.setText("");
        selectedNoteId = -1;     }


    private void deleteNote() {
        if (selectedNoteId == -1) {
            JOptionPane.showMessageDialog(this, "No note selected for deletion");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this note?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                Database.deleteNoteById(selectedNoteId);
                loadNotes(); 
                noteTextArea.setText(""); 
                selectedNoteId = -1; 
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting note");
            }
        }
    }


    private void logout() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            
            LoginPanel loginPanel = new LoginPanel(parentFrame);
            parentFrame.setContentPane(loginPanel);
            parentFrame.revalidate();
        }
    }

    
    private void downloadNotes() {
        try {
            
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileWriter writer = new FileWriter(file);

                
                for (Note note : notes) {
                    writer.write("Note ID: " + note.getId() + "\n");
                    writer.write("Content: " + note.getContent() + "\n\n");
                }

                writer.close();
                JOptionPane.showMessageDialog(this, "Notes downloaded successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error downloading notes");
        }
    }
}
