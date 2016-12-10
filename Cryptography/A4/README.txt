Files:
    RSATool.java - Initializes the RSA variables
		 - Able to Encrypt the AES-key 
		 - Able to Decerypt the AES-key
		 - has the Hash functions G and H *provided
    -provided by the class-
    CryptoUtilities.java 
    Client.java 
    Server.java 
    ServerThread.java

Compile Instruction:
    Client: javac Client.java
    Sever: javac Server.java

-- RSA -- 
Generation:
	- create sophie germain primes for "p" and "q"
	- create "n" by multiplying "p" and "q"
	- calculate "phi(n)"
	- calculate "e" and "e" is always odd
	- caluclate "d" by "e.modInverse(phi(n))"
Encryption:
	- Step 1: create BigInteger "r" of size(K0*8) 'cuz k0 is in byte, want bits'
	- Step 2: We concatenate 0's to the plaintext until size [K-K0]
		  Calculate G(r) as a BigInteger
		  XOR G(r) with the message padded with 0's to calculate "s"
	- Step 3: Calculate H(s) as a BigInteger
		  XOR H(s) with "r" to create a BigInteger "t"
		  concatenate "s" and "t"
	- check*: to make sure (s||t) < N and (s||t) is positive, else go back to step 1
	- Step 4: raise "s||t" by "e" and mode by "N" and return
Decryption:
	- perform Chinese Remainder Theorem
	- Remove padded 0's
	- return the decrypted Message