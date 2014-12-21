package hmmalgo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class KanjiSearch {
	
	public static int searchCharacter(String kanji){
		
		int isCharFound = 0;
		try {
			File file = new File(Constant.FILE_PATH);
			//System.out.println(file.getPath());
			FileInputStream fstream = new FileInputStream(file);
			
		    // Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			InputStreamReader isr = new InputStreamReader(fstream, "UTF8");
    		BufferedReader br = new BufferedReader(isr);
    		String defaultEncoding = isr.getEncoding();
    		
			String line = null;
			//Scanner scanner = new Scanner(fileIn);
			
			while (((line = br.readLine()) != null)){
				//line = scanner.nextLine();
				if(line.contains(kanji)){
					isCharFound = 1;
					//System.out.println(line);
				}
			}
//			if(isCharFound == 0)
//				System.out.println("Character not found");
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){//Catch exception if any
      			System.err.println("Error: " + e.getMessage());
    		}
		return isCharFound;
	}
}
