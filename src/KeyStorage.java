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

	public int[] byteToIntArray(byte byteKey[]) {
		IntBuffer intBuf = ByteBuffer.wrap(byteKey).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
		return new int[intBuf.remaining()]; 
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
	
	public String encrypt_message(String message) {
        byte[] value = message.getBytes();
		System.out.println("value: " + Arrays.toString(value));
        int[] intValue = byteToIntArray(value);
		System.out.println("intValue: " + Arrays.toString(intValue));
        int[] intSecretKey = byteToIntArray(secretKey);
		System.out.println("intSecretKey: " + Arrays.toString(intSecretKey));
        cipher.encryption(intValue, intSecretKey);
        System.out.println("intValue After Encryption: " + Arrays.toString(intValue));
        byte[] encryptedValue = intToByteArray(intValue);
        return new String(encryptedValue);
	}
	
	public String decrypt_message(String message) {
		byte[] value = message.getBytes();
		System.out.println("value: " + Arrays.toString(value));
        int[] intValue = byteToIntArray(value);
		System.out.println("intValue: " + Arrays.toString(intValue));
        int[] intSecretKey = byteToIntArray(secretKey);
		System.out.println("intSecretKey: " + Arrays.toString(intSecretKey));
        cipher.decryption(intValue, intSecretKey);
        System.out.println("intValue After Encryption: " + Arrays.toString(intValue));
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
