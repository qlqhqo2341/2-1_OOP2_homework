package struct;

import struct.Variable;
public class Field extends Variable{
	private Method used[];
	
	public Field(String type, Method... used){
		super(type);
		this.used = used.clone();
	}
	
	public Method[] getMethods(){
		return used.clone();
	}
	
}
