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
					for (String v : Keyword.getaccess()) { // 접근범위를 설정해줍니다.
						if (k.contains(v)) {
							access = v;
							accessModified = true;
							break;
						}
					}

					if (accessModified) // 만약 이 줄로 접근범위가 설정되있었다면
										// 다음 줄로 건너뜁니다.
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
								if (k.equals("(") && met == null) { // 메소드로 판명
																	// 될경우
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

		// assume that token refer to next to '('

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
		int nameStarter = part.indexOf(obj.getName() + "::")
				+ obj.getName().length() + 2;

		if (parStarter < 0 || nameStarter < 0)
			return null;

		String name = part.substring(nameStarter, parStarter - 1).trim();

		for (Method v : obj.getMethods())
			if (v.getName().equals(name)) {
				String parStr = part.substring(parStarter,
						part.lastIndexOf(')'));
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

						if (k.contains("void"))
							break;

						v.getParameter()[i - 1].setName(parToken.nextToken());
					}

					if (differ)
						continue;

				} catch (NoSuchElementException e) {
					// TODO: handle exception
					e.printStackTrace();
					return null;
				}
				// Found Method!
				v.setDefine(part);

				return v;
			}

		return null;
	}

	public CppClass getCppClass() {
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

		CppClass k = new Parser(obj).getCppClass();

		return;
	}

}
