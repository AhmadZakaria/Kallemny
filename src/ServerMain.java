
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain {

    ArrayList clientOutputStreams;
    ArrayList<String> onlineUsers = new ArrayList();

    public class ClientHandler implements Runnable {

        BufferedReader reader;
        Socket sock;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user) {
            // new inputStreamReader and then add it to a BufferedReader
            client = user;
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } // end try
            catch (Exception ex) {
                System.out.println("error beginning StreamReader");
            } // end catch

        } // end ClientHandler()

        public void run() {
            String message;
            String[] data;
            String connect = "Connect";
            String disconnect = "Disconnect";
            String chat = "Chat";

            try {
                while ((message = reader.readLine()) != null) {

                    System.out.println("Received: " + message);
                    data = message.split("¥");
                    for (String token : data) {

                        System.out.println(token);

                    }

                    if (data[2].equals(connect)) {

                        tellEveryone((data[0] + "¥" + data[1] + "¥" + chat));
                        userAdd(data[0]);

                    } else if (data[2].equals(disconnect)) {

                        tellEveryone((data[0] + "¥has disconnected." + "¥" + chat));
                        userRemove(data[0]);

                    } else if (data[2].equals(chat)) {

                        tellEveryone(message);

                    } else {
                        System.out.println("No Conditions were met.");
                    }


                } // end while
            } // end try
            catch (Exception ex) {
                System.out.println("lost a connection");
                clientOutputStreams.remove(client);
            } // end catch
        } // end run()
    } // end class ClientHandler

    public static void main(String[] args) {
        new ServerMain().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList();

        try {
            ServerSocket serverSock = new ServerSocket(5000);

            while (true) {
                // set up the server writer function and then begin at the same
                // the listener using the Runnable and Thread
                Socket clientSock = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                clientOutputStreams.add(writer);

                // use a Runnable to start a 'second main method that will run
                // the listener
                Thread listener = new Thread(new ServerMain.ClientHandler(clientSock, writer));
                listener.start();
                System.out.println("got a connection");
            } // end while
        } // end try
        catch (Exception ex) {
            System.out.println("error making a connection");
        } // end catch

    } // end go()

    public void userAdd(String data) {
        String message;
        String add = "¥ ¥Connect", done = "Server¥ ¥Done";
        onlineUsers.add(data);
        String[] tempList = new String[(onlineUsers.size())];
        onlineUsers.toArray(tempList);

        for (String token : tempList) {

            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void userRemove(String data) {
        String message;
        String add = "¥ ¥Connect", done = "Server¥ ¥Done";
        onlineUsers.remove(data);
        String[] tempList = new String[(onlineUsers.size())];
        onlineUsers.toArray(tempList);

        for (String token : tempList) {

            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void tellEveryone(String message) {
        // sends message to everyone connected to server
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                System.out.println("Sending" + message);
                writer.flush();
            } // end try
            catch (Exception ex) {
                System.out.println("error telling everyone");
            } // end catch
        } // end while
    } // end tellEveryone()
}
