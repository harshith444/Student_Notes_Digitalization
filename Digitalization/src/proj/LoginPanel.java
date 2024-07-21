package proj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JFrame parentFrame;

    public LoginPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                
                boolean isValid = Database.validateUser(username, password);
                if (isValid) {
                    if (Database.isAdmin(username)) {
                        AdminPanel adminPanel = new AdminPanel(parentFrame);
                        parentFrame.setContentPane(adminPanel);
                        parentFrame.revalidate();
                    } else {
                        NotesPanel notesPanel = new NotesPanel(parentFrame, username); 
                        parentFrame.setContentPane(notesPanel);
                        parentFrame.revalidate();
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Invalid username or password");
                }
            }
        });
        
        
    }
}
