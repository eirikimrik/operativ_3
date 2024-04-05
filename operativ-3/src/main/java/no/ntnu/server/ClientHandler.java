package no.ntnu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;
    private boolean runThread;

    public ClientHandler(Socket socket, Server server, boolean runThread) throws IOException {
        this.socket = socket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.runThread = runThread;
    }

    @Override
    public void run() {
        if (runThread) {
            runThread();
        } else {
            runNoThread();
        }
    }

    public void runThread() {
        new Thread(() -> {
            String response = "";
            do {
                String message = readClientMessage();
                if (!message.isEmpty()) {
                    Date dateBefore = new Date();
                    Long before = dateBefore.getTime();
                    System.out.println("Received message: " + message);
                    response = handleMessage(message);
                    Date dateAfter = new Date();
                    Long after = dateAfter.getTime();
                    System.out.println("Time to process: " + (after - before) + "ms");
                }

            } while (!response.isEmpty());
            try {
                server.clientDisconnected(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void runNoThread() {
        String response = "";
        do {
            String message = readClientMessage();
            if (!message.isEmpty()) {
                Date dateBefore = new Date();
                Long before = dateBefore.getTime();
                System.out.println("Received message: " + message);
                response = handleMessage(message);
                Date dateAfter = new Date();
                Long after = dateAfter.getTime();
                System.out.println("Time to process: " + (after - before) + "ms");
            }

        } while (!response.isEmpty());
        try {
            server.clientDisconnected(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleMessage(String message) {
        Map<String, Double> result = server.getServerLogic().getResult(message);
        server.sendResponseToAllClients(result.toString());
        return result.toString();
    }

    private String readClientMessage() {
        String message = "";
        try {
            message = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void send(String message) {
        writer.println(message);
    }

    public void disconnect() throws IOException {
        this.socket.close();
        reader.close();
        writer.close();
    }
}
