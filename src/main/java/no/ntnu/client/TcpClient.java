package no.ntnu.client;

import static no.ntnu.server.Server.PORT_NUMBER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * The TCP client class for connecting to a server and sending/receiving messages.
 */
public class TcpClient {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    /**
     * Connects the client to the server.
     *
     * @return {@code true} if the connection is successful, {@code false} otherwise.
     */
    public boolean connect() {
        boolean connected = false;

        try {
            socket = new Socket("localhost", PORT_NUMBER);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            connected = true;
        } catch (IOException e) {
            System.out.println("Error: no server found");
            // e.printStackTrace();
        }

        return connected;
    }

    /**
     * Runs the client, allowing the user to input commands and sending them to the server.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void run() throws IOException {
        if (connect()) {
            boolean running = true;
            while (running) {
                System.out.println("Enter Command (e.g., F5 P45):");
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    if (input.equals("exit")) {
                        running = false;
                    } else if (input.matches("^F\\d+\\s+P\\d+$")) {
                        send(input);
                    } else {
                        System.out.println("Invalid command format. Please enter a valid command (e.g., F5 P10).");
                    }
                }
            }
        }
    }

    /**
     * Starts a separate thread to listen for incoming messages from the server.
     */
    public void startListening() {
        new Thread(() -> {
            String message = "";
            do {
                try {
                    if (reader != null) {
                        message = reader.readLine();
                        handleIncomingMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (!message.isEmpty());
        }).start();
    }

   
    private void handleIncomingMessage(String message) {
        System.out.println("Received message: " + message);
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to send.
     * @return {@code true} if the message is sent successfully, {@code false} otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean send(String message) throws IOException {
        boolean sent = false;
        System.out.println("Sending message: " + message);
        writer.println(message);
        String serverResponse = reader.readLine();
        System.out.println("  >>> " + serverResponse);
        return sent;
    }

    /**
     * Stops the client and closes the connection.
     */
    public void stop() {
        try {
            socket.close();
            socket = null;
            writer = null;
            reader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
