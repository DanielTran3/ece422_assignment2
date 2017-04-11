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

public class KeyExchange {
	
	private KeyPairGenerator keyGenDH;
	private KeyPair pairKey;
	private PrivateKey privKey;
	private PublicKey pubKey;
	private String encrypt_privKey;
	private String encrypt_pubKey;
	private byte[] serverKey;
	private TEA cipher;
	static {
		System.loadLibrary("encryption");
		System.loadLibrary("decryption");
	}
	public KeyExchange() {
		try {
			this.keyGenDH = KeyPairGenerator.getInstance("DiffieHellman");
			this.keyGenDH.initialize(512, new SecureRandom());
			this.encrypt_privKey = null;
			this.encrypt_pubKey = null;
			this.serverKey = null;
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

	//public int[] byteToIntArray(byte byteKey[]) {
	//	IntBuffer intBuf = ByteBuffer.wrap(byteKey).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
	//	return new int[intBuf.remaining()]; 
	//}
	
	public int[] byteToIntArray(byte buf[]) {
		int intArr[] = new int[buf.length / 4];
	   int offset = 0;
	   for(int i = 0; i < intArr.length; i++) {
		  intArr[i] = (buf[3 + offset] & 0xFF) | ((buf[2 + offset] & 0xFF) << 8) |
		              ((buf[1 + offset] & 0xFF) << 16) | ((buf[0 + offset] & 0xFF) << 24);  
	   offset += 4;
	   }
	   return intArr;
	}	
	public byte[] intToByteArray(int encIntArray[]) {
		ByteBuffer byteBuf = ByteBuffer.allocate(encIntArray.length * 4);
		IntBuffer intBuf = byteBuf.asIntBuffer();
		intBuf.put(encIntArray);
		return byteBuf.array(); 
	}	
	
	public String encrypt_key(Key key) {
		System.out.println(key);
		System.out.println("Full Key: " + Arrays.toString(key.getEncoded()));
		System.out.println("Full Key Length: " + key.getEncoded().length);
        byte[] leftKey = Arrays.copyOfRange(key.getEncoded(), 0, 113);
		System.out.println("leftKey: " + Arrays.toString(leftKey));
        byte[] rightKey = Arrays.copyOfRange(key.getEncoded(), 113, 227);
		System.out.println("rightKey: " + Arrays.toString(rightKey));
        int[] intLeftKey = byteToIntArray(leftKey);
		System.out.println("intLeftKey: " + Arrays.toString(intLeftKey));
        int[] intRightKey = byteToIntArray(rightKey);
		System.out.println("intRightKey: " + Arrays.toString(intRightKey));
        cipher.encryption(intLeftKey, intRightKey);
        int[] encryptedIntArray = new int[intLeftKey.length + intRightKey.length];
        System.arraycopy(intLeftKey, 0, encryptedIntArray, 0, intLeftKey.length);
        System.arraycopy(intRightKey, 0, encryptedIntArray, intLeftKey.length, intRightKey.length);
        byte[] encryptedByteArray = intToByteArray(encryptedIntArray);
        return new String(encryptedByteArray);
	}
	
	public String decrypt_key(String key) {
        byte[] leftKey = Arrays.copyOfRange(key.getBytes(), 0, 113);
        byte[] rightKey = Arrays.copyOfRange(key.getBytes(), 113, 227);
        int[] intLeftKey = byteToIntArray(leftKey);
        int[] intRightKey = byteToIntArray(rightKey);
        cipher.decryption(intLeftKey, intRightKey);
        int[] decryptedIntArray = new int[intLeftKey.length + intRightKey.length];
        System.arraycopy(intLeftKey, 0, decryptedIntArray, 0, intLeftKey.length);
        System.arraycopy(intRightKey, 0, decryptedIntArray, intLeftKey.length, intRightKey.length);
        byte[] encryptedByteArray = intToByteArray(decryptedIntArray);
        return new String(encryptedByteArray);
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
	
	public String getEncrpytedPrivateKey() {
		return this.encrypt_privKey;
	}
	
	public String getEncryptedPublicKey() {
		return this.encrypt_pubKey;
	}
	
	public void setEncryptedPrivateKey(String en_priv) {
		this.encrypt_privKey = en_priv;
	}
	
	public void setEncryptedPublicKey(String en_pub) {
		this.encrypt_pubKey = en_pub;
	}

	public byte[] getEncServerKey() {
		return this.serverKey;
	}
	
	public void setEncServerKey(byte[] enc_serverKey) {
		this.serverKey = enc_serverKey;
	}
}
