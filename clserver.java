import java.net.*;
import java.io.*;
import java.util.Scanner;

public class CLChatServer {
    public static final int DEFAULT_PORT = 32007;
    public static final String HANDSHAKE = "CLChat";
    public static final char MESSAGE = '0';
    public static final char CLOSE = '1';
    public static void main(String[] args) {
        int port;
        ServerSocket listener;
        Socket connection;
        Scanner incoming;
        PrintWriter outgoing;
        String messageOut;
        String messageIn;
        Scanner userInput;
        try {
            listener = new ServerSocket(DEFAULT_PORT);
            System.out.println("Listening on port " + listener.getLocalPort());
            connection = listener.accept();
            incoming = new Scanner( connection.getInputStream());
            outgoing = new PrintWriter(connection.getOutputStream());
            outgoing.println(HANDSHAKE);
            outgoing.flush();
            messageIn = incoming.nextLine();
            if (! HANDSHAKE.equals(messageIn) ) {
                throw new Exception("Connected program is not a CLChat!");
            }
            System.out.println("Connected. Waiting for the first message.");
        }
        catch (Exception e) {
            System.out.println("An error occurred while opening connection.");
            System.out.println(e.toString());
            return;
        }
        try {
            userInput = new Scanner(System.in);
            System.out.println("NOTE: Enter 'quit' to end the program.\n");
            while (true) {
                System.out.println("WAITING...");
                messageIn = incoming.nextLine();
                if (messageIn.length() > 0) {
                    if (messageIn.charAt(0) == CLOSE) {
                        System.out.println("Client Logged out of the system!!.");
                        connection.close();
                        break;
                    }
                    messageIn = messageIn.substring(1);
                }
                System.out.println("Client: " + messageIn);
                System.out.print("Myself: ");
                messageOut = userInput.nextLine();
                if (messageOut.equalsIgnoreCase("quit")) {
                    outgoing.println(CLOSE);
                    outgoing.flush();
                    connection.close();
                    System.out.println("Connection closed.");
                    break;
                }
                outgoing.println(MESSAGE + messageOut);
                outgoing.flush();
                if (outgoing.checkError()) {
                    throw new IOException("Error occurred while transmitting message.");
                }
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, an error has occurred. Connection lost.");
            System.out.println("Error: " + e);
            System.exit(1);
        }
    }
}