package struct;


public class CppClass {
	private Method[] methods;
	private Field[] fields;
	private String name;
	
	public CppClass(String name){
		this.name = name;
		methods = new Method[0];
		fields = new Field[0];
		
	}
	
	public void addMethod(Method obj){
		Method[] new_mets = new Method[methods.length+1];
		int i;
		for(i=0;i<methods.length;i++)
			new_mets[i]=methods[i];
		new_mets[i]=obj;
		methods=new_mets;
	}
	public void addField(Field obj){
		Field[] new_fies = new Field[fields.length+1];
		int i;
		for(i=0;i<fields.length;i++)
			new_fies[i]=fields[i];
		new_fies[i]=obj;
		fields=new_fies;
	}
	
	public Method getMethod(String name){
		for(Method v: methods)
			if(v.getName().equals(name))
				return v;
		
		return null;
	}
	
	public Field getField(String name){
		for(Field v: fields)
			if(v.getName().equals(name))
				return v;

		return null;
	}
	
	public Method[] getMethods(){
		return methods;
	}
	
	public Field[] getFields(){
		return fields;
	}
	
	public String getName(){
		return name;
	}
}
