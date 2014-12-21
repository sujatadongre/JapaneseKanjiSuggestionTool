package hmmalgo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlphaPass {
	
	
	public static String calculateFwdProbs(int T, int noofstates, double[] startprobs,
			double[][] transi_probs, double[][] emission_probs,
			ArrayList<Integer> observations, String userStr) {
		
		String suggestedString = null;
		//create an array for 192 characters
		String[] arrJapChars = new String[143];
		double[] scalec = new double[T];
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
		if(arrJapChars!= null && arrJapChars.length > 0){
			double[][] fwdprobs = new double[noofstates][T];
			double totalProb = 0;
			
			for(int a = 0; a < arrJapChars.length; a++){
				
				//if already any other character is appended at the end,
				//in the next loop, remove that previously appended character and append new one
				if(isFirstLoop == false){
					obs.remove(obs.size()-1);
				}
				obs.add(arrJapChars[a]);
					
				int char_index = 0;		
				//compute alpha0(i)
				scalec[0] = 0;
				for(int i = 0; i <= noofstates - 1 ; i++){
					fwdprobs[i][0] = startprobs[i] * emission_probs[i][observations.get(0)];
					scalec[0] = scalec[0] + fwdprobs[i][0];
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
					
					
					
					//scale the alpha0(i)
					scalec[0] = 1/scalec[0];
					for(int i = 0; i < noofstates; i++){
						fwdprobs[i][0] = scalec[0] * fwdprobs[i][0];
					}
					
					//compute alphat(i)
					for(int t = 1; t < T; t++){
						scalec[t] = 0;
						for(int i = 0; i < noofstates; i++){
							fwdprobs[i][t] = 0;
							for(int j = 0; j < noofstates; j++){
								fwdprobs[i][t] = fwdprobs[i][t] + (fwdprobs[j][t-1] * transi_probs[j][i]);
							}//end j
							fwdprobs[i][t] = fwdprobs[i][t] * emission_probs[i][observations.get(t)];
							scalec[t] += fwdprobs[i][t];	
							totalProb += fwdprobs[i][t];
						}//end i
						
						//scale alphat(i)
						scalec[t] = 1/scalec[t];
						for(int i = 0; i < noofstates; i++){
							fwdprobs[i][t] = scalec[t] * fwdprobs[i][t];
							System.out.println("fwdprobs[i][t] => " + fwdprobs[i][t]);
						}//end i						
					}//end t	
					
					System.out.println("Summation probs = > " + totalProb);
				}
				
				htJapChars.put(currentObs, totalProb);
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
			
			for(int l = myArray.length-1; l >= 0; l--){
				suggestedString = new String();
				for(int i = 0; i < obs.size()-1; i++){
					suggestedString += obs.get(i);
				}
				suggestedString += ((Map.Entry)myArray[l]).getKey();
				int isCharFound = KanjiSearch.searchCharacter(suggestedString);
				if(isCharFound == 1){
					System.out.println("**********1. もうしかして? :" + suggestedString);
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

}
