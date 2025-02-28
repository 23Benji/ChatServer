package main;

import client.ChatRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import res.Colors;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JButton loginButton;

    public LoginWindow() {
        this.setTitle("Login");
        this.setSize(500, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        this.getContentPane().setBackground(Colors.WHITEBLUE.getAwtColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Überschrift (Titel)
        JLabel titleLabel = new JLabel("Welcome to ChatServer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
        titleLabel.setForeground(Colors.DARKBLUE.getAwtColor());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        this.add(titleLabel, gbc);

        // Benutzername-Label (zentriert)
        JLabel label = new JLabel("Enter your username:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Colors.MIDDLEBLUE.getAwtColor());
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        this.add(label, gbc);

        // Benutzername-Feld
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setBackground(Colors.LIGHTBLUE.getAwtColor());
        usernameField.setForeground(Colors.DARKBLUE.getAwtColor());
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        usernameField.addActionListener(e -> handleLogin());
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.add(usernameField, gbc);

        // Login-Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(Colors.DARKBLUE.getAwtColor());
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        loginButton.addActionListener(e -> handleLogin());

        // Button unter das Textfeld setzen
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        this.add(loginButton, gbc);

        // Fenster zentrieren und anzeigen
        this.setLocationRelativeTo(null);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty() && ChatRoom.usernames.add(username)) {
            this.setVisible(false);
            ChatRoom CR = new ChatRoom(username);
            CR.setVisible(true);
            CR.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setVisible(true);
                }
            });
            usernameField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Username already taken or invalid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
