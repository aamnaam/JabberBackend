import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JabberServer implements Runnable {
    private static final int PORT_NUMBER = 44444;
    private ServerSocket serverSocket;

    public JabberServer() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
//        serverSocket.setSoTimeout(5000);
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
                Thread.sleep(1000);
                Socket clientSocket = serverSocket.accept();
                ClientConnection client
                        = new ClientConnection(clientSocket, new JabberDatabase());
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
}
