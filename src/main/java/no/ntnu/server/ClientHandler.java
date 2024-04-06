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

public class ClientHandler extends Thread {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;
    private boolean runThread;
    private final Map<Integer, String> messageQueue;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClientHandler(Socket socket, Server server, boolean runThread) throws IOException {
        this.socket = socket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.runThread = runThread;
        this.messageQueue = new HashMap<>();
        startMessageProcessor();
    }

    private void startMessageProcessor() {
        new Thread(() -> {
            while (true) {
                if (messageQueue.entrySet().size() > 0) {
                    String message = messageQueue.get(messageQueue.keySet().stream().min(Integer::compareTo).orElseThrow());
                    server.handleMessage(this, message);
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

    public void runThread() {
        new Thread(() -> {
            do {
                readClientMessage();
            } while (true);
        }).start();
    }

    public void runNoThread() {
        do {
            readClientMessage();
        } while (true);
    }

    public synchronized String handleMessage(String message) {
        System.out.println(LocalDateTime.now().format(formatter) + " Computing: " + message);
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
            server.getServerLogic().decompileMessage(messageQueue, message);
        
            System.out.println(LocalDateTime.now().format(formatter) + " Message " + message + " added to queue");
            System.out.println(messageQueue); 
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
