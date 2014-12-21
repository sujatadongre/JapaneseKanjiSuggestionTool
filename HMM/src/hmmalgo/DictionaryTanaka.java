package hmmalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class DictionaryTanaka {

	
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

	/**
	 * This method reads characters from corpus and stores each word in a hashmap with its no of occurences
	 * @return dictionary hashmap
	 */
	private static HashMap<String, Integer> createDicto() {
		String corpusToRead = "TanakaCorpus-JP.txt";
		HashMap<String, Integer> hmDicto = new HashMap<String, Integer>();//to store words and its corro no of occrences in corpus
		String word = new String();
		
		try {
			File fileIn  = new File(corpusToRead);
			Scanner scanner = new Scanner(fileIn);
			
			
			char c;
			String line = null;
					
			while ((scanner.hasNextLine())){
				//boolean isCharBegins = false;
				int idx1 = -1;
				line = scanner.nextLine();
				
				for(int i = 0; i < line.length(); i++){
					c = line.charAt(i);
					//check if c is either hiragana/katakana/kanji character
//					if(c == ' ' && isCharBegins == false){
//						continue;
//					}
					if(isKanji(c) || isHiragana(c) || isKatakana(c)){
						//in that case start making it as a word unless you hit any special character
						word += c;
					}else
					if(!isKanji(c) || !isHiragana(c) || !isKatakana(c) || (i+1 == line.length())){//special character detected
						//in that case store word in hashmap
						//if its already there in hashmap, increment its count value
						//else add new key to the hashmap with count value as 1
						if(i - idx1 > 1){//check if prev character was not a special character
							if(hmDicto.containsKey(word)){//word already exists
								hmDicto.put(word, hmDicto.get(word) + 1);
							}else{//first time entry
								hmDicto.put(word, 1);
							}
							word = new String();//make word empty
						}
						idx1 = i;
					}
				}//end for
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return hmDicto;
	}
}

