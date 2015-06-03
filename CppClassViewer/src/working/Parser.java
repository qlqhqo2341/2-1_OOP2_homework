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

		// 클래스 기본정보 읽기에 실패하면 CppClass를 생성하지 않습니다.
		if (obj == null)
			return;

		dd = nextBlock();
		while (dd != null) { // 다음 블럭이 없거나 에러가 날때까지 읽습니다.
			Method needBody = findMethod(dd[0]); // 해당하는 블럭의 헤더 정보로 대응하는 메소드를
													// 찾습니다.

			// 해당하는 메소드를 찾을 수 없으면 다음으로 넘어갑니다.
			if (needBody != null) {

				// 선언정보와 블럭정보를 설정해줍니다.
				needBody.setDefine(dd[0]);
				needBody.setBody(obj.getFields(), dd[1]); // 사용되는 필드가 자동으로
															// 검색됩니다.
			}

			// 다음 블럭을 받아옵니다.
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
			// TODO: handle exception
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

				/***************	소멸자인지 확인합니다.	************/
				if (k.contains("~" + cppName)) { // 소멸자임을 확인합니다.
					while (!k.equals("(")) {
						// 소멸자로 판명했으나 함수 형식이 아니라면
						// 에러를 호출합니다.
						if (k.equals(";"))
							throw new Exception(
									"deconstructor is must Function");
						k = token.nextToken();
					}

					Method decon = makeMethod("DeConstructor", "~" + cppName,
							access, token);
					obj.addMethod(decon);
				/***************	소멸자인지 확인끝	************/
				
				/***************	생성자인지 확인합니다.	************/	
				} else if (k.contains(cppName)) { // 생성자임을 확인합니다.
					while (!k.equals("(")) {
						// 소멸자의 경우과 같습니다.
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
					
					/***************	접근범위 설정인지 확인합니다.	************/
					for (String v : Keyword.getaccess()) { // 접근범위를 설정해줍니다.
						if (k.contains(v)) {
							access = v;
							accessModified = true;
							break;
						}
					}
					/***************	접근범위 설정인지 확인끝		************/
					
					if (accessModified) // 만약 이 줄로 접근범위가 설정되있었다면
										// 다음 줄로 건너뜁니다.
						continue;
					
					//만약에 Int, void같은 타입으로 시작한다면 반환형이 존재하는 메소드나 타입이 정의된 필드 일것입니다.
					/***************	필드나 메소드인지 확인합니다.	************/
					for (String v : Keyword.getTypes()) {
						if (k.contains(v)) { // 타입으로 시작하므로 필드이거나 메소드 일것입니다.

							Method met = null;
							int arr = -1;

							if (access == null)
								throw new Exception("need to define access");

							
							//타입의 다음에 나올 단어는 이름입니다.
							//분리자가 나오는 토큰은 넘기고 단어가 나올때까지 토큰을 계속 받습니다.
							do {
								name = token.nextToken();
							} while (delim.contains(name));
							
							//이름에 혹시 배열을 의미하는 괄호가 존재하는지 확인합니다.
							int arrOpener = name.indexOf('[');
							int arrCloser = name.indexOf(']');

							//만약에 괄호가 존재한다면 배열의 크기를 읽어서 arr에 저장합니다.
							if (arrOpener < arrCloser && arrOpener > 0) {
								arr = Integer.parseInt(name.substring(
										arrOpener + 1, arrCloser));
								name = name.substring(0, arrOpener);
							}
							type = v;

							//문장이 끝날 때 까지 토큰을 받습니다.
							while (!k.equals(";")) {
								
								//만약에 왼쪽 괄호가 있다면 그것은 함수 (메소드) 일것입니다.
								/***************	메소드인지 점검하기		************/
								if (k.equals("(") && met == null) { // 메소드로 판명
																	// 될경우
									met = makeMethod(type, name, access, token);
									obj.addMethod(met);
									continue;
								}
								
								//다음 단어에 배열을 의미하는 괄호가 혹시라도 존재하는 지 확인하고
								//아까와 같은 작업을 다시 합니다.
								arrOpener = k.indexOf('[');
								arrCloser = k.indexOf(']');
								if (arrOpener < arrCloser && arrOpener > 0)
									arr = Integer.parseInt(k.substring(
											arrOpener + 1, arrCloser));

								k = token.nextToken();

							}

							//만약에 함수를 의미하는 괄호가 발견 되지 않아서 함수 설정이 되지 않았다면
							//이것은 필드 일것입니다.
							/***************	필드인지 점검하기		************/
							if (met == null) { // if this line field.
								if (arr >= 0)
									obj.addField(new Field(name, type, arr));
								else
									obj.addField(new Field(name, type));
							}

						}
					}

				}
				// finish one cycle.
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
	private Method makeMethod(String returnType, String name, String access,
			StringTokenizer token) throws Exception {
		//token 이 메소드의 ( 다음을 가리키고 잇는 상황을 가정합니다.
		Method met = new Method(returnType, name, access);
		String k = token.nextToken();

		//메소드의 괄호 까지 읽습니다.
		while (!k.equals(")")) {// 메소드의 파라메터를 읽어서 맞게 만들어줍니다.
			if (k.equals(";"))
				throw new Exception("Why not close " + name + " Function?");

			
			for (String v : Keyword.getTypes())
				
				//만약에 읽다가 타입이 발견되면
				if (k.contains(v)) {
					//이것을 해당 파라메터에 추가해줍니다.
					met.addParameter(new Parameter(null, v));
					break;
				}

			//다음 토큰을 가져옵니다.
			k = token.nextToken();
		}

		if (met.getParameter().length == 0) // 비어있을 경우 void 파라메터를 만들어줍니다.
			met.addParameter(new Parameter(null, "void"));

		return met;
	}

	// 파라메터를 다시 읽어서 맞는 메소드 객체를 반환해줍니다.
	private Method findMethod(String part) {
		//선언정보를 가지고 있는 문자 에서 파라메터정보가 시작 되는 부분의 위치를 찾스비다.
		int parStarter = part.indexOf('(') + 1;
		//선언정보를 가지고 있는 문자열 에서 클래스 이름 다음에 메소드 이름이 시작하는 부분의 위치를 찾습니다.
		int nameStarter = part.indexOf(obj.getName() + "::")
				+ obj.getName().length() + 2;

		//만일 둘중에 하나라도 찾지 못하면 null을 반환합니다.
		if (parStarter < 0 || nameStarter < 0)
			return null;

		// 정확히 메소드 이름만 잘라내고 필요없는 앞 뒤공백은 지워줍니다.
		String name = part.substring(nameStarter, parStarter - 1).trim();

		
		for (Method v : obj.getMethods())
			
			//만약에 만들어져 있는 메소드 객체중에서 같은 이름을 찾았다면
			if (v.getName().equals(name)) {
				/************	선언정보의 파라메터 정보와 현재 메소드 객체의 파라메터정보가 같은지 검사	***************/
				//파라메터 정보만을 가지고 있는 새로운 문자열을 만듭니다.
				String parStr = part.substring(parStarter,
						part.lastIndexOf(')'));
				
				//이 문자열을 이용해 파라메터를 각각 분리하는 새로운 StringTokenizer를 사용합니다.
				StringTokenizer parToken = new StringTokenizer(parStr, " ,\t\n");

				int i = 0;
				try {
					boolean differ = false;
					
					//선언 정보 분리가 끝나거나 다르다고 판명이 나기 전까지 진행합니다.
					while (parToken.hasMoreTokens() && differ == false) {
						String k = parToken.nextToken();

						//만약에 선언정보의 파라메터가 더 많다면 다른 메소드라고 표시합니다.
						if (i >= v.getParameter().length) {
							differ = true;
							break;
						}

						//만약에 순서대로 확인한 파라메터 타입이 다르면 다른 메소드라고 표시합니다.
						if (!k.contains(v.getParameter()[i++].getType())) {
							differ = true;
							break;
						}

						//혹시나 void 형이 발견 되었다면 루프를 끝냅니다.
						if (k.contains("void")){
							i=1;
							break;
						}
						v.getParameter()[i - 1].setName(parToken.nextToken());
					} //파라메터 정보를 하나씩 확인 했습니다.
					//선언정보의 파라메터 갯수와 메소드의 파라메터 갯수가 다르다면 메소드가 다를겁니다.
					if(i!=v.getParameter().length)
						differ=true;
					//하지만 빈 괄호와 void를 비교해서 differ가 생긴 경우면
					//원래대로 돌려 줍니다.
					if(i==0 && v.getParameter().length==1)
						if(v.getParameter()[0].getType().equals("void"))
							differ=false;
					if (differ)
						//만약에 다른 것으로 판정이 나면 다음 메소드를 검사합니다. for (Method v : obj.getMethods()) 의 다음 루프로 건너 뜁니다.
						continue;
				} catch (NoSuchElementException e) {
					// TODO: handle exception
					// 선언정보의 파라메터 갯수가 메소드의 파라메터 갯수보다 적으면 발생됩니다.
					continue;
				}
				// Found Method!
				// 메소드를 찾았으면 선언정보를 메소드에 설정해줍니다.
				v.setDefine(part);
				return v;
			}
		//만약에 찾이 못햇다면 null을 반환해줍니다.
		return null;
	}

	public CppClass getCppClass() {
		return obj;
	}



}
