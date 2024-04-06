package no.ntnu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int PORT_NUMBER = 8080;
    
    private final List<ClientHandler> clients;
    private final ServerLogic serverLogic;
    private boolean isRunning;
    private boolean runThread;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Server(ServerLogic serverLogic, boolean runThread) {
        this.clients = new ArrayList<>();
        this.serverLogic = serverLogic;
        this.runThread = runThread;
    }

    public void handleMessage(ClientHandler clientHandler, String message) {
        executorService.submit(() -> clientHandler.handleMessage(message));
    }

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
            listeningSocket = new ServerSocket(PORT_NUMBER);
        } catch (Exception e) {
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

    public void clientDisconnected(ClientHandler clientHandler) throws IOException {
        clientHandler.disconnect();
        clients.remove(clientHandler);
    }

    public void sendResponseToAllClients(String response) {
        for (ClientHandler client : clients) {
            client.send(response);
        }
    }

    public ServerLogic getServerLogic() {
        return serverLogic;
    }

}