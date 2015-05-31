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

		initClass(nextBlock());

		if (obj == null)
			return;

		String dd[] = nextBlock();
		while (dd != null) {
			Method needBody = findMethod(dd[0]);
			needBody.setBody(obj.getFields(), dd[1]);
			dd = nextBlock();
		}

	}

	public String[] nextBlock() {

		String part[] = new String[2];
		StringBuffer body = new StringBuffer();

		int closer = 1;

		try {
			part[0] = mainToken.nextToken();

			
			//After Parsing Define Class
			//Maybe semicolon is exists.
			if (part[0].indexOf(';') == 0) {
				part[0] = part[0].substring(1);
			}

			String k = mainToken.nextToken();

			if (!k.equals("{"))
				return null;

			while (closer > 0) {
				k = mainToken.nextToken();
				if (k.equals("{"))
					closer++;
				if (k.equals("}"))
					closer--;
				body.append(k);
			}
		} catch (NoSuchElementException e) {
			// TODO: handle exception
			return null;
		}

		body.deleteCharAt(body.length() - 1); // remove last closer
		part[1] = body.toString();
		return part;

	}

	public void initClass(String[] parts) {

		// parts[0]에는 클래스의 이름이 저장 되어있음
		// class ????

		// parts[1]에는 클래스를 정의하는 내용이 들어가있음
		/*
		 * StringTokenizer를 이용해서 public:, private:이 나오는 것을 확인하고 int, void, 클래스
		 * 이름 등의 키워드를 체크하고 맞다면
		 * 
		 * 이름을 체크하고 동시에 ;으로 바로 끝나면 Field 클래스 ()가 포함되어 있다면 Method클래스를 생성함.
		 * 
		 * 괄호 안의 키워드를 체크해서 Parameter클래스를 만들어서 Method에 설정해줄것.
		 * 
		 * 각 클래스를 CppClass.addMethod 나 addField로 클래스에 넣어줌.
		 */

		try {
			StringTokenizer token = new StringTokenizer(parts[0]);
			String cppName = null, name = null, access = null, type = null;
			String k = token.nextToken();

			while (!k.equals("class"))
				k = token.nextToken();

			cppName = token.nextToken();

			obj = new CppClass(cppName);

			String delim=" \t\n(),;";
			token = new StringTokenizer(parts[1], delim, true);

			while (token.hasMoreTokens()) {
				k = token.nextToken();

				if (k.contains("~" + cppName)) { // DeConstructor
					while (!k.equals("(")) {
						if (k.equals(";"))
							throw new Exception(
									"deconstructor is must Function");
						k = token.nextToken();
					}

					Method decon = makeMethod("DeCon", "~" + cppName, access,
							token);
					obj.addMethod(decon);

				} else if (k.contains(cppName)) { // Constructor
					while (!k.equals("(")) {
						if (k.equals(";"))
							throw new Exception("constructor is must Function");
						k = token.nextToken();
					}

					Method con = makeMethod("Con", cppName, access, token);
					obj.addMethod(con);
				}

				else {
					boolean accessModified = false;
					for (String v : Keyword.getaccess()) { // Define Access
						if (k.contains(v)) {
							access = v;
							accessModified = true;
							break;
						}
					}

					if (accessModified) // if this line is defining access,
										// continue next line
						continue;

					for (String v : Keyword.getTypes()) { // Field or Method.
						if (k.contains(v)) {
							Method met = null;
							if (access == null)
								throw new Exception("need to define access");

							
							do {
								name=token.nextToken();
							} while (delim.contains(name));
			

							type = v;

							while (!k.equals(";")) {
								if (k.equals("(") && met == null) { // if this
																	// line
																	// function
									met = makeMethod(type, name, access, token);
									obj.addMethod(met);
									continue;
								}
								
								k = token.nextToken();
								
							}

							if (met == null) { // if this line field.
								obj.addField(new Field(name, type));
							}

						}
					}

				}
				// finish one cycle.

			}

		} catch (NoSuchElementException e) {
			// TODO: handle exception
		} catch (Exception e) {
			obj = null;
			System.out.println(e.getMessage());
			e.printStackTrace();

		}

	}

	private Method makeMethod(String returnType, String name, String access,
			StringTokenizer token) throws Exception {

		// assume that token refer to next to '('

		Method met = new Method(returnType, name, access);
		String k = token.nextToken();

		while (!k.equals(")")) {
			if (k.equals(";"))
				throw new Exception("Why not close " + name + " Function?");

			for (String v : Keyword.getTypes())
				if (k.contains(v)) {
					met.addParameter(new Parameter(null, v));
					break;
				}

			k = token.nextToken();
		}

		if (met.getParameter().length == 0)
			met.addParameter(new Parameter(null, "void"));

		return met;
	}

	private Method findMethod(String part){
		int parStarter=part.indexOf('(')+1;
		int nameStarter=part.indexOf(obj.getName()+"::") + obj.getName().length() + 2;
		
		
		if(parStarter < 0 || nameStarter < 0 )
			return null;
		
		String name = part.substring(nameStarter, parStarter-1).trim();
		
		for(Method v : obj.getMethods())
			if(v.getName().equals(name)){
				String parStr = part.substring(parStarter, part.lastIndexOf(')'));
				StringTokenizer parToken = new StringTokenizer(parStr, " ,\t\n");
				
				int i=0;
				try {
					boolean differ=false;
					while (parToken.hasMoreTokens() && differ==false) {
						String k= parToken.nextToken();
						
						if(i>=v.getParameter().length){
							differ=true;
							break;
						}
						
						if(!k.contains(v.getParameter()[i++].getType())){
							differ=true;		
							break;
						}
						
						if(k.contains("void"))
							break;
						
						v.getParameter()[i-1].setName(parToken.nextToken());
					}
					
					if(differ)
						continue;
					
				} catch (NoSuchElementException e) {
					// TODO: handle exception
					e.printStackTrace();
					return null;
				}
				
				return v;
			}
		
		return null;
	}

	public CppClass getCppClass(){
		return obj;
	}
	
	public static void main(String[] args) {
		String obj = null;
		try {
			StringBuffer buf = new StringBuffer();
			FileInputStream stream = new FileInputStream("Queue.cpp");
			int k = stream.read();
			while (k != -1) {
				buf.append((char) k);
				k = stream.read();
			}
			obj = buf.toString();
			stream.close();
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("file read error!");
			e.printStackTrace();
			System.exit(0);
		}

		new Parser(obj).getCppClass();
		
		return;
	}

}
