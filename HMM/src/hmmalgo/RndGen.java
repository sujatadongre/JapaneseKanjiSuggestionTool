package hmmalgo;

import java.util.Random;

public class RndGen {
	
	public static void main(String args[]){
		double N = 2;
		double M = 5;
		
		int noofstates = 2;
		int noofobservations = 5;
		
		
		double[][] emission_probs = new double[noofstates][noofobservations];//N * M
		
		Random rndGen = new Random();
		double startRange = -0.000000000001;
		double endRange = 0.000000000001;
		boolean isFirstObs = true;
		//boolean isFirstState = true;
		
		for(int j = 0; j <= noofstates - 1; j++){
			//System.out.println("State: " + states[j]);
			double cnt = M;
			//if(isFirstState == true){
				//double cnt = M;
				for(int k = 0; k <= noofobservations - 1; k++){
					
					double number = startRange +((double)rndGen.nextDouble()*(endRange-startRange));
					System.out.println("number => " + number);
					
					double temp = 0.0;
					
					System.out.println("cnt is => " + cnt);
					
					if(isFirstObs == true){
						System.out.println("half calc=> " + ((1/cnt)));
						temp = (((1/cnt)) + number);
						cnt--;
						isFirstObs = false;
					}
					else{
						System.out.println("half calc=> " + (1 - temp));
						temp = ((((1 - temp)/cnt))+ number);
						cnt--;
					}		
					System.out.println("temp => " + temp);
					//emission_probs[j][k] = 1 - temp;
					//sumProbs += temp;
					emission_probs[j][k] = temp;
					System.out.println(" j = " + j + " k = " + k + " prob = " + emission_probs[j][k]);
				}
				//isFirstState = false;
//			}else
//			if(isFirstState == false){
//				startRange = -0.01;
//				endRange = 0.01;
//				for(int k = 0; k <= noofobservations - 1; k++){
//					
//					double number = startRange +((double)rndGen.nextDouble()*(endRange-startRange));
//					System.out.println("number => " + number);
//					
//					double temp = 0.0;
//					
//					System.out.println("cnt is => " + cnt);
//					
//					if(isFirstObs == true){
//						System.out.println("half calc=> " + ((1/cnt)+0.13));
//						temp = (((1/cnt)+0.13) + number);
//						cnt--;
//						isFirstObs = false;
//					}
//					else{
//						System.out.println("half calc=> " + ((1 - temp)+0.11));
//						temp = (((((1 - temp)+0.11)/cnt))+ number);
//						cnt--;
//					}		
//					System.out.println("temp => " + temp);
//					//emission_probs[j][k] = 1 - temp;
//					//sumProbs += temp;
//					emission_probs[j][k] = temp;
//					System.out.println(" j = " + j + " k = " + k + " prob = " + emission_probs[j][k]);
//				}
//			}

		}
		
	}
}
