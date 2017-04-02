import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
	static String shadowFile = "shadow_file.txt";
	static List<String> passwordList;
	
	
	public void main (String args[]) {
        int port = Integer.parseInt(args[0]);

        if (args.length != 1) {
			System.out.println("Please Enter Only One Inputs: Portnumber");
			System.exit(0);
		}

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            
            while (true) {
                // Create new thread for the client
                new ClientThread(serverSocket.accept()).start();
            }
        }
        catch (IOException e) {
            System.out.println("Failure to Open Socket");
            e.printStackTrace();
        }

    }
}
