import java.io.IOException;
import java.net.Socket;

public class Client {
    public void main (String args[]) {
        int port = Integer.parseInt(args[0]);
        String hostname = args[1];

        if (args.length != 2) {
			System.out.println("Please Enter Only Two Inputs: Portnumber Hostname");
			System.exit(0);
		}

        try {
            Socket clientSocket = new Socket(hostname, port);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInput()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        }

        catch (IOException e) {
            System.out.println("Failed to create socket.");
            e.printStackTrace();
        }

    }
}
