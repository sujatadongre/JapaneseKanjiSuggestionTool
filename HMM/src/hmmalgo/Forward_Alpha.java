package hmmalgo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import hmmalgo.FwdViterbiT;

public class Forward_Alpha {
	
	public final static String suggestedString = null;
	public Forward_Alpha(){
		//String result = callExp("でくて");
	}
		
	public String callExp(String qry){	
		//Assume user enters K1K2
		//then append each hiragana character h1, h2 etc to K1K2
		//e.g. K1K2h1, K1K2h2,....
		//for each of these K1K2h1, K1K2h2, program will output some prob,
		//your job is to find the highest prob string and display tht string with prob
		//Forward_New.forward_viterbi(states, startprobs, transi_probs, emission_probs);
		//Viterbi.ViterbiAlgo(states, startprobs, transi_probs, emission_probs);
		//System.out.println("Please enter input =>");
		//Scanner input = new Scanner(System.in);
		//String userStr = input.nextLine();
		//System.out.println(userStr);
		//String userStr = "あなむ";
		
		 String filename = Constant.SERIAL_FILE_PATH;
		 List<Object> probDetails = null;

		 //HMM_Parse_Jap hNew = null;
		 String myStr = new String();
		 FileInputStream fis = null;
		 ObjectInputStream in = null;
		 try
		 {
		    fis = new FileInputStream(filename);
		    in = new ObjectInputStream(fis);
		    probDetails = (ArrayList<Object>) in.readObject();
		    in.close();
		 }
		 catch(IOException ex)
		 {
		     ex.printStackTrace();
		 }
		 catch(ClassNotFoundException ex)
		 {
		    ex.printStackTrace();
		 }
		  // print out restored time
		 System.out.println("Person Details Size: " + probDetails.size());
		 String[] stArr = (String[]) probDetails.get(0);
		 String resultStr = forward_viterbi((String[]) probDetails.get(0), (double[])probDetails.get(1), (double[][])probDetails.get(2), (double[][])probDetails.get(3), qry, suggestedString);
		 System.out.println("resultStr => " + resultStr);
		 return resultStr;
	}
	
	public static String forward_viterbi(String[] states, double[] start_prob, 
			double[][] transi_prob, double[][] emi_prob, String userStr, String suggestedString){
		
		
		//create an array for 192 characters
		String[] arrJapChars = new String[143];
		
		String currentObs = " ";
		boolean isFirstLoop = true;
		HashMap<String, Double> htJapChars = new HashMap<String, Double>();
		Object[] myArray;
		//ArrayList<ProbComparator> arrSortArr = new ArrayList<ProbComparator>();
		
		ProbComparator probComp = new ProbComparator();
		arrJapChars = initJapChars(arrJapChars);
		ArrayList<String> obs = new ArrayList<String>();

		//2. try to erase last character and add h1,...hn
		for(int s = 0; s < userStr.length()-1; s++){
			obs.add(userStr.substring(s, s+1));
		}
		
		//then iterate through each element in an array
		//and append it to obs[] array
		//so there will be one more outer loop for existing code
		if(arrJapChars!= null && arrJapChars.length > 0){
			
			for(int a = 0; a < arrJapChars.length; a++){
					
				//if already any other character is appended at the end,
				//in the next loop, remove that previously appended character and append new one
				if(isFirstLoop == false){
					obs.remove(obs.size()-1);
				}
				obs.add(arrJapChars[a]);
					
				int char_index = 0;
				Hashtable<String, Object[]> T = new Hashtable<String, Object[]>();


			
				//for all states
				//first initialize each state with its corresponding start probabilities	
				for(int i = 0; i < states.length; i++){
					T.put(states[i], new Object[] {start_prob[i], states[i], start_prob[i]});
				}
			
				//for each observation in observation sequence
				for(int output = 0; output < obs.size(); output++){
					
					//check which row to look at in the emission probability matrix
					//if in the obs array, its kanji, always look at row with index 1
					//else get the int value of the character and look at the corro row
					
					//System.out.println("obs is => " + obs.get(output));
					currentObs = obs.get(output);
					System.err.println("currentObs => " + currentObs + " obs.get(output) => " + obs.get(output));
					if((isKanji(obs.get(output).charAt(0)) || obs.get(output) == "＊")){
						char_index = 1;
						//System.out.println("kanji it is " + char_index);
					}else
					if(isHiragana(obs.get(output).charAt(0))){
						char_index = ((int)(obs.get(output).charAt(0) - '\u3040'));
					}
					else
					if(isKatakana(obs.get(output).charAt(0))){
						char_index = ((int)(obs.get(output).charAt(0) - '\u3040'));
					}else{
						//do nothing ignore that character
						System.out.println("unknown character　=> " + currentObs);
					}
					
					Hashtable<String, Object[]> U = new Hashtable<String, Object[]>();
				
				//for next_state in states array
				for(int j = 0; j < states.length; j++){
					double total = 0;
					
					//for current state in states array
					for(int k = 0; k < states.length; k++){
						Object[] objs = T.get(states[k]);
						double prob = (Double) objs[0];//start prob initially
						double p = emi_prob[k][char_index] * transi_prob[k][j];
						
						prob *= p;
						total += prob;
					}//end for k
					U.put(states[j], new Object[] {total});
				}//end for j
				T = U;		
			}//end for i
			
			double total = 0;
			double prob;
			for (String state : states)
			{
				Object[] objs = T.get(state);
				prob = ((Double) objs[0]).doubleValue();
				total += prob;
			}
			System.out.println("Forward Probability => " + total);
			htJapChars.put(currentObs, total);
			isFirstLoop = false;
			}
			
			Collection c = htJapChars.values();
			//obtain an Iterator for Collection
			Iterator itr = c.iterator();
			//iterate through HashMap values iterator
			while(itr.hasNext()){
				System.out.println(itr.next());
			}
		
			myArray = htJapChars.entrySet().toArray();
			Arrays.sort(myArray, (Comparator)probComp);
			
			for(int i = 0; i < myArray.length; i++){
				System.out.println(((Map.Entry)myArray[i]).getKey()  + " = " + (((Map.Entry)myArray[i]).getValue()));
			}
			System.out.println("myArray length => " + myArray.length);
			
			
			//check if there are any sentences with the suggested string in Tanaka Corpus?
			
			//if isCharfound == 1 then there are some sentences with suggested string
			//so in that case you can display user the suggested string
			//but if isCharFound == 0 then its completely wrong suggestion
			//in that case, get the second highest observation and search in corpus file
			//Arrays.sort(arrSortArr.toArray());
			for(int l = myArray.length-1; l >= 0; l--){
				suggestedString = new String();
				for(int i = 0; i < obs.size()-1; i++){
					suggestedString += obs.get(i);
				}
				suggestedString += ((Map.Entry)myArray[l]).getKey();
				int isCharFound = KanjiSearch.searchCharacter(suggestedString);
				if(isCharFound == 1){
					System.out.println("**********1. Forward もうしかして? :" + suggestedString);
					break;
				}
			}
		}else{
				System.out.println("Japanese character array is empty.");
		}
			
		return suggestedString;

	}
			
		private static String[] initJapChars(String[] arrJapChars) {
			
			arrJapChars[0] = "＊";
			arrJapChars[1] = "あ";
			arrJapChars[2] = "い";
			arrJapChars[3] = "う";
			arrJapChars[4] = "え";
			arrJapChars[5] = "お";
			arrJapChars[6] = "か";
			arrJapChars[7] = "が";
			arrJapChars[8] = "き";
			arrJapChars[9] = "ぎ";
			arrJapChars[10] = "く";
			arrJapChars[11] = "ぐ";
			arrJapChars[12] = "け";
			arrJapChars[13] = "げ";
			arrJapChars[14] = "こ";
			arrJapChars[15] = "ご";
			arrJapChars[16] = "さ";
			arrJapChars[17] = "ざ";
			arrJapChars[18] = "し";
			arrJapChars[19] = "じ";
			arrJapChars[20] = "す";
			arrJapChars[21] = "ず";
			arrJapChars[22] = "せ";
			arrJapChars[23] = "ぜ";
			arrJapChars[24] = "そ";
			arrJapChars[25] = "ぞ";
			arrJapChars[26] = "た";
			arrJapChars[27] = "だ";
			arrJapChars[28] = "ち";
			arrJapChars[29] = "ぢ";
			arrJapChars[30] = "つ";
			arrJapChars[31] = "づ";
			arrJapChars[32] = "て";
			arrJapChars[33] = "で";
			arrJapChars[34] = "と";
			arrJapChars[35] = "ど";
			arrJapChars[36] = "な";
			arrJapChars[37] = "に";
			arrJapChars[38] = "ぬ";
			arrJapChars[39] = "ね";
			arrJapChars[40] = "の";
			arrJapChars[41] = "は";
			arrJapChars[42] = "ば";
			arrJapChars[43] = "ぱ";
			arrJapChars[44] = "ひ";
			arrJapChars[45] = "び";
			arrJapChars[46] = "ぴ";
			arrJapChars[47] = "ふ";
			arrJapChars[48] = "ぶ";
			arrJapChars[49] = "ぷ";
			arrJapChars[50] = "へ";
			arrJapChars[51] = "べ";
			arrJapChars[52] = "ぺ";
			arrJapChars[53] = "ほ";
			arrJapChars[54] = "ぼ";
			arrJapChars[55] = "ぽ";
			arrJapChars[56] = "ま";
			arrJapChars[57] = "み";
			arrJapChars[58] = "む";
			arrJapChars[59] = "め";
			arrJapChars[60] = "も";
			arrJapChars[61] = "や";
			arrJapChars[62] = "ゆ";
			arrJapChars[63] = "よ";
			arrJapChars[64] = "ら";
			arrJapChars[65] = "り";
			arrJapChars[66] = "る";
			arrJapChars[67] = "れ";
			arrJapChars[68] = "ろ";
			arrJapChars[69] = "わ";
			arrJapChars[70] = "を";
			arrJapChars[71] = "ん";
			arrJapChars[72] = "ア";
			arrJapChars[73] = "イ";
			arrJapChars[74] = "ウ";
			arrJapChars[75] = "エ";
			arrJapChars[76] = "オ";
			arrJapChars[77] = "カ";
			arrJapChars[78] = "ガ";
			arrJapChars[79] = "キ";
			arrJapChars[80] = "ギ";
			arrJapChars[81] = "ク";
			arrJapChars[82] = "グ";
			arrJapChars[83] = "ケ";
			arrJapChars[84] = "ゲ";
			arrJapChars[85] = "コ";
			arrJapChars[86] = "ゴ";
			arrJapChars[87] = "サ";
			arrJapChars[88] = "ザ";
			arrJapChars[89] = "シ";
			arrJapChars[90] = "ジ";
			arrJapChars[91] = "ス";
			arrJapChars[92] = "ズ";
			arrJapChars[93] = "セ";
			arrJapChars[94] = "ゼ";
			arrJapChars[95] = "ソ";
			arrJapChars[96] = "ゾ";
			arrJapChars[97] = "タ";
			arrJapChars[98] = "ダ";
			arrJapChars[99] = "チ";
			arrJapChars[100] = "ヂ";
			arrJapChars[101] = "ツ";
			arrJapChars[102] = "ヅ";
			arrJapChars[103] = "テ";
			arrJapChars[104] = "デ";
			arrJapChars[105] = "ト";
			arrJapChars[106] = "ド";
			arrJapChars[107] = "ナ";
			arrJapChars[108] = "ニ";
			arrJapChars[109] = "ヌ";
			arrJapChars[110] = "ネ";
			arrJapChars[111] = "ノ";
			arrJapChars[112] = "ハ";
			arrJapChars[113] = "バ";
			arrJapChars[114] = "パ";
			arrJapChars[115] = "ヒ";
			arrJapChars[116] = "ビ";
			arrJapChars[117] = "ピ";
			arrJapChars[118] = "フ";
			arrJapChars[119] = "ブ";
			arrJapChars[120] = "プ";
			arrJapChars[121] = "ヘ";
			arrJapChars[122] = "ベ";
			arrJapChars[123] = "ペ";
			arrJapChars[124] = "ホ";
			arrJapChars[125] = "ボ";
			arrJapChars[126] = "ポ";
			arrJapChars[127] = "マ";
			arrJapChars[128] = "ミ";
			arrJapChars[129] = "ム";
			arrJapChars[130] = "メ";
			arrJapChars[131] = "モ";
			arrJapChars[132] = "ヤ";
			arrJapChars[133] = "ユ";
			arrJapChars[134] = "ヨ";
			arrJapChars[135] = "ラ";
			arrJapChars[136] = "リ";
			arrJapChars[137] = "ル";
			arrJapChars[138] = "レ";
			arrJapChars[139] = "ロ";
			arrJapChars[140] = "ワ";
			arrJapChars[141] = "ン";
			arrJapChars[142] = "ー";
			return arrJapChars;
		}

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
		
		public static void main(String args[]){
			Forward_Alpha fa = new Forward_Alpha();
			
			System.out.println("Please enter input =>");
			Scanner input = new Scanner(System.in);
			String userStr = input.nextLine();
			System.out.println(userStr);
			String myStr = fa.callExp(userStr);
			System.out.println(myStr);
		}

}
