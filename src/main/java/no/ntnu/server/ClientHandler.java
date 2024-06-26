package no.ntnu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles a client.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;
    private boolean runThread;
    private final BlockingQueue<String> messageQueue;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Handler for clients.
     * @param socket Socket.
     * @param server Server.
     * @param runThread If you want to run it as a thread.
     * @throws IOException Exception.
     */
    public ClientHandler(Socket socket, Server server, boolean runThread) throws IOException {
        this.socket = socket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.runThread = runThread;
        this.messageQueue = new LinkedBlockingQueue<>();
        startMessageProcessor();
    }

    private void startMessageProcessor() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = messageQueue.take();
                    server.handleMessage(this, message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    


    @Override
    public void run() {
        if (runThread) {
            runThread();
        } else {
            runNoThread();
        }
    }

    /**
     * Run with thread.
     */
    public void runThread() {
        new Thread(() -> {
            do {
                readClientMessage();
            } while (true);
        }).start();
    }

    /**
     * Run without thread.
     */
    public void runNoThread() {
        do {
            readClientMessage();
        } while (true);
    }

    /**
     * Handles messages.
     * @param message message.
     * @return the result of the message.
     */
    public synchronized String handleMessage(String message) {
        System.out.println(LocalDateTime.now().format(formatter) + " Computing: "+message);
        Map<String, Double> result = server.getServerLogic().getResult(message);
        String resultString = result.toString();
        send(resultString);  
        return resultString;
    }
    

    private String readClientMessage() {
        String message = "";
        try {
            message = reader.readLine();
            System.out.println("Received message: " + message);  // Print the received message
            messageQueue.put(message); // Add the message to the queue
        
            System.out.println(LocalDateTime.now().format(formatter) + " Message " + message + " added to queue");
            System.out.println(messageQueue); 
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Send message to client.
     * @param message Message.
     */
    public void send(String message) {
        writer.println(message);
    }

    /**
     * Disconnect client.
     * @throws IOException Exception.
     */
    public void disconnect() throws IOException {
        this.socket.close();
        reader.close();
        writer.close();
    }
}
