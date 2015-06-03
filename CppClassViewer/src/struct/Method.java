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

	// 이 메소드의 선언문을 저장하고 읽어옵니다.
	public void setDefine(String defineStr) {
		// 선언문은 한번 저장하면 수정이 불가능하도록 합니다.
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

	// 메소드의 내용을 저장하면서 사용한 필드가 있는지 검사합니다.
	public void setBody(Field[] fields, String body) {
		this.body = body;
		this.fields = new Field[fields.length];
		fieldsSize = 0;

		StringTokenizer token = new StringTokenizer(body, " \t\n{}()[]=+-*/%;");

		// 우선 모든 필드에 대해 이 메소드 정보를 지웁니다.
		for (Field f : fields)
			f.removeMethod(this);

		while (token.hasMoreTokens()) {
			String k = token.nextToken();

			for (Field find : fields)
				if (find.getName().equals(k) && !hasField(find)) {
					// 필드 문자열을 발견하면 현재 메소드에 그 필드를 추가하고.
					// 필드에도 이 메소드를 추가합니다.
					this.fields[fieldsSize++] = find;
					find.addMethods(this);
				}
		}
	}

	public String getBody() {
		return body;
	}

	// 이 필드가 사용되는지 확인합니다.
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

	// 메소드 이름과 파라메터 정보를 같이 표현해줍니다.
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
