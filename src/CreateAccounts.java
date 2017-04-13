import java.io.Console;

public class CreateAccounts extends Thread {
	private static String directory = System.getProperty("user.dir");

	public static void main (String args[]) {
		// Ensure the user only inputs one value for the shadowfile filename
		if (args.length != 1) {
			System.out.println("Please Enter Only Two Inputs: shadowfile");
			System.exit(0);
		}

		System.out.println("Adding Accounts to the Shadow File!");
		System.out.println("Currently in folder: " + directory);
		// Get the filename and initialize FileIO variable and Hashing variable
        Console readInput = System.console();
        String filename = args[0];
        String choice;
		FileIO fileIO = new FileIO();
		Hashing hashing = new Hashing();

        while(true) {
			// If user wants to write a username and password to shadowfile or not
        	choice = readInput.readLine("Would you like to create an account? [y/n]: ");
        	if (choice.equals("n")) {
        		break;
        	}
        	else if (choice.equals("y")) {
				// Get username and password as input
        		String username = readInput.readLine("Enter your Username: ");
    			String password = readInput.readLine("Enter your Password: ");
				while(username.length() < 2) {
					System.out.println("Please enter a longer username (2+ characters)");
					username = readInput.readLine("Enter your Username: ");
				}
				while(password.length() < 2) {
					System.out.println("Please enter a longer password (2+ characters)");
					username = readInput.readLine("Enter your Password: ");
				}
    			String saltString;
				// Generate a salt and convert it to hex as a string
    			byte[] salt = hashing.generateSalt();
    			saltString = hashing.hashToHex(salt);
				// Hash the password with the salt and convert it to hex as a string
    			byte[] hash_password = hashing.sha256Hash(saltString, password);
    			password = hashing.hashToHex(hash_password);
				// Write the username, salt, and password to the shadowfile
    			fileIO.writeShadowFile(filename, saltString, username, password);
    			System.out.println("--------------------------------------------");
        	}
        	else {
        		System.out.println("Invalid Input.");
        	}
        }
	}
}
