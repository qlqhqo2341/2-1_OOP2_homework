package struct;

import struct.Variable;

public class Field extends Variable {
	private Method used[];
	private Integer arr;

	public Field(String name, String type) {
		super(name, type);
		used = new Method[0];
		arr = null;
	}

	public Field(String name, String type, int size) {
		super(name, type);
		used = new Method[0];
		arr = new Integer(size);
	}

	public void addMethods(Method... met) {
		Method[] new_used = new Method[used.length + met.length];
		int i;
		for (i = 0; i < used.length; i++)
			new_used[i] = used[i];
		for (; i < used.length + met.length; i++)
			new_used[i] = met[i - used.length];
		used = new_used;

	}

	public void removeMethod(Method met) {
		Method[] new_used = null;
		int i = 0, j = 0;

		if (used.length == 0)
			return;

		if (!hasMethod(met))
			return;

		new_used = new Method[used.length - 1];

		while (i < used.length) {
			if (!used[i].equals(met))
				new_used[j++] = used[i];
			i++;
		}
		used = new_used;
	}

	// 이 필도에 해당 메소드 정보가 존재하는지 확인합니다.
	public boolean hasMethod(String name) {
		for (Method v : used)
			if (v.getName().equals(name))
				return true;
		return false;
	}

	public boolean hasMethod(Method obj) {
		for (Method v : used)
			if (v == obj)
				return true;

		return false;
	}

	public Method[] getMethods() {
		return used.clone();
	}

	public Integer getArr() {
		return arr;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String arrStr = (getArr() != null) ? "[" + this.arr + "]" : "";
		return getName() + " : " + getType() + arrStr;
	}

}
