package no.ntnu.client;

import static no.ntnu.server.Server.PORT_NUMBER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * The client.
 */
public class TcpClient {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    /**
     * Connect to server.
     * @return If the connection were sucessful.
     */
    public boolean connect() {
        boolean connected = false;

        try {
            socket = new Socket("localhost", PORT_NUMBER);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            connected = true;
        } catch (IOException e) {
            System.out.println("Error: no server found");;
            //e.printStackTrace();
        }

        return connected;
    }

    /**
     * Runs the client.
     * @throws IOException Exception.
     */
    public void run() throws IOException {
        if (connect()) {
            boolean running = true;
            while (running) {
                System.out.println("Enter Command:");
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNextLine()) {
                    String message = scanner.nextLine().trim(); // Remove leading/trailing spaces
                    if (message.equals("exit")) {
                        running = false;
                    } else if (message.matches("^F\\d+$")) {
                        // Valid command in the format F(number)
                        send(message);
                    } else {
                        System.out.println("Invalid command format. Please enter a valid command (e.g., F10).");
                    }
                }
            }
        }
    }

    public boolean send(String message) throws IOException {
        boolean sent = false;
        System.out.println("Sending message: " + message);
        writer.println(message);
        String serverResponse = reader.readLine();
        System.out.println("  >>> " + serverResponse);
        return sent;
    }

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
