package no.ntnu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The ClientHandler class manages the communication between the server and a client.
 * It handles reading messages from the client, processing them, and sending responses back.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;
    private boolean process = false;
    private boolean runThread;
    private final Map<Integer, String> messageQueue;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Constructor.
     */
    public ClientHandler(Socket socket, Server server, boolean runThread) throws IOException {
        this.socket = socket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.runThread = runThread;
        this.messageQueue = new HashMap<>();
        startMessageProcessor();
    }

    /**
     * Starts the message processing thread.
     */
    private void startMessageProcessor() {
        System.out.println("Starting message processor");
        new Thread(() -> {
            while (true) {
                if (messageQueue.size() > 0 && !process) {
                    process = true;
                    int minValue = getMinPriority();
                    if (minValue > -1) {
                        String message = messageQueue.get(minValue);
                        server.handleMessage(this, message);
                        messageQueue.remove(minValue);
                        process = false;
                    }
                } else {
                    Thread.currentThread().interrupt();
                }

            }
        }).start();
    }


    /**
     * Runs the client, with or without seperate thread.
     */
    @Override
    public void run() {
        if (runThread) {
            runThread();
        } else {
            runNoThread();
        }
    }

    /**
     * Runs the client handler in a separate thread.
     */
    public void runThread() {
        new Thread(() -> {
            do {
                readClientMessage();
            } while (true);
        }).start();
    }

    /**
     * Runs the client handler without creating a separate thread.
     */
    public void runNoThread() {
        do {
            readClientMessage();
        } while (true);
    }


    /**
     * Retrieves the minimum priority from the message queue.
     *
     * @return The minimum priority value.
     */
    private int getMinPriority() {
        int max = Integer.MAX_VALUE;
        for (int i : messageQueue.keySet()) {
            if (i < max) {
                max = i;
            }
        }

        if (messageQueue.isEmpty()) {
            return -1;
        } else {
            return max;
        }
    }

    /**
     * Handles a message received from the client by delegating the processing to the server.
     *
     * @param message The message received from the client.
     * @return The result string obtained from processing the message.
     */
    public synchronized String handleMessage(String message) {
        System.out.println(LocalDateTime.now().format(formatter) + " Computing: " + message);
        Map<String, Double> result = server.getServerLogic().getResult(message);
        String resultString = result.toString();
        send(resultString);
        return resultString;
    }


    /**
     * Reads a message sent by the client and adds it to the message queue for processing.
     *
     * @return The message received from the client.
     */
    private String readClientMessage() {
        String message = "";
        try {
            message = reader.readLine();
            System.out.println("Received message: " + message); // Print the received message
            String decompiledMessage =
                    server.getServerLogic().decompileMessage(messageQueue, message);

            System.out.println(LocalDateTime.now().format(formatter) + " Message "
                    + decompiledMessage + " added to queue");
            System.out.println(messageQueue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message to send.
     */
    public void send(String message) {
        writer.println(message);
    }

    /**
     * Closes the socket and disconnects the client connection.
     *
     * @throws IOException If an I/O error occurs when closing the socket or streams.
     */
    public void disconnect() throws IOException {
        this.socket.close();
        reader.close();
        writer.close();
    }
}
