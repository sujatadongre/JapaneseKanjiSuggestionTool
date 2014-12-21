package hmmalgo;



import java.io.FileOutputStream;

import java.io.IOException;

import java.io.ObjectOutputStream;



public class Hello {

  public String say() {

    return "Hello";

  }

  public static void main(String args[]){

	  HMM_Parse_Jap h = new HMM_Parse_Jap();

//	  Viterbi_New vNew = new Viterbi_New();
//	  Forward_Alpha fa = new Forward_Alpha();
	  
	  
//
//	  String myStr = vNew.callExp("あなむ");
//
//	  System.out.println("Did you mean: " + myStr);

	  //Viterbi_First_Append vFirst = new Viterbi_First_Append();

	  //String myStr = h.parseJapText("あなむ");

	 //System.out.println("Did you mean: " + myStr);

	  
//
//	  String filename = Constant.SERIAL_FILE_PATH;
//
//	  
//
//	  FileOutputStream fos = null;
//	  ObjectOutputStream out = null;
//
//     try
//
//     {
//
//       fos = new FileOutputStream(filename);
//
//       out = new ObjectOutputStream(fos);
//
//       //out.writeObject(h.parseJapText("あなむ"));
//
//       out.close();
//
//     }
//
//     catch(IOException ex)
//
//     {
//
//       ex.printStackTrace();
//
//     }
//
  }

}

