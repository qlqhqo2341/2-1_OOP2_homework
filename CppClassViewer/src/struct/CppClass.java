package struct;

public class CppClass {
	private Method[] methods;
	private Field[] fields;
	private String name;
	
	public CppClass(String name){
		this.name = name;
		methods=null;
		fields=null;
		
	}
	
	public void initArr(int numFields, int numMethods){
		fields = new Field[numFields];
		methods = new Method[numMethods];
	}
	
	
	public String getName(){
		return name;
	}
	public Method[] getMethods(){
		return methods.clone();
	}
	public Field[] getFileds(){
		return fields.clone();
	}
}
