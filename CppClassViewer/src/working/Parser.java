package working;

import java.util.StringTokenizer;
import java.util.Vector;

import struct.*;
public class Parser {
	CppClass obj;
	String allText;
	StringTokenizer mainToken;
	
	
	
	
	public Parser(String text){
		allText=text;
		mainToken = new StringTokenizer(allText, "{");
		
	}

	public String[] nextFunction(){

		String part[] = new String[2];
		String define;
		StringBuffer body = new StringBuffer();
		
		int closer=0;
		int last_closer=-1;
		
		if(mainToken.hasMoreTokens()){
			define=mainToken.nextToken();
		}
		else
			return null;
		
		
		while(mainToken.hasMoreTokens() && closer<=0){
			closer++;
			StringBuffer t = new StringBuffer();
			
			t.append(mainToken.nextToken());
			for(int k=0;k<t.length();k++){
				if(t.charAt(k)=='}'){
					closer--;
					last_closer=k+body.length();
				}
			}
			body.append(t);
		}
		
		if(closer>0)
			return null;
		else{
			if(last_closer >= 0)
				body.delete(last_closer, body.length());
			
			part[0]=define;
			part[1]=body.toString();
			return part;
		}
		
	}

	
	
	
	public static void main(String[] args) {
		
			
	}
	
	
	
}
