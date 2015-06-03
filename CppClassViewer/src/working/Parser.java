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

				/***************	�Ҹ������� Ȯ���մϴ�.	************/
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
				/***************	�Ҹ������� Ȯ�γ�	************/
				
				/***************	���������� Ȯ���մϴ�.	************/	
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
					
					/***************	���ٹ��� �������� Ȯ���մϴ�.	************/
					for (String v : Keyword.getaccess()) { // ���ٹ����� �������ݴϴ�.
						if (k.contains(v)) {
							access = v;
							accessModified = true;
							break;
						}
					}
					/***************	���ٹ��� �������� Ȯ�γ�		************/
					
					if (accessModified) // ���� �� �ٷ� ���ٹ����� �������־��ٸ�
										// ���� �ٷ� �ǳʶݴϴ�.
						continue;
					
					//���࿡ Int, void���� Ÿ������ �����Ѵٸ� ��ȯ���� �����ϴ� �޼ҵ峪 Ÿ���� ���ǵ� �ʵ� �ϰ��Դϴ�.
					/***************	�ʵ峪 �޼ҵ����� Ȯ���մϴ�.	************/
					for (String v : Keyword.getTypes()) {
						if (k.contains(v)) { // Ÿ������ �����ϹǷ� �ʵ��̰ų� �޼ҵ� �ϰ��Դϴ�.

							Method met = null;
							int arr = -1;

							if (access == null)
								throw new Exception("need to define access");

							
							//Ÿ���� ������ ���� �ܾ�� �̸��Դϴ�.
							//�и��ڰ� ������ ��ū�� �ѱ�� �ܾ ���ö����� ��ū�� ��� �޽��ϴ�.
							do {
								name = token.nextToken();
							} while (delim.contains(name));
							
							//�̸��� Ȥ�� �迭�� �ǹ��ϴ� ��ȣ�� �����ϴ��� Ȯ���մϴ�.
							int arrOpener = name.indexOf('[');
							int arrCloser = name.indexOf(']');

							//���࿡ ��ȣ�� �����Ѵٸ� �迭�� ũ�⸦ �о arr�� �����մϴ�.
							if (arrOpener < arrCloser && arrOpener > 0) {
								arr = Integer.parseInt(name.substring(
										arrOpener + 1, arrCloser));
								name = name.substring(0, arrOpener);
							}
							type = v;

							//������ ���� �� ���� ��ū�� �޽��ϴ�.
							while (!k.equals(";")) {
								
								//���࿡ ���� ��ȣ�� �ִٸ� �װ��� �Լ� (�޼ҵ�) �ϰ��Դϴ�.
								/***************	�޼ҵ����� �����ϱ�		************/
								if (k.equals("(") && met == null) { // �޼ҵ�� �Ǹ�
																	// �ɰ��
									met = makeMethod(type, name, access, token);
									obj.addMethod(met);
									continue;
								}
								
								//���� �ܾ �迭�� �ǹ��ϴ� ��ȣ�� Ȥ�ö� �����ϴ� �� Ȯ���ϰ�
								//�Ʊ�� ���� �۾��� �ٽ� �մϴ�.
								arrOpener = k.indexOf('[');
								arrCloser = k.indexOf(']');
								if (arrOpener < arrCloser && arrOpener > 0)
									arr = Integer.parseInt(k.substring(
											arrOpener + 1, arrCloser));

								k = token.nextToken();

							}

							//���࿡ �Լ��� �ǹ��ϴ� ��ȣ�� �߰� ���� �ʾƼ� �Լ� ������ ���� �ʾҴٸ�
							//�̰��� �ʵ� �ϰ��Դϴ�.
							/***************	�ʵ����� �����ϱ�		************/
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
				// �ʵ�, �޼ҵ带 �����ϰų� ���ٹ����� �����ϴ� �ϳ��� ����Ŭ�� �������ϴ�.

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
		//token �� �޼ҵ��� ( ������ ����Ű�� �մ� ��Ȳ�� �����մϴ�.
		Method met = new Method(returnType, name, access);
		String k = token.nextToken();

		//�޼ҵ��� ��ȣ ���� �н��ϴ�.
		while (!k.equals(")")) {// �޼ҵ��� �Ķ���͸� �о �°� ������ݴϴ�.
			if (k.equals(";"))
				throw new Exception("Why not close " + name + " Function?");

			
			for (String v : Keyword.getTypes())
				
				//���࿡ �дٰ� Ÿ���� �߰ߵǸ�
				if (k.contains(v)) {
					//�̰��� �ش� �Ķ���Ϳ� �߰����ݴϴ�.
					met.addParameter(new Parameter(null, v));
					break;
				}

			//���� ��ū�� �����ɴϴ�.
			k = token.nextToken();
		}

		if (met.getParameter().length == 0) // ������� ��� void �Ķ���͸� ������ݴϴ�.
			met.addParameter(new Parameter(null, "void"));

		return met;
	}

	// �Ķ���͸� �ٽ� �о �´� �޼ҵ� ��ü�� ��ȯ���ݴϴ�.
	private Method findMethod(String part) {
		//���������� ������ �ִ� ���� ���� �Ķ���������� ���� �Ǵ� �κ��� ��ġ�� ã�����.
		int parStarter = part.indexOf('(') + 1;
		//���������� ������ �ִ� ���ڿ� ���� Ŭ���� �̸� ������ �޼ҵ� �̸��� �����ϴ� �κ��� ��ġ�� ã���ϴ�.
		int nameStarter = part.indexOf(obj.getName() + "::")
				+ obj.getName().length() + 2;

		//���� ���߿� �ϳ��� ã�� ���ϸ� null�� ��ȯ�մϴ�.
		if (parStarter < 0 || nameStarter < 0)
			return null;

		// ��Ȯ�� �޼ҵ� �̸��� �߶󳻰� �ʿ���� �� �ڰ����� �����ݴϴ�.
		String name = part.substring(nameStarter, parStarter - 1).trim();

		
		for (Method v : obj.getMethods())
			
			//���࿡ ������� �ִ� �޼ҵ� ��ü�߿��� ���� �̸��� ã�Ҵٸ�
			if (v.getName().equals(name)) {
				/************	���������� �Ķ���� ������ ���� �޼ҵ� ��ü�� �Ķ���������� ������ �˻�	***************/
				//�Ķ���� �������� ������ �ִ� ���ο� ���ڿ��� ����ϴ�.
				String parStr = part.substring(parStarter,
						part.lastIndexOf(')'));
				
				//�� ���ڿ��� �̿��� �Ķ���͸� ���� �и��ϴ� ���ο� StringTokenizer�� ����մϴ�.
				StringTokenizer parToken = new StringTokenizer(parStr, " ,\t\n");

				int i = 0;
				try {
					boolean differ = false;
					
					//���� ���� �и��� �����ų� �ٸ��ٰ� �Ǹ��� ���� ������ �����մϴ�.
					while (parToken.hasMoreTokens() && differ == false) {
						String k = parToken.nextToken();

						//���࿡ ���������� �Ķ���Ͱ� �� ���ٸ� �ٸ� �޼ҵ��� ǥ���մϴ�.
						if (i >= v.getParameter().length) {
							differ = true;
							break;
						}

						//���࿡ ������� Ȯ���� �Ķ���� Ÿ���� �ٸ��� �ٸ� �޼ҵ��� ǥ���մϴ�.
						if (!k.contains(v.getParameter()[i++].getType())) {
							differ = true;
							break;
						}

						//Ȥ�ó� void ���� �߰� �Ǿ��ٸ� ������ �����ϴ�.
						if (k.contains("void")){
							i=1;
							break;
						}
						v.getParameter()[i - 1].setName(parToken.nextToken());
					} //�Ķ���� ������ �ϳ��� Ȯ�� �߽��ϴ�.
					//���������� �Ķ���� ������ �޼ҵ��� �Ķ���� ������ �ٸ��ٸ� �޼ҵ尡 �ٸ��̴ϴ�.
					if(i!=v.getParameter().length)
						differ=true;
					//������ �� ��ȣ�� void�� ���ؼ� differ�� ���� ����
					//������� ���� �ݴϴ�.
					if(i==0 && v.getParameter().length==1)
						if(v.getParameter()[0].getType().equals("void"))
							differ=false;
					if (differ)
						//���࿡ �ٸ� ������ ������ ���� ���� �޼ҵ带 �˻��մϴ�. for (Method v : obj.getMethods()) �� ���� ������ �ǳ� �ݴϴ�.
						continue;
				} catch (NoSuchElementException e) {
					// TODO: handle exception
					// ���������� �Ķ���� ������ �޼ҵ��� �Ķ���� �������� ������ �߻��˴ϴ�.
					continue;
				}
				// Found Method!
				// �޼ҵ带 ã������ ���������� �޼ҵ忡 �������ݴϴ�.
				v.setDefine(part);
				return v;
			}
		//���࿡ ã�� ���޴ٸ� null�� ��ȯ���ݴϴ�.
		return null;
	}

	public CppClass getCppClass() {
		return obj;
	}



}
