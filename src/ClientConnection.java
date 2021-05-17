/*
 *
 * @author Mohd Aamaan, 2252554
 * Handles requests and responses for each client.
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection implements Runnable {
    private final Socket socket;
    private final JabberDatabase db;
    private static int usersRegistered = 0;
    private int userID;
    private String username;

    private static final String SIGN_IN_REQ_MSG = "signin ";
    private static final String SIGN_IN_SUCCESS_MSG = "signedin";
    private static final String UNKNOWN_USER_MSG = "unknown-user";
    private static final String REGISTER_REQ_MSG = "register ";
    private static final String SIGN_OUT_REQ_MSG = "signout";
    private static final String TIMELINE_REQ_MSG = "timeline";
    private static final String TIMELINE_RESP_MSG = "timeline";
    private static final String USERS_TO_FOLLOW_REQ_MSG = "users";
    private static final String USERS_TO_FOLLOW_RESP_MSG = "users";
    private static final String POST_JAB_REQ_MSG = "post ";
    private static final String POST_JAB_RESP_MSG = "posted";
    private static final String LIKE_JAB_REQ_MSG = "like ";
    private static final String LIKE_JAB_RESP_MSG = "posted";
    private static final String FOLLOW_USER_REQ_MSG = "follow ";
    private static final String FOLLOW_USER_RESP_MSG = "posted";

    private static String generateEMail() {
        return "user" + (usersRegistered++) + "@address.com";
    }

    public ClientConnection(Socket socket, JabberDatabase db) {
        this.socket = socket;
        this.db = db;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            ObjectInputStream ois
                    = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos
                    = new ObjectOutputStream(socket.getOutputStream());
            String requestMessage = "";

            while (!requestMessage.equals(SIGN_OUT_REQ_MSG)) {
                JabberMessage request = (JabberMessage) ois.readObject();
                requestMessage = request.getMessage();
                JabberMessage response;

                if (requestMessage.equals(SIGN_OUT_REQ_MSG)) {
                    continue;
                }

                if (requestMessage.startsWith(SIGN_IN_REQ_MSG)) {
                    String inputUsername = requestMessage.split(" ")[1];
                    System.out.printf("user %s login\n", inputUsername);
                    if (db.getUserID(inputUsername) == -1) {
                        response = new JabberMessage(UNKNOWN_USER_MSG);
                        System.out.printf("user %s login unsuccessful\n", inputUsername);
                    } else {
                        username = inputUsername;
                        userID = db.getUserID(username);
                        System.out.printf("user %s, ID %d login successful\n", username, userID);
                        response = new JabberMessage(SIGN_IN_SUCCESS_MSG);

                    }
                    oos.writeObject(response);
                    oos.flush();
                }

                else if (requestMessage.startsWith(REGISTER_REQ_MSG)) {
                    username = requestMessage.split(" ")[1];
                    String emailAdd = generateEMail();
                    db.addUser(username, emailAdd);
                    userID = db.getUserID(username);
                    System.out.printf("user %s registered, ID given = %d\n", username, userID);
                    response = new JabberMessage(SIGN_IN_SUCCESS_MSG);

                    oos.writeObject(response);
                    oos.flush();
                }

                else if (requestMessage.equals(TIMELINE_REQ_MSG)) {
                    ArrayList<ArrayList<String>> timelineData
                            = db.getTimelineOfUserEx(userID);
                    System.out.printf("user %d requested timeline\n", userID);
                    response = new JabberMessage(
                            TIMELINE_RESP_MSG,
                            timelineData
                    );
                    oos.writeObject(response);
                    oos.flush();
                }

                else if (requestMessage.equals(USERS_TO_FOLLOW_REQ_MSG)) {
                    ArrayList<ArrayList<String>> usersToFollow
                            = db.getUsersNotFollowed(userID);
                    System.out.printf("user %d requested users to follow\n", userID);

                    response = new JabberMessage(
                            USERS_TO_FOLLOW_RESP_MSG,
                            usersToFollow
                    );
                    oos.writeObject(response);
                    oos.flush();
                }

                else if (requestMessage.startsWith(POST_JAB_REQ_MSG)) {
                    String jabText = requestMessage.substring(requestMessage.indexOf(' ')+1);
                    System.out.printf("user %d posts jab %s\n", userID, jabText);
                    db.addJab(username, jabText);

                    response = new JabberMessage(POST_JAB_RESP_MSG);
                    oos.writeObject(response);
                    oos.flush();
                }

                else if (requestMessage.startsWith(LIKE_JAB_REQ_MSG)) {
                    String jabID = requestMessage.substring(requestMessage.indexOf(' ')+1);
                    System.out.printf("user %d likes jab %s\n", userID, jabID);
                    db.addLike(userID, Integer.parseInt(jabID));

                    response = new JabberMessage(LIKE_JAB_RESP_MSG);
                    oos.writeObject(response);
                    oos.flush();
                }

                else if (requestMessage.startsWith(FOLLOW_USER_REQ_MSG)) {
                    String usernameToFollow = requestMessage.substring(requestMessage.indexOf(' ')+1);
                    System.out.printf("user %d follows %s\n", userID, usernameToFollow);
                    db.addFollower(userID, usernameToFollow);

                    response = new JabberMessage(FOLLOW_USER_RESP_MSG);
                    oos.writeObject(response);
                    oos.flush();
                }
            }
        } catch (EOFException e) {
            System.out.println(username + " disconnected");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
