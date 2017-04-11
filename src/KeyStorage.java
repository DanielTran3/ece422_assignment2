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

//	public int[] byteToIntArray(byte byteKey[]) {
//		IntBuffer intBuf = ByteBuffer.wrap(byteKey).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
//		int[] tempArray = new int[intBuf.remaining()];
//		return intBuf.get(tempArray);
//	}

	public int[] byteToIntArray(byte[] input)
	{
	    int[] ret = new int[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	        ret[i] = input[i] & 0xff; // Range 0 to 255, not -128 to 127
	    }
	    return ret;
	}

//	public int[] byteToIntArray(byte buf[]) {
//		int intArr[] = new int[buf.length / 4];
//		int offset = 0;
//		for(int i = 0; i < intArr.length; i++) {
//			intArr[i] = (buf[3 + offset] & 0xFF) | ((buf[2 + offset] & 0xFF) << 8) |
//	              ((buf[1 + offset] & 0xFF) << 16) | ((buf[0 + offset] & 0xFF) << 24);
//			offset += 4;
//			}
//		return intArr;
//	}
	public byte[] intToByteArray(int encIntArray[]) {
		ByteBuffer byteBuf = ByteBuffer.allocate(encIntArray.length * 4);
		IntBuffer intBuf = byteBuf.asIntBuffer();
		intBuf.put(encIntArray);
		return byteBuf.array();
	}

	public byte[] encrypt_message(byte[] value) {
        int[] intValue = byteToIntArray(value);
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.encryption(intValue, intSecretKey);
//        byte[] encryptedValue = intToByteArray(intValue);
//        return new String(encryptedValue);
        return intToByteArray(intValue);
	}

	public String encrypt_message_String(byte[] value) {
		int[] intValue = byteToIntArray(value);
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.encryption(intValue, intSecretKey);
        byte[] encryptedValue = intToByteArray(intValue);
        return new String(encryptedValue);
	}

	public byte[] decrypt_message(byte[] value) {
        int[] intValue = byteToIntArray(value);
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.decryption(intValue, intSecretKey);

        return intToByteArray(intValue);
	}

	public String decrypt_message_String(byte[] value) {
		int[] intValue = byteToIntArray(value);
        int[] intSecretKey = byteToIntArray(secretKey);
        cipher.decryption(intValue, intSecretKey);
        byte[] decryptedValue = intToByteArray(intValue);
        return new String(decryptedValue);
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
