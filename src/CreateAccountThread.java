import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class CreateAccount extends Thread {

	private String clientID;
	private String clientPassword;
	private Socket serverSocket;
	private KeyStorage serverKeys;
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";
	private FileIO fileIO;
	private Hashing hashing;
	private String directory = System.getProperty("user.dir");
	private byte[] salt;
	private byte[] byte_password;

	public CreateAccount(Socket accept) {
		this.serverSocket = accept;
		fileIO = new FileIO();
		hashing = new Hashing();
	}

	public static void main (String[] args) {
		if (args.length != 3) {
			System.out.println("Please Enter Only Two Inputs: shadowfile username password");
			System.exit(0);
		}        
		System.out.println("Adding Accounts to the Shadow File!");
		System.out.println("Currently in folder: " + directory);
		try {
			String filename = args[0];
			String username = args[1];
			String password = args[2];

			String saltString;
			salt = hashing.generateSalt();
			saltString = hashing.hashToHex(salt);
			byte_password = hashing.sha256Hash(saltString, password);
			password = hashing.hashToHex(byte_password);
			fileIO.writeShadowFile(Server.getShadowFile(), saltString, username, password);
			count++;

			readFromClient.close();
			writeToClient.close();
			this.serverSocket.close();
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
}
