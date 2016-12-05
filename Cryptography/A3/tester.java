import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;




public class tester {
		
	public static void main(String[] args) {
		
		BigInteger test1 = new BigInteger("20");
		byte[] temp = test1.toByteArray();
		BigInteger test2 = new BigInteger(temp);
		
		System.out.println(test1 + " : " + test2);
		
		/*Random rnd= new Random();
		
		BigInteger q;
		BigInteger p;
		int n = -1;
		
		
		do{
			n++;
			q = new BigInteger(512, 3, rnd);
			
			p = q.multiply(BigInteger.valueOf(2)).add(BigInteger.valueOf(1));

			
		}while(!p.isProbablePrime(3));
		
		System.out.println("Q: " + q.toString());
		System.out.println("P: " + p.toString());
		System.out.println("Tries to get prime: " + n);
		
		
	
		BigInteger g = BigInteger.ZERO;
		
		BigInteger temp1;
		BigInteger temp2;
		
		
		do{
			
			g = g.add(BigInteger.ONE);
			
			temp1 = g.modPow(q, p);
			
		}while(temp1.equals(BigInteger.ONE));
		
		System.out.println("G: " + g);
		
		// get random a, b
		BigInteger a = BigInteger.valueOf(rnd.nextInt(Integer.MAX_VALUE));
		BigInteger b = BigInteger.valueOf(rnd.nextInt(Integer.MAX_VALUE));
		
		BigInteger alice = g.modPow(a, p);
		BigInteger bob = g.modPow(b, p); 
		
		BigInteger alice_r = bob.modPow(a, p);
		BigInteger bob_r = alice.modPow(b, p); 
		
		System.out.println(alice_r.toString() + ":" + bob_r.toString());
		System.out.println(alice_r.equals(bob_r));*/
	}
}

