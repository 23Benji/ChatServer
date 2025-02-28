package main;

import client.ChatRoom;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import res.Colors;

public class LoginWindow {
    private JFrame loginFrame;
    private JTextField usernameField;
    private JButton loginButton;

    public LoginWindow() {
        loginFrame = new JFrame("Login");
        loginFrame.setSize(500, 250);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.getContentPane().setBackground(Colors.WHITEBLUE.getAwtColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Gleicher Abstand links & rechts
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Überschrift (Titel)
        JLabel titleLabel = new JLabel("Welcome to ChatServer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
        titleLabel.setForeground(Colors.DARKBLUE.getAwtColor());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginFrame.add(titleLabel, gbc);

        // Benutzername-Label (zentriert)
        JLabel label = new JLabel("Enter your username:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Colors.MIDDLEBLUE.getAwtColor());
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        loginFrame.add(label, gbc);

        // Benutzername-Feld
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setBackground(Colors.LIGHTBLUE.getAwtColor());
        usernameField.setForeground(Colors.DARKBLUE.getAwtColor());
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Abgerundete Optik
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginFrame.add(usernameField, gbc);

        // Login-Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(Colors.DARKBLUE.getAwtColor());
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Login-Logik
        loginButton.addActionListener(e -> handleLogin());

        // KeyListener für die Enter-Taste im Textfeld
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick(); // Löst Button-Klick aus
                }
            }
        });

        // Button unter das Textfeld setzen
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginFrame.add(loginButton, gbc);

        // Fenster zentrieren und anzeigen
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty() && ChatRoom.usernames.add(username)) {
            new ChatRoom(username);
            usernameField.setText("");
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Username already taken or invalid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginWindow();
    }
}
