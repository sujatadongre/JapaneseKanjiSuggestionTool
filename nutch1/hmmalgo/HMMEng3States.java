package hmmalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class HMMEng3States {
	
	public static void main(String args[]){
		
		int maxIterations = 100;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;
		
		double N = 3;//no of states say q, r
		double M = 27;//observations can be alphabets from a to j
		int noofstates = 3;
		int noofobservations = 27;
				
		ArrayList<Integer> opSeqList = generate_op_seq();
		
		int T = opSeqList.size();//length of observations
		System.out.println("T => " + T);
		
		String[] states = new String[3];//states array {q, r, s}
		states[0] = "q";//State 0
		states[1] = "r";//State 1
		states[2] = "s";//State 2
		
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
	}//end main
	
	
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
	
		corpusToRead = "A01.txt";
		opSeqList = readCorpusFile(corpusToRead);
		totalCharsRead += opSeqList.size();
			
		for(int i = 0; i < opSeqList.size(); i++){
			finalOutput.add(opSeqList.get(i));
		}
	
		if(totalCharsRead < 50000){
			corpusToRead = "H20.txt";
			opSeqList = readCorpusFile(corpusToRead);
			totalCharsRead += opSeqList.size();
		}
		for(int i = 0; i < opSeqList.size(); i++){
			finalOutput.add(opSeqList.get(i));
		}
		if(totalCharsRead < 50000){
			corpusToRead = "J18.txt";
			opSeqList = readCorpusFile(corpusToRead);
			totalCharsRead += opSeqList.size();
		}
		for(int i = 0; i < opSeqList.size(); i++){
			finalOutput.add(opSeqList.get(i));
		}
		if(totalCharsRead < 50000){
			corpusToRead = "R08.txt";
			opSeqList = readCorpusFile(corpusToRead);
			totalCharsRead += opSeqList.size();
		}
		for(int i = 0; i < opSeqList.size(); i++){
			finalOutput.add(opSeqList.get(i));
		}
		if(totalCharsRead < 50000){
			corpusToRead = "F06.txt";
			opSeqList = readCorpusFile(corpusToRead);
			totalCharsRead += opSeqList.size();
		}
			
		for(int i = 0; i < opSeqList.size(); i++){
			while(finalOutput.size() < 50000){
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
			
			File fileIn  = new File(corpusToRead);

			Scanner scanner = new Scanner(fileIn);
				
			char c;
			String line = null;
			String subLine = null;
			
			while (scanner.hasNextLine()){
				 boolean isCharBegins = false;
				 line = scanner.nextLine();
				 subLine = line.toLowerCase().substring(12, line.length());
				 	 
				 for(int i = 0; i < subLine.length(); i++){
					c = subLine.charAt(i);
					if(c == ' ' && isCharBegins == false){
						continue;
					}
					else{
						if((c >= 97 && c <= 122) || (c == 32)){
							isCharBegins = true;
							if(c == 32){
								opSeqList.add((int)(c - 6));
							}else
								opSeqList.add((int)(c - 97));
							}//end inner if
						else{
							isCharBegins = true;
						}
					}//end outer if
				}//end for
			 }//end while
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return opSeqList;		
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
			
		//initialization as per the paper
		//initialization as per the paper
		startprobs[0] = 0.27346;
		startprobs[1] = 0.31864;
		startprobs[2] = 0.4079;
		
		transi_probs[0][0] = 0.37468;
		transi_probs[0][1] = 0.42221;
		transi_probs[0][2] = 0.20311;
		
		transi_probs[1][0] = 0.31556;
		transi_probs[1][1] = 0.32724;
		transi_probs[1][2] = 0.3572;
		
		transi_probs[2][0] = 0.42465;
		transi_probs[2][1] = 0.27389;
		transi_probs[2][2] = 0.30146;
		
		emission_probs[0][0] = 0.03735;
		emission_probs[0][1] = 0.03408;
		emission_probs[0][2] = 0.03455;
		emission_probs[0][3] = 0.03828;
		emission_probs[0][4] = 0.03782;
		emission_probs[0][5] = 0.03922;
		emission_probs[0][6] = 0.03688;
		emission_probs[0][7] = 0.03408;
		emission_probs[0][8] = 0.03875;
		emission_probs[0][9] = 0.04062;
		emission_probs[0][10] = 0.03735;
		emission_probs[0][11] = 0.03968;
		emission_probs[0][12] = 0.03548;
		emission_probs[0][13] = 0.03735;
		emission_probs[0][14] = 0.04062;
		emission_probs[0][15] = 0.03595;
		emission_probs[0][16] = 0.03641;
		emission_probs[0][17] = 0.03408;
		emission_probs[0][18] = 0.04062;
		emission_probs[0][19] = 0.03548;
		emission_probs[0][20] = 0.03922;
		emission_probs[0][21] = 0.04062;
		emission_probs[0][22] = 0.03455;
		emission_probs[0][23] = 0.03595;
		emission_probs[0][24] = 0.03408;
		emission_probs[0][25] = 0.03408;
		emission_probs[0][26] = 0.03688;
		
		emission_probs[1][0] = 0.03909;
		emission_probs[1][1] = 0.03537;
		emission_probs[1][2] = 0.03537;
		emission_probs[1][3] = 0.03909;
		emission_probs[1][4] = 0.03583;
		emission_probs[1][5] = 0.03630;
		emission_probs[1][6] = 0.04048;
		emission_probs[1][7] = 0.03537;
		emission_probs[1][8] = 0.03816;
		emission_probs[1][9] = 0.03909;
		emission_probs[1][10] = 0.03490;
		emission_probs[1][11] = 0.03723;
		emission_probs[1][12] = 0.03537;
		emission_probs[1][13] = 0.03909;
		emission_probs[1][14] = 0.03397;
		emission_probs[1][15] = 0.03397;
		emission_probs[1][16] = 0.03816;
		emission_probs[1][17] = 0.03676;
		emission_probs[1][18] = 0.04048;
		emission_probs[1][19] = 0.03443;
		emission_probs[1][20] = 0.03537;
		emission_probs[1][21] = 0.03955;
		emission_probs[1][22] = 0.03816;
		emission_probs[1][23] = 0.03723;
		emission_probs[1][24] = 0.03769;
		emission_probs[1][25] = 0.03955;
		emission_probs[1][26] = 0.03397;
		
		emission_probs[2][0] = 0.03909;
		emission_probs[2][1] = 0.03537;
		emission_probs[2][2] = 0.03537;
		emission_probs[2][3] = 0.03909;
		emission_probs[2][4] = 0.03583;
		emission_probs[2][5] = 0.03630;
		emission_probs[2][6] = 0.04048;
		emission_probs[2][7] = 0.03537;
		emission_probs[2][8] = 0.03816;
		emission_probs[2][9] = 0.03909;
		emission_probs[2][10] = 0.03490;
		emission_probs[2][11] = 0.03723;
		emission_probs[2][12] = 0.03537;
		emission_probs[2][13] = 0.03909;
		emission_probs[2][14] = 0.03397;
		emission_probs[2][15] = 0.03397;
		emission_probs[2][16] = 0.03816;
		emission_probs[2][17] = 0.03676;
		emission_probs[2][18] = 0.04048;
		emission_probs[2][19] = 0.03443;
		emission_probs[2][20] = 0.03537;
		emission_probs[2][21] = 0.03955;
		emission_probs[2][22] = 0.03816;
		emission_probs[2][23] = 0.03723;
		emission_probs[2][24] = 0.03769;
		emission_probs[2][25] = 0.03955;
		emission_probs[2][26] = 0.03397;
		
		displayResults(startprobs, transi_probs, emission_probs, states,noofstates, noofobservations);
		
	}
}


