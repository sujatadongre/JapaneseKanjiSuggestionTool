package hmmalgo;

import java.util.Random;

public class RndGen_New {
	public static void main(String args[]){
		double N = 2;
		double M = 5;
		
		int noofstates = 2;
		int noofobservations = 5;
		
		
		double startRange = 0;
		double endRange = 1/(M * M);
		
		double[][] emission_probs = new double[noofstates][noofobservations];//N * M
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
			//iterate through i = 1 to 5
			
			for(int i = 0; i < noofobservations; i++){
				System.out.println("i = " + i);
				int j = rndGen.nextInt(noofobservations);//pick j randomly [0,5]
				System.out.println("j = " + j);
				
				//randomly generate some epsilon value
				double number = startRange +((double)rndGen.nextDouble()*(endRange-startRange));
				System.out.println("number => " + number);
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
				System.out.println("emission_probs["+s+"]["+k+"] => " + emission_probs[s][k]);
			}
		}
	}
}
