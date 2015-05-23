package struct;

public class CppClass {
	private Method[] methods;
	private Field[] fields;
	private String name;
	
	
	public CppClass(String name,Method[] meth, Field[] fie){
		methods=meth.clone();
		fields=fie.clone();
		this.name = name;
	}
	public Method[] getMethods(){
		return methods.clone();
	}
	public Field[] getFileds(){
		return fields.clone();
	}
}
