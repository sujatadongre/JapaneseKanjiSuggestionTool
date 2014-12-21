package hmmalgo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HMM_Parse_Jap implements Serializable{
	
	public HMM_Parse_Jap(){
		createHMM();
	}
	
	public void createHMM(){
		
		//System.out.println("qry => " + qry);
		
		//int maxIterations = 100;
		int maxIterations = Constant.MAX_ITERATIONS;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;
		
		//double N = 2;//no of states say q, r
		double N = Constant.NO_OF_STATES;
		//double M = 191;//observations can be alphabets from a to j
		double M = Constant.NO_OF_OBS;
		//int noofstates = 2;
		int noofstates = Constant.INT_NO_OF_STATES; 
		//int noofobservations = 191;
		int noofobservations = Constant.INT_NO_OF_OBS;
				
		ArrayList<Integer> opSeqList = generate_op_seq();
		
		int T = opSeqList.size();//length of observations
		System.out.println("T => " + T);
		
		String[] states = new String[Constant.INT_NO_OF_STATES];
		switch(Constant.INT_NO_OF_STATES){
			case 2:
				//states array {q, r}
				states[0] = "q";//State 0
				states[1] = "r";//State 1
				break;
			case 3:
				states[0] = "q";//State 0
				states[1] = "r";//State 1
				states[2] = "s";//State 2
				break;
			case 4:
				states[0] = "q";//State 0
				states[1] = "r";//State 1
				states[2] = "s";//State 2
				states[3] = "t";//State 3
				break;
			default:
				System.out.println("Problems creating states array");
				break;
		}
		
		double[] startprobs = new double[noofstates];//Start probabilities 1 * N
		double[][] transi_probs = new double[noofstates][noofstates];// N * N
		double[][] emission_probs = new double[noofstates][noofobservations];//N * M
		
		double[] c = new double[T];
		
		double[][] fwdprobs;
		double[][] bckprobs;
		double[][][] gammaijt;
		double[][] gammait;
		
		//call initialization method
		initialize(N, M, T, noofstates, noofobservations, startprobs, transi_probs, emission_probs,
					states, opSeqList);
		
		while(iters < maxIterations){	
			System.out.println("Going for => " + iters +"th iteration");
				
			//the alpha-pass
			fwdprobs = calculateAlphaPass(T, noofstates, startprobs, transi_probs, emission_probs, opSeqList, c);
			
			//the beta-pass
			bckprobs = calculateBetaPass(T, noofstates, startprobs, transi_probs, emission_probs, opSeqList, c);
			
			//compute gammat(i,j) and gammat(i)
			gammaijt = computeGammaijt(fwdprobs, bckprobs, T, noofstates, transi_probs, emission_probs, opSeqList);
				
			gammait = computeGammait(gammaijt, noofstates, T);
						
			//re-estimate A, B and start probs
			reestimateHMM(gammaijt, gammait, noofstates, noofobservations, T, opSeqList,
						startprobs, transi_probs, emission_probs, states);
			
			//compute log[P(O/HMM model lambda)]
			double logProb = 0;
			for(int i = 0; i < T; i++){
				//System.out.println("i => " + i);
				logProb = logProb + (Math.log(c[i])/Math.log(2)); 
			}
			logProb = -logProb;
			System.out.println("logProb => " + logProb);
			System.out.println("oldLogProb => " + oldLogProb);
			
			if(logProb > oldLogProb){	
				oldLogProb = logProb;	
			}
			else{
				System.out.println("Probabilities are not getting any better hence stopping");
				break;
			}
			iters++;	
		}//end while
		
		//here end of iterations
		//now I want to store the final emission probability matrix
		//so that other experiments can make use of it
		//storeHMM(states, startprobs, transi_probs, emission_probs);
		
		/**Code added for calling alpha-pass
		 */
		//Start-alpha pass code
		System.out.println("Please enter input =>");
		Scanner inputWindow = new Scanner(System.in);
		String userStrWindow = inputWindow.nextLine();
		System.out.println(userStrWindow);
		AlphaPass.calculateFwdProbs(T, noofstates, startprobs, transi_probs, emission_probs, opSeqList, userStrWindow);
		//End-alpha pass code
	}
	
	public void storeHMM(String[] states, double[] startprobs, double[][] transi_probs, double[][] emission_probs){
	  //HMM_Parse_Jap h = new HMM_Parse_Jap();
	  //String myStr = h.parseJapText("あなむ");
	 //System.out.println("Did you mean: " + myStr);
	  
	  String filename = Constant.SERIAL_FILE_PATH;
	  List<Object> listFinalProbs = new ArrayList<Object>();

	  FileOutputStream fos = null;
	  ObjectOutputStream out = null;
     try
     {
       fos = new FileOutputStream(filename);
       out = new ObjectOutputStream(fos);
       listFinalProbs.add(states);
       listFinalProbs.add(startprobs);
       listFinalProbs.add(transi_probs);
       listFinalProbs.add(emission_probs);
       out.writeObject(listFinalProbs);
       out.flush();
       out.close();
     }
     catch(IOException ex)
     {
       ex.printStackTrace();
     }
	}
			
		//Viterbi_First_Append.ViterbiAlgo(states, startprobs, transi_probs, emission_probs, userStr);
	
		//call dictionary window program
//		HashMap<String, Integer> hmDicto = DictionaryWindow_New.createDicto();
//		//accept input from user
//		System.out.println("Please enter input of length 2 =>");
//		Scanner inputWindow = new Scanner(System.in);
//		String userStrWindow = inputWindow.nextLine();
//		System.out.println(userStrWindow);
//		userStrWindow = "あな";
//		//search if this user input exists in hmDicto with positive count
//		//if yes
//		if(hmDicto.containsKey(userStrWindow)){
//			//check if count is +ve
//			Integer cnt = (Integer) hmDicto.get(userStrWindow);
//			System.out.println("cnt => " + cnt);
//			if(cnt < 0){
//				//call HMM
//				//HMM is ready
//				Viterbi_Last_Append.ViterbiAlgo(states, startprobs, transi_probs, emission_probs, userStrWindow);				
//			}else{
//				System.out.println("Your input is correct. Do not have any other suggestions");
//			}
//		}else{//if no
//			System.out.println("Hmm... characters not found in the hashmap");
//		}	
		//return resultStr;
	//}//end main
	
	
	//Aim: To display matrices pi, A and B
	private static void displayResults(double[] startprobs, double[][] transi_probs, double[][] emission_probs, 
										String[] states, int noofstates, int noofobservations) {
		
		//printing start probability
		for(int i = 0; i < startprobs.length; i++){
			System.out.println("start_probability are: " + startprobs[i]);
		}
		
		//printing transition probabilities
		for(int i = 0; i < transi_probs.length; i++){
			System.out.println("State: " + states[i]);
			for(int j = 0; j < transi_probs.length; j++){
				System.out.println("transi_probability are: " + states[j] + " " + transi_probs[i][j]);	
			}
		}
		
		//printing probabilities from state j for observation k
		for(int j = 0; j <= noofstates - 1; j++){
			for(int k = 0; k <= noofobservations - 1; k++){
				//System.out.println("Probabilities are: " + states[j] + " " + observations.get(k) + " " + emission_probs[j][k]);
				System.out.println("Probabilities are: " + states[j] + " " + k + " " + emission_probs[j][k]);
			}
		}
		
	}
	
	//Aim: To generate output sequence
	//Read Brown Corpus file A01.txt for 50000 characters
	//Write corresponding values of alphabets and spaces to some other file say outputsequence.txt
	private static ArrayList<Integer> generate_op_seq() {
		
		int totalCharsRead = 0;
		String corpusToRead = new String();
		ArrayList<Integer> opSeqList = new ArrayList<Integer>();
		ArrayList<Integer> finalOutput = new ArrayList<Integer>();
		
		//corpusToRead = "TanakaCorpus-JP_New.txt";
		corpusToRead = "TanakaCorpus-JP_New.txt";
		//corpusToRead = "JP_New.txt";
		opSeqList = readCorpusFile(corpusToRead);
		totalCharsRead += opSeqList.size();
			
		for(int i = 0; i < opSeqList.size(); i++){
			if(finalOutput.size() > Constant.FINAL_OP_SIZE){
				break;
			}
			else{
				finalOutput.add(opSeqList.get(i));
			}
		}
		System.out.println("totalCharsRead => " + totalCharsRead);
		System.out.println("finalOutput => " + finalOutput.size());
		return finalOutput;
	}
	
	//Aim: To read the corpus file and return no. of characters read 
	private static ArrayList<Integer> readCorpusFile(String corpusToRead) {
		
		ArrayList<Integer> opSeqList = new ArrayList<Integer>();
		try {
			File file = new File(Constant.FILE_PATH);
			System.out.println(file.getPath());
			FileInputStream fstream = new FileInputStream(file);
			
		    // Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			InputStreamReader isr = new InputStreamReader(fstream, "UTF8");
    		BufferedReader br = new BufferedReader(isr);
    		String defaultEncoding = isr.getEncoding();
    		System.out.println("defaultEncoding => " + defaultEncoding);
			
			//File fileIn  = new File("C:\\programDownloaded\\apache-tomcat-5.5.30\\apache-tomcat-5.5.30\\webapps\\ROOT\\TanakaCorpus-JP_New.txt");
			//System.out.println("checking length => " + fileIn.length());
			//Scanner scanner = new Scanner(fileIn);
			//System.out.println("checking scanner => " + scanner.hasNext() + " " + opSeqList.size());
			//char c;
    		String c;
			String line = null;
					
			while (((line = br.readLine()) != null) && (opSeqList.size() < Constant.TOTAL_NO_OF_OBS)){
				//System.out.println("in while loop");
				//line = scanner.nextLine();
				//System.out.println("line => " + line);	 
				 
				for(int i = 0; i < line.length(); i++){
					c = line.substring(i, i+1);
					//System.out.println("reading c => " + c);
					if(isKanji(c.charAt(0))){
						opSeqList.add(1);
					}
					else
					if(isHiragana(c.charAt(0))){
						//System.out.println("testing => " + c + ((int)c - (int)'\u3040'));
						opSeqList.add((int)(c.charAt(0) - '\u3040'));
					}
					else
					if(isKatakana(c.charAt(0))){
						//System.out.println("testing => " + c + ((int)c - (int)'\u30a0'));
						//System.out.println("testing => " + c + ((int)c - (int)'\u3040'));
						//opSeqList.add((int)(c - '\u30a0'));
						opSeqList.add((int)(c.charAt(0) - '\u3040'));
					}else{
						//do nothing ignore that character
					}
				}//end for
			}
			//scanner.close();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e){//Catch exception if any
  			System.err.println("Error: " + e.getMessage());
		}
		return opSeqList;
		
	}

	private static boolean isKatakana(char c) {
		//return (isHalfwidthKatakana(c) || isFullwidthKatakana(c));
		return (isFullwidthKatakana(c));
	}

//	private static boolean isHalfwidthKatakana(char c) {
//		if (!(('\uff66' <= c) && (c <= '\uff9d')))
//        {
//            return false;
//        }
//		return true;
//	}

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


	//Aim: To re-estimate start probs, transition probs and emission probs
	private static void reestimateHMM(double[][][] gammaijt, double[][] gammait, int noofstates, 
										int noofobservations, int T, ArrayList<Integer> observations,
										double[] startprobs, double[][] transi_probs, double[][] emission_probs,
										String[] states) {
		
		
		double[] new_startprobs = new double[noofstates];
		double[][] new_transi_probs = new double[noofstates][noofstates];
		double[][] new_emission_probs = new double[noofstates][noofobservations];
		
		//re-estimate start probs
		for(int i = 0; i <= noofstates-1; i++){
			new_startprobs[i] = gammait[i][0];
		}
		
		//re-estimate transition probs
		for(int i = 0; i <= noofstates - 1; i++){
			for(int j = 0; j <= noofstates - 1; j++){
				double numer = 0;
				double denom = 0;
				for(int t = 0; t <= T - 2; t++){
					numer = numer + gammaijt[i][j][t];
					denom = denom + gammait[i][t];
				}//end t
				new_transi_probs[i][j] = numer/denom;
			}//end j
		}//end i
		
		//re-estimate emission probs
		for(int i = 0; i <= noofstates - 1; i++){
			for(int j = 0; j <= noofobservations - 1; j++){
				double numer = 0;
				double denom = 0;
				for(int t = 0; t <= T - 2; t++){
					if(observations.get(t) == j){
						numer = numer + gammait[i][t];
					}//end if
					denom = denom + gammait[i][t];
				}//end t
				new_emission_probs[i][j] = numer/denom;
			}//end j
		}//end i
		
		//call the copy function
		//whr old array values are replaced by the new values
		//copy start probs
		copyToOneDArray(startprobs, new_startprobs);
		//copy transi probs
		copyToTwoDArrayTransi(transi_probs, new_transi_probs);
		//copy emissi probs
		copyToTwoDArrayEmission(emission_probs, new_emission_probs, noofstates, noofobservations);
		
		displayResults(startprobs, transi_probs, emission_probs, states,noofstates, noofobservations);
	}

	//copy new transi probs to old transi probs array
	private static void copyToTwoDArrayTransi(double[][] transi_probs,
			double[][] new_transi_probs) {
		for(int i = 0; i < transi_probs.length; i++){
			for(int j = 0; j < transi_probs.length; j++){
				transi_probs[i][j] = new_transi_probs[i][j];
			}
		}
	}

	//copy new emissi probs to old emissi probs array
	private static void copyToTwoDArrayEmission(double[][] oldProbs,
			double[][] newProbs, int statesLength, int obsLength) {
		for(int i = 0; i < statesLength; i++){
			for(int j = 0; j < obsLength; j++){
				oldProbs[i][j] = newProbs[i][j];
			}
		}
		
	}

	//copy new start probs to old start probs array
	private static void copyToOneDArray(double[] startprobs,
			double[] new_startprobs) {
		for(int i = 0; i < startprobs.length; i++){
			startprobs[i] = new_startprobs[i];
		}
	}


	//Aim: To compute gammait(i,t)
	private static double[][] computeGammait(double[][][] gammaijt, int noofstates, int T) {
		
		double[][] gammait = new double[noofstates][T-1];
		
		for(int t = 0; t <= T - 2; t++){
			for(int i = 0; i < noofstates; i++){
				gammait[i][t] = 0;
				for(int j = 0; j < noofstates; j++){
					gammait[i][t] = gammait[i][t] + gammaijt[i][j][t];
				}
			}
		}
		return gammait;
	}

	//Aim: To compute gammat(i,j)
	private static double[][][] computeGammaijt(double[][] fwdprobs, double[][] bckprobs, 
			int T, int noofstates, double[][] transi_probs, double[][] emission_probs,
			ArrayList<Integer> observations) {
		
		double[][][] gammaijt = new double[noofstates][noofstates][T-1];
		
		for(int t = 0; t <= T - 2; t++){
			double denom = 0;
			for(int i = 0; i < noofstates; i++){
				for(int j = 0; j < noofstates; j++){
					denom = denom + (fwdprobs[i][t] * transi_probs[i][j] * emission_probs[j][observations.get(t+1)] 
					                   * bckprobs[j][t+1]);
				}//end j
			}//end i
			for(int i = 0; i < noofstates; i++){
				for(int j = 0; j < noofstates; j++){
					gammaijt[i][j][t] = (fwdprobs[i][t] * transi_probs[i][j] * emission_probs[j][observations.get(t+1)]
					                     * bckprobs[j][t+1])/denom;
				}//end j
			}//end i
		}//end t
		return gammaijt;
	}

	//Aim: To calculate backward probabilities
	private static double[][] calculateBetaPass(int T, int noofstates, double[] startprobs,
									double[][] transi_probs, double[][] emission_probs,
									ArrayList<Integer> observations, double[] c) {
		double[][] bckprobs = new double[noofstates][T];
		//let bckprobs[i][T-1] = 1 scaled by cT-1
		for(int i = 0; i < noofstates; i++){
			bckprobs[i][T-1] = c[T-1];
		}
		
		//compute betapass
		for(int t = T - 2; t >= 0; t--){
			for(int i = 0; i < noofstates; i++){
				bckprobs[i][t] = 0;
				for(int j = 0; j < noofstates; j++){
					bckprobs[i][t] = bckprobs[i][t] + (transi_probs[i][j] * emission_probs[j][observations.get(t+1)] 
					                                    * bckprobs[j][t+1]);
				}//end j
				bckprobs[i][t] = c[t] * bckprobs[i][t];
			}//end i
		}//end t
		return bckprobs;
	}	

	//Aim: To calculate forward probabilities
	private static double[][] calculateAlphaPass(int T, int noofstates, double[] startprobs,
									double[][] transi_probs, double[][] emission_probs,
									ArrayList<Integer> observations, double[] c) {
		
		double[][] fwdprobs = new double[noofstates][T];
		
		//compute alpha0(i)
		c[0] = 0;
		for(int i = 0; i <= noofstates - 1 ; i++){
			fwdprobs[i][0] = startprobs[i] * emission_probs[i][observations.get(0)];
			c[0] = c[0] + fwdprobs[i][0];
		}
		
		//scale the alpha0(i)
		c[0] = 1/c[0];
		for(int i = 0; i < noofstates; i++){
			fwdprobs[i][0] = c[0] * fwdprobs[i][0];
		}
		
		//compute alphat(i)
		for(int t = 1; t < T; t++){
			c[t] = 0;
			for(int i = 0; i < noofstates; i++){
				fwdprobs[i][t] = 0;
				for(int j = 0; j < noofstates; j++){
					fwdprobs[i][t] = fwdprobs[i][t] + (fwdprobs[j][t-1] * transi_probs[j][i]);
				}//end j
				fwdprobs[i][t] = fwdprobs[i][t] * emission_probs[i][observations.get(t)];
				c[t] += fwdprobs[i][t];	
			}//end i
			
			//scale alphat(i)
			c[t] = 1/c[t];
			for(int i = 0; i < noofstates; i++){
				fwdprobs[i][t] = c[t] * fwdprobs[i][t];
			}//end i			
		}//end t	
		return fwdprobs;
	}

	//Aim: To set the HMM model, initialize A, B and start_probs
	private static void initialize(double N, double M, int T, int noofstates, int noofobservations,
				double[] startprobs, double[][]transi_probs, double[][]emission_probs,
				String[] states, ArrayList<Integer> observations) {
		
		
//		for(int j = 0; j <= noofstates - 1; j++){
//			//System.out.println("State: " + states[j]);
//			for(int k = 0; k <= noofobservations - 1; k++){
//				//double number = startRange +((double)rndGen.nextDouble()*(endRange-startRange));
//				emission_probs[j][k] = (1/M);
//				//System.out.println("j = " + j + " k = " + k + " prob = " + emission_probs[j][k]);
//			}	
//		}
//		
//		//initialization as per the paper
//		startprobs[0] = 0.51316;
//		startprobs[1] = 0.48684;
//		
//		transi_probs[0][0] = 0.47468;
//		transi_probs[0][1] = 0.52532;
//		transi_probs[1][0] = 0.51656;
//		transi_probs[1][1] = 0.48344;
		
		startprobs = generate_start_probs(N, noofstates, startprobs);
		transi_probs = generate_transi_probs(M, N, noofstates, noofobservations, transi_probs);
		emission_probs = generate_emission_probs(M, noofstates, noofobservations, emission_probs);
				
		displayResults(startprobs, transi_probs, emission_probs, states,noofstates, noofobservations);	
	}
	
	//this method is used to generate start probabilities at random
	private static double[] generate_start_probs(double N, int noofstates, double[] startprobs) {
		
		double startRange = 0;
		double endRange = 1/N;
		
		double[] arr = new double[noofstates];
		
		//initialize the array
		for(int i = 0; i < noofstates; i++){
			startprobs[i] = 1/N;
		}
		
		for(int k = 0; k < noofstates; k++){
			arr[k] = 1/N;
		}
		
		Random rndGen = new Random();
		for(int i = 0; i < noofstates; i++){
			//System.out.println("i = " + i);
			int j = rndGen.nextInt(noofstates);//pick j randomly
			//System.out.println("j = " + j);
			
			//randomly generate some epsilon value
			double number = startRange +((double)rndGen.nextDouble()*(endRange-startRange));
			//System.out.println("number => " + number);
			arr[i] = arr[i] + number;//add it to i
			//System.out.println("i_number => " + arr[i]);
			arr[j] = arr[j] - number;//subtract it from j
			//System.out.println("j_number => " + arr[j]);
			
			double temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
		//change random number generator seed
		rndGen.setSeed(System.currentTimeMillis());
			
		//assign arr values to emission prob matrix
		for(int k = 0; k < noofstates; k++){	
			startprobs[k] = arr[k];
			//System.out.println("startprobs["+k+"]=> " + startprobs[k]);
		}
		return startprobs;
	}

	//this method is used to generate transition probabilities at random
	private static double[][] generate_transi_probs(double M, double N,
			int noofstates, int noofobservations, double[][] transi_probs) {
		double startRange = 0;
		double endRange = 1/(N * N);
		
		double[][] arr = new double[noofstates][noofstates];
		
		//initialize the array
		for(int i = 0; i < noofstates; i++){
			for(int j = 0; j < noofstates; j++){
				transi_probs[i][j] = 1/N;
			}
		}
		
		for(int i = 0; i < noofstates; i++){
			for(int l = 0; l < noofstates; l++){
				arr[i][l] = 1/N; 
			}
		}
		
		Random rndGen = new Random();
		
		for(int i = 0; i < noofstates; i++){
			//System.out.println("i = " + i);
			int j = rndGen.nextInt(noofstates);//pick j randomly
			//System.out.println("j = " + j);
				
			//randomly generate some epsilon value
			double number = startRange +((double)rndGen.nextDouble()*(endRange-startRange));
				
			arr[i][i] = arr[i][i] - number;//add it to i
			arr[i][j] = arr[i][j] + number;//subtract it from j
			arr[j][j] = arr[j][j] - number;
			arr[j][i] =  arr[j][i] + number;
		}
		//change random number generator seed
		rndGen.setSeed(System.currentTimeMillis());
			
		//assign arr values to emission prob matrix
		for(int k = 0; k < noofstates; k++){
			for(int l = 0; l < noofstates; l++){
				transi_probs[k][l] = arr[k][l];
				//System.out.println("transi_probs["+k+"]["+l+"] => " + transi_probs[k][l]);
			}
		}
		return transi_probs;
	}

	//this method is used to generate emission probabilities at random
	private static double[][] generate_emission_probs(double M, int noofstates,
			int noofobservations, double[][] emission_probs) {
		double startRange = 0;
		double endRange = 1/(M * M);
		
		double[] arr = new double[noofobservations];
		
		//initialize the array
		for(int i = 0; i < noofstates; i++){
			for(int j = 0; j < noofobservations; j++){
				emission_probs[i][j] = 1/M;
				//System.out.println("emission_probs[" + i +"][" +j + "] => " + emission_probs[i][j]);
			}
		}
		
		for(int k = 0; k < noofobservations; k++){
			arr[k] = 1/M;
		}
		
		Random rndGen = new Random();
		
		for(int s = 0; s < noofstates; s++){
			//iterate through i	
			for(int i = 0; i < noofobservations; i++){
				//System.out.println("i = " + i);
				int j = rndGen.nextInt(noofobservations);//pick j randomly [0,5]
				//System.out.println("j = " + j);
				
				//randomly generate some epsilon value
				double number = startRange +((double)rndGen.nextDouble()*(endRange-startRange));
				//System.out.println("number => " + number);
				arr[i] = arr[i] + number;//add it to i
				//System.out.println("i_number => " + arr[i]);
				arr[j] = arr[j] - number;//subtract it from j
				//System.out.println("j_number => " + arr[j]);
				
				double temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
			}
			//change random number generator seed
			//rndGen.setSeed(19580427);
			rndGen.setSeed(System.currentTimeMillis());
			//assign arr values to emission prob matrix
			for(int k = 0; k < noofobservations; k++){
				emission_probs[s][k] = arr[k];
				//System.out.println("emission_probs["+s+"]["+k+"] => " + emission_probs[s][k]);
			}
		}
		return emission_probs;
	}
	
	
}


