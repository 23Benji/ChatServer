package client;

import res.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom extends JFrame {
    private static final int PORT = 65535;
    private static final String HOST = "10.216.220.164";
    private JTextArea chatArea;
    private JButton sendButton;
    private JTextField messageField;
    private PrintStream out;
    public static Set<String> usernames = new HashSet<>();

    public ChatRoom(String username) {
        try {
            Socket client = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // Use UTF-8 encoding for proper emoji handling
            out = new PrintStream(client.getOutputStream(), true, StandardCharsets.UTF_8);

            createGUI(username);
            out.println(username);
            new ChatClientThread(in, chatArea).start();

            // Detect window closing
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
        this.setTitle("ChatRoom - User: " + username);
        this.setSize(800, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(Colors.WHITEBLUE.getAwtColor());
        this.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Colors.DARKBLUE.getAwtColor());
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel headerLabel = new JLabel("ChatRoom - User: " + username);
        headerLabel.setForeground(Colors.WHITEBLUE.getAwtColor());
        headerPanel.add(headerLabel);
        this.add(headerPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setBackground(Colors.WHITEBLUE.getAwtColor());
        // Set a font that supports emojis
        chatArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        this.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(this.getWidth(), 35));

        JButton emojiButton = new JButton("😀");
        emojiButton.addActionListener(e -> showEmojiPicker());
        inputPanel.add(emojiButton, BorderLayout.WEST);

        messageField = new JTextField("");
        messageField.setForeground(Color.BLACK);
        messageField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        messageField.addActionListener(e -> sendMessage());
        inputPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setBackground(Colors.MIDDLEBLUE.getAwtColor());
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        this.add(inputPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    private class ChatClientThread extends Thread {
        private BufferedReader in;

        public ChatClientThread(BufferedReader in, JTextArea chatArea) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line == null) break;
                    SwingUtilities.invokeLater(() -> chatArea.append(line + "\n"));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> chatArea.append("Connection lost.\n"));
            }
        }
    }

    private void showEmojiPicker() {
        JDialog emojiDialog = new JDialog(this, "Choose Emoji", true);
        emojiDialog.setLayout(new BorderLayout());
        JPanel emojiPanel = new JPanel(new GridLayout(0, 5));
        JScrollPane scrollPane = new JScrollPane(emojiPanel);
        emojiDialog.add(scrollPane, BorderLayout.CENTER);
        emojiDialog.setSize(400, 300);

        String[] emojis = {
                "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
                "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
                "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩",
                "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣",
                "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬",
                "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗",
                "🤔", "🤭", "🤫", "🤥", "😶", "😐", "😑", "😬", "🙄", "😯",
                "😦", "😧", "😮", "😲", "🥱", "😴", "🤤", "😪", "😵", "🤐",
                "🥴", "🤢", "🤮", "🤧", "😷", "🤒", "🤕", "🤑", "🤠", "😈",
                "👿", "👹", "👺", "🤡", "💩", "👻", "💀", "☠️", "👽", "👾",
                "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿",
                "😾", "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤏", "✌️", "🤞",
                "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍",
                "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝",
                "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂",
                "🦻", "👃", "🧠", "🦷", "🦴", "👀", "👁️", "👅", "👄", "🦋"
        };

        // Add emoji buttons to the emoji panel
        for (String emoji : emojis) {
            JButton btn = new JButton(emoji);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btn.addActionListener(e -> {
                messageField.setText(messageField.getText() + emoji);
                emojiDialog.dispose();
            });
            emojiPanel.add(btn);
        }

        emojiDialog.setLocationRelativeTo(this);
        emojiDialog.setVisible(true);
    }
}