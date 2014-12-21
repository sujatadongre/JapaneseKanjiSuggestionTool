package hmmalgo;

import java.util.Random;

public class RndGen_StartProbs {

	
	public static void main(String args[]){
		
		double N = 2;
		//double M = 5;
		
		int noofstates = 2;
		//int noofobservations = 5;
		
		double startRange = 0;
		double endRange = 1/N;
		
		double[] startprobs = new double[noofstates];//Start probabilities 1 * N
		double[] arr = new double[noofstates];
		
		//initialize the array
		for(int i = 0; i < noofstates; i++){
			startprobs[i] = 1/N;
		}
		
		for(int k = 0; k < noofstates; k++){
			arr[k] = 1/N;
		}
		
		Random rndGen = new Random();
		
		//for(int s = 0; s < noofstates; s++){
			//iterate through i	
			//arr = new double[noofstates];
			for(int i = 0; i < noofstates; i++){
				System.out.println("i = " + i);
				int j = rndGen.nextInt(noofstates);//pick j randomly
				System.out.println("j = " + j);
				
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
				System.out.println("startprobs["+k+"]=> " + startprobs[k]);
			}
		//}
		//return transi_probs;
	}
}
