package hmmalgo;

import hmmalgo.ViterbiT;
public class Viterbi {
	
		
			public static String[] states = {"Rainy", "Sunny"};
			public static String[] observations = {"walk", "shop", "clean"};
			public static double[] start_probability = {0.6, 0.4};
			public static double[][] transi_probability = {{0.7, 0.3}, {0.4, 0.6}};
			public static double[][] emission_probability = {{0.1, 0.4, 0.5}, {0.6, 0.3,0.1}};
			
			public static void ViterbiAlgo(String[] obs, String[] states, double[] start_prob, 
					double[][] transi_prob, double[][] emi_prob){
				//String[] obs = {"大", "好"};
				ViterbiT[] Tstates = new ViterbiT[states.length];
				//for all states
				//first initialize each state with its corresponding start probabilities 
				for(int i = 0; i < states.length; i++){
					Tstates[i] = new ViterbiT(start_prob[i], states[i]);
				}
				
				//for each observation in observation sequence
				for(int i = 0; i < obs.length; i++){
					ViterbiT[] Ustates = new ViterbiT[states.length];
					
					//for each state at time t+1
					for(int j = 0; j < states.length; j++){
						//initialize the most probable path to empty string
						//and highest probability to 0;
						String max_path = new String();
						double max_p = 0;
						
						//for current state at time t
						for(int k = 0; k < states.length; k++){
							
							String v_path = Tstates[k].state;
							double v_prob = Tstates[k].start_p;
							
							double p = emi_prob[k][i] * transi_prob[k][j];
							v_prob *= p;
							if(v_prob > max_p){
								max_p = v_prob;
								max_path = v_path + states[j];
							}
							
						}
						Ustates[j] = new ViterbiT(max_p, max_path);	
					}
					Tstates = Ustates;	
				}
				
				String max_path = new String();
				double max_p = 0;
				for(int i = 0; i < states.length; i++){
					double v_prob = Tstates[i].start_p;
					String v_path = Tstates[i].state;
					if(v_prob > max_p){
						max_p = v_prob;
						max_path = v_path;
					}
				}
				
				System.out.println("Viterbi Path => " + max_path);
				System.out.println("Viterbi Probability => " + max_p);
			}
			
			public static void main(String args[]){

				//printing states
				for(int i=0; i < states.length; i++){
					System.out.println("states are: " + states[i]);
				}
				//printing observations
				for(int i = 0; i < observations.length; i++){
					System.out.println("observations are: " + observations[i]);
				}
				//printing start probability
				for(int i = 0; i < start_probability.length; i++){
					System.out.println("start_probability are: " + start_probability[i]);
				}
				//printing transition probability
				for(int i = 0; i < transi_probability.length; i++){
					System.out.println("State: " + states[i]);
					for(int j = 0; j < transi_probability.length; j++){
						System.out.println("transi_probability are: " + states[j] + " " + transi_probability[i][j]);	
					}
				}
				//printing emission probability
				for(int i = 0; i < states.length; i++){
					System.out.println("State: " + states[i]);
					for(int j = 0; j < observations.length; j++){
						System.out.println("emission_probability are: " + observations[j] + " " + emission_probability[i][j]);
					}	
				}
				
				ViterbiAlgo(observations, states, start_probability, transi_probability, emission_probability);
				
			}
	}
