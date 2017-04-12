import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.nio.file.Files;
import javax.crypto.KeyAgreement;

public class CreateAccounts extends Thread {

	private static FileIO fileIO;
	private static Hashing hashing;
	private static String directory = System.getProperty("user.dir");
	
	public CreateAccounts() {
		fileIO = new FileIO();
		hashing = new Hashing();
	}

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
