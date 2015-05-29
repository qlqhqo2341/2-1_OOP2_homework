package working;

import java.io.*;
import java.util.*;

import struct.*;

public class Parser {
	CppClass obj;
	String allText;
	StringTokenizer mainToken;

	public Parser(String text) {
		allText = text;
		mainToken = new StringTokenizer(allText, "{}", true);


		initClass(nextFunction());
		
		String dd[] = nextFunction();
		while (dd != null) {
			System.out.println(dd[0] + "\nof body is below\n" + dd[1]);
			System.out.println("body is end");
			dd = nextFunction();
		}

	}

	public String[] nextFunction() {

		String part[] = new String[2];
		StringBuffer body = new StringBuffer();

		int closer = 1;
		
		try {
			part[0]=mainToken.nextToken();
			
			if(part[0].indexOf(';')==0){
				part[0]=part[0].substring(1);
			}
			
			String k = mainToken.nextToken();
			
			if(!k.equals("{"))
				return null;
			
			while(closer>0){
				k=mainToken.nextToken();
				if(k.equals("{"))
					closer++;
				if(k.equals("}"))
					closer--;
				body.append(k);
			}
		} catch (NoSuchElementException e) {
			// TODO: handle exception
			return null;
		}
		
		
		body.deleteCharAt(body.length()-1); //remove last closer
		part[1]=body.toString();
		return part;
		
	}

	public void initClass(String[] parts) {
		//parts[0]에는 클래스의 이름이 저장 되어있음
		// class ????
	
		//parts[1]에는 클래스를 정의하는 내용이 들어가있음
		/* StringTokenizer를 이용해서
		 * public:, private:이 나오는 것을 확인하고
		 * int, void, 클래스 이름 등의 키워드를 체크하고 맞다면
		 * 
		 * 이름을 체크하고 동시에 ;으로 바로 끝나면 Field 클래스
		 * ()가 포함되어 이다면 Method클래스를 생성함.
		 * 
		 * 괄호 안의 키워드를 체크해서 Parameter클래스를 만들어서 Method에 설정해줄것.
		 * 
		 * 각 클래스를 CppClass.addMethod 나 addField로 클래스에 넣어줌.
		*/
		
		
		StringTokenizer token = new StringTokenizer(parts[0]);
		String name = null;
		while (token.hasMoreTokens()) {
			if (token.nextToken().equals("class")) {
				if (token.hasMoreTokens()) {
					name = token.nextToken();
				}
			}
		}

		if (name == null) {
			System.out.println("is it CppClass??");
			return;
		}

		obj = new CppClass(name);

		int numFields = 0, numMethods = 0;
		// count num of fields, methods
		StringTokenizer countToken = new StringTokenizer(parts[1], " \n(");
		StringTokenizer lineToken = new StringTokenizer(parts[1], "\n");

		while (countToken.hasMoreTokens()) {
			String k = countToken.nextToken();
			if (Keyword.isType(k)) {
				String type = k;
				String var_name = countToken.nextToken();

				if (var_name.charAt(var_name.length() - 1) == ';') {
					numFields++;
				} else {
					k = var_name;

				}

			}

			if (obj.getName().equals(k)) {
				
			}
			if (obj.getName().equals('~' + k)) {

			}
		}

	}

	public static void main(String[] args) {
		String obj=null;
		try {
			StringBuffer buf = new StringBuffer();
			FileInputStream stream = new FileInputStream("Queue.cpp");
			int k=stream.read();
			while(k!=-1){
				buf.append((char)k);
				k=stream.read();
			}
			obj=buf.toString();
			stream.close();
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("file read error!");
			e.printStackTrace();
			System.exit(0);
		}
		
		new Parser(obj);

	}

}
