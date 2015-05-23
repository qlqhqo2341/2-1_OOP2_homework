package working;



import java.util.StringTokenizer;
import java.util.Vector;

import struct.*;
public class Parser {
	CppClass obj;
	String allText;
	
	
	
	
	public Parser(String text){
		allText=text;

		String range[] = divide();
	}

	public String[] divide(){
		StringTokenizer token = new StringTokenizer(allText, "{");
		StringBuffer buffer;
		Vector<String> range;
		
		
		
	}

	
	
	
	public static void main(String[] args) {
		
			
	}
	
	
	
}
