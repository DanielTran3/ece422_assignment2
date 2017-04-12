import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList;

public class Server {
	// Lists for passwords, usernames, and salts
	private static String shadowFile = "shadow_file.txt";
	private static List<String> passwordList;
	private static List<String> usernameList;
	private static List<String> saltList;
	private static FileIO shadowFileIO;

	// Return the index of a username
	public static int getUsernameIndex(String user) {
		for (int i = 0; i < listLength(); i++) {
			if (user.equals(usernameList.get(i))) {
				return i;
			}
		}
		return -1;
	}

	// Using index of username to get the password
	public static String getPassword(int index) {
		return passwordList.get(index);
	}

	// Using index of username to get the salt
	public static String getSalt(int index) {
		return saltList.get(index);
	}

	// Get length of the lists (username, salt, and password should all be the same length)
	public static int listLength() {
		return passwordList.size();
	}

	public static void main (String args[]) {
		// Ensure that the user only puts one input for the portnumber
        if (args.length != 1) {
			System.out.println("Please Enter Only One Inputs: Portnumber");
			System.exit(0);
		}
		// Get the portnumber and initialize the lists
		int port = Integer.parseInt(args[0]);
		passwordList = new ArrayList<String>();
		usernameList = new ArrayList<String>();
		saltList = new ArrayList<String>();

        try {
			// Create the server's socket (ServerSocket)
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server hostname: " + InetAddress.getLocalHost().getHostName());

			// Load the contents of the shadowfile into the lists
			shadowFileIO = new FileIO();
			shadowFileIO.loadShadowFile(shadowFile, usernameList, saltList, passwordList);

            while (true) {
                // Create new thread for the client
                new ServerThread(serverSocket.accept()).start();
			}
        }
		// Exception if the socket cannot be opened
        catch (IOException e) {
            System.out.println("Failure to Open Socket");
            e.printStackTrace();
        }

    }
}
