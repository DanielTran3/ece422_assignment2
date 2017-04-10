import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class KeyExchange {
	
	private KeyPairGenerator keyGenDH;
	private KeyPair pairKey;
	private PrivateKey privKey;
	private PublicKey pubKey;
	private byte[] encrypt_privKey;
	private byte[] encrypt_pubKey;
	private byte[] serverKey;

	public KeyExchange() {
		try {
			this.keyGenDH = KeyPairGenerator.getInstance("DiffieHellman");
			this.keyGenDH.initialize(128, new SecureRandom());
			this.encrypt_privKey = null;
			this.encrypt_pubKey = null;
			this.serverKey = null;
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
	
	public byte[] getEncrpytedPrivateKey() {
		return this.encrypt_privKey;
	}
	
	public byte[] getEncryptedPublicKey() {
		return this.encrypt_pubKey;
	}
	
	public void setEncryptedPrivateKey(byte[] en_priv) {
		this.encrypt_privKey = en_priv;
	}
	
	public void setEncryptedPublicKey(byte[] en_pub) {
		this.encrypt_pubKey = en_pub;
	}

	public byte[] getEncServerKey() {
		return this.serverKey;
	}
	
	public void setEncServerKey(byte[] enc_serverKey) {
		this.serverKey = enc_serverKey;
	}
}
