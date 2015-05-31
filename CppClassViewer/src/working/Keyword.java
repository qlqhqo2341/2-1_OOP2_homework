package working;

public class Keyword {
	private static String type[]={"int","void","bool"};
	private static String access[]={"public","private"};
	
	public static String[] getaccess(){
		return access;
	}
	
	public static String[] getTypes(){
		return type;
	}
	
	public static boolean isType(String str){
		for(String v : type){
			if(v==str)
				return true;
		}
		return false;
	}
	
	public static boolean isAccess(String str){
		for(String v : access){
			if(v==str)
				return true;
		}
		return false;
	}
}
