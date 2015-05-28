package working;

import java.util.StringTokenizer;
import java.util.Vector;

import struct.*;

public class Parser {
	CppClass obj;
	String allText;
	StringTokenizer mainToken;

	public Parser(String text) {
		allText = text;
		mainToken = new StringTokenizer(allText, "{}", true);
		String[] dd = nextFunction();

		while (dd != null) {
			System.out.println(dd[0] + "\nof body is below\n" + dd[1]);
			System.out.println("body is end");
			dd = nextFunction();
		}

	}

	public String[] nextFunction() {

		String part[] = new String[2];
		String define;
		StringBuffer body = new StringBuffer();

		int closer = 0;
		int last_closer = -1;

		if (mainToken.hasMoreTokens()) {
			define = mainToken.nextToken();
		} else
			return null;

		if (mainToken.hasMoreTokens())
			do {
				closer++;
				StringBuffer t = new StringBuffer();

				t.append(mainToken.nextToken());
				for (int k = 0; k < t.length(); k++) {
					if (t.charAt(k) == '}') {
						closer--;
						last_closer = k + body.length();
					}
				}
				body.append(t);
			} while (mainToken.hasMoreTokens() && closer > 0);
		else
			return null;
		
		if (closer > 0)
			return null;
		else {
			if (last_closer >= 0)
				body.delete(last_closer, body.length());

			part[0] = define;
			part[1] = body.toString();
			return part;
		}

	}

	public void initClass(String[] parts) {
		StringTokenizer token = new StringTokenizer(parts[0]);
		String name = null;
		while (token.hasMoreTokens()) {
			if (token.nextToken().equals("class")) {
				if (token.hasMoreTokens()) {
					name = token.nextToken();
				}
			}
		}

		if (name == null) {
			System.out.println("is it CppClass??");
			return;
		}

		obj = new CppClass(name);

		int numFields = 0, numMethods = 0;
		// count num of fields, methods
		StringTokenizer countToken = new StringTokenizer(parts[1], " \n(");
		StringTokenizer lineToken = new StringTokenizer(parts[1], "\n");

		while (countToken.hasMoreTokens()) {
			String k = countToken.nextToken();
			if (Keyword.isType(k)) {
				String type = k;
				String var_name = countToken.nextToken();

				if (var_name.charAt(var_name.length() - 1) == ';') {
					numFields++;
				} else {
					k = var_name;

				}

			}

			if (obj.getName().equals(k)) {

			}
			if (obj.getName().equals('~' + k)) {

			}
		}

	}

	public static void main(String[] args) {
		Parser k = new Parser("class me{ hohoho	}"
				+ "\nint me::ddfdf(sss){nlidfe}");

	}

}
