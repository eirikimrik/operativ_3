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
    private boolean process = false;
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

    public void send(String message) {
        writer.println(message);
    }

    public void disconnect() throws IOException {
        this.socket.close();
        reader.close();
        writer.close();
    }
}
