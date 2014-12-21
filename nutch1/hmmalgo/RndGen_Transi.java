package hmmalgo;

import java.util.Random;

public class RndGen_Transi {

	public static void main(String args[]){
		
		double N = 3;
		//double M = 5;
		
		int noofstates = 3;
		//int noofobservations = 5;
		
		double startRange = 0;
		double endRange = 1/(N * N);
		
		double[][] transi_probs = new double[noofstates][noofstates];//N * N
		double[][] arr = new double[noofstates][noofstates];
		
		//initialize the array
		for(int i = 0; i < noofstates; i++){
			for(int j = 0; j < noofstates; j++){
				transi_probs[i][j] = 1/N;
			}
		}
		
		for(int i = 0; i < noofstates; i++){
			for(int l = 0; l < noofstates; l++){
				arr[i][l] = 1/N; 
			}
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
				
				arr[i][i] = arr[i][i] - number;//add it to i
				
				arr[i][j] = arr[i][j] + number;//subtract it from j
				
				arr[j][j] = arr[j][j] - number;
				
				arr[j][i] =  arr[j][i] + number;
				
//				double temp = arr[i][j];
//				arr[i][j] = arr[j][i];
//				arr[j][i] = temp;
			}
			//change random number generator seed
			rndGen.setSeed(System.currentTimeMillis());
			
			//assign arr values to emission prob matrix
			for(int k = 0; k < noofstates; k++){
				for(int l = 0; l < noofstates; l++){
					transi_probs[k][l] = arr[k][l];
					System.out.println("transi_probs["+k+"]["+l+"] => " + transi_probs[k][l]);
				}
			}
		//}
		//return transi_probs;
	}
}
