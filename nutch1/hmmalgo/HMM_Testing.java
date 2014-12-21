package hmmalgo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

import com.sun.corba.se.impl.ior.OldPOAObjectKeyTemplate;

public class HMM_Testing {
	
	public static void main(String args[]){
		
		int maxIterations = 3;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;
		
		double N = 2;//no of states say q, r
		double M = 3;//observations can be alphabets from a to j
		int noofstates = 2;
		int noofobservations = 3;
			
		//generate_op_seq();
		
		//ArrayList<Integer> opSeqList = read_op_seq();
		ArrayList<Integer> opSeqList = new ArrayList<Integer>(3);
		opSeqList.add(0);
		opSeqList.add(1);
		opSeqList.add(2);
		int T = opSeqList.size();//length of observations
		
		//System.out.println("opSeqList size => " + opSeqList.size());
	
		String[] states = new String[2];//states array {q, r}
		states[0] = "q";//State 0
		states[1] = "r";//State 1
		//int[] observations = new int[T];//observations array
		
		double[] startprobs = new double[noofstates];//Start probabilities 1 * N
		double[][] transi_probs = new double[noofstates][noofstates];// N * N
		double[][] emission_probs = new double[noofstates][noofobservations];//N * M
		
		double[] c = new double[T];
		
		double[][] fwdprobs;
		double[][] bckprobs;
		double[][][] gammaijt;
		double[][] gammait;
		
		double logProb = 0;
		
		
		//call initialization method
		initialize(N, M, T, noofstates, noofobservations, startprobs, transi_probs, emission_probs,
					states, opSeqList);
		
		while(iters < maxIterations){				
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
			for(int i = 0; i <= T - 1; i++){
				logProb = logProb + Math.log(c[i]); 
			}
			logProb = -logProb;
			System.out.println("logProb => " + logProb);
			
			if(logProb > oldLogProb){	
				oldLogProb = logProb;	
			}
			else{
				System.out.println("Probabilities are not getting any better hence stopping");
				break;
			}
			
			
			iters++;
			//display the matrices pi, A and B
			//displayResults(startprobs, transi_probs, emission_probs, states, noofstates, noofobservations);	
		}//end while	
	}//end main
	
	
	//Aim: To display matrices pi, A and B
	private static void displayResults(double[] startprobs, double[][] transi_probs, double[][] emission_probs, 
										String[] states, int noofstates, int noofobservations) {
		
		//log.info("displaying results........");
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

	//Aim: To read outputsequence file and store each element in an ArrayList
	private static ArrayList<Integer> read_op_seq() {
		ArrayList<Integer> opSeqList = new ArrayList<Integer>();
		try{
			Scanner s = null;
		    s = new Scanner(new BufferedReader(new FileReader("outputsequence.txt")));
			while (s.hasNext()) {
				//System.out.println(s.next());
				opSeqList.add(s.nextInt());
	        }
			s.close();  
		}catch (FileNotFoundException e) 
	     {
	         System.err.println("FileCopy: " + e);
	     }
	     return opSeqList;
	}

	//Aim: To generate output sequence
	//Read Brown Corpus file A01.txt for 50000 characters
	//Write corresponding values of alphabets and spaces to some other file say outputsequence.txt
	private static void generate_op_seq() {
		try{
			File fileIn  = new File("A01.txt");
			File fileOut = new File("outputsequence.txt");
						
			BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileIn),Charset.forName("UTF8")));
			Writer writer = new BufferedWriter(new FileWriter(fileOut));
			
			char c;
			
			int cntCharsRead = 0;
			Scanner scanner = new Scanner(fileIn);
			String line = null;
			String subLine = null;
			
			 while (scanner.hasNextLine()){
				 boolean isCharBegins = false;
				 line = scanner.nextLine();
				 //System.out.println("line => " + line.toLowerCase());
				 subLine = line.toLowerCase().substring(12, line.length());
				 //System.out.println("subLine =>" + subLine);
				 
				 
				 for(int i = 0; i < subLine.length(); i++){
					if(cntCharsRead <= 50000){
						c = subLine.charAt(i);
						if(c == ' ' && isCharBegins == false){
							continue;
						}
						else{
							if((c >= 97 && c <= 122) || (c == 32)){//reading only alphabets and space
								 //char ch = (char)c;
					        	 //System.out.println("char => " + ch);
								switch (c){
									case 97:
										writer.write(Constant.a);
										writer.write(" ");
										break;
									case 98:
										writer.write(Constant.b);
										writer.write(" ");
										break;
									case 99:
										writer.write(Constant.c);
										writer.write(" ");
										break;
									case 100:
										writer.write(Constant.d);
										writer.write(" ");
										break;
									case 101:
										writer.write(Constant.e);
										writer.write(" ");
										break;
									case 102:
										writer.write(Constant.f);
										writer.write(" ");
										break;
									case 103:
										writer.write(Constant.g);
										writer.write(" ");
										break;
									case 104:
										writer.write(Constant.h);
										writer.write(" ");
										break;
									case 105:
										writer.write(Constant.i);
										writer.write(" ");
										break;
									case 106:
										writer.write(Constant.j);
										writer.write(" ");
										break;
									case 107:
										writer.write(Constant.k);
										writer.write(" ");
										break;
									case 108:
										writer.write(Constant.l);
										writer.write(" ");
										break;
									case 109:
										writer.write(Constant.m);
										writer.write(" ");
										break;
									case 110:
										writer.write(Constant.n);
										writer.write(" ");
										break;
									case 111:
										writer.write(Constant.o);
										writer.write(" ");
										break;
									case 112:
										writer.write(Constant.p);
										writer.write(" ");
										break;
									case 113:
										writer.write(Constant.q);
										writer.write(" ");
										break;
									case 114:
										writer.write(Constant.r);
										writer.write(" ");
										break;
									case 115:
										writer.write(Constant.s);
										writer.write(" ");
										break;
									case 116:
										writer.write(Constant.t);
										writer.write(" ");
										break;
									case 117:
										writer.write(Constant.u);
										writer.write(" ");
										break;
									case 118:
										writer.write(Constant.v);
										writer.write(" ");
										break;
									case 119:
										writer.write(Constant.w);
										writer.write(" ");
										break;
									case 120:
										writer.write(Constant.x);
										writer.write(" ");
										break;
									case 121:
										writer.write(Constant.y);
										writer.write(" ");
										break;
									case 122:
										writer.write(Constant.z);
										writer.write(" ");
										break;
									case 32:
										writer.write(Constant.space);
										writer.write(" ");
										break;
									default:
										System.out.println("Character not found");
								}//end switch		
							}//end inner if
							isCharBegins = true;
							cntCharsRead++;
						}//end outer if
					}
				}//end for
			 }//end while
			
			reader.close();
			writer.close();
			scanner.close();
		}catch (FileNotFoundException e) 
	      {
	         System.err.println("FileCopy: " + e);
	      } 
	      catch (IOException e) 
	      {
	         System.err.println("FileCopy: " + e);
	      }
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
			//System.out.println("new_startprobs[" + i + "] => " + new_startprobs[i]);
		}
		startprobs = new_startprobs;
		
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
				//System.out.println("new_transi_probs[" + i + "][" + j + "] => " + new_transi_probs[i][j]);
			}//end j
		}//end i
		
		transi_probs = new_transi_probs;
		
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
				//System.out.println("new_emission_probs[" + i + "][" + j + "] => " + new_emission_probs[i][j]);
			}//end j
		}//end i
		
		emission_probs = new_emission_probs;
		displayResults(startprobs, transi_probs, emission_probs, states,noofstates, noofobservations);
	}

	//Aim: To compute gammait(i,t)
	private static double[][] computeGammait(double[][][] gammaijt, int noofstates, int T) {
		
		double[][] gammait = new double[noofstates][T-1];
		
		for(int t = 0; t <= T - 2; t++){
			for(int i = 0; i <= noofstates - 1; i++){
				gammait[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					gammait[i][t] = gammait[i][t] + gammaijt[i][j][t];
				}
				//System.out.println("gammait[" + i + "]["+ t + "] => " + gammait[i][t]);
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
			for(int i = 0; i <= noofstates - 1; i++){
				for(int j = 0; j <= noofstates - 1; j++){
					denom = denom + (fwdprobs[i][t] * transi_probs[i][j] * emission_probs[j][observations.get(t+1)] 
					                   * bckprobs[j][t+1]);
					//System.out.println("denom => " + denom);
				}//end j
			}//end i
			for(int i = 0; i <= noofstates - 1; i++){
				//gammait[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					gammaijt[i][j][t] = (fwdprobs[i][t] * transi_probs[i][j] * emission_probs[j][observations.get(t+1)]
					                     * bckprobs[j][t+1])/denom;
					//System.out.println("gammaijt[" + i + "][" + j + "]["+ t + "] => " + gammaijt[i][j][t]);
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
		for(int i = 0; i <= noofstates - 1; i++){
			bckprobs[i][T-1] = c[T-1];
			//System.out.println("bckprobs[" + i + "]["+ (T-1) + "] => " + bckprobs[i][T-1]);
		}
		
		//compute betapass
		for(int t = T - 2; t >= 0; t--){
			for(int i = 0; i <= noofstates - 1; i++){
				bckprobs[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					bckprobs[i][t] = bckprobs[i][t] + (transi_probs[i][j] * emission_probs[j][observations.get(t+1)] 
					                                    * bckprobs[j][t+1]);
				}//end j
				bckprobs[i][t] = c[t] * bckprobs[i][t];
			}//end i
		}//end t
		
		return bckprobs;
	}	

	private static double[][] calculateAlphaPass(int T, int noofstates, double[] startprobs,
			double[][] transi_probs, double[][] emission_probs,
			ArrayList<Integer> observations, double[] c) {

		double[][] fwdprobs = new double[noofstates][T];
			
		c[0] = 0;
		//compute alpha0(i)
		for(int i = 0; i <= noofstates - 1; i++){
			fwdprobs[i][0] = startprobs[i] * emission_probs[i][observations.get(0)];
			c[0] += fwdprobs[i][0];
		}
		
		c[0] = 1/c[0];
		for(int i = 0; i <= noofstates - 1; i++){
			fwdprobs[i][0] *= c[0];	
		}
		
		//compute alphat(i)
		for(int t = 1; t <= T-1; t++){
			c[t] = 0;
			for(int i = 0; i <= noofstates - 1; i++){
				fwdprobs[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					fwdprobs[i][t] = fwdprobs[i][t] + (fwdprobs[j][t-1] * transi_probs[j][i]);
				}//end j
				fwdprobs[i][t] = fwdprobs[i][t] * emission_probs[i][observations.get(t)];
				c[t] += fwdprobs[i][t];
			}//end i	
			
			c[t] = 1/c[t];
			for(int i = 0; i <= noofstates - 1; i++){
				fwdprobs[i][t] *= c[t];
			}
			
		}//end t
		
		return fwdprobs;
	}

	//Aim: To set the HMM model, initialize A, B and start_probs
	private static void initialize(double N, double M, int T, int noofstates, int noofobservations,
				double[] startprobs, double[][]transi_probs, double[][]emission_probs,
				String[] states, ArrayList<Integer> observations) {
				
		startprobs[0] = 0.6;
		startprobs[1] = 0.4;
		
		transi_probs[0][0] = 0.7;
		transi_probs[0][1] = 0.3;
		transi_probs[1][0] = 0.4;
		transi_probs[1][1] = 0.6;
		
		emission_probs[0][0] = 0.1;
		emission_probs[0][1] = 0.4;
		emission_probs[0][2] = 0.5;
		emission_probs[1][0] = 0.7;
		emission_probs[1][1] = 0.2;
		emission_probs[1][2] = 0.1;

		displayResults(startprobs, transi_probs, emission_probs, states,noofstates, noofobservations);
		
	}
}


