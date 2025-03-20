package client;

import res.Colors;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import res.EmojiList;

public class ChatRoom extends JFrame {
    private static int PORT = 65535;
    private static String HOST ="localhost";
    private JTextPane chatArea;
    private JButton sendButton;
    private JTextField messageField;
    private PrintStream out;
    private JLabel typingLabel;
    private JLabel onlineCountLabel;
    private Set<String> typingUsers = new HashSet<>();
    private Timer stopTypingTimer;
    private boolean typingStatusSent = false;
    public static Set<String> usernames = new HashSet<>();

    public ChatRoom(String username) {
        try {
            Socket client = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintStream(client.getOutputStream(), true, StandardCharsets.UTF_8);

            createGUI(username);
            out.println(username);
            new ChatClientThread(in, chatArea).start();

            stopTypingTimer = new Timer(1000, e -> {
                out.println("/stoptyping");
                typingStatusSent = false;
            });
            stopTypingTimer.setRepeats(false);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    out.println("/logout " + username);
                    out.close();
                    try {
                        client.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not connect to server", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createGUI(String username) {
        this.setTitle("ChatRoom" + username);
        this.setSize(800, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(Colors.WHITEBLUE.getAwtColor());
        this.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Colors.DARKBLUE.getAwtColor());
        headerPanel.setSize(800, 200);

        JLabel headerLabel = new JLabel("User: " + username);
        headerLabel.setForeground(Colors.WHITEBLUE.getAwtColor());
        headerPanel.add(headerLabel, BorderLayout.WEST);

        onlineCountLabel = new JLabel("Online: 0");
        onlineCountLabel.setForeground(Colors.WHITEBLUE.getAwtColor());
        headerPanel.add(onlineCountLabel, BorderLayout.EAST);
        this.add(headerPanel, BorderLayout.NORTH);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(Colors.WHITEBLUE.getAwtColor());
        chatArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        this.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        JPanel typingIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typingIndicatorPanel.setBackground(Colors.WHITEBLUE.getAwtColor());
        typingLabel = new JLabel();
        typingLabel.setForeground(Color.GRAY);
        typingIndicatorPanel.add(typingLabel);
        southPanel.add(typingIndicatorPanel);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(this.getWidth(), 35));

        JButton emojiButton = new JButton("😀");
        emojiButton.addActionListener(e -> showEmojiPicker());
        inputPanel.add(emojiButton, BorderLayout.WEST);

        messageField = new JTextField("");
        messageField.setForeground(Color.BLACK);
        messageField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        messageField.addActionListener(e -> sendMessage());
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!typingStatusSent) {
                    out.println("/typing");
                    typingStatusSent = true;
                }
                stopTypingTimer.restart();
            }
        });
        inputPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setBackground(Colors.MIDDLEBLUE.getAwtColor());
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        southPanel.add(inputPanel);
        this.add(southPanel, BorderLayout.SOUTH);

        this.setVisible(true);
        SwingUtilities.invokeLater(() -> messageField.requestFocusInWindow());
    }

    public static void setHOST(String HOST) {
        ChatRoom.HOST = HOST;
    }
    public static String getHOST() {
        return HOST;
    }

    public static void setPORT(int PORT) {
        ChatRoom.PORT=PORT;
    }

    public static int getPORT() {
        return PORT;
    }



    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
            out.println("/stoptyping");
            typingStatusSent = false;
            stopTypingTimer.stop();
        }
    }

    private Color getUserColor(String username) {
        // Generate a hash from the username
        int hash = username.hashCode();

        // Create a base color from the hash
        Color baseColor = new Color(
                (hash & 0x7F0000) >> 16,    // Red: 0-127 (darker range)
                (hash & 0x007F00) >> 8,      // Green: 0-127 (darker range)
                hash & 0x00007F              // Blue: 0-127 (darker range)
        );

        // Adjust brightness to ensure it's dark enough
        float[] hsb = Color.RGBtoHSB(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                null
        );

        // Ensure the color is dark by setting brightness to 40-70%
        hsb[2] = 0.4f + (Math.abs(hash) % 31) * 0.01f; // 40-70% brightness

        // Convert back to RGB
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    private void appendStyledMessage(String username, String message) {
        StyledDocument doc = chatArea.getStyledDocument();
        try {
            StyleContext sc = StyleContext.getDefaultStyleContext();

            // Create style for username with bold font and custom color
            AttributeSet userStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, getUserColor(username));
            userStyle = sc.addAttribute(userStyle, StyleConstants.Bold, true);
            userStyle = sc.addAttribute(userStyle, StyleConstants.FontFamily, "Segoe UI Emoji");
            userStyle = sc.addAttribute(userStyle, StyleConstants.FontSize, 16);

            // Insert username
            doc.insertString(doc.getLength(), username + ": ", userStyle);

            // Create style for message (normal weight, black)
            AttributeSet msgStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
            msgStyle = sc.addAttribute(msgStyle, StyleConstants.Bold, false);
            msgStyle = sc.addAttribute(msgStyle, StyleConstants.FontFamily, "Segoe UI Emoji");
            msgStyle = sc.addAttribute(msgStyle, StyleConstants.FontSize, 16);

            // Insert message
            doc.insertString(doc.getLength(), message + "\n", msgStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void updateTypingIndicator() {
        if (typingUsers.isEmpty()) {
            typingLabel.setText("");
        } else {
            String text = String.join(", ", typingUsers) + (typingUsers.size() == 1 ? " is typing..." : " are typing...");
            typingLabel.setText(text);
        }
    }

    private void showEmojiPicker() {
        JDialog emojiDialog = new JDialog(this, "Choose Emoji", true);
        emojiDialog.setLayout(new BorderLayout());
        JPanel emojiPanel = new JPanel(new GridLayout(0, 5));
        JScrollPane scrollPane = new JScrollPane(emojiPanel);
        emojiDialog.add(scrollPane, BorderLayout.CENTER);
        emojiDialog.setSize(400, 300);
        for (String emoji : EmojiList.EMOJIS) {
            JButton btn = new JButton(emoji);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btn.addActionListener(e -> {
                messageField.setText(messageField.getText() + emoji);
                messageField.requestFocusInWindow();
                emojiDialog.dispose();
            });
            emojiPanel.add(btn);
        }

        emojiDialog.setLocationRelativeTo(this);
        emojiDialog.setVisible(true);
    }

    private class ChatClientThread extends Thread {
        private BufferedReader in;
        private JTextPane chatArea;

        public ChatClientThread(BufferedReader in, JTextPane chatArea) {
            this.in = in;
            this.chatArea = chatArea;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line == null) break;
                    SwingUtilities.invokeLater(() -> processMessage(line));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> appendMessage("Connection lost.\n", Color.RED));
            }
        }

        private void processMessage(String line) {
            if (line.startsWith("/userscount ")) {
                onlineCountLabel.setText("Online: " + line.substring(12));
            } else if (line.startsWith("/typing ")) {
                String user = line.substring(8);
                typingUsers.add(user);
                updateTypingIndicator();
            } else if (line.startsWith("/stoptyping ")) {
                String user = line.substring(12);
                typingUsers.remove(user);
                updateTypingIndicator();
            } else {
                int colonIndex = line.indexOf(':');
                if (colonIndex != -1) {
                    String username = line.substring(0, colonIndex);
                    String message = line.substring(colonIndex + 1).trim();
                    appendStyledMessage(username, message);
                } else {
                    appendMessage(line + "\n", Color.BLACK);
                }
            }
        }

        private void appendStyledMessage(String username, String message) {
            StyledDocument doc = chatArea.getStyledDocument();
            try {
                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet userStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, getUserColor(username));
                doc.insertString(doc.getLength(), username + ": ", userStyle);
                AttributeSet msgStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
                doc.insertString(doc.getLength(), message + "\n", msgStyle);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        private void appendMessage(String text, Color color) {
            StyledDocument doc = chatArea.getStyledDocument();
            try {
                StyleConstants.setForeground(doc.getStyle(StyleContext.DEFAULT_STYLE), color);
                doc.insertString(doc.getLength(), text, null);
            } catch (BadLocationException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}