Files:
    CryptoUtilities.java - Contains all the methods needed to encrypt and decrypt a file
    Client.java - Connects to Server by giving IP address and port # in the command line
                - Asks user for shared key, filename and destination filename
                - Sends Filename, Filesize and File to the server
                - Waits for acknowledgement before closing connection
    Server.java - Creates a serversocket given a port # in the command line
                - Asks user for shared key, and listens for incoming connections
                - when a connection is accept it creates a new thread and accepts information from the client
    ServerThread.java - waits for client to send, Filename, Filesize and File
                      - decrypts the accepted file and writes it with the given Filename
                      - Sends acknowledgement and closes connection with client
                      - Kills thread (itself)

Compile Instruction:
    Client: javac Client.java
    Sever: javac Server.client
    
--File Transfer Protocol--

File Protocol Messages(Filename, Filesize and File):
    Filename: - Initially a String but sent as a byte[] by calling CryptoUtilities.encrypt
                on the byte[] of the Filename
              - Send the encyrpted byte[] size then the encrypted byte[]
              - Server recieves the size then the byte array
              - Server turns byte[] into string by "new String(byte[], "UTF-8");"
    Size: - is an Integer based on the encyrpted file byte[] size
          - Sends the size to the server by "dataOut.writeInt("encyrpted file byte[]".length);"
          - Server recieves the size with dataIn.readInt()
    File:  - Client reads in the file and encrypts it in a byte[]
           - Send the byte[] to Server
           - Server reads in the byte[] based on previous information
           - Server runs decryption on the File
           - Server writes the File

Encryption and Data Integrity
    Encryption: Is applied with using AES-128-CBC and applying it to the Filename, Filesize and File
                before sending it to the server.

    Data Integrity: Is placed in the HMAC-SHA-1 hash that is appended at the end of the file before it
                    is to be decrypted. This will then be used as a value to compare to the orginal file
                    after it is decrypted.

Prevent Attacks
    Confidentiality: All of the information that is sent to the server has been encrypted before hand
                     there if any adversary would want to know any information that is being sent to 
                     the server they would need to know the shared key that the client has inputted.

    Integrity: If the hash appended at the end of the file and the hash generated from the decrypted file
               match then Integrity has been perserved if not they that will notify us that the message has 
               been modified by an adversary.
