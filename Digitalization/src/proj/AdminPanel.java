package proj;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class AdminPanel extends JPanel {
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JTextArea notesTextArea;
    private JButton viewNotesButton;
    private JButton logoutButton;
    private List<String> users;
    private JFrame parentFrame;

    public AdminPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());


        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(200, 0)); 

        notesTextArea = new JTextArea();
        notesTextArea.setWrapStyleWord(true);
        notesTextArea.setLineWrap(true);
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);

        viewNotesButton = new JButton("View Notes");
        logoutButton = new JButton("Logout");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(viewNotesButton);
        buttonPanel.add(logoutButton);


        add(userScrollPane, BorderLayout.WEST);
        add(notesScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadUsers();

        viewNotesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewNotes();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {

                    viewNotesButton.setEnabled(userList.getSelectedIndex() != -1);
                }
            }
        });
    }


    private void loadUsers() {
        try {
            users = Database.getAllUsers(); 
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users");
        }
    }


    private void viewNotes() {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedUser = users.get(selectedIndex);
            try {
                List<Note> userNotes = Database.getNotesByUsername(selectedUser);
                StringBuilder notesContent = new StringBuilder();
                for (Note note : userNotes) {
                    notesContent.append("Note ID: ").append(note.getId()).append("\n");
                    notesContent.append("Content: ").append(note.getContent()).append("\n\n");
                }
                notesTextArea.setText(notesContent.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading notes for the selected user");
            }
        }
    }


    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            LoginPanel loginPanel = new LoginPanel(parentFrame);
            parentFrame.setContentPane(loginPanel);
            parentFrame.revalidate();
        }
    }
}
