import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;

/**
 * Client program.  Connects to the server and sends text accross.
 */

public class Client {
	
	
    private Socket sock;  //Socket to communicate with.
	
    /**
     * Main method, starts the client.
     * @param args args[0] needs to be a hostname, args[1] a port number.
     */
    public static void main (String [] args){
    	
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
    public Client (String ipaddress, int port){
    	/* Allows us to get input from the keyboard. */
    	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    	PrintWriter pw;
    	DataOutputStream out; 
    	BufferedReader in;
    	
    	FileInputStream in_file = null;
		
    	/* Try to connect to the specified host on the specified port. */
    	try {
    		sock = new Socket (InetAddress.getByName(ipaddress), port);
    	}catch (UnknownHostException e) {
    		System.out.println ("Usage: java Client hostname port#");
    		System.out.println ("First argument is not a valid hostname");
    		return;
    	}catch (IOException e) {
    		System.out.println ("Could not connect to " + ipaddress + ".");
    		return;
    	}
		
    	/* Status info */
    	System.out.println ("Connected to " + sock.getInetAddress().getHostAddress() + " on port " + port);
    	
    	try {
    		in = new BufferedReader (new InputStreamReader (sock.getInputStream()));
    		out = new DataOutputStream(sock.getOutputStream());
    		pw = new PrintWriter(out);
    	}catch (IOException e) {
    		System.out.println ("Could not create output stream.");
    		return;
    	}
		
    	/* Wait for the user to type stuff. */
    	try {
    		// get user inputs
    		System.out.println("------------------------------------------------------");
    		System.out.println("Welcome to the 99.99% Secure File Transfer Application");
    		System.out.println("------------------------------------------------------");

    		System.out.print("Please enter shared Secret Key: ");
    		String seed = stdIn.readLine();
    		
    		System.out.print("Please enter Filename to send: ");
    		String filename_in = stdIn.readLine();
    		filename_in = "vanastrea.jpg";
    		
    		System.out.print("Please enter destination Filename: ");
    		String filename_out = stdIn.readLine(); 
    		
    		FileInputStream file = new FileInputStream(filename_in);
    	    byte[] msg = new byte[file.available()];
    	    int temp = file.read(msg);
 			
    	    // send the filename destination and filesize
    		pw.println(filename_out);
    		
    		String size = Integer.toString(CryptoUtilities.file_size(filename_in));
    		pw.println(size);
    		pw.flush();
    		
    		// make message 
    		byte[] message = secureFile.encrypt(filename_in, "hi");
    		out.write(message);

    		
    		System.out.println("Transfer is done");
    		System.out.println("--------------------");
    		System.out.println("Acknowledge Recieved");
    		System.out.println("--------------------");
    		System.out.print("Closing Client");
    		for(int i = 0; i < 3; i++){
    			TimeUnit.MILLISECONDS.sleep(500);
    			System.out.print(".");
    		}
    		System.out.print("DONE");
    		
    		
    		// exits the server
    		pw.println("test");
    		stdIn.close ();
			out.close ();
			sock.close();
    		
    		/*	if ((out.checkError()) || (userinput.compareTo("exit") == 0) || (userinput.compareTo("die") == 0)) {
    				System.out.println ("Client exiting.");
    				stdIn.close ();
    				out.close ();
    				sock.close();
    				done = true;
    				return;
    			}*/
    		
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
    }
}
