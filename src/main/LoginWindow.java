package main;

import client.ChatRoom;
import res.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JButton loginButton;
    private static ArrayList<String> users = new ArrayList<>();
    private static final String IP_REGEX = "^((25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
    private String serverIP = "127.0.0.1";
    private int serverPort = 8080;

    public LoginWindow() {
        this.setTitle("Login");
        this.setSize(500, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(Colors.WHITEBLUE.getAwtColor());

        // 🔹 TOP PANEL for Settings Button (Aligned to Top Right)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Panel to keep the settings button at the TOP RIGHT
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        settingsPanel.setOpaque(false);

        // ⚙️ Settings Button
        JButton settingsButton = new JButton(new ImageIcon(ChatRoom.class.getResource("/res/setting.png")));
        settingsButton.setPreferredSize(new Dimension(32, 32));
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.addActionListener(e -> openSettingsDialog());

        settingsPanel.add(settingsButton);
        topPanel.add(settingsPanel, BorderLayout.NORTH);

        // 🔹 TITLE
        JLabel titleLabel = new JLabel("Welcome to ChatServer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
        titleLabel.setForeground(Colors.DARKBLUE.getAwtColor());
        topPanel.add(titleLabel, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);

        // 🔹 CENTER PANEL for Login Elements
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel label = new JLabel("Enter your username:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Colors.MIDDLEBLUE.getAwtColor());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(label, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setBackground(Colors.LIGHTBLUE.getAwtColor());
        usernameField.setForeground(Colors.DARKBLUE.getAwtColor());
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        usernameField.addActionListener(e -> handleLogin());
        gbc.gridy = 1;
        centerPanel.add(usernameField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(Colors.DARKBLUE.getAwtColor());
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 2;
        centerPanel.add(loginButton, gbc);

        this.add(centerPanel, BorderLayout.CENTER);
        this.setLocationRelativeTo(null);
    }


    private void handleLogin() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty() && !users.contains(username)) {
            users.add(username);
            this.setVisible(false);
            ChatRoom CR = new ChatRoom(username);
            CR.setHOST(serverIP);
            CR.setVisible(true);
            CR.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setVisible(true);
                    users.remove(username);
                }
            });
            usernameField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Username already taken or invalid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(320, 180);
        settingsDialog.setLayout(new GridBagLayout());
        settingsDialog.getContentPane().setBackground(Colors.WHITEBLUE.getAwtColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel ipLabel = new JLabel("Server IP:");
        ipLabel.setForeground(Colors.DARKBLUE.getAwtColor());

        JTextField ipField = new JTextField(ChatRoom.getHOST());
        styleTextField(ipField);

        JLabel portLabel = new JLabel("Server Port:");
        portLabel.setForeground(Colors.DARKBLUE.getAwtColor());

        JTextField portField = new JTextField(String.valueOf(ChatRoom.getPORT()));
        styleTextField(portField);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton);
        saveButton.addActionListener(e -> {
            String ip = ipField.getText().trim();
            String port = portField.getText().trim();

            // Check if IP is valid
            if (!isValidIP(ip)) {
                JOptionPane.showMessageDialog(settingsDialog, "Invalid IP address!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ChatRoom.setHOST(ip);
            try {
                ChatRoom.setPORT(Integer.parseInt(port));
                settingsDialog.dispose(); // Close settings dialog
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsDialog, "Invalid port number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsDialog.add(ipLabel, gbc);

        gbc.gridx = 1;
        settingsDialog.add(ipField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        settingsDialog.add(portLabel, gbc);

        gbc.gridx = 1;
        settingsDialog.add(portField, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsDialog.add(saveButton, gbc);

        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }

    private static boolean isValidIP(String ip) {
        if (ip.equalsIgnoreCase("localhost"))
            return true;
        else
            return IP_PATTERN.matcher(ip).matches();

    }


    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Colors.LIGHTBLUE.getAwtColor());
        field.setForeground(Colors.DARKBLUE.getAwtColor());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.DARKBLUE.getAwtColor(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(Colors.DARKBLUE.getAwtColor());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

}
