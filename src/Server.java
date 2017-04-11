import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
	private static String shadowFile = "shadow_file.txt";
	private static List<String> passwordList;
	private static FileIO shadowFileIO;

	public List<String> getPasswordList() {
		return passwordList;
	}

	public boolean inShadowFile(String encryptedPassword) {
		return passwordList.contains(encryptedPassword);
	}

	public static String getShadowFile() {
		return shadowFile;
	}
	
	public static void main (String args[]) {
        if (args.length != 1) {
			System.out.println("Please Enter Only One Inputs: Portnumber");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server hostname: " + InetAddress.getLocalHost().getHostName());
			
			shadowFileIO = new FileIO();
			passwordList = shadowFileIO.readShadowFile(shadowFile);		

            while (true) {
                // Create new thread for the client
                new ServerThread(serverSocket.accept()).start();
            }
        }
        catch (IOException e) {
            System.out.println("Failure to Open Socket");
            e.printStackTrace();
        }

    }
}
