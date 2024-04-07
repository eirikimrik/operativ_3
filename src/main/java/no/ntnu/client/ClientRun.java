package no.ntnu.client;

import java.io.IOException;

/**
 * Creates a new instance of a client.
 * @param args args.
 * @throws IOException Exception.
 */
public class ClientRun {
    
    public static void main(String[] args) throws IOException {
        TcpClient client = new TcpClient();
        client.run();
    }

}
