import java.math.*;

public class tester {

	public static void main(String[] args) {

		// create 2 BigInteger objects
		BigInteger bi1, bi2;

		bi1 = new BigInteger("1");
		bi2 = new BigInteger("3");

		// create int object
		int res;

		// compare bi1 with bi2
		RSATool rsa = new RSATool(true);
		byte[] l = rsa.encrypt(new String("xdfsdfsdfafdsadfasdfasfdsadfsadfsdfsdf").getBytes());
		byte[] m = rsa.decrypt(l);
		System.out.println(new String(m));
		System.out.println(m.length);
	}
}