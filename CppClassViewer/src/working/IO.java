package working;

import java.io.*;
public class IO {

	public static String read(File file){
		StringBuffer buf= new StringBuffer();
		try {
			FileInputStream input=new FileInputStream(file);
			int k;
			
			k=input.read();
			while(k!=-1){
				buf.append((char)k);
				k=input.read();
			}
			input.close();
		}  catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println("File Not found Error!");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("File read error!");
			e.printStackTrace();
			return null;
		}
		
		return buf.toString();
	}
	
	public static void write(File file, String text){
		try {
			FileOutputStream output = new FileOutputStream(file);
			for(char c : text.toCharArray()){
				output.write(c);
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println("File not making");
			e.printStackTrace();
		} catch (IOException e){
			System.out.println("can't save");
			e.printStackTrace();
		}
	}
	
	
}
