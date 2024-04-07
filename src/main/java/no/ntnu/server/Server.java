package no.ntnu.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server.
 */
public class Server {

    public static final int PORT_NUMBER = 8080;
    
    private final List<ClientHandler> clients;
    private final ServerLogic serverLogic;
    private boolean isRunning;
    private boolean runThread;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Server.
     * @param serverLogic The logic of the server.
     * @param runThread If the server should run on threads.
     */
    public Server(ServerLogic serverLogic, boolean runThread) {
        this.clients = new ArrayList<>();
        this.serverLogic = serverLogic;
        this.runThread = runThread;
    }

    /**
     * Handles incoming messages.
     * @param clientHandler Client.
     * @param message Message.
     */
    public void handleMessage(ClientHandler clientHandler, String message) {
        executorService.submit(() -> clientHandler.handleMessage(message));
    }

    /**
     * Start the server.
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

    /**
     * Disconnect client.
     * @param clientHandler Client to disconnect.
     * @throws IOException Exception.
     */
    public void clientDisconnected(ClientHandler clientHandler) throws IOException {
        clientHandler.disconnect();
        clients.remove(clientHandler);
    }

    /**
     * Sends message to all connected clients.
     * @param response Response to send.
     */
    public void sendResponseToAllClients(String response) {
        for (ClientHandler client : clients) {
            client.send(response);
        }
    }

    /**
     * Returns the server logic.
     * @return the server logic.
     */
    public ServerLogic getServerLogic() {
        return serverLogic;
    }

}
