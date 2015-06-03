package struct;

import java.util.StringTokenizer;

public class Method {
	private String name;
	private String access;
	private String returnType;
	private String defineStr;

	private Parameter par[];
	private Field fields[];
	private String body;
	private int fieldsSize;

	public Method(String returnType, String name, String access) {
		this.returnType = returnType;
		this.name = name;
		this.access = access;
		this.par = new Parameter[0];
		fields = null;
		fieldsSize = 0;
		body = null;
		defineStr = null;
	}

	// �� �޼ҵ��� ������ �����ϰ� �о�ɴϴ�.
	public void setDefine(String defineStr) {
		// ������ �ѹ� �����ϸ� ������ �Ұ����ϵ��� �մϴ�.
		if (this.defineStr == null)
			this.defineStr = defineStr;
	}

	public String getDefine() {
		return defineStr;
	}

	public void addParameter(Parameter par) {
		Parameter[] newPar = new Parameter[this.par.length + 1];

		int i;
		for (i = 0; i < this.par.length; i++) {
			newPar[i] = this.par[i];
		}
		newPar[i] = par;
		this.par = newPar;
	}

	// �޼ҵ��� ������ �����ϸ鼭 ����� �ʵ尡 �ִ��� �˻��մϴ�.
	public void setBody(Field[] fields, String body) {
		this.body = body;
		this.fields = new Field[fields.length];
		fieldsSize = 0;

		StringTokenizer token = new StringTokenizer(body, " \t\n{}()[]=+-*/%;");

		// �켱 ��� �ʵ忡 ���� �� �޼ҵ� ������ ����ϴ�.
		for (Field f : fields)
			f.removeMethod(this);

		while (token.hasMoreTokens()) {
			String k = token.nextToken();

			for (Field find : fields)
				if (find.getName().equals(k) && !hasField(find)) {
					// �ʵ� ���ڿ��� �߰��ϸ� ���� �޼ҵ忡 �� �ʵ带 �߰��ϰ�.
					// �ʵ忡�� �� �޼ҵ带 �߰��մϴ�.
					this.fields[fieldsSize++] = find;
					find.addMethods(this);
				}
		}
	}

	public String getBody() {
		return body;
	}

	// �� �ʵ尡 ���Ǵ��� Ȯ���մϴ�.
	public boolean hasField(Field fie) {
		for (int i = 0; i < fieldsSize; i++) {
			if (fields[i].equals(fie))
				return true;
		}
		return false;
	}

	public Parameter[] getParameter() {
		return par.clone();
	}

	public Field[] getFields() {
		if (fields != null) {
			return fields.clone();
		} else {
			System.out.println(name + " are not parsed.");
			return null;
		}
	}

	public String getAccess() {
		return access;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return returnType;
	}

	// �޼ҵ� �̸��� �Ķ���� ������ ���� ǥ�����ݴϴ�.
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer(getName());
		buf.append(" ( ");

		for (Parameter p : par) {
			buf.append(p.getType());
			if (p.getName() != null)
				buf.append(" " + p.getName());
			buf.append(", ");
		}

		buf.delete(buf.length() - 2, buf.length());
		buf.append(" )");
		return buf.toString();
	}
}
