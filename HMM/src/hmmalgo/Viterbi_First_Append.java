package hmmalgo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Viterbi_First_Append {
			
	public static void ViterbiAlgo(String[] states, double[] start_prob, 
			double[][] transi_prob, double[][] emi_prob, String userStr){
		
		//create an array for 192 characters
		char[] arrJapChars = new char[143];
		char currentObs = ' ';
		boolean isFirstLoop = true;
		HashMap<Character, Double> htJapChars = new HashMap<Character, Double>();
		Object[] myArray;
		ProbComparator probComp = new ProbComparator();
		arrJapChars = initJapChars(arrJapChars);
		ArrayList<Character> obs = new ArrayList<Character>();
//		System.out.println("Please enter input =>");
//		Scanner input = new Scanner(System.in);
//		String userStr = input.nextLine();
//		System.out.println(userStr);
		
		
		//1. get the user input string and search it in Tanaka Corpus
//		int isCharFound = KanjiSearch.searchCharacter(userStr);
//		if(isCharFound == 1){
//			System.out.println("********** Tanaka Corpus suggestions? :" + userStr);
//		}else{
//			System.out.println("No string found in Tanaka Corpus... Soon, you will get my suggestion");
//		}
		//2. try to erase last character and add h1,...hn
		for(int s = 1; s < userStr.length(); s++){
			//obs.add(userStr.charAt(s));
			obs.add(userStr.charAt(s));
		}
		
		//then iterate through each element in an array
		//and append it to obs[] array
		//so there will be one more outer loop for existing code
		if(arrJapChars!= null && arrJapChars.length > 0){
			String bestest_v_path = "";
			double bestest_v_prob = 0;
			char bestest_obs = ' ';
			
			for(int a = 0; a < arrJapChars.length; a++){
					
				//char[] obs = {'大', '好', 'き'};
				//char[] obs = {'大', '好'};
				if(arrJapChars[a] != ' '){
					
//					obs.add('大');//this shld be changed with user input later
//					obs.add('好');//this shld be changed with user input later
					
					//if already any other character is appended at the end,
					//in the next loop, remove that previously appended character and append new one
					if(isFirstLoop == false){
						obs.remove(obs.get(0));
					}
					obs.add(0, arrJapChars[a]);
						
					int char_index = 0;
					Hashtable<String, Object[]> T = new Hashtable<String, Object[]>();
					
					//for all states
					//first initialize each state with its corresponding start probabilities	
					for(int i = 0; i < states.length; i++){
						T.put(states[i], new Object[] {start_prob[i], states[i], start_prob[i]});
					}
						
//					for(int s = 0; s < states.length; s++){
//						System.out.println(T.get(states[s])[0]);
//						System.out.println(T.get(states[s])[1]);
//						System.out.println(T.get(states[s])[2]);
//					}
				
					//for each observation in observation sequence
					for(int output = 0; output < obs.size(); output++){
						
						//check which row to look at in the emission probability matrix
						//if in the obs array, its kanji, always look at row with index 1
						//else get the int value of the character and look at the corro row
						
						//System.out.println("obs is => " + obs.get(output));
						currentObs = obs.get(0);
						//System.out.println("currentObs => " + currentObs);
						if((isKanji(obs.get(output)) || obs.get(output) == '＊')){
							char_index = 1;
							//System.out.println("kanji it is " + char_index);
						}else
						if(isHiragana(obs.get(output))){
							char_index = ((int)(obs.get(output) - '\u3040'));
						}
						else
						if(isKatakana(obs.get(output))){
							char_index = ((int)(obs.get(output) - '\u3040'));
						}else{
							//do nothing ignore that character
							System.out.println("unknown character");
						}
						
						Hashtable<String, Object[]> U = new Hashtable<String, Object[]>();
						
						//for each state at time t+1
						for(int j = 0; j < states.length; j++){
							//initialize the most probable path to empty string
							//and highest probability to 0;
							
							double total = 0;
							String argmax = "";
							double valmax = 0;
			 
							double prob = 1;
							String v_path = "";
							double v_prob = 1;
								
							//for current state at time t
							for(int k = 0; k < states.length; k++){
								
								Object[] objs = T.get(states[k]);
								prob = ((Double) objs[0]).doubleValue();//start prob initially
								v_path = (String) objs[1];
								v_prob = ((Double) objs[2]).doubleValue();//start prob initially
								
//								System.out.println("Calculating probability");
//								System.out.println("for observation => " + obs.get(output));
								
								//double p = emi_prob[k][output] * transi_prob[k][j];
								double p = emi_prob[k][char_index] * transi_prob[k][j];
//								System.out.println("emission prob => " + emi_prob[k][char_index] 
//								                    + " transition prob => " + transi_prob[k][j]
//								                    + " emission * transition => " + p);
//								System.out.println("previous probability => " + v_prob);
								prob *= p;
								v_prob *= p;
								//System.out.println("after multiplying with previous probability => " + v_prob);
								total += prob;
								
								if (v_prob > valmax)
								{
									argmax = v_path + "," + states[j];
									valmax = v_prob;
								}
							}
							U.put(states[j], new Object[] {total, argmax, valmax});
//							System.out.println("path => " + argmax);
//							System.out.println("prob => " + valmax);
						}
						T = U;	
					}
					
					double total = 0;
					String argmax = "";
					double valmax = 0;
			 
					double prob;
					String v_path;
					double v_prob;
						
					for (String state : states)
					{
						Object[] objs = T.get(state);
						prob = ((Double) objs[0]).doubleValue();
						v_path = (String) objs[1];
						v_prob = ((Double) objs[2]).doubleValue();
						total += prob;
						if (v_prob > valmax)
						{
							argmax = v_path;
							valmax = v_prob;
						}
					}	
					
					//save probability of current observation into the hashmap
					htJapChars.put(currentObs, valmax);
					
					//System.out.println("viterbi path with current observation => " + argmax);
					//System.out.println("viterbi prob with current observation => " + valmax);
//					System.out.println("total => " + total);
					
					//once best path is calculated from existing code/loop 
					//assign it as the latest bestest path
					//when for new character in the character array c
					if(valmax > bestest_v_prob){
						bestest_v_path = argmax;
						bestest_v_prob = valmax;
						bestest_obs = arrJapChars[a];
					}
					
					//System.out.println("the bestest path => " + bestest_v_path);
					//System.out.println("the bestest prob => " + bestest_v_prob);
					//System.out.println("bestest character => " + bestest_obs);
					isFirstLoop = false;
				}
				
			}//end of for japanese character array
			
			//String suggestedString = obs.get(0).toString()+obs.get(1).toString()+bestest_obs;
			
			//if(bestest_obs != '＊'){
				//sort hashtable values from lowest to highest
				myArray = htJapChars.entrySet().toArray();
				Arrays.sort(myArray, (Comparator)probComp);
				
//				for(int i = 0; i < myArray.length; i++){
//					System.out.println(((Map.Entry)myArray[i]).getKey()  + " = " + (((Map.Entry)myArray[i]).getValue()));
//				}
//				System.out.println("myArray length => " + myArray.length);
				
				
				//check if there are any sentences with the suggested string in Tanaka Corpus?
				
				//if isCharfound == 1 then there are some sentences with suggested string
				//so in that case you can display user the suggested string
				//but if isCharFound == 0 then its completely wrong suggestion
				//in that case, get the second highest observation and search in corpus file
				for(int l = myArray.length-1; l >= 0; l--){
					String suggestedString = new String();
					for(int i = 1; i < obs.size(); i++){
						suggestedString += obs.get(i);
					}
					//suggestedString += ((Map.Entry)myArray[l]).getKey();
					suggestedString = ((Map.Entry)myArray[l]).getKey() + suggestedString;
					int isCharFound = KanjiSearch.searchCharacter(suggestedString);
					if(isCharFound == 1){
						System.out.println("**********1. もうしかして? :" + suggestedString);
						break;
					}
				}				
//			}else{//bestest character is kanji so next letter is probabaly kanji and hence no need to look into tanaka corpus
//				System.out.println("Next character starts with kanji");
//			}

			//one that is suggested as best one = 0.1
			//check both conditions at the same time
			//check if probabilities are going too low as 1/10th i.e 0.01 then stop
			//if found sentences in tanaka but probs getting too low then stop no suggestions
			
		}else{
			System.out.println("Japanese character array is empty.");
		}
	}

	private static char[] initJapChars(char[] arrJapChars) {
	
		arrJapChars[0] = '＊';
		arrJapChars[1] = 'あ';
		arrJapChars[2] = 'い';
		arrJapChars[3] = 'う';
		arrJapChars[4] = 'え';
		arrJapChars[5] = 'お';
		arrJapChars[6] = 'か';
		arrJapChars[7] = 'が';
		arrJapChars[8] = 'き';
		arrJapChars[9] = 'ぎ';
		arrJapChars[10] = 'く';
		arrJapChars[11] = 'ぐ';
		arrJapChars[12] = 'け';
		arrJapChars[13] = 'げ';
		arrJapChars[14] = 'こ';
		arrJapChars[15] = 'ご';
		arrJapChars[16] = 'さ';
		arrJapChars[17] = 'ざ';
		arrJapChars[18] = 'し';
		arrJapChars[19] = 'じ';
		arrJapChars[20] = 'す';
		arrJapChars[21] = 'ず';
		arrJapChars[22] = 'せ';
		arrJapChars[23] = 'ぜ';
		arrJapChars[24] = 'そ';
		arrJapChars[25] = 'ぞ';
		arrJapChars[26] = 'た';
		arrJapChars[27] = 'だ';
		arrJapChars[28] = 'ち';
		arrJapChars[29] = 'ぢ';
		arrJapChars[30] = 'つ';
		arrJapChars[31] = 'づ';
		arrJapChars[32] = 'て';
		arrJapChars[33] = 'で';
		arrJapChars[34] = 'と';
		arrJapChars[35] = 'ど';
		arrJapChars[36] = 'な';
		arrJapChars[37] = 'に';
		arrJapChars[38] = 'ぬ';
		arrJapChars[39] = 'ね';
		arrJapChars[40] = 'の';
		arrJapChars[41] = 'は';
		arrJapChars[42] = 'ば';
		arrJapChars[43] = 'ぱ';
		arrJapChars[44] = 'ひ';
		arrJapChars[45] = 'び';
		arrJapChars[46] = 'ぴ';
		arrJapChars[47] = 'ふ';
		arrJapChars[48] = 'ぶ';
		arrJapChars[49] = 'ぷ';
		arrJapChars[50] = 'へ';
		arrJapChars[51] = 'べ';
		arrJapChars[52] = 'ぺ';
		arrJapChars[53] = 'ほ';
		arrJapChars[54] = 'ぼ';
		arrJapChars[55] = 'ぽ';
		arrJapChars[56] = 'ま';
		arrJapChars[57] = 'み';
		arrJapChars[58] = 'む';
		arrJapChars[59] = 'め';
		arrJapChars[60] = 'も';
		arrJapChars[61] = 'や';
		arrJapChars[62] = 'ゆ';
		arrJapChars[63] = 'よ';
		arrJapChars[64] = 'ら';
		arrJapChars[65] = 'り';
		arrJapChars[66] = 'る';
		arrJapChars[67] = 'れ';
		arrJapChars[68] = 'ろ';
		arrJapChars[69] = 'わ';
		arrJapChars[70] = 'を';
		arrJapChars[71] = 'ん';
		arrJapChars[72] = 'ア';
		arrJapChars[73] = 'イ';
		arrJapChars[74] = 'ウ';
		arrJapChars[75] = 'エ';
		arrJapChars[76] = 'オ';
		arrJapChars[77] = 'カ';
		arrJapChars[78] = 'ガ';
		arrJapChars[79] = 'キ';
		arrJapChars[80] = 'ギ';
		arrJapChars[81] = 'ク';
		arrJapChars[82] = 'グ';
		arrJapChars[83] = 'ケ';
		arrJapChars[84] = 'ゲ';
		arrJapChars[85] = 'コ';
		arrJapChars[86] = 'ゴ';
		arrJapChars[87] = 'サ';
		arrJapChars[88] = 'ザ';
		arrJapChars[89] = 'シ';
		arrJapChars[90] = 'ジ';
		arrJapChars[91] = 'ス';
		arrJapChars[92] = 'ズ';
		arrJapChars[93] = 'セ';
		arrJapChars[94] = 'ゼ';
		arrJapChars[95] = 'ソ';
		arrJapChars[96] = 'ゾ';
		arrJapChars[97] = 'タ';
		arrJapChars[98] = 'ダ';
		arrJapChars[99] = 'チ';
		arrJapChars[100] = 'ヂ';
		arrJapChars[101] = 'ツ';
		arrJapChars[102] = 'ヅ';
		arrJapChars[103] = 'テ';
		arrJapChars[104] = 'デ';
		arrJapChars[105] = 'ト';
		arrJapChars[106] = 'ド';
		arrJapChars[107] = 'ナ';
		arrJapChars[108] = 'ニ';
		arrJapChars[109] = 'ヌ';
		arrJapChars[110] = 'ネ';
		arrJapChars[111] = 'ノ';
		arrJapChars[112] = 'ハ';
		arrJapChars[113] = 'バ';
		arrJapChars[114] = 'パ';
		arrJapChars[115] = 'ヒ';
		arrJapChars[116] = 'ビ';
		arrJapChars[117] = 'ピ';
		arrJapChars[118] = 'フ';
		arrJapChars[119] = 'ブ';
		arrJapChars[120] = 'プ';
		arrJapChars[121] = 'ヘ';
		arrJapChars[122] = 'ベ';
		arrJapChars[123] = 'ペ';
		arrJapChars[124] = 'ホ';
		arrJapChars[125] = 'ボ';
		arrJapChars[126] = 'ポ';
		arrJapChars[127] = 'マ';
		arrJapChars[128] = 'ミ';
		arrJapChars[129] = 'ム';
		arrJapChars[130] = 'メ';
		arrJapChars[131] = 'モ';
		arrJapChars[132] = 'ヤ';
		arrJapChars[133] = 'ユ';
		arrJapChars[134] = 'ヨ';
		arrJapChars[135] = 'ラ';
		arrJapChars[136] = 'リ';
		arrJapChars[137] = 'ル';
		arrJapChars[138] = 'レ';
		arrJapChars[139] = 'ロ';
		arrJapChars[140] = 'ワ';
		arrJapChars[141] = 'ン';
		arrJapChars[142] = 'ー';	
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


	
//	public static void main(String args[]){
//
//		//printing states
//		for(int i=0; i < states.length; i++){
//			System.out.println("states are: " + states[i]);
//		}
//		//printing observations
//		for(int i = 0; i < observations.length; i++){
//			System.out.println("observations are: " + observations[i]);
//		}
//		//printing start probability
//		for(int i = 0; i < start_probability.length; i++){
//			System.out.println("start_probability are: " + start_probability[i]);
//		}
//		//printing transition probability
//		for(int i = 0; i < transi_probability.length; i++){
//			System.out.println("State: " + states[i]);
//			for(int j = 0; j < transi_probability.length; j++){
//				System.out.println("transi_probability are: " + states[j] + " " + transi_probability[i][j]);	
//			}
//		}
//		//printing emission probability
//		for(int i = 0; i < states.length; i++){
//			System.out.println("State: " + states[i]);
//			for(int j = 0; j < observations.length; j++){
//				System.out.println("emission_probability are: " + observations[j] + " " + emission_probability[i][j]);
//			}	
//		}
//		
//		ViterbiAlgo(states, start_probability, transi_probability, emission_probability);
//	}
}
