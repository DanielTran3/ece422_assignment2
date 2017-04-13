import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.KeyAgreement;

public class Client {
	private static KeyStorage clientKeys;
	private static final String fileNotFound = "FILE NOT FOUND";
	private static final String fileFound = "FILE FOUND";
	private static final String ACCESS_DENIED = "Access-Denied";
	private static final String ACCESS_GRANTED = "Access-Granted";

    public static void main (String args[]) {

		// Ensure user inputs portnumber and hostname
        if (args.length != 2) {
			System.out.println("Please Enter Only Two Inputs: Portnumber Hostname");
			System.exit(0);
		}

		// Get portnumber and hostname
        int port = Integer.parseInt(args[0]);
        String hostname = args[1];
        Console readInput = System.console();

        try {
			// Create client socket
			System.out.println("Connecting to Computer: " + hostname + " On port: " + port);
            Socket clientSocket = new Socket(hostname, port);
            System.out.println("Connection Successful!");

			// Create streams for read and write to server
            ObjectOutputStream writeToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream readFromServer = new ObjectInputStream(clientSocket.getInputStream());

			// Initialize FileIO variable to download files
            FileIO fileIO = new FileIO();

            if (readInput == null) {
    			System.out.println("Error in reading from console.");
    			System.exit(0);
    		}

			// Get username, password, and check their lengths
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

			// Generate Public and Private Keys
            clientKeys = new KeyStorage();
            clientKeys.generateKeys();

			// Send PublicKey to Server
            writeToServer.writeObject(clientKeys.getPublicKey());
            writeToServer.flush();
			// Read Server's PublicKey
            PublicKey serverPubKey = (PublicKey) readFromServer.readObject();
			// Generate and store shared Secret Key
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(clientKeys.getPrivateKey());
			ka.doPhase(serverPubKey, true);
			clientKeys.setSecretKey(ka.generateSecret());

			// Send encrypted username to Server
			writeToServer.writeObject(clientKeys.encrypt_message(username.getBytes()));
            writeToServer.flush();
			// Send encrypted password to Server
            writeToServer.writeObject(clientKeys.encrypt_message(password.getBytes()));
            writeToServer.flush();

			// Read and decrypt access message from server for a username password match
            if (clientKeys.decrypt_message_String((int[]) readFromServer.readObject()).equals(ACCESS_DENIED)) {
            	System.out.println(ACCESS_DENIED);
            }
            else {
            	System.out.println(ACCESS_GRANTED);
				// Get a string for the directory that the user's files will be downloaded to
				String directory = System.getProperty("user.dir") + '/' + username + "_Downloads";
				// If the directory doesn't exist, then create it
				fileIO.dirExists(directory);

				// Extra required variables
	            String file_request;
	            String ack;
				byte[] readFile;	
				byte[] cleanedReadFile;

	            while(true) {
					// Read in filename that we want from the server
					file_request = readInput.readLine("Enter Filename or type \"exit\" to exit: ");
					while(file_request.length() < 2) {
						System.out.println("Please enter a longer filename (2+ characters)");
						username = readInput.readLine("Enter Filename or type \"exit\" to exit: ");
					}

					// If user typed "exit", then send encrypted "finished" message to server
					if (file_request.equals("exit")) {
						System.out.println("Ending Session...");
	            		writeToServer.writeObject(clientKeys.encrypt_message("finished".getBytes()));
						writeToServer.flush();
						break;
					}

					// Send encrypted filename to server
	                writeToServer.writeObject(clientKeys.encrypt_message(file_request.getBytes()));
					writeToServer.flush();

					// Read and decrypt acknowledgement from server
	                ack = clientKeys.decrypt_message_String((int[]) readFromServer.readObject());

					// If the file doesn't exist, then re-prompt user for new file
					if (ack.equals(fileNotFound)) {
	                	System.out.println(ack);
	                	continue;
	                }

					// If the file exists, read and decrypt file from server, print to terminal
					// and save into directory
	                if (ack.equals(fileFound)) {
	                	System.out.println("File Found! Displaying...");
	                	readFile = clientKeys.decrypt_message((int[]) readFromServer.readObject());
						// Clear padding bytes from the byte to int conversion						
						cleanedReadFile = new byte[readFile.length/4];						
						for (int i = 0; i < cleanedReadFile.length; i++) {
							cleanedReadFile[i] = readFile[(i*4) + 3];
						}
	                	System.out.println("________________________________________________________________");
						System.out.println("File: " + new String(cleanedReadFile));
						System.out.println("________________________________________________________________");
						fileIO.saveToFile(directory, file_request, cleanedReadFile);
	                }
	            }
            }
			// Close all readers, writers, and sockets
			readFromServer.close();
			writeToServer.close();
			clientSocket.close();
        }
		// Catch for any exceptions that can occur
        catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Failed to create socket.");
            e.printStackTrace();
        }
    }
}
