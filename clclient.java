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
        String hostName;
        Socket connection;
        Scanner incoming;
        PrintWriter outgoing;
        String messageOut;
        String messageIn;
        Scanner userInput;
        if (args.length > 0){
            hostName = args[0];
        }
        else {
            System.out.println("Usage: java DateClient <server host name>");
            return;
        }
        try {
            connection = new Socket( hostName, DEFAULT_PORT);
            incoming = new Scanner(connection.getInputStream());
            outgoing = new PrintWriter(connection.getOutputStream());
            outgoing.println(HANDSHAKE); // Send handshake to client.
            outgoing.flush();
            messageIn = incoming.nextLine(); // Receive handshake from client.
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
            System.out.println("NOTE: Enter ’quit’ to end the program.\n");
            while (true) {
                System.out.println("WAITING...");
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
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, an error has occurred. Connection lost.");
            System.out.println("Error: " + e);
            System.exit(1);
        }
    }
}