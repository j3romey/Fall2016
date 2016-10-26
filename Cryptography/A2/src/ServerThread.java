import java.net.*;

import javax.crypto.spec.SecretKeySpec;

import java.io.*;

/**
 * Thread to deal with clients who connect to Server.  Put what you want the
 * thread to do in it's run() method.
 */

public class ServerThread extends Thread
{
    private Socket sock;  //The socket it communicates with the client on.
    private Server parent;  //Reference to Server object for message passing.
    private int idnum;  //The client's id number.
    private SecretKeySpec key;
	
    /**
     * Constructor, does the usual stuff.
     * @param s Communication Socket.
     * @param p Reference to parent thread.
     * @param id ID Number.
     */
    public ServerThread (Socket s, Server p, int id, String k)
    {
	parent = p;
	sock = s;
	idnum = id;
	key = CryptoUtilities.key_from_seed(k.getBytes());
    }
	
    /**
     * Getter for id number.
     * @return ID Number
     */
    public int getID ()
    {
	return idnum;
    }
	
    /**
     * Getter for the socket, this way the parent thread can
     * access the socket and close it, causing the thread to
     * stop blocking on IO operations and see that the server's
     * shutdown flag is true and terminate.
     * @return The Socket.
     */
    public Socket getSocket ()
    {
	return sock;
    }
	
    /**
     * This is what the thread does as it executes.  Listens on the socket
     * for incoming data and then echos it to the screen.  A client can also
     * ask to be disconnected with "exit" or to shutdown the server with "die".
     */
    public void run (){
    	
    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    
    	DataInputStream dataIn;
    	DataOutputStream dataOut;
		
    	try {
    		dataIn = new DataInputStream(sock.getInputStream());
    		dataOut = new DataOutputStream(sock.getOutputStream());
    
    	}catch (UnknownHostException e) {
    		System.out.println ("Unknown host error.");
    		return;
    	}catch (IOException e) {
    		System.out.println ("Could not establish communication.");
    		return;
    	}
    	
    	try {
    		System.out.println("-------------------------------------");
    		System.out.println("Incoming File Transfer from Client " + idnum);
    		System.out.println("-------------------------------------");
			int length = dataIn.readInt();
			byte[] message = new byte[length];
			
			if(length > 0){
				dataIn.readFully(message, 0, message.length);
			}
			
			// get filename
			String name = new String(message, "UTF-8");
			System.out.println("Filename: " + name);
			
			// get file length
			length = dataIn.readInt();
			System.out.println("File size: " + length);
			
			// get encrypted file
			message = new byte[length];
			if(length > 0){
				dataIn.readFully(message, 0, message.length);
			}
			
			byte[] hashed_plaintext = CryptoUtilities.decrypt(message,key);
			System.out.println("Recieved File");
			
			byte[] plaintext = CryptoUtilities.extract_message(hashed_plaintext);
			System.out.println("Decrypted File");
			
			FileOutputStream f_out = new FileOutputStream(name);
			f_out.write(plaintext);
			f_out.close();
			System.out.println("Finished Writing File");
			
			if (CryptoUtilities.verify_hash(hashed_plaintext,key)) {
				byte[] ack = CryptoUtilities.encrypt("Acknowledged".getBytes(), key); 
				dataOut.writeInt(ack.length);
				dataOut.write(ack);
			}else{
				byte[] nack = CryptoUtilities.encrypt("Not Acknowledged".getBytes(), key);
				dataOut.writeInt(nack.length);
				dataOut.write(nack);
			}
			System.out.println("Sent Verification");
			System.out.println("-----------------");
			
			
			dataIn.close();
			dataOut.close();
			parent.kill(this);
			sock.close();
			
			System.out.println("Close Connection with Client " + idnum);
			System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}
