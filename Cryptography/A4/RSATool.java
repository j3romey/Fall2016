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
		
		p = new BigInteger(bitLen, certainty, rnd);
		
		
		
		do{
			q = new BigInteger(bitLen, certainty, rnd);
		}while(q.subtract(p).compareTo(BigInteger.valueOf((long) Math.pow(2, 16))) == 1);
		
		n = p.multiply(q);
		System.out.println("length of N :" + n.bitLength());
		phiN = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		
		e = BigInteger.valueOf(65537);
		
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
		
		BigInteger M = new BigInteger(plaintext);
		
		BigInteger C = M.modPow(e, n);
		
		return C.toByteArray();
		
		// TODO: implement RSA-OAEP encryption here (replace following return
		// statement)
		
		/*BigInteger ST = BigInteger.ZERO;
		
		do{
		// step 1
		byte[] r = new byte[K0];
		rnd.nextBytes(r);
		
		
		// step 2
		byte[] Gr = G(r);
		
		byte[] app = new byte[K1];
		byte[] M_app = new byte[plaintext.length + K1];
		System.arraycopy(plaintext, 0, M_app, 0, plaintext.length);
		System.arraycopy(app, 0, M_app, plaintext.length, app.length);
		
		
		debug("G(r) length: " + Gr.length);
		debug("M_app length: " + M_app.length);
		
		BigInteger S = new BigInteger(M_app).xor(new BigInteger(Gr));
		byte[] s = S.toByteArray();
		
		// step 3
		byte[] Hs = H(s);
		
		BigInteger T = new BigInteger(r).xor(new BigInteger(Hs));
		byte[] t = T.toByteArray();
		
		debug("H(s) length: " + Hs.length);
		debug("t length: " + t.length);
		/*for(int i = 0; i < r.length; i++){
			t[i] = (byte) (r[i] ^ Hs[i]);
		}
		
		//step 4
		byte[] st = new byte[s.length + t.length];
		System.arraycopy(s, 0, st, 0, s.length);
		System.arraycopy(t, 0, st, s.length, t.length);
		System.out.println(new String(st));
		System.out.println("------------------------------");
		
		debug("st length:" + st.length);
		
		ST = new BigInteger(st);
		}while(ST.compareTo(n) == -1);
		
		return ST.modPow(e, n).toByteArray();*/
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
		
		BigInteger C = new BigInteger(ciphertext);
		BigInteger M = C.modPow(d, n);
		
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
		
		return M.toByteArray();
	}
}