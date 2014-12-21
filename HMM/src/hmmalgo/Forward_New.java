package hmmalgo;

import hmmalgo.FwdViterbiT;

public class Forward_New {
	
		
//		public static String[] states = {"q", "r"};
//		public static String[] observations = {"eye", "drops", "off", "shelf"};
//		public static double[] start_probability = {1.0, 0.0};
//		public static double[][] transi_probability = {{0.7, 0.3}, {0.4, 0.6}};
//		public static double[][] emission_probability = {{0.4, 0.3, 0.2, 0.1}, {0.2, 0.4, 0.1, 0.3}};
		
		public static void forward_viterbi(String[] states, double[] start_prob, 
				double[][] transi_prob, double[][] emi_prob){
			//String[] obs = {"大", "好"};//assume as user input
			String[] obs = {"大", "好", "い"};//assume as user input
			
			FwdViterbiT[] Tstates = new FwdViterbiT[states.length];
			for(int i = 0; i < states.length; i++){
				Tstates[i] = new FwdViterbiT(start_prob[i]);
			}
			
			//for each observation in observation sequence
			for(int i = 0; i < obs.length; i++){
				FwdViterbiT[] Ustates = new FwdViterbiT[states.length];
				
				//for next_state in states array
				for(int j = 0; j < states.length; j++){
					double total = 0;
					double prob = 1;
					//for current state in states array
					for(int k = 0; k < states.length; k++){
						System.out.println("k => " + k + " Tstates[k].start_p => " + Tstates[k].start_p);
						prob = Tstates[k].start_p;
						double p = emi_prob[k][i] * transi_prob[k][j];
						prob *= p;
						total += prob;
					}//end for k
					Ustates[j] = new FwdViterbiT(total);
				}//end for j
				Tstates = Ustates;	
			}//end for i
			
			double total = 0;
			
			for(int i = 0; i < states.length; i++){
				double prob = Tstates[i].start_p;
				total += prob;
			}
			System.out.println("Forward Probability => " + total);

		}
		
//		public static void main(String args[]){
//			//printing states
//			for(int i=0; i < states.length; i++){
//				System.out.println("states are: " + states[i]);
//			}
//			//printing observations
//			for(int i = 0; i < observations.length; i++){
//				System.out.println("observations are: " + observations[i]);
//			}
//			//printing start probability
//			for(int i = 0; i < start_probability.length; i++){
//				System.out.println("start_probability are: " + start_probability[i]);
//			}
//			//printing transition probability
//			for(int i = 0; i < transi_probability.length; i++){
//				System.out.println("State: " + states[i]);
//				for(int j = 0; j < transi_probability.length; j++){
//					System.out.println("transi_probability are: " + states[j] + " " + transi_probability[i][j]);	
//				}
//			}
//			//printing emission probability
//			for(int i = 0; i < states.length; i++){
//				System.out.println("State: " + states[i]);
//				for(int j = 0; j < observations.length; j++){
//					System.out.println("emission_probability are: " + observations[j] + " " + emission_probability[i][j]);
//				}	
//			}
//			
//			forward_viterbi(observations, states, start_probability, transi_probability, emission_probability);
//			
//		}
}
