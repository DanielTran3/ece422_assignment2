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
			this.keyGenDH.initialize(1024, new SecureRandom());
			this.encrypt_privKey = null;
			this.encrypt_pubKey = null;
			this.serverKey = null;
			this.cipher = new TEA();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int[] byteToIntArray(byte byteKey[]) {
		IntBuffer intBuf = ByteBuffer.wrap(byteKey).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
		return new int[intBuf.remaining()]; 
	}
	
	public byte[] intToByteArray(int encIntArray[]) {
		ByteBuffer byteBuf = ByteBuffer.allocate(encIntArray.length * 4);
		IntBuffer intBuf = byteBuf.asIntBuffer();
		intBuf.put(encIntArray);
		return byteBuf.array(); 
	}
	
	public void generateKeys() {
		this.pairKey = this.keyGenDH.generateKeyPair();
		this.privKey = this.pairKey.getPrivate();
		this.pubKey = this.pairKey.getPublic();
	}
	
	public String encrypt_key(Key key) {
        byte[] leftKey = Arrays.copyOfRange(key.getEncoded(), 0, 31);
        byte[] rightKey = Arrays.copyOfRange(key.getEncoded(), 32, 63);
        int[] intLeftKey = byteToIntArray(leftKey);
        int[] intRightKey = byteToIntArray(rightKey);
        cipher.encryption(intLeftKey, intRightKey);
        int[] encryptedIntArray = new int[intLeftKey.length + intRightKey.length];
        System.arraycopy(intLeftKey, 0, encryptedIntArray, 0, intLeftKey.length);
        System.arraycopy(intRightKey, 0, encryptedIntArray, intLeftKey.length, intRightKey.length);
        byte[] encryptedByteArray = intToByteArray(encryptedIntArray);
        return new String(encryptedByteArray);
	}
	
	public String decrypt_key(String key) {
        byte[] leftKey = Arrays.copyOfRange(key.getBytes(), 0, 31);
        byte[] rightKey = Arrays.copyOfRange(key.getBytes(), 32, 63);
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
