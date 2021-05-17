/*
 *
 * @author Mohd Aamaan, 2252554
 * Main class for running the Jabber backend.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JabberServer implements Runnable {
    private static final int PORT_NUMBER = 44444;
    private final ServerSocket serverSocket;

    public JabberServer() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
//        serverSocket.setSoTimeout(300);
        new Thread(this).start();
    }

    public static void main(String[] args) throws IOException {
        JabberServer server = new JabberServer();
    }

    @Override
    public void run() {
        try {
            System.out.println("Server waiting for clients");
            while (true) {
                Thread.sleep(100);
                Socket clientSocket = serverSocket.accept();
                new ClientConnection(clientSocket, new JabberDatabase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
