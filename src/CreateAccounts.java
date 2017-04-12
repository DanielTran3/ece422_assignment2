import java.io.Console;

public class CreateAccounts extends Thread {

	private static String directory = System.getProperty("user.dir");
	
	public static void main (String args[]) {
		if (args.length != 1) {
			System.out.println("Please Enter Only Two Inputs: shadowfile");
			System.exit(0);
		}  
		
		System.out.println("Adding Accounts to the Shadow File!");
		System.out.println("Currently in folder: " + directory);
        Console readInput = System.console();
        String filename = args[0];
        String choice;
		FileIO fileIO = new FileIO();
		Hashing hashing = new Hashing();

        while(true) {
        	choice = readInput.readLine("Would you like to create an account? [y/n]: ");
        	if (choice.equals("n")) {
        		break;
        	}
        	else if (choice.equals("y")) {
        		String username = readInput.readLine("Enter your Username: ");
    			String password = readInput.readLine("Enter your Password: ");
    			String saltString;
    			byte[] salt = hashing.generateSalt();
    			saltString = hashing.hashToHex(salt);
    			byte[] byte_password = hashing.sha256Hash(saltString, password);
    			password = hashing.hashToHex(byte_password);
    			fileIO.writeShadowFile(filename, saltString, username, password);
    			System.out.println("--------------------------------------------");	
        	}
        	else {
        		System.out.println("Invalid Input.");
        	}
        }
	}
}
