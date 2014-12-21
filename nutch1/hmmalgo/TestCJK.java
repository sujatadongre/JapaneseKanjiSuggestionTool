package hmmalgo;

import java.io.IOException;
import java.io.StringReader;

public class TestCJK {
	public static void main(String args[]){
		StringReader sr = new StringReader("Sujata Dongre Gajanan");
		CJKTokenizer cjk = new CJKTokenizer(sr);
		try {
			System.out.println(cjk.next());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
