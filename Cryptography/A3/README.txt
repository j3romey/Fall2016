Files:
    CryptoUtilities.java - Contains all the methods needed to encrypt and decrypt a file
                         - Also contains methods to generate/send/receive keys
    Client.java - Connects to Server by giving IP address and port # in the command line
                - Asks user for filename and destination filename
                - generates P and Q and sends it to sever
                - calculates G then a publickey and sends it to server
                - calculates the server publickey with the client private key                 
                - Sends Filename, Filesize and File to the server
                - Waits for acknowledgement before closing connection
    Server.java - Creates a serversocket given a port # in the command line
                - listens for incoming connections
                - when a connection is accept it creates a new thread and accepts information from the client
 
    ServerThread.java - accepts P, Q, and client publickey and then generates G
                      - calculates server publickey and sends it to client
                      - calculates the client publickey with the sever private key 
                      - waits for client to send, Filename, Filesize and File
                      - decrypts the accepted file and writes it with the given Filename
                      - Sends acknowledgement and closes connection with client
                      - Kills thread (itself)

Compile Instruction:
    Client: javac Client.java
    Sever: javac Server.java

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

--Diffie Hellman--
    Client:
    - calculates P and Q values
    - calculates G from (P & Q)
    - creates a privatekey value from 0 to (P-2)
    - creates publickey = G^privatekey (mod P)

    - sends P and G to server 
    - send publickey to Server
    - recieves Server's publickey

    - creates a shared key by raising the Server's public key to the Client's privatekey
    

    Server: 
    - recieve P and G from the Client
    - get the Client's publickey
    - creates a privatekey value from 0 to (P-2)
    - creates publickey = G^privatekey (mod P)
    - send publickey to Client

     - creates a shared key by raising the Client's public key to the Server's privatekey
    


