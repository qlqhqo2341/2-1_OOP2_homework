import java.util.StringTokenizer;


public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			StringTokenizer token = new StringTokenizer("��������	��������", " \t");
			while(token.hasMoreTokens()){
				
				System.out.println(token.nextToken());
			}
			
	}

}
