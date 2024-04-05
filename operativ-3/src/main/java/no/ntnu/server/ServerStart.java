package no.ntnu.server;

public class ServerStart {
    public static void main(String[] args) {
        ServerLogic logic = new ServerLogic();
        Server server = new Server(logic, true);
        server.start();
    }
}
