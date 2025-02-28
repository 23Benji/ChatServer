package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
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
                    System.out.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                if (server != null) server.close();
            } catch (Exception e1) {
                // Ignorieren
            }
        }
    }

    // Synchronisierte Methode zum Hinzufügen eines Streams
    public static synchronized void addOutputStream(PrintStream out) {
        outputStreams.add(out);
    }

    // Synchronisierte Methode zum Entfernen eines Streams
    public static synchronized void removeOutputStream(PrintStream out) {
        outputStreams.remove(out);
    }

    // Synchronisierte Methode zum Senden einer Nachricht an alle Clients
    public static synchronized void broadcastMessage(String message) {
        for (PrintStream out : outputStreams) {
            out.println(message);
        }
    }
}