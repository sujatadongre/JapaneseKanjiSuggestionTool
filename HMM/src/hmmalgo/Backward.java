package hmmalgo;

import hmmalgo.FwdViterbiT;


public class Backward {
	
		public static String[] states = {"Rainy", "Sunny"};
		public static String[] observations = {"walk", "shop", "clean"};
		public static double[] start_probability = {0.6, 0.4};
		public static double[][] transi_probability = {{0.7, 0.3}, {0.4, 0.6}};
		public static double[][] emission_probability = {{0.1, 0.4, 0.5}, {0.6, 0.3,0.1}};
		
		public static void backward_algo(String[] obs, String[] states, double[] start_prob, 
				double[][] transi_prob, double[][] emi_prob){
			FwdViterbiT[] Tstates = new FwdViterbiT[states.length];
			//initialization
			for(int i = 0; i < states.length; i++){
				Tstates[i] = new FwdViterbiT(1);
			}
			
			//for each observation in observation sequence
			for(int i = obs.length-1; i >= 0 ; i--){
				FwdViterbiT[] Ustates = new FwdViterbiT[states.length];
				
				//for next_state in states array
				for(int j = 0; j < states.length; j++){
					double total = 0;
										
					//for current state in states array
					for(int k = 0; k < states.length; k++){
						double prob = Tstates[k].start_p;

						double p = emi_prob[k][i] * transi_prob[k][j];
						prob *= p;
						total += prob;
					}//end for k
					Ustates[j] = new FwdViterbiT(total);
				}//end for j
				Tstates = Ustates;	
			}//end for i
			
			System.out.println("Backward Probability => " + Tstates[states.length-1].start_p);

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
			
			backward_algo(observations, states, start_probability, transi_probability, emission_probability);
			
		}
}
