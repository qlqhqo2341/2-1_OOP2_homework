package struct;

import struct.Variable;
public class Field extends Variable{
	private Method used[];
	private int usedSize;
	
	
	public Field(String name, String type, int numMethods){
		super(name, type);
		used = new Method[numMethods];
		usedSize=0;
	}
	
	public void addMethods(Method... met){
		for(Method v : met){
			used[usedSize++]=v;
		}
	}

	public Method[] getMethods(){
		return used.clone();
	}
	
}
