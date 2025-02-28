package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private static final int PORT = 65535;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintStream out;
    public static Set<String> usernames = new HashSet<>();

    public ChatRoom(String username) {
        try {
            Socket client = new Socket("192.168.42.166", PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintStream(client.getOutputStream());

            createGUI(username);
            out.println(username);
            new ChatClientThread(in,chatArea).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not connect to server", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createGUI(String username) {
        frame = new JFrame("Chat - " + username);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    out.println(message);
                    messageField.setText("");
                }
            }
        });
        frame.add(messageField, BorderLayout.SOUTH);

        frame.setVisible(true);
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
}