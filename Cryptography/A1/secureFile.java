
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
		String out_filename = "TESTING";
		
		FileInputStream file_in = null;
		FileOutputStream file_out = null;
		
		int read_bytes = 0;
		int digest_size = 0;
		// byte [] file_bytes
		byte[] digest = null;
		byte[] combined = null;
		byte[] cipherFile = null;
		
		
		//byte[] dataBytes = "This is test data".getBytes();
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA"); 
			
			// open file
			file_in = new FileInputStream(filename);
			file_out = new FileOutputStream(out_filename);
			// read file to byte
			byte[] file_bytes = new byte[file_in.available()];
			read_bytes = file_in.read(file_bytes);
			
			// get digest
			md.update(file_bytes);
			digest = md.digest();
			digest_size = digest.length;
		
			// encrypt
			KeyGenerator kg = KeyGenerator.getInstance("AES-128");
			
			SecretKey sKey = kg.generateKey();
			
			Cipher cipher = Cipher.getInstance("CBC");
			cipher.init(Cipher.ENCRYPT_MODE, sKey);
			cipherFile = cipher.doFinal(file_bytes);
			
			// write to file
			file_out.write(cipherFile);
			
			
			//md.update(dataBytes);
			// = md.digest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
}
