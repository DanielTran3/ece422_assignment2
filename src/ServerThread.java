import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import javax.crypto.KeyAgreement;

public class ServerThread extends Thread {

	private String clientID;
	private String clientPassword;
	private Socket serverSocket;
	private KeyExchange serverKeys;

	public ServerThread(Socket accept) {
		this.serverSocket = accept;

	}

	public void run() {
        System.out.println("Server Connected to Client!");
		try {
			//PrintWriter writeToClient = new PrintWriter(this.serverSocket.getOutputStream(), true);
            //BufferedReader readFromClient = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			ObjectOutputStream writeToClient = new ObjectOutputStream(serverSocket.getOutputStream());
            ObjectInputStream readFromClient = new ObjectInputStream(serverSocket.getInputStream());
			String credentials;
			while ((credentials = (String) readFromClient.readObject()) != null) {
				System.out.println(credentials);
			}
			serverKeys = new KeyExchange();
			serverKeys.generateKeys();
			
			PublicKey clientPubKey = (PublicKey) readFromClient.readObject();
            writeToClient.writeObject(serverKeys.getPublicKey());
            writeToClient.flush();
            
            KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(serverKeys.getPrivateKey());
			ka.doPhase(clientPubKey, true);
			byte[] secretKey = ka.generateSecret();
			System.out.println(Arrays.toString(secretKey));
			//serverKeys.setEncryptedPublicKey(serverKeys.encrypt_key(serverKeys.getPublicKey()));
            //System.out.println("Encrypted Key: " + serverKeys.getEncryptedPublicKey());
            //System.out.println("DecryptedKey: " + serverKeys.decrypt_key(serverKeys.getEncryptedPublicKey()));

			//serverKeys.setEncryptedPublicKey(serverKeys.getPublicKey().getEncoded());
			// Read Client encrypted public key
			
			readFromClient.close();
			writeToClient.close();
			this.serverSocket.close();
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}

}

//String clientUsername = readFromClient.readLine();
//String clientPassword = readFromClient.readLine();
//if ((clientUsername == null) || (clientPassword == null)) {
//	System.out.println("Error Occurred in receiving credentials.");
//	System.out.println(clientUsername);
//	System.out.println(clientPassword);
//}
//else {
//	System.out.println(clientUsername);
//	System.out.println(clientPassword);
//}