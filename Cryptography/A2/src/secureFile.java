import java.io.*;
import javax.crypto.spec.*;

/**
 * This program performs the following cryptographic operations on the input file:
 *   - computes a random 128-bit key (1st 16 bits of SHA-1 hash of a user-supplied seed)
 *   - computes a HMAC-SHA1 hash of the file's contents
 *   - encrypts the file+hash using AES-128-CBC
 *   - outputs the encrypted data
 *
 * Compilation:    javac secureFile.java
 * Execution: java secureFile [plaintext-filename] [ciphertext-filename] [seed]
 *
 * @author Mike Jacobson
 * @version 1.0, September 25, 2013
 * 
 * 
 * Rewritten to just return the encrypted byte[]
 * 
 */
public class secureFile{

    public static byte[] encrypt(String filename_in, String seed) throws Exception{
	FileInputStream in_file = null;
	//FileOutputStream out_file = null;
	
	byte[] aes_ciphertext = null;
	
	try{
	    // open input and output files
	    in_file = new FileInputStream(filename_in);
	    //out_file = new FileOutputStream(filename_out);

	    // read input file into a byte array
	    byte[] msg = new byte[in_file.available()];
	    in_file.read(msg);

	    // compute key:  1st 16 bytes of SHA-1 hash of seed
	    SecretKeySpec key = CryptoUtilities.key_from_seed(seed.getBytes());

	    // append HMAC-SHA-1 message digest
	    byte[] hashed_msg = CryptoUtilities.append_hash(msg,key);

	    // do AES encryption
	    aes_ciphertext = CryptoUtilities.encrypt(hashed_msg,key);

	    // output the ciphertext
	    //out_file.write(aes_ciphertext);
	    //out_file.close();
	}
	catch(Exception e){
	    System.out.println(e);
	}
	finally{
	    if (in_file != null){
	    	in_file.close();
	    }
	}
	return aes_ciphertext;
    }

}