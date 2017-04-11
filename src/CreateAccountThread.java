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

public class CreateAccountThread extends Thread {

	private String clientID;
	private String clientPassword;
	private Socket serverSocket;
	private KeyStorage serverKeys;
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";
	private FileIO fileIO;
	private Hashing hashing;
	private String directory = System.getProperty("user.dir");

	public CreateAccountThread(Socket accept) {
		this.serverSocket = accept;
		fileIO = new FileIO();
	}

	public void run() {
        System.out.println("Adding Accounts to the Shadow File!");
		System.out.println("Currently in folder: " + directory);
		try {
			ObjectOutputStream writeToClient = new ObjectOutputStream(serverSocket.getOutputStream());
            ObjectInputStream readFromClient = new ObjectInputStream(serverSocket.getInputStream());
			
			serverKeys = new KeyStorage();
			serverKeys.generateKeys();

			PublicKey clientPubKey = (PublicKey) readFromClient.readObject();
            writeToClient.writeObject(serverKeys.getPublicKey());
            writeToClient.flush();

            KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(serverKeys.getPrivateKey());
			ka.doPhase(clientPubKey, true);
			serverKeys.setSecretKey(ka.generateSecret());
			System.out.println("Secret Key: " + Arrays.toString(serverKeys.getSecretKey()));

			String username;
			String password;
			byte[] salt;
			int count = 0;
			while (count < 10) {
				username = serverKeys.decrypt_message_String((int[]) readFromClient.readObject());
				password = serverKeys.decrypt_message_String((int[]) readFromClient.readObject());
				System.out.println("Decrypted Username: " + username);
				System.out.println("Decrypted Password: " + username);
				salt = hashing.generateSalt();
				
				password = hashing.sha256Hash(salt, password);
				fileIO.writeShadowFile(Server.getShadowFile(), salt.toString(), username, password);
				count++;
			}

			readFromClient.close();
			writeToClient.close();
			this.serverSocket.close();
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
}
