package hmmalgo;

import java.text.DecimalFormat;

public class HMMMain {
	public int noofstates;
	public int noofobservations;
	public String[] states;
	public String[] observations;
	public double[] startprobs;
	public double[][] transi_probs;
	public double[][] emission_probs;
	public double[][][] probs_for_obs;//3D array to store transition probabilities from state i to state j for observation k
	
	
//	this.states = new String[]{"Rainy", "Sunny"};
//	this.observations = new String[]{"walk", "shop", "clean"};
//	
//	this.startprobs = new double[]{0.6, 0.4};
//	
//	this.transi_probs = new double[][]{{0.7, 0.3}, {0.4, 0.6}};
//	this.emission_probs = new double[][]{{0.1, 0.4, 0.5}, {0.6, 0.3,0.1}};
	
	public HMMMain(int noofstates, int noofobservations){
		this.noofstates = noofstates;
		this.noofobservations = noofobservations;
		
		states = new String[noofstates];
		observations = new String[noofobservations];
		
		startprobs = new double[noofstates];
		transi_probs = new double[noofstates][noofstates];
		emission_probs = new double[noofstates][noofobservations];
		probs_for_obs = new double[noofstates][noofstates][noofobservations];
		
	}
	
	/**
	 * @param observations String Array
	 * @return fwdprobs Double Arrray
	 * To calculate forward probabilities and return forward probability matrix
	*/
	public double[][] forward_viterbi(String[] observations){
		
		double[][] fwdprobs = new double[noofstates][observations.length+1];
		
		for(int i = 0; i < states.length; i++){
			fwdprobs[i][0] = startprobs[i];
		}
		
		//for each observation in observation sequence
		for(int i = 0; i < observations.length; i++){
					
			//for next_state in states array
			for(int j = 0; j < states.length; j++){
				double total = 0;
				//for current state in states array
				for(int k = 0; k < states.length; k++){
					
					//double p = fwdprobs[k][i] * emission_probs[k][i] * transi_probs[k][j];
					double p = fwdprobs[k][i] * probs_for_obs[k][j][i];
					total += p;
					
				}//end for k
				fwdprobs[j][i+1] = total;
			}//end for j
			//Tstates = Ustates;	
		}//end for i
		
		return fwdprobs;

	}
	
	/**
	 * @param observations String Array
	 * @return bckprobs Double Arrray
	 * To calculate backward probabilities and return backward probability matrix
	*/
	public double[][] backward_viterbi(String[] observations){
		
		double[][] bckprobs = new double[noofstates][observations.length+1];
		
		for(int i = 0; i < states.length; i++){
			bckprobs[i][observations.length] = 1;
		}
		
		//for each observation in observation sequence
		for(int i = observations.length-1; i >= 0 ; i--){
						
			//for next_state in states array
			for(int j = 0; j < states.length; j++){
				double total = 0;
									
				//for current state in states array
				for(int k = 0; k < states.length; k++){
				
					//double p = bckprobs[k][i+1] * emission_probs[k][i] * transi_probs[k][j];
					double p = bckprobs[k][i+1] * probs_for_obs[k][j][i];
					total += p;
					
				}//end for k
				bckprobs[j][i] = total;
			}//end for j		
		}//end for i
		return bckprobs;
	}
	
	/**
	 * @param i int each state as we need to re-estimate starting probabilities of each state
	 * @param t int timestamp
	 * @param observations string array set of all observations
	 * @param fwdprobs double array matrix of all the calculated forward probabilities
	 * @param bckprobs double array matrix of all the calculated backward probabilities
	 * @return double array new calculated probability matrix
	 * To calculate eqn 9.17 for the eqn on page 335
	 */
	public double calculateC(int i, int timestamp, double[][] fwdprobs, double[][] bckprobs, String[] observations){
		
		
		double countC = 0;
		for(int obsK = 0; obsK < observations.length; obsK++){
			for(int statei = 0; statei < noofstates; statei++){
				for(int statej = 0; statej < noofstates; statej++){
				
					countC = (fwdprobs[statei][timestamp] * 
							probs_for_obs[statei][statej][obsK]
							* bckprobs[statej][timestamp+1]);
				}
			}
		}	
		return countC;
	}
	
	
	/**
	 * @param observations String Array
	 * @param noofsteps int
	 * @return void
	 * To train the initial probabilities
	*/
	public void trainHMM(String[] observations, int noofsteps){
		
		double[][] fwdprobs;
		double[][] bckprobs;
		
		double[] new_startprobs = new double[noofstates];
		double[][] new_transi_probs = new double[noofstates][noofstates];
		double[][] new_emission_probs = new double[noofstates][noofobservations];
		DecimalFormat df = new DecimalFormat("#.##");
		
		for(int steps = 0 ; steps < noofsteps; steps++){
			fwdprobs = forward_viterbi(observations);
			bckprobs = backward_viterbi(observations);
			
			//new trained transition probabilities
			//double upperProb = 0;
			//double lowerProb = 0;
			for(int i = 0; i < noofstates; i++){
				for(int j = 0; j < noofstates; j++){
					double countC = 0;
					double summationOfC = 0;
					for(int ts = 0; ts <= observations.length-1; ts++){
						
						countC = calculateC(i, ts, fwdprobs, bckprobs, observations);
						//System.out.println("countC => " + df.format(countC));
						System.out.println("countC => " + countC);
					
					}
					//new_transi_probs[i][j] = countC / summationOfC;
				}
				
			}
			
		}
		
		//transi_probs = new_transi_probs;
		
	}
	
	/**
	 * @param
	 * @return
	 * To display start probabilities, transsition probabilities and emission probabilities
	 */
	public void display(){
		//printing states
		for(int i=0; i < states.length; i++){
			System.out.println("states are: " + states[i]);
		}
		//printing observations
		for(int i = 0; i < observations.length; i++){
			System.out.println("observations are: " + observations[i]);
		}
		//printing start probability
		for(int i = 0; i < startprobs.length; i++){
			System.out.println("start_probability are: " + startprobs[i]);
		}
		//printing transition probability
		for(int i = 0; i < transi_probs.length; i++){
			System.out.println("State: " + states[i]);
			for(int j = 0; j < transi_probs.length; j++){
				System.out.println("transi_probability are: " + states[j] + " " + transi_probs[i][j]);	
			}
		}
		//printing emission probability
		for(int i = 0; i < states.length; i++){
			System.out.println("State: " + states[i]);
			for(int j = 0; j < observations.length; j++){
				System.out.println("emission_probability are: " + observations[j] + " " + emission_probs[i][j]);
			}	
		}
		//printing probabilities from state i to state j for observation k
		for(int i = 0; i < states.length; i++){
			for(int j = 0; j < states.length; j++){
				for(int k = 0; k < observations.length; k++){
					System.out.println("Probabilities are: " + states[i]+ " " + states[j] + " " + observations[k] + " " + probs_for_obs[i][j][k]);
				}
			}
			
		}
	}
	
	public static void main(String args[]){
		
		//HMMMain hmm = new HMMMain(2, 3);
		HMMMain hmm = new HMMMain(2, 4);
		
//		hmm.states[0] = "Rainy";
//		hmm.states[1] = "Sunny";
		
		hmm.states[0] = "q";
		hmm.states[1] = "r";
		
//		hmm.observations[0] = "walk";
//		hmm.observations[1] = "shop";
//		hmm.observations[2] = "clean";
		
		hmm.observations[0] = "eye";
		hmm.observations[1] = "drops";
		hmm.observations[2] = "off";
		hmm.observations[3] = "shelf";
		
		//start probabilities
//		hmm.startprobs[0] = 0.6;//Rainy
//	    hmm.startprobs[1] = 0.4;//Sunny
	    
		hmm.startprobs[0] = 1.0;//q
	    hmm.startprobs[1] = 0.0;//r

	    //transition probabilities
//	    hmm.transi_probs[0][0] = 0.7;
//	    hmm.transi_probs[0][1] = 0.3;
//	    hmm.transi_probs[1][0] = 0.4;
//	    hmm.transi_probs[1][1] = 0.6;
	    
	    hmm.transi_probs[0][0] = 0.7;
	    hmm.transi_probs[0][1] = 0.3;
	    hmm.transi_probs[1][0] = 0.4;
	    hmm.transi_probs[1][1] = 0.6;
	    
	    //emission probabilities
//	    hmm.emission_probs[0][0] = 0.1;
//	    hmm.emission_probs[0][1] = 0.4;
//	    hmm.emission_probs[0][2] = 0.5;
//	    hmm.emission_probs[1][0] = 0.6;
//	    hmm.emission_probs[1][1] = 0.3;
//	    hmm.emission_probs[1][2] = 0.1;
	    
	    hmm.emission_probs[0][0] = 0.4;
	    hmm.emission_probs[0][1] = 0.3;
	    hmm.emission_probs[0][2] = 0.2;
	    hmm.emission_probs[0][3] = 0.1;
	    hmm.emission_probs[1][0] = 0.2;
	    hmm.emission_probs[1][1] = 0.4;
	    hmm.emission_probs[1][2] = 0.1;
	    hmm.emission_probs[1][3] = 0.3;
	    
	    //probabilities from state i to state j for observation k
	    hmm.probs_for_obs[0][0][0] = 0.4;//q q eye
	    hmm.probs_for_obs[0][1][0] = 0.3;//q r eye
	    hmm.probs_for_obs[1][0][0] = 0.2;//r q eye
	    hmm.probs_for_obs[1][1][0] = 0.4;//r r eye
	    
	    hmm.probs_for_obs[0][0][1] = 0.2;//q q drops
	    hmm.probs_for_obs[0][1][1] = 0.2;//q r drops
	    hmm.probs_for_obs[1][0][1] = 0.5;//r q drops
	    hmm.probs_for_obs[1][1][1] = 0.1;//r r drops
	    
	    hmm.probs_for_obs[0][0][2] = 0.6;//q q off
	    hmm.probs_for_obs[0][1][2] = 0.1;//q r off
	    hmm.probs_for_obs[1][0][2] = 0.4;//r q off
	    hmm.probs_for_obs[1][1][2] = 0.3;//r r off
	    
	    hmm.probs_for_obs[0][0][3] = 0.1;//q q shelf
	    hmm.probs_for_obs[0][1][3] = 0.7;//q r shelf
	    hmm.probs_for_obs[1][0][3] = 0.3;//r q shelf
	    hmm.probs_for_obs[1][1][3] = 0.2;//r r shelf
		
	    //display first assumed model parameters
	    hmm.display();
	    
	    int obslenth = hmm.observations.length;
	    int noofsteps = 3;//no of steps for training assume 3 for now
	    
	    //calculate forward probability for each HMM state
	    double[][] fwdprobarr = new double[20][20];
	    for(int i = 0; i < noofsteps; i++){
	    	fwdprobarr = hmm.forward_viterbi(hmm.observations);
	    }
	    //display the forward probability matrix
	    for(int i = 0; i < hmm.states.length; i++){
	    	for(int j = 0; j <= hmm.observations.length; j++){
	    		System.out.println("Forward probability matrix is:" + fwdprobarr[i][j]);
	    	}
	    }
	    //calculate backward probability for each HMM state
	    double[][] bckprobarr = new double[20][20];
	    for(int i = 0; i < noofsteps; i++){
	    	bckprobarr = hmm.backward_viterbi(hmm.observations);
	    }
	    //display the backward probability matrix
	    for(int i = 0; i < hmm.states.length; i++){
	    	for(int j = 0; j <= hmm.observations.length; j++){
	    		System.out.println("Backward probability matrix is:" + bckprobarr[i][j]);
	    	}
	    }
	    
	    //call the training method to get new probabilities
	    hmm.trainHMM(hmm.observations, noofsteps);

	    
	    //after training display newly calculated model parameters
	    hmm.display();
	    
	}
}
