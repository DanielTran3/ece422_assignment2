import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

public class KeyStorage {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generateKeys() {
		this.pairKey = this.keyGenDH.generateKeyPair();
		this.privKey = this.pairKey.getPrivate();
		this.pubKey = this.pairKey.getPublic();
	}

	public int[] byteToIntArray(byte[] input)
	{
	    int[] ret = new int[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	        ret[i] = input[i];
	    }
	    return ret;
	}

	public byte[] intToByteArray(int[] encIntArray) {
		ByteBuffer byteBuf = ByteBuffer.allocate(encIntArray.length * 4);
		IntBuffer intBuf = byteBuf.asIntBuffer();
		intBuf.put(encIntArray);
		return byteBuf.array();
	}

	public int[] encrypt_message(byte[] value) {
        int[] intValue = byteToIntArray(value);
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.encryption(intValue, intSecretKey);
        return intValue;
	}

	//public String encrypt_message_String(byte[] value) {
        //return new String(encrypt_message(value));
	//}

	public byte[] decrypt_message(int[] value) {
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.decryption(value, intSecretKey);
        return intToByteArray(value);
	}

	public String decrypt_message_String(int[] value) {
        return new String(decrypt_message(value));
	}

	public PrivateKey getPrivateKey() {
		return this.privKey;
	}

	public PublicKey getPublicKey() {
		return this.pubKey;
	}

	public void setPrivateKey(PrivateKey priv) {
		this.privKey = priv;
	}

	public void setPublicKey(PublicKey pub) {
		this.pubKey = pub;
	}

	public byte[] getSecretKey() {
		return this.secretKey;
	}

	public void setSecretKey(byte[] sk) {
		this.secretKey = sk;
	}
}
