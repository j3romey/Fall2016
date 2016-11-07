
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
		String out_filename = filename;
		
		FileInputStream file_in = null;
		FileOutputStream file_out = null;
	
		// digest size is 20 bytes long
		byte[] digest = null;
		byte[] combined = null;
		byte[] cipherFile = null;
		SecretKey secKey = null;
		
		//byte[] dataBytes = "This is test data".getBytes();
		
		try {
			// open file
			file_in = new FileInputStream(filename);
			
			// read file to byte
			byte[] file_bytes = new byte[file_in.available()];
			file_in.read(file_bytes);
			file_in.close();
				
			// create digest
			digest = makeDigest(file_bytes); 
			
			
			// create key
			secKey = makeKey(seed);
			
			//combine  file_bytes and digest
			combined = combine(file_bytes, digest);
			
			// encrypt
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secKey);
			cipherFile = cipher.doFinal(combined);

			/* debugging statements
			
			System.out.println("File Size: " + file_bytes.length);
			System.out.println("Digest Size: " + digest_size);
			System.out.println("Combined Size: " + combined.length);
			System.out.println("Ciper Size: " + cipherFile.length);
			System.out.println("Encrypted Filename: " +  out_filename);
			*/
			
			// write to file
			file_out = new FileOutputStream(out_filename);
			file_out.write(cipherFile);
			file_out.close();
			System.out.println("Encryption Complete");
			System.out.println("SUCESSFULLY ENCRYPTED FILE: " + filename);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("File Specified Not Found, Please Try Again");
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static byte[] makeDigest(byte[] a){
		
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance("SHA1");
			md.update(a);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md.digest();
	}
	
	public static SecretKey makeKey(String a){
		KeyGenerator kg = null;
		SecureRandom random = null;
		try {
			kg = KeyGenerator.getInstance("AES");
			random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(a.getBytes());
			
			kg.init(128, random);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return kg.generateKey();
	}

	
	public static byte[] combine(byte[] a, byte[] b){
		byte[] temp = new byte[a.length + b.length];
		
		System.arraycopy(a, 0, temp, 0, a.length);
		System.arraycopy(b, 0, temp, a.length, b.length);
		
		return temp;
	}
	
	
}
