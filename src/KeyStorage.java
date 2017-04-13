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
        int[] full_encrypt = new int[intValue.length];
        int[] block = new int[2];
        for (int i = 0; i < full_encrypt.length - 1; i++) {
        	block[0] = intValue[i];
        	block[1] = intValue[i + 1];
        	cipher.encryption(block, intSecretKey);
        	full_encrypt[i] = block[0];
        	full_encrypt[i+1] = block[1];
        }
        return full_encrypt;
	}

	// Decrypt a message, returning it as an byte[]
	public byte[] decrypt_message(int[] value) {
        int[] intSecretKey = byteToIntArray(secretKey);
        int[] full_decrypt = new int[value.length];
        int[] block = new int[2];
        for (int i = full_decrypt.length - 1; i > 0; i++) {
        	block[0] = value[i - 1];
        	block[1] = value[i];
        	cipher.decryption(block, intSecretKey);
        	full_decrypt[i-1] = block[0];
        	full_decrypt[i] = block[1];
        }
        return intToByteArray(full_decrypt);
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
