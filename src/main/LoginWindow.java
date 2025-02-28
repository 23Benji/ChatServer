package main;

import client.ChatRoom;

import javax.swing.*;
import java.awt.*;

public class LoginWindow {
    private JFrame loginFrame;
    private JTextField usernameField;

    public LoginWindow() {
        loginFrame = new JFrame("Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.getContentPane().setBackground(Color.BLACK);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding (top, left, bottom, right)

        JLabel label = new JLabel("Enter your username:");
        label.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns for the label
        panel.add(label, gbc);

        usernameField = new JTextField(15);
        usernameField.setBackground(Color.BLACK);
        usernameField.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across two columns for the text field
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the text field fill the available width
        panel.add(usernameField, gbc);

        loginFrame.add(panel, BorderLayout.CENTER);

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(80, 30));
        loginButton.setBackground(Color.white);
        loginButton.setForeground(Color.black);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty() && ChatRoom.usernames.add(username)) {
                new ChatRoom(username);
                usernameField.setText("");
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Username already taken or invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(loginButton);
        loginFrame.add(buttonPanel, BorderLayout.SOUTH);

        loginFrame.setLocationRelativeTo(null); // Center the window
        loginFrame.setVisible(true);
    }
}
