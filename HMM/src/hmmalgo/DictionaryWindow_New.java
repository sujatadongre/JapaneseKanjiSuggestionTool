package hmmalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class DictionaryWindow_New {
	
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


	public static HashMap createDicto(){
		File fileIn  = new File("TanakaCorpus-JP.txt");
		String line = null;
		HashMap<String, Integer> hmDicto = new HashMap<String, Integer>();
			
		try {
			Scanner scanner = new Scanner(fileIn);			
			while ((scanner.hasNextLine())){
				line = scanner.nextLine();
				
				for(int i = 0; i < line.length(); i++){
					if(i+3 != line.length()){
						String str = line.substring(i, i+3);
						//System.out.println("str => " + str);
						if(isKanji(str.charAt(0)) || isHiragana(str.charAt(0)) || isKatakana(str.charAt(0))){//check if 1st character is valid japanese character
							if(isKanji(str.charAt(1)) || isHiragana(str.charAt(1)) || isKatakana(str.charAt(1))){//check if 2nd character is also valid japanese character
								//make word
								String word = str.substring(0, 2); 
								//check if this string already exists in hashmap
								if(hmDicto.containsKey(word)){//word already exists
									//check if the 3rd character is special character then add 1
									//else subtract 1
									if(isKanji(str.charAt(2)) || isHiragana(str.charAt(2)) || isKatakana(str.charAt(2))){
										hmDicto.put(word, hmDicto.get(word) - 1);
									}else{
										hmDicto.put(word, hmDicto.get(word) + 1);
									}
								}else{//first time entry
									
									if(isKanji(str.charAt(2)) || isHiragana(str.charAt(2)) || isKatakana(str.charAt(2))){
										hmDicto.put(word, -1);
									}else{
										hmDicto.put(word, 1);
									}
								}
							}
						}
					}else{
						break;
					}
				}
			}
			scanner.close();
			
			//print hashmap
			//printDicto(hmDicto);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hmDicto;
	}
}
