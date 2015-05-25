package struct;

public abstract class Variable {
	private final String TYPE;
	private final String name;
	
	public Variable(String name, String type){
		this.name = name;
		TYPE=type;
	}
	
	public String getType(){
		return TYPE;
	}
	public String getName(){
		return name;
	}
	
}
