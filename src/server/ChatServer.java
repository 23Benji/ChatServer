package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChatServer {
    public static final int PORT = 65535;
    protected static ArrayList<PrintStream> outputStreams = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
            System.out.println("Chat server started");
            while (true) {
                Socket client = server.accept();
                try {
                    new ChatServerThread(client).start();
                } catch (IOException e) {
                    System.out.println(e.getClass().getName() + ":" + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                if (server != null) server.close();
            } catch (Exception e1) {}
        }
    }

    public static synchronized void addOutputStream(PrintStream out) {
        outputStreams.add(out);
    }

    public static synchronized void removeOutputStream(PrintStream out) {
        outputStreams.remove(out);
    }

    public static synchronized void broadcastMessage(String message) {
        for (PrintStream out : outputStreams) {
            out.println(message);
        }
    }

    private static class ChatServerThread extends Thread {
        private Socket client;
        private BufferedReader in;
        private PrintStream out;
        private String name;

        public ChatServerThread(Socket client) throws IOException {
            this.client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintStream(client.getOutputStream());
        }

        @Override
        public void run() {
            try {
                addOutputStream(out);
                name = in.readLine();
                broadcastMessage(name + " signed in successfully");
                broadcastMessage("/userscount " + outputStreams.size());

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("/logout")) {
                        break;
                    } else if (line.equals("/typing")) {
                        broadcastMessage("/typing " + name);
                    } else if (line.equals("/stoptyping")) {
                        broadcastMessage("/stoptyping " + name);
                    } else {
                        broadcastMessage(name + ": " + line);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            } finally {
                removeOutputStream(out);
                if (name != null) {
                    broadcastMessage(name + " signed out");
                    broadcastMessage("/userscount " + outputStreams.size());
                }
                try {
                    client.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}