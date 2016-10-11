import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class huffman {
	public static void main(String[] args) {
		
		double subtract_whole = 2 + 222 + 1614;
		double subtract_portion = 0;
		double percent = 0;
		String filename = "A1.txt";
		FileInputStream file_in = null;
		double total_char = 0;
		HashMap<Character, Double> library = new HashMap<Character, Double>();
		StringBuilder builder = new StringBuilder();
		int ch;
		double new_val;
		// read in file
		try {
			file_in = new FileInputStream(filename);
			
			while((ch = file_in.read()) != -1){
				builder.append((char)ch);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String file = builder.toString();
		
		for(int i=0; i<file.length(); i++){
			if(library.containsKey(file.charAt(i))){
				new_val = library.get(file.charAt(i));
				new_val++;
				library.replace(file.charAt(i), new_val);
			}else{
				library.put(file.charAt(i), 1.0);
			}
			
			total_char++;
		}
	
		for (Entry<Character, Double> entry : library.entrySet()) {
		    percent = entry.getValue() / (total_char - subtract_whole);
			
			System.out.println(entry.getKey()+" :\t "+entry.getValue()); //+ "\t" + percent + "%");
		}
		
		
		System.out.println("TOTAL UNIQUE CHARACTERS(exclude spaces): " + (library.size() - 3));
		//System.out.println(builder.toString());	
	}
}
