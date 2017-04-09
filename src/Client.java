import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class Client {
    public void main (String args[]) {

        if (args.length != 2) {
			System.out.println("Please Enter Only Two Inputs: Portnumber Hostname");
			System.exit(0);
		}
        int port = Integer.parseInt(args[0]);
        String hostname = args[1];

        try {
            System.out.println("Connecting to Computer: " + hostname + " on port: " + port);
            Socket clientSocket = new Socket(hostname, port);
            System.out.println("Connection Successful!");
            PrintWriter writeToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader readFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            int i = 0;
            while (i < 10) {
            	writeToServer.println("hi %i" + i);
            }
            System.out.println(readFromServer.readLine());
        }

        catch (IOException e) {
            System.out.println("Failed to create socket.");
            e.printStackTrace();
        }

    }
}
