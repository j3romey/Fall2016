Authentication Algorithm
    - Uses Java's MessageDigest with the instance of "SHA1"
Type and Source of PRNG
    - Uses Java's SecureRandom with the instance of "SHA1PRNG"
    - Source is the "Seed" from user input
method for encoding the input to the encryption algorithm
    - Since the Digest is always 20 bytes long it is just appeneded to the end of the file
    - when decrypting, I decrypt the whole file then separate the original file from the digest

Compilation Method
    secureFile
    - javac secureFile.java
    - java secureFile [filename] [seed]

    decryptFile
    - javac decryptFile.java
    - java decryptFile [filename] [seed]


