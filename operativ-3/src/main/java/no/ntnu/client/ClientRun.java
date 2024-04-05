package no.ntnu.client;

import java.io.IOException;

public class ClientRun {
    
    public static void main(String[] args) throws IOException {
        TcpClient client = new TcpClient();
        client.run();
    }

}
