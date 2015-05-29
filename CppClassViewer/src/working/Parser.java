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
		//parts[0]���� Ŭ������ �̸��� ���� �Ǿ�����
		// class ????
	
		//parts[1]���� Ŭ������ �����ϴ� ������ ������
		/* StringTokenizer�� �̿��ؼ�
		 * public:, private:�� ������ ���� Ȯ���ϰ�
		 * int, void, Ŭ���� �̸� ���� Ű���带 üũ�ϰ� �´ٸ�
		 * 
		 * �̸��� üũ�ϰ� ���ÿ� ;���� �ٷ� ������ Field Ŭ����
		 * ()�� ���ԵǾ� �̴ٸ� MethodŬ������ ������.
		 * 
		 * ��ȣ ���� Ű���带 üũ�ؼ� ParameterŬ������ ���� Method�� �������ٰ�.
		 * 
		 * �� Ŭ������ CppClass.addMethod �� addField�� Ŭ������ �־���.
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
