
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.interfaces.*;
import java.security.interfaces.DSAKey;
import java.math.*;

public class secureFile {
	public static void main(String[] args) {
		String filename = args[0];
		String seed = args[1];
		
		byte[] dataBytes = "This is test data".getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			KeyGenerator kg = KeyGenerator.getInstance("DES");
			SecretKey sKey = kg.generateKey();
			md.update(dataBytes);
			byte[] digest = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
}
