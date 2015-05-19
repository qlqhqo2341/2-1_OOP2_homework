package working;

import java.io.*;

public class ReadFile {
	public static void main(String[] args) {
		int b = 0;
		StringBuffer buffer = new StringBuffer();
		FileInputStream file = null;
		try {
			file = new FileInputStream(args[0]);
			b = file.read();
			while( b!=-1 ){
				buffer.append((char)b);
				b = file.read();
			}
			System.out.println(buffer);
		} catch (FileNotFoundException e) {
			System.out.println("Oops : FileNotFoundException");
		} catch (IOException e){
			System.out.println("Input error");
		}
		
	}
}
