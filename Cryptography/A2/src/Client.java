import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;

/**
 * Client program.  Connects to the server and sends text accross.
 */

public class Client 
{
    private Socket sock;  //Socket to communicate with.
	
    /**
     * Main method, starts the client.
     * @param args args[0] needs to be a hostname, args[1] a port number.
     */
    public static void main (String [] args)
    {
	if (args.length != 2) {
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("hostname is a string identifying your server");
	    System.out.println ("port is a positive integer identifying the port to connect to the server");
	    return;
	}

	try {
	    Client c = new Client (args[0], Integer.parseInt(args[1]));
	}
	catch (NumberFormatException e) {
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("Second argument was not a port number");
	    return;
	}
    }
	
    /**
     * Constructor, in this case does everything.
     * @param ipaddress The hostname to connect to.
     * @param port The port to connect to.
     */
    public Client (String ipaddress, int port)
    {
	/* Allows us to get input from the keyboard. */
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	String userinput;
	
	DataOutputStream dataOut;
	DataInputStream dataIn;
		
	/* Try to connect to the specified host on the specified port. */
	try {
	    sock = new Socket (InetAddress.getByName(ipaddress), port);
	}
	catch (UnknownHostException e) {
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("First argument is not a valid hostname");
	    return;
	}
	catch (IOException e) {
	    System.out.println ("Could not connect to " + ipaddress + ".");
	    return;
	}
		
	/* Status info */
	System.out.println ("Connected to " + sock.getInetAddress().getHostAddress() + " on port " + port);
		
	try {
		dataOut = new DataOutputStream(sock.getOutputStream());
		dataIn = new DataInputStream(sock.getInputStream());
	  
	}
	catch (IOException e) {
	    System.out.println ("Could not create output stream.");
	    return;
	}
	
	
	
	/* Wait for the user to type stuff. */
	try {
		
		System.out.println("------------------------------------------------------");
		System.out.println("Welcome to the 99.99% Secure File Transfer Application");
		System.out.println("------------------------------------------------------");	
	       	
	    
    	
    	System.out.print("Please enter shared Secret Key: ");
    	String seed = stdIn.readLine();
    	
    	System.out.print("Please enter Filename to send: ");
    	String filename_in = stdIn.readLine();
    	
    	System.out.print("Please enter destination Filename: ");
    	String filename_out = stdIn.readLine();
    	/*
    	String filename_in = "vanastrea.jpg";
	    String filename_out = "vanastrea3.jpg";
	    String seed = "hi";
	    */
	    SecretKeySpec key = CryptoUtilities.key_from_seed(seed.getBytes());
	    
	    byte[] file_to_server = filename_out.getBytes();
	    dataOut.writeInt(file_to_server.length);
	    dataOut.write(file_to_server);
	    dataOut.flush();
		
		FileInputStream file = new FileInputStream(filename_in);
		byte[] msg = new byte[file.available()];
		int read_bytes = file.read(msg);
		
		//dataOut.writeInt(msg.length);
		
		byte[] hashed_msg = CryptoUtilities.append_hash(msg,key);
		byte[] aes_ciphertext = CryptoUtilities.encrypt(hashed_msg,key);
		dataOut.writeInt(aes_ciphertext.length);
		dataOut.write(aes_ciphertext);
		
		
		int length = dataIn.readInt();
		byte[] message = new byte[length];
		if(length > 0){
			dataIn.readFully(message, 0, message.length);
		}
		
		byte[] message_dec = CryptoUtilities.decrypt(message, key);
		String response = new String(message_dec, "UTF-8");
		
		System.out.println("File Sent...");
		System.out.print("Waiting for Acknowledgement");
		for(int i = 0; i < 3; i++){
			TimeUnit.MILLISECONDS.sleep(600);
			System.out.print(".");
		}
		System.out.println(".");
		
		System.out.println("-----------------------");
		System.out.println("---Transfer Complete---");
		System.out.println("-----------------------");
		System.out.println("File is " + response);


		System.out.print("Closing Client");
		for(int i = 0; i < 3; i++){
			TimeUnit.MILLISECONDS.sleep(600);
			System.out.print(".");
		}
		System.out.println("CLOSED");
		
		dataIn.close();
		dataOut.close();
		sock.close();
		
	    
	} catch (IOException | InterruptedException e) {
	    System.out.println ("Could not read from input.");
	    return;
	}		
    }
}
