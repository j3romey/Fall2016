import java.io.*;
import java.math.*;
import java.security.*;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * This class provides an implementation of 1024-bit RSA-OAEP.
 *
 * @author Mike Jacobson
 * @version 1.0, October 23, 2013
 */
public class RSATool {
	// OAEP constants
	private final static int K = 128; // size of RSA modulus in bytes
	private final static int K0 = 16; // K0 in bytes
	private final static int K1 = 16; // K1 in bytes
	
	// message length = K - k0 - k1
	
	private final static int bitLen = 1536;
	private final static int certainty = 3;

	// RSA key data
	private BigInteger n;
	private BigInteger e, d, p, q, phiN;

	// TODO: add whatever additional variables that are required to implement
	// Chinese Remainder decryption as described in Problem 2

	// SecureRandom for OAEP and key generation
	private SecureRandom rnd;

	private boolean debug = false;

	/**
	 * Utility for printing protocol messages
	 * 
	 * @param s
	 *            protocol message to be printed
	 */
	private void debug(String s) {
		if (debug)
			System.out.println("Debug RSA: " + s);
	}

	/**
	 * G(M) = 1st K-K0 bytes of successive applications of SHA1 to M
	 */
	private byte[] G(byte[] M) {
		MessageDigest sha1 = null;
		try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
			System.exit(1);
		}

		byte[] output = new byte[K - K0];
		byte[] input = M;

		int numBytes = 0;
		while (numBytes < K - K0) {
			byte[] hashval = sha1.digest(input);

			if (numBytes + 20 < K - K0)
				System.arraycopy(hashval, 0, output, numBytes, K0);
			else
				System.arraycopy(hashval, 0, output, numBytes, K - K0 - numBytes);

			numBytes += 20;
			input = hashval;
		}

		return output;
	}

	/**
	 * H(M) = the 1st K0 bytes of SHA1(M)
	 */
	private byte[] H(byte[] M) {
		MessageDigest sha1 = null;
		try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
			System.exit(1);
		}

		byte[] hashval = sha1.digest(M);

		byte[] output = new byte[K0];
		System.arraycopy(hashval, 0, output, 0, K0);

		return output;
	}
	public BigInteger createSophieGermain(){
    	rnd= new SecureRandom();
    	
    	BigInteger Q;
    	BigInteger P;
    	
		int i = 0;
		do{
			Q = new BigInteger(K*4-1, rnd);
			P = Q.multiply(BigInteger.valueOf(2)).add(BigInteger.valueOf(1));
		}while(!P.isProbablePrime(3));
		
		return P;
    }
	
	
	/**
	 * Construct instance for decryption. Generates both public and private key
	 * data.
	 *
	 * TODO: implement key generation for RSA as per the description in your
	 * write-up. Include whatever extra data is required to implement Chinese
	 * Remainder decryption as described in Problem 2.
	 */
	public RSATool(boolean setDebug) {
		// set the debug flag
		debug = setDebug;

		rnd = new SecureRandom();
		
		p = createSophieGermain();
		q = createSophieGermain();
		

		n = p.multiply(q);
		
		System.out.println("length of N :" + n.bitLength());
		phiN = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		
		e = new BigInteger("3");
		
		while(!(e.gcd(phiN)).equals(BigInteger.ONE)){
			e = e.add(new BigInteger("2"));
		}
		
		d = e.modInverse(phiN);
	}

	/**
	 * Construct instance for encryption, with n and e supplied as parameters.
	 * No key generation is performed - assuming that only a public key is
	 * loaded for encryption.
	 */
	public RSATool(BigInteger new_n, BigInteger new_e, boolean setDebug) {
		// set the debug flag
		debug = setDebug;

		// initialize random number generator
		rnd = new SecureRandom();

		n = new_n;
		e = new_e;

		d = p = q = null;

		// TODO: initialize RSA decryption variables here
	}

	public BigInteger get_n() {
		return n;
	}

	public BigInteger get_e() {
		return e;
	}

	/**
	 * Encrypts the given byte array using RSA-OAEP.
	 *
	 * TODO: implement RSA encryption
	 *
	 * @param plaintext
	 *            byte array representing the plaintext
	 * @throw IllegalArgumentException if the plaintext is longer than K-K0-K1
	 *        bytes
	 * @return resulting ciphertext
	 */
	public byte[] encrypt(byte[] plaintext) {
		debug("In RSA encrypt");
		debug("plaintext length: " + plaintext.length);
		// make sure plaintext fits into one block
		if (plaintext.length > K - K0 - K1)
			throw new IllegalArgumentException("plaintext longer than one block");

		// regular RSA
		
		//BigInteger M = new BigInteger(plaintext);
		
		//BigInteger C = M.modPow(e, n);
		
		//return C.toByteArray();

	BigInteger BIG_st;	
	
	do{	
		//step 1
		rnd = new SecureRandom();
		BigInteger r = new BigInteger(K0*8, rnd);
		
		// step 2
		byte[] zeros = new byte[K-K0-plaintext.length];
		byte[] combine = new byte[K-K0];
		
		System.arraycopy(plaintext, 0, combine, 0, plaintext.length);
		System.arraycopy(zeros, 0, combine, plaintext.length, zeros.length);
		
		BigInteger BIG_combine = new BigInteger(combine);
		BigInteger BIG_Gr = new BigInteger(G(r.toByteArray()));
		BigInteger BIG_s = BIG_combine.xor(BIG_Gr);
		
		// step 3
		BigInteger BIG_Hs = new BigInteger(1, H(BIG_s.toByteArray()));
		BigInteger BIG_t = r.xor(BIG_Hs);
		
		BIG_st = new BigInteger(BIG_s.toString() + BIG_t.toString());
	}while(BIG_st.compareTo(n) > 0 || BIG_st.signum() < 0);	
		
		// step 4
	return BIG_st.modPow(e, n).toByteArray();
	
	}

	/**
	 * Decrypts the given byte array using RSA.
	 *
	 * TODO: implement RSA-OAEP decryption using the Chinese Remainder method
	 * described in Problem 2
	 *
	 * @param ciphertext
	 *            byte array representing the ciphertext
	 * @throw IllegalArgumentException if the ciphertext is not valid
	 * @throw IllegalStateException if the class is not initialized for
	 *        decryption
	 * @return resulting plaintexttext
	 */
	public byte[] decrypt(byte[] ciphertext) {
		debug("In RSA decrypt");

		// note t should read only 16 bytes
		
		// make sure class is initialized for decryption
		if (d == null)
			throw new IllegalStateException("RSA class not initialized for decryption");

		// TODO: implement RSA-OAEP decryption here (replace following return
		// statement)

		//BigInteger M = new BigInteger(ciphertext).modPow(d, n);
		byte[] M = new byte[K0];
		
		BigInteger C = new BigInteger(ciphertext);
		BigInteger st = C.modPow(d, n);
		
		/*// Chinese remainder 
		// Step 1
		BigInteger P1 = p.subtract(BigInteger.ONE);
		BigInteger Q1 = q.subtract(BigInteger.ONE);
		BigInteger DP = d.mod(P1);
		BigInteger DQ = d.mod(Q1);
		
		// step 2
		BigInteger C = new BigInteger(ciphertext);
		BigInteger MP = C.modPow(DP,p);
		BigInteger MQ = C.modPow(DQ,q);
		
		// step 3
		//BigInteger x = p.mod();
		BigInteger x = p.modInverse(q);
		BigInteger px = p.multiply(x);
		BigInteger qy = BigInteger.ONE.subtract(px);
		
		// step 4
		BigInteger pxMQ = px.multiply(MQ);
		BigInteger qyMP = qy.multiply(MP);
		
		BigInteger M = pxMQ.add(qyMP);
		M = M.mod(n);*/
		
		// step 2
		
		int lastBits = 39;
		
		BigInteger limit = new BigInteger("2").pow(128);
		
		do{
			char[] s_part = new char [st.toString().length()-lastBits];
			char[] t_part = new char [lastBits];
			
			st.toString().getChars(0, s_part.length, s_part, 0);
			st.toString().getChars(s_part.length, st.toString().length(), t_part, 0);
			
			BigInteger s = new BigInteger(String.valueOf(s_part));
			BigInteger t = new BigInteger(String.valueOf(t_part));
			
			lastBits--;
			if(t.compareTo(limit) > 0){
				continue;
			}
			
			BigInteger BIG_Hs = new BigInteger(1, H(s.toByteArray()));
			BigInteger BIG_u = t.xor(BIG_Hs);
			
			BigInteger BIG_Gu = new BigInteger(G(BIG_u.toByteArray()));
			BigInteger BIG_v = s.xor(BIG_Gu);
			
			byte[] vBytes = BIG_v.toByteArray();
			
			int zeroCounter = 0;
			boolean check = true;

			for(int i = 0; i < K-K0-K1; i++){
				if(vBytes[vBytes.length-1-i] != 0){
					check = false;
					break;
				}
			}
			
			if(check){
				System.arraycopy(vBytes, 0, M, 0, M.length);
				break;
			}			
		}while (lastBits > 35);
		
		return M;
	}
}