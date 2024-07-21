package proj;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Student Notes Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            LoginPanel loginPanel = new LoginPanel(frame);
            frame.setContentPane(loginPanel);
            frame.setVisible(true);
        });
    }
}
