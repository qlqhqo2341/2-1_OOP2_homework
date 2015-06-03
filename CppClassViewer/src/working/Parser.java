package working;

import java.io.*;
import java.util.*;

import struct.*;

public class Parser {
	CppClass obj;
	String allText;

	// CppŬ���� ������ ū ���� �����µ� ����Ǵ� StringTokenizer
	StringTokenizer mainToken;

	// �����ڿ��� �⺻���� �Ľ��� ��� �������� ����.
	public Parser(String text) {
		allText = text;
		mainToken = new StringTokenizer(allText, "{}", true);
		String dd[] = nextBlock();
		if (dd == null)
			return;

		// ���� ó�� ���� �о CppŬ������ �޼ҵ�� �ʵ带 Ȯ���մϴ�.
		initClass(dd);

		// Ŭ���� �⺻���� �б⿡ �����ϸ� CppClass�� �������� �ʽ��ϴ�.
		if (obj == null)
			return;

		dd = nextBlock();
		while (dd != null) { // ���� ���� ���ų� ������ �������� �н��ϴ�.
			Method needBody = findMethod(dd[0]); // �ش��ϴ� ���� ��� ������ �����ϴ� �޼ҵ带
													// ã���ϴ�.

			// �ش��ϴ� �޼ҵ带 ã�� �� ������ �������� �Ѿ�ϴ�.
			if (needBody != null) {

				// ���������� �������� �������ݴϴ�.
				needBody.setDefine(dd[0]);
				needBody.setBody(obj.getFields(), dd[1]); // ���Ǵ� �ʵ尡 �ڵ�����
															// �˻��˴ϴ�.
			}

			// ���� ���� �޾ƿɴϴ�.
			dd = nextBlock();
		}

	}

	// ���� ���� ���𹮰� ���� ��ȯ���ݴϴ�.
	public String[] nextBlock() {

		String part[] = new String[2];
		StringBuffer body = new StringBuffer();

		int closer = 1;

		if (!mainToken.hasMoreTokens())
			return null;

		try {
			part[0] = mainToken.nextToken();

			// Ŭ������ �Ľ��� �������� �����ݷ��� �����ϱ� ������
			// �̰��� �����ݴϴ�.
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

		body.deleteCharAt(body.length() - 1); // �ٵ� ���� �κ� �߿� ������ ��ȣ�� �����ݴϴ�.
		part[1] = body.toString();
		return part;

	}

	// Ŭ������ ����� ���õ� �޼ҵ�� �ʵ尴ü�� ����� �ݴϴ�.
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

				if (k.contains("~" + cppName)) { // �Ҹ������� Ȯ���մϴ�.
					while (!k.equals("(")) {
						// �Ҹ��ڷ� �Ǹ������� �Լ� ������ �ƴ϶��
						// ������ ȣ���մϴ�.
						if (k.equals(";"))
							throw new Exception(
									"deconstructor is must Function");
						k = token.nextToken();
					}

					Method decon = makeMethod("DeConstructor", "~" + cppName,
							access, token);
					obj.addMethod(decon);

				} else if (k.contains(cppName)) { // ���������� Ȯ���մϴ�.
					while (!k.equals("(")) {
						// �Ҹ����� ���� �����ϴ�.
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
					for (String v : Keyword.getaccess()) { // ���ٹ����� �������ݴϴ�.
						if (k.contains(v)) {
							access = v;
							accessModified = true;
							break;
						}
					}

					if (accessModified) // ���� �� �ٷ� ���ٹ����� �������־��ٸ�
										// ���� �ٷ� �ǳʶݴϴ�.
						continue;

					for (String v : Keyword.getTypes()) {
						if (k.contains(v)) { // Ÿ������ �����ϹǷ� �ʵ��̰ų� �޼ҵ� �ϰ��Դϴ�.

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
								if (k.equals("(") && met == null) { // �޼ҵ�� �Ǹ�
																	// �ɰ��
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

	// �޼ҵ� ��ü�� ���� ��ȯ���ݴϴ�.
	private Method makeMethod(String returnType, String name, String access,
			StringTokenizer token) throws Exception {

		// assume that token refer to next to '('

		Method met = new Method(returnType, name, access);
		String k = token.nextToken();

		while (!k.equals(")")) {// �޼ҵ��� �Ķ���͸� �о �°� ������ݴϴ�.
			if (k.equals(";"))
				throw new Exception("Why not close " + name + " Function?");

			for (String v : Keyword.getTypes())
				if (k.contains(v)) {
					met.addParameter(new Parameter(null, v));
					break;
				}

			k = token.nextToken();
		}

		if (met.getParameter().length == 0) // ������� ��� void �Ķ���͸� ������ݴϴ�.
			met.addParameter(new Parameter(null, "void"));

		return met;
	}

	// �Ķ���͸� �ٽ� �о �´� �޼ҵ� ��ü�� ��ȯ���ݴϴ�.
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
