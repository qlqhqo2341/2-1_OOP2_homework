package struct;

import java.util.StringTokenizer;

public class Method {
	private String name;
	private String access;
	private Parameter par[];
	private Field fields[];
	private String body;
	private int fieldsSize;
	
	public Method(String name, String access, Parameter... par){
		this.name = name;
		this.access=access;
		this.par = par.clone();
		fields = null;
		fieldsSize = 0;
		body = null;
	}

	public void setBody(Field[] fields, String body){
		this.body = body;
		this.fields = new Field[fields.length];
		
		StringTokenizer token = new StringTokenizer(body, " \n{}()=+-*/%;");
		
		while(token.hasMoreTokens()){
			String k = token.nextToken();
			
			for(Field find : fields)
				if(find.getName().equals(k))
					this.fields[fieldsSize++]=find;
			
		}
		
		for(int k=0;k<fieldsSize;k++){
			fields[k].addMethods(this);
		}
		
	}
	
	public Parameter[] getParameter(){
		return par.clone();
	}
	
	public Field[] getFields(){
		if(fields!=null){
			return fields.clone();
		}
		else{
			System.out.println(name + "are not parsed.");
			return null;
		}
	}
	
	public String getAccess(){
		return access;
	}
	
	public String getName(){
		return name;
	}
}
