package working;

import java.io.*;

public class ReadFile {

	FileInputStream file= null;
	
	public ReadFile(String file_path){
		try{
			file=new FileInputStream(file_path);
		}
		catch(FileNotFoundException e){
			System.out.println("FileNotFound");
		}
	}
	
	public String getText(){
		int b = 0;
		StringBuffer buffer = new StringBuffer();
		try {
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
		return buffer.toString();
	}
	
}
