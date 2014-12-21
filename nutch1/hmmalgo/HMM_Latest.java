package hmmalgo;

public class HMM_Latest {
	
	public static void main(String args[]){
		
		int maxIterations = 3;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;
		
		double N = 2;//no of states say q, r
		double M = 6;//observations can be {eye, drops, off, shelf, table, chair}
		int T = 4;//length of observations {eye drops off shelf}
		int noofstates = 2;
		int noofobservations = 6;
		
		String[] states = new String[2];//states array {q, r}
		int[] observations = new int[T];//observations array
		
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
					states, observations);
		
		//the alpha-pass
		fwdprobs = calculateAlphaPass(T, noofstates, startprobs, transi_probs, emission_probs, observations, c);
		
		//the beta-pass
		bckprobs = calculateBetaPass(T, noofstates, startprobs, transi_probs, emission_probs, observations, c);
		
		//compute gammat(i,j) and gammat(i)
		gammaijt = computeGammaijt(fwdprobs, bckprobs, T, noofstates, transi_probs, emission_probs, observations);
		
		gammait = computeGammait(gammaijt, noofstates, T);
		
		//re-estimate A, B and start probs
		reestimateHMM(gammaijt, gammait, noofstates, noofobservations, T, observations);
		
		//compute log[P(O/HMM model lambda)]
		double logProb = 0;
		for(int i = 0; i <= T - 1; i++){
			logProb = logProb + Math.log(c[i]); 
		}
		logProb = -logProb;
		System.out.println("logProb => " + logProb);
		
		//to iterate or not to iterate
		iters++;
		if(iters < maxIterations && logProb > oldLogProb){
			oldLogProb = logProb;
			//the alpha-pass
			fwdprobs = calculateAlphaPass(T, noofstates, startprobs, transi_probs, emission_probs, observations, c);
			
			//the beta-pass
			bckprobs = calculateBetaPass(T, noofstates, startprobs, transi_probs, emission_probs, observations, c);
			
			//compute gammat(i,j) and gammat(i)
			gammaijt = computeGammaijt(fwdprobs, bckprobs, T, noofstates, transi_probs, emission_probs, observations);
			
			gammait = computeGammait(gammaijt, noofstates, T);
			
			//re-estimate A, B and start probs
			reestimateHMM(gammaijt, gammait, noofstates, noofobservations, T, observations);
		}
		else{
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
			for(int j = 0; j < states.length; j++){
				for(int k = 0; k < T; k++){
					System.out.println("Probabilities are: " + states[j] + " " + observations[k] + " " + emission_probs[j][k]);
				}
			}
		}
	}
	
	//Aim: To re-estimate start probs, transition probs and emission probs
	private static void reestimateHMM(double[][][] gammaijt, double[][] gammait, int noofstates, 
										int noofobservations, int T, int[] observations) {
		
		double[] new_startprobs = new double[noofstates];
		double[][] new_transi_probs = new double[noofstates][noofstates];
		double[][] new_emission_probs = new double[noofstates][noofobservations];
		
		//re-estimate start probs
		for(int i = 0; i <= noofstates-1; i++){
			new_startprobs[i] = gammait[i][0];
			System.out.println("new_startprobs[" + i + "] => " + new_startprobs[i]);
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
				System.out.println("new_transi_probs[" + i + "][" + j + "] => " + new_transi_probs[i][j]);
			}//end j
		}//end i
		
		//re-estimate emission probs
		for(int i = 0; i <= noofstates - 1; i++){
			for(int j = 0; j <= noofobservations - 1; j++){
				double numer = 0;
				double denom = 0;
				for(int t = 0; t <= T - 2; t++){
					if(observations[t] == j){
						numer = numer + gammait[i][t];
					}//end if
					denom = denom + gammait[i][t];
				}//end t
				new_emission_probs[i][j] = numer/denom;
				System.out.println("new_emission_probs[" + i + "][" + j + "] => " + new_emission_probs[i][j]);
			}//end j
		}//end i
	}

	//Aim: To compute gammait(i,t)
	private static double[][] computeGammait(double[][][] gammaijt, int noofstates, int T) {
		
		double[][] gammait = new double[noofstates][T];
		
		for(int t = 0; t <= T - 2; t++){
			for(int i = 0; i <= noofstates - 1; i++){
				gammait[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					gammait[i][t] = gammait[i][t] + gammaijt[i][j][t];
				}
				System.out.println("gammait[" + i + "]["+ t + "] => " + gammait[i][t]);
			}
		}
		return gammait;
	}

	//Aim: To compute gammat(i,j)
	private static double[][][] computeGammaijt(double[][] fwdprobs, double[][] bckprobs, 
			int T, int noofstates, double[][] transi_probs, double[][] emission_probs,
			int[] observations) {
		
		double[][][] gammaijt = new double[noofstates][noofstates][T];
		
		for(int t = 0; t <= T - 2; t++){
			double denom = 0;
			for(int i = 0; i <= noofstates - 1; i++){
				for(int j = 0; j <= noofstates - 1; j++){
					denom = denom + (fwdprobs[i][t] * transi_probs[i][j] * emission_probs[j][observations[t+1]] 
					                   * bckprobs[j][t+1]);
					System.out.println("denom => " + denom);
				}//end j
			}//end i
			for(int i = 0; i <= noofstates - 1; i++){
				//gammait[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					gammaijt[i][j][t] = (fwdprobs[i][t] * transi_probs[i][j] * emission_probs[j][observations[t+1]]
					                     * bckprobs[j][t+1])/denom;
					System.out.println("gammaijt[" + i + "][" + j + "]["+ t + "] => " + gammaijt[i][j][t]);
				}//end j
			}//end i
		}//end t
		return gammaijt;
	}

	//Aim: To calculate backward probabilities
	private static double[][] calculateBetaPass(int T, int noofstates, double[] startprobs,
									double[][] transi_probs, double[][] emission_probs,
									int[] observations, double[] c) {
		double[][] bckprobs = new double[noofstates][T];
		//let bckprobs[i][T-1] = 1 scaled by cT-1
		//double cTminus1 = 1;
		for(int i = 0; i <= noofstates - 1; i++){
			bckprobs[i][T-1] = c[T-1];
			System.out.println("bckprobs[" + i + "]["+ (T-1) + "] => " + bckprobs[i][T-1]);
		}
		
		//compute betapass
		for(int t = T - 2; t >= 0; t--){
			for(int i = 0; i <= noofstates - 1; i++){
				bckprobs[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					bckprobs[i][t] = bckprobs[i][t] + (transi_probs[i][j] * emission_probs[j][observations[t+1]] 
					                                    * bckprobs[j][t+1]);
				}//end j
				bckprobs[i][t] = c[t] * bckprobs[i][t];
				System.out.println("bckprobs[" + i + "]["+ t + "] => " + bckprobs[i][t]);
			}//end i
		}//end t
		return bckprobs;
	}	

	//Aim: To calculate forward probabilities
	private static double[][] calculateAlphaPass(int T, int noofstates, double[] startprobs,
									double[][] transi_probs, double[][] emission_probs,
									int[] observations, double[] c) {
		
		double[][] fwdprobs = new double[noofstates][T];
		//compute alpha0(i)
		c[0] = 0;
		for(int i = 0; i <= noofstates - 1; i++){
			fwdprobs[i][0] = startprobs[i] * emission_probs[i][observations[0]];
			c[0] = c[0] + fwdprobs[i][0];
		}
		
		//scale the alpha0(i)
		c[0] = 1/c[0];
		for(int i = 0; i <= noofstates - 1; i++){
			fwdprobs[i][0] = c[0] * fwdprobs[i][0];
			System.out.println("fwdprobs[" + i + "][0] => " + fwdprobs[i][0]);
		}
		
		//compute alphat(i)
		for(int t = 1; t <= T-1; t++){
			c[t] = 0;
			for(int i = 0; i <= noofstates - 1; i++){
				fwdprobs[i][t] = 0;
				for(int j = 0; j <= noofstates - 1; j++){
					fwdprobs[i][t] = fwdprobs[i][t] + (fwdprobs[j][t-1] * transi_probs[j][i]);
				}//end j
				fwdprobs[i][t] = fwdprobs[i][t] * emission_probs[i][observations[t]];
				c[t] += fwdprobs[i][t];	
			}//end i
			
			//scale alphat(i)
			c[t] = 1/c[t];
			for(int i = 0; i <= noofstates - 1; i++){
				fwdprobs[i][t] = c[t] * fwdprobs[i][t];
				System.out.println("fwdprobs[" + i +"]["+ t + "] => "+ fwdprobs[i][t]);
			}//end i
		}//end t
		
		return fwdprobs;
	}

	//Aim: To set the HMM model, initialize A, B and start_probs
	private static void initialize(double N, double M, int T, int noofstates, int noofobservations,
				double[] startprobs, double[][]transi_probs, double[][]emission_probs,
				String[] states, int[] observations) {
				
		
		states[0] = "q";//State 0
		states[1] = "r";//State 1
		
		
		observations[0] = 0;//"eye";//Obs 0
		observations[1] = 1;//"drops";// Obs 1
		observations[2] = 2;//"off";//Obs 2
		observations[3] = 3;//"shelf";//Obs 3
		
		
		for(int i = 0; i < N; i++){
			startprobs[i] = 1/N;
		}
		//printing start probability
		for(int i = 0; i < startprobs.length; i++){
			System.out.println("start_probability are: " + startprobs[i]);
		}
		
		
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				transi_probs[i][j] = 1/N;
			}
		}
		//printing transition probabilities
		for(int i = 0; i < transi_probs.length; i++){
			System.out.println("State: " + states[i]);
			for(int j = 0; j < transi_probs.length; j++){
				System.out.println("transi_probability are: " + states[j] + " " + transi_probs[i][j]);	
			}
		}
		
		
		for(int j = 0; j < states.length; j++){
			System.out.println("State: " + states[j]);
			for(int k = 0; k < T; k++){
				emission_probs[j][k] = 1/M;
			}	
		}
		//printing probabilities from state j for observation k
		for(int j = 0; j < states.length; j++){
			for(int k = 0; k < T; k++){
				System.out.println("Probabilities are: " + states[j] + " " + observations[k] + " " + emission_probs[j][k]);
			}
		}
		
	}
}


