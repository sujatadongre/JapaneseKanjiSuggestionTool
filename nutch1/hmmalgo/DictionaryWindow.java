package hmmalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class DictionaryWindow {

	public static void main(String args[]){
		//create hashmap dictionary
		HashMap<String, Integer> hmDicto = new HashMap<String, Integer>();
		hmDicto = createDicto();
		//print the dictionary
		printDicto(hmDicto);
	}
	
	/**
	 * sort and print the dictionary hashmap
	 * @param hmDicto
	 */
	private static void printDicto(HashMap<String, Integer> hmDicto) {
		 // First we're getting values array  
        ArrayList<Integer> values = new ArrayList<Integer>();
        values.addAll(hmDicto.values());
        // and sorting it (in reverse order) 
        Collections.sort(values, Collections.reverseOrder());
 
        int last_i = -1;
        // Now, for each value  
        for (Integer i : values) { 
            if (last_i == i) // without duplicates  
                continue;
            last_i = i;
            // we print all hash keys  
            for (String s : hmDicto.keySet()) { 
                if (hmDicto.get(s) == i) // which have this value  
                    System.out.println(s + ":" + i);
            } 
        } 
	}
	
	
	private static boolean isKatakana(char c) {
		//return (isHalfwidthKatakana(c) || isFullwidthKatakana(c));
		return (isFullwidthKatakana(c));
	}
	private static boolean isFullwidthKatakana(char c) {
		if (!(('\u30a1' <= c) && (c <= '\u30fe')))
        {
            return false;
        }
		return true;
	}


	private static boolean isHiragana(char c) {
		if (!(('\u3041' <= c) && (c <= '\u309e')))
        {
            return false;
        }
		return true;
	}

	//Aim: To check if the character from the tanaka coprus is a kanji character or not
	//Input: single character from the file
	//Output: boolean value if is kanji or not
	private static boolean isKanji(char c) {
		Boolean charIsKanji = false;
		if (('\u4e00' <= c) && (c <= '\u9fa5'))
        {
            charIsKanji = true;
        }
        if (('\u3005' <= c) && (c <= '\u3007'))
        {
            charIsKanji = true;
        }
        if (!charIsKanji)
        {
            return false;
        }
		
		return charIsKanji;
	}

	private static HashMap<String, Integer> createDicto() {
		String corpusToRead = "TanakaCorpus-JP.txt";
		HashMap<String, Integer> hmDicto = new HashMap<String, Integer>();//to store words and its corro no of occrences in corpus
		String word = new String();
		boolean isLength2 = false;
		
		try {
			File fileIn  = new File(corpusToRead);
			Scanner scanner = new Scanner(fileIn);
			
			char ch1;
			char ch2;
			String line = null;
					
			while ((scanner.hasNextLine())){
				//boolean isCharBegins = false;
				//int idx1 = -1;
				line = scanner.nextLine();
				
				for(int i = 0; i < line.length(); i++){
					ch1 = line.charAt(i);
					//System.out.println("ch1 => " + ch1);
					if(isKanji(ch1) || isHiragana(ch1) || isKatakana(ch1)){
						word += ch1;
						for(int j = i+1; j < line.length(); j++){
							ch2 = line.charAt(j);
							if(isLength2 == false){
								if(isKanji(ch2) || isHiragana(ch2) || isKatakana(ch2)){
									word += ch2;
									word = new String();//make word empty
									isLength2 = true;
								}else{//2nd character is a special char
									//if(j - idx1 > 1){//check if prev character was not a special character
//										if(hmDicto.containsKey(word)){//word already exists
//											hmDicto.put(word, hmDicto.get(word) + 1);
//										}else{//first time entry
//											hmDicto.put(word, 1);
//										}
//										word = new String();//make word empty
//										isLength2 = false;
									//do nothing ignore this character
										word = new String();//make word empty
										isLength2 = false;
										break;
									//}
									//idx1 = j;
								}
							}else
							if(isLength2 == true){
								if(!isKanji(ch2) && !isHiragana(ch2) && !isKatakana(ch2) && (j+1 != line.length())){//window size 2 reached
									//if(j - idx1 > 1){//check if prev character was not a special character
										if(hmDicto.containsKey(word)){//word already exists
											hmDicto.put(word, hmDicto.get(word) + 1);
										}else{//first time entry
											hmDicto.put(word, 1);
										}
										word = new String();//make word empty
									//}
									//idx1 = j;
									isLength2 = false;
									break;
								}else{
									//if(j - idx1 > 1){//check if prev character was not a special character
										if(hmDicto.containsKey(word)){//word already exists
											hmDicto.put(word, hmDicto.get(word) - 1);
										}else{//first time entry
											hmDicto.put(word, -1);
										}
										word = new String();//make word empty
									//}
									//idx1 = j;
									isLength2 = false;
									break;
								}
							}
						}
					}else{
						continue;
					}
				}	
			}//end for
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return hmDicto;
	}
}

