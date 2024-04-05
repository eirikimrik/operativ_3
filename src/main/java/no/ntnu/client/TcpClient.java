package no.ntnu.client;

import static no.ntnu.server.Server.PORT_NUMBER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TcpClient {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public boolean connect() {
        boolean connected = false;

        try {
            socket = new Socket("localhost", PORT_NUMBER);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return connected;
    }

    public void run() throws IOException {
        if (connect()) {
            boolean running = true;
            while (running) {
                System.out.println("Enter Command:");
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    if (message.equals("exit")) {
                        running = false;
                    } else {
                        send(message);
                    }
                }
            }
        }
    }

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
