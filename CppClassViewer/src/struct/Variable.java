package struct;

public abstract class Variable {
	private final String TYPE;
	
	public Variable(String type){
		TYPE=type;
	}
	
	public String getType(){
		return TYPE;
	}
	
	
}
