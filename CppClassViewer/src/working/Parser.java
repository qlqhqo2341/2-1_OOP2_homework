package working;

import java.io.*;
import java.util.*;
import struct.*;

public class Parser {
	CppClass obj;
	String allText;
	// Cpp클래스 파일의 큰 블럭을 나누는데 사욛되는 StringTokenizer
	StringTokenizer mainToken;
	// 생성자에서 기본적인 파싱을 모두 끝내도록 해줌.
	
	public Parser(String text) {
		allText = text;
		mainToken = new StringTokenizer(allText, "{}", true);
		String dd[] = nextBlock();
		
		if (dd == null)
			return;
		
		// 가장 처음 블럭을 읽어서 Cpp클래스의 메소드와 필드를 확인합니다.
		initClass(dd);
		
		if (obj == null)
			return;
		
		dd = nextBlock();
		while (dd != null) {
			Method needBody = findMethod(dd[0]); // 해당하는 블럭의 헤더 정보로 대응하는 메소드를 찾습니다.
			if (needBody != null) {
				
				// 선언정보와 블럭정보를 설정해줍니다.
				needBody.setDefine(dd[0]);
				needBody.setBody(obj.getFields(), dd[1]);
			}
			dd = nextBlock();
		}
	}
	
	// 다음 블럭을 선언문과 같이 반환해줍니다.
	public String[] nextBlock() {
		String part[] = new String[2];
		StringBuffer body = new StringBuffer();
		int closer = 1;
		
		if (!mainToken.hasMoreTokens())
			return null;
		
		try {
			part[0] = mainToken.nextToken();
			
			// 클래스를 파싱한 다음에는 세미콜론이 존재하기 때문에
			// 이것을 지워줍니다.
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
			System.out.println("nextBlock() error! need to match parenthese");
			e.printStackTrace();
			return null;
		}
		body.deleteCharAt(body.length() - 1); // 바디를 나눈 부분 중에 마지막 괄호는 지워줍니다.
		part[1] = body.toString();
		return part;
	}
	
	// 클래스를 만들고 관련된 메소드와 필드객체를 만들어 줍니다.
	public void initClass(String[] parts) {
		try {
			StringTokenizer token = new StringTokenizer(parts[0]);
			String cppName = null, name = null, access = null, type = null;
			String k = token.nextToken();
			
			while (!k.equals("class"))
				k = token.nextToken();
			cppName = token.nextToken();
			obj = new CppClass(cppName, parts[0], parts[1]);
			
			String delim = " \t\n(),;";
			token = new StringTokenizer(parts[1], delim, true);
			while (token.hasMoreTokens()) {
				k = token.nextToken();
				
				if (k.contains("~" + cppName)) { // 소멸자임을 확인합니다.
					while (!k.equals("(")) {
						if (k.equals(";"))
							throw new Exception(
									"deconstructor is must Function");
						k = token.nextToken();
					}
					Method decon = makeMethod("DeConstructor", "~" + cppName,
							access, token);
					obj.addMethod(decon);
				}

				else if (k.contains(cppName)) { // 생성자임을 확인합니다.
					while (!k.equals("(")) {
						if (k.equals(";"))
							throw new Exception("constructor is must Function");
						k = token.nextToken();
					}
					Method con = makeMethod("Constructor", cppName, access,
							token);
					obj.addMethod(con);
				}
				
				else {
					boolean accessModified = false;

					for (String v : Keyword.getaccess()) { // 접근범위를 설정해줍니다.
						if (k.contains(v)) {
							access = v;
							accessModified = true;
							break;
						}
					}

					if (accessModified)
						continue;

					for (String v : Keyword.getTypes()) {
						if (k.contains(v)) { // 타입으로 시작하므로 필드이거나 메소드 일것입니다.
							Method met = null;
							int arr = -1;
							if (access == null)
								throw new Exception("need to define access");

							do {
								name = token.nextToken();
							} while (delim.contains(name));

							int arrOpener = name.indexOf('[');
							int arrCloser = name.indexOf(']');

							if (arrOpener < arrCloser && arrOpener > 0) {
								arr = Integer.parseInt(name.substring(
										arrOpener + 1, arrCloser));
								name = name.substring(0, arrOpener);
							}
							type = v;

							while (!k.equals(";")) {
								//만약에 왼쪽 괄호가 있다면 그것은 함수 (메소드) 일것입니다.

								if (k.equals("(") && met == null) { // 메소드로 판명 될경우
									met = makeMethod(type, name, access, token);
									obj.addMethod(met);
									continue;
								}

								arrOpener = k.indexOf('[');
								arrCloser = k.indexOf(']');
								if (arrOpener < arrCloser && arrOpener > 0)
									arr = Integer.parseInt(k.substring(
											arrOpener + 1, arrCloser));
								k = token.nextToken();
							}
							
							//만약에 함수를 의미하는 괄호가 발견 되지 않아서 함수 설정이 되지 않았다면
							//이것은 필드 일것입니다.
							if (met == null) {
								if (arr >= 0)
									obj.addField(new Field(name, type, arr));
								else
									obj.addField(new Field(name, type));
							}
						}
					}
				}
				// 필드, 메소드를 설정하거나 접근범위를 설정하는 하나의 사이클이 끝났습니다.
			}
		} catch (NoSuchElementException e) {
			// TODO: handle exception
		} catch (Exception e) {
			obj = null; 
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// 메소드 객체를 만들어서 반환해줍니다.
	private Method makeMethod(String returnType, String name, String access, StringTokenizer token) throws Exception {

		Method met = new Method(returnType, name, access);
		String k = token.nextToken();

		while (!k.equals(")")) {// 메소드의 파라메터를 읽어서 맞게 만들어줍니다.
			
			if (k.equals(";"))
				throw new Exception("Why not close " + name + " Function?");
			
			for (String v : Keyword.getTypes())
				if (k.contains(v)) {
					met.addParameter(new Parameter(null, v));
					break;
				}
			k = token.nextToken();
		}
		if (met.getParameter().length == 0) // 비어있을 경우 void 파라메터를 만들어줍니다.
			met.addParameter(new Parameter(null, "void"));
		return met;
	}

	// 파라메터를 다시 읽어서 맞는 메소드 객체를 반환해줍니다.
	private Method findMethod(String part) {
		int parStarter = part.indexOf('(') + 1;
		int nameStarter = part.indexOf(obj.getName() + "::") + obj.getName().length() + 2;
		
		if (parStarter < 0 || nameStarter < 0)
			return null;
		String name = part.substring(nameStarter, parStarter - 1).trim();
		
		for (Method v : obj.getMethods())
			if (v.getName().equals(name)) {
				String parStr = part.substring(parStarter, part.lastIndexOf(')'));
				StringTokenizer parToken = new StringTokenizer(parStr, " ,\t\n");

				int i = 0;
				try {
					boolean differ = false;
					while (parToken.hasMoreTokens() && differ == false) {
						String k = parToken.nextToken();
						if (i >= v.getParameter().length) {
							differ = true;
							break;
						}
						if (!k.contains(v.getParameter()[i++].getType())) {
							differ = true;
							break;
						}
						if (k.contains("void")){
							i=1;
							break;
						}
						v.getParameter()[i - 1].setName(parToken.nextToken());
					} 
					
					if(i!=v.getParameter().length)
						differ=true;
					
					//하지만 빈 괄호와 void를 비교해서 differ가 생긴 경우면
					//원래대로 돌려 줍니다.
					if(i==0 && v.getParameter().length==1)
						if(v.getParameter()[0].getType().equals("void"))
							differ=false;
					
					if (differ)
						continue;
				} catch (NoSuchElementException e) {
					continue;
				}
				v.setDefine(part);
				return v;
			}
		return null;
	}
	public CppClass getCppClass() {
		return obj;
	}
}
