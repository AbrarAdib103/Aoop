package com.example.demo6;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 8080;
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static List<String> clientNames = new ArrayList<>();
    private static int clientCount = 0;
    private static List<String> messageLog = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                clientCount++;
                String clientName = "Client-" + (clientCount - 1);
                clientNames.add(clientName);
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                // Send previous messages to the new client
                for (String message : messageLog) {
                    writer.println(message);
                }

                // Print event message to server console
                System.out.println(clientName + " connected.");

                new Thread(new ClientHandler(clientSocket, writer, clientName)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;
        private String clientName;

        public ClientHandler(Socket socket, PrintWriter writer, String clientName) {
            this.clientSocket = socket;
            this.writer = writer;
            this.clientName = clientName;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    messageLog.add(clientName + ": " + message);
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcastMessage(String message) {
        for (PrintWriter clientWriter : clientWriters) {
            clientWriter.println(message);
        }
    }
}
