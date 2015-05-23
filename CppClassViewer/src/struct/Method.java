package struct;

public class Method {
	private Parameter used[];
	private String access;
	
	public Method(String access,Parameter... obj){
		used=obj.clone();
		this.access=access;
	}
	
	public Parameter[] getParameter(){
		return used.clone();
	}
	public String getAccess(){
		return access;
	}
}
