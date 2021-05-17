/*
 *
 * @author Mohd Aamaan, 2252554
 * Pseudo-Client class that can make request to JabberServer.
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        System.out.println("Client running");
        Scanner sc = new Scanner(System.in);

        try (Socket clientSocket = new Socket("127.0.0.1", 44444)) {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            while (true) {
                System.out.println("Enter request");
                String request = sc.nextLine();

                if (request.equals("signout")) break;

                JabberMessage jabberMessage = new JabberMessage(request);
                oos.writeObject(jabberMessage);

                JabberMessage response = (JabberMessage) ois.readObject();
                System.out.println(response.getMessage());
                if (response.getData() != null) {
                    JabberDatabase.print2(response.getData());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
