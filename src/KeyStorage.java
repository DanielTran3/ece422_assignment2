import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

// Stores keys and performs encryption/decryption of messages
public class KeyStorage {
	// Store all of the keys
	private KeyPairGenerator keyGenDH;
	private KeyPair pairKey;
	private PrivateKey privKey;
	private PublicKey pubKey;
	private byte[] secretKey;
	private TEA cipher;
	static {
		System.loadLibrary("encryption");
		System.loadLibrary("decryption");
	}
	public KeyStorage() {
		try {
			this.keyGenDH = KeyPairGenerator.getInstance("DiffieHellman");
			this.keyGenDH.initialize(512, new SecureRandom());
			this.secretKey = null;
			this.cipher = new TEA();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	// Generate the public and private keys and store them
	public void generateKeys() {
		this.pairKey = this.keyGenDH.generateKeyPair();
		this.privKey = this.pairKey.getPrivate();
		this.pubKey = this.pairKey.getPublic();
	}

	// Convert a byte[] into an int[]
	public int[] byteToIntArray(byte[] input)
	{
	    int[] ret = new int[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	        ret[i] = input[i];
	    }
	    return ret;
	}

	// Convert an int[] into a byte[]
	public byte[] intToByteArray(int[] encIntArray) {
		ByteBuffer byteBuf = ByteBuffer.allocate(encIntArray.length * 4);
		IntBuffer intBuf = byteBuf.asIntBuffer();
		intBuf.put(encIntArray);
		return byteBuf.array();
	}

	// Encrypt a message, returning it as an int[]
	public int[] encrypt_message(byte[] value) {
        int[] intValue = byteToIntArray(value);
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.encryption(intValue, intSecretKey);
        return intValue;
	}

	// Decrypt a message, returning it as an byte[]
	public byte[] decrypt_message(int[] value) {
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.decryption(value, intSecretKey);
        return intToByteArray(value);
	}

	// Decrypt a message, returning it as a String
	public String decrypt_message_String(int[] value) {
		String nullMessage = new String(decrypt_message(value));
        return nullMessage.replaceAll("\0", "");
	}

	// Getters and setters for some of the keys
	public PrivateKey getPrivateKey() {
		return this.privKey;
	}

	public PublicKey getPublicKey() {
		return this.pubKey;
	}

	public void setSecretKey(byte[] sk) {
		this.secretKey = sk;
	}
}
