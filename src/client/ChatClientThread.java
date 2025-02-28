package client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;


public class ChatClientThread extends Thread {
    private BufferedReader in;
    private JTextArea chatArea;

    public ChatClientThread(BufferedReader in,JTextArea chatArea) {
        this.in = in;
        this.chatArea = chatArea;
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

