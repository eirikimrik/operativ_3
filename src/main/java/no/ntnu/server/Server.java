package no.ntnu.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Server class represents the server component of the application.
 * It manages client connections, message handling, and server operations.
 */
public class Server {

    public static final int PORT_NUMBER = 8080;
    
    private final List<ClientHandler> clients;
    private final ServerLogic serverLogic;
    private boolean isRunning;
    private boolean runThread;
    private boolean process;
    private Map<Integer, String> messageQueue;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Constructor that initiates Server object.
     * @param serverLogic The server logic instance responsible for message processing.
     * @param runThread A boolean indicating whether client handlers should run in separate threads.
     */
    public Server(ServerLogic serverLogic, boolean runThread) {
        this.messageQueue = new HashMap<>();
        this.process = false;
        this.clients = new ArrayList<>();
        this.serverLogic = serverLogic;
        this.runThread = runThread;
    }

    /**
     * Submits a message handling task to the executor service for asynchronous processing.
     *
     * @param clientHandler The client handler responsible for handling the message.
     * @param message       The message received from the client.
     */
    public void handleMessage(ClientHandler clientHandler, String message) {
        executorService.submit(() -> {
            clientHandler.handleMessage(message);
        });
    }

    /**
     * Starts the server by opening a listening socket and accepting client connections.
     */
    public void start() {
        ServerSocket socket = openListeningSocket();
        if (socket != null) {
            isRunning = true;
            while (isRunning) {
                ClientHandler client = acceptNewClient(socket);
                if (client != null) {
                    clients.add(client);
                    
                    client.run();
                }
            }
        }
    }

    /**
     * Opens a server socket and binds it to the specified port.
     *
     * @return The opened ServerSocket instance.
     */
    private ServerSocket openListeningSocket() {
        ServerSocket listeningSocket = null;
        try {
            System.out.println("Starting server on port " + PORT_NUMBER);
            listeningSocket = new ServerSocket();
            // Bind the server socket to localhost
            listeningSocket.bind(new InetSocketAddress("localhost", PORT_NUMBER));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listeningSocket;
    }

    /**
     * Accepts a new client connection and creates a client handler to manage communication with the client.
     *
     * @param socket The ServerSocket instance used for accepting client connections.
     * @return The created ClientHandler instance for the new client.
     */
    private ClientHandler acceptNewClient(ServerSocket socket) {
        ClientHandler client = null;
        try {
            Socket clientSocket = socket.accept();
            client = new ClientHandler(clientSocket, this, runThread);
            System.out.println("New client connected from: " + clientSocket.getRemoteSocketAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    public void clientDisconnected(ClientHandler clientHandler) throws IOException {
        clientHandler.disconnect();
        clients.remove(clientHandler);
    }

    public void sendResponseToAllClients(String response) {
        for (ClientHandler client : clients) {
            client.send(response);
        }
    }

    public Map<Integer, String> getQueue() {
        return this.messageQueue;
    }

    public void setProcess(boolean process) {
        this.process = process;
    }

    public boolean getProcess() {
        return this.process;
    }

    /**
     * Retrieves the server logic instance associated with this server.
     *
     * @return The ServerLogic instance responsible for message processing.
     */
    public ServerLogic getServerLogic() {
        return serverLogic;
    }

}
