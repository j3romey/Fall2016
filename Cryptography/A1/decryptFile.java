
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.interfaces.*;
import java.security.interfaces.DSAKey;
import java.util.Arrays;
import java.math.*;


public class decryptFile {
	public static void main(String[] args) {
		String filename = args[0];
		String seed = args[1];	
		String out_filename = filename;
		

		FileInputStream file_in = null;
		FileOutputStream file_out = null;
	
		// digest size is 20 bytes long
		int digest_size = 20;

		byte[] cipherFile = null;
		byte[] decrypted = null;
		
		byte[] digest_from_digest = null;
		byte[] digest_from_file = null;
		byte[] main_file = null;
		
		SecretKey secKey = null;
		
		try {
			// read file
			file_in = new FileInputStream(filename);
			cipherFile = new byte[file_in.available()];
			file_in.read(cipherFile);
			file_in.close();
			
			// create key
			secKey = makeKey(seed);

			// decrypt
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secKey);
			decrypted = cipher.doFinal(cipherFile);
			
			// get the main file and digest separated
			main_file = split(decrypted, digest_size, 0);
			digest_from_digest = split(decrypted, digest_size, 1);
			
			//check digest
			digest_from_file = makeDigest(main_file);
			
			/*
			System.out.println("Cipher Size: " + cipherFile.length);
			System.out.println("Main Size: " + main_file.length);
			System.out.println("Digest Size: " + digest_from_digest.length);
			System.out.println("Digest Made: "+ digest_from_file.length);
			System.out.println("Match Digest: " + compareDigest(digest_from_file, digest_from_digest));
			*/
			
			if(compareDigest(digest_from_file, digest_from_digest)){
				System.out.println("MESSAGE AUTHENTICATED, DIGEST ARE A MATCH");
			}else{
				System.out.println("WARNING MESSAGE UNAUTHENTICATED, DIGEST ARE A MISMATCH");
			}
			
			// write decrypted file
			file_out = new FileOutputStream(out_filename);
			file_out.write(main_file);
			file_out.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(" ____ ____ ____ ____ ____ ____ ____ ");
			System.out.println("||W |||A |||R |||N |||I |||N |||G ||");
			System.out.println("||__|||__|||__|||__|||__|||__|||__||");
			System.out.println("|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|");
			System.out.println("FILE HAS BEEN TAMPERED WITH OR ERROR IN DECRYPTION");
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
	
	public static byte[] split(byte[] a, int s, int i){
		
		byte[] temp = null;
		if(i == 0){
			
			temp = new byte[a.length-i];
			temp = Arrays.copyOfRange(a, 0, (a.length-s));
			
		}else{
			temp = new byte[i];
			temp = Arrays.copyOfRange(a, a.length-s, a.length);
		}
		
		return temp;
	}
	
	public static boolean compareDigest(byte[]a, byte[] b){
		String temp_a = Arrays.toString(a);
		String temp_b = Arrays.toString(b);
		
		return temp_a.equals(temp_b);
		
	}
}
