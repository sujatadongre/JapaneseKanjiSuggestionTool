package hmmalgo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Hashtable;


public class HMM_New {
	
	/**
	 * @param observations String Array
	 * @return fwdprobs Double Arrray
	 * To calculate forward probabilities and return forward probability matrix
	*/
	public static double[][] forward_viterbi(int noofstates, Hashtable<String, Integer> htSymbols,
										double[][][] probs_for_obs){
		
		double[][] fwdprobs = new double[noofstates][htSymbols.size()+1];
		
		for(int i = 0; i < noofstates; i++){
			for(int j = 0; j < noofstates; j++){
				for(int k = 0; k < htSymbols.size(); k++){
					fwdprobs[i][0] = probs_for_obs[i][j][k];		
				}
			}
			
		}
		
		//for each observation in observation sequence
		for(int i = 0; i < htSymbols.size(); i++){
					
			//for next_state in states array
			for(int j = 0; j < noofstates; j++){
				double total = 0;
				//for current state in states array
				for(int k = 0; k < noofstates; k++){
					
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
	public static double[][] backward_viterbi(int noofstates, Hashtable<String, Integer> htSymbols,
										double[][][] probs_for_obs){
		
		double[][] bckprobs = new double[noofstates][htSymbols.size()+1];
		
		for(int i = 0; i < noofstates; i++){
			bckprobs[i][htSymbols.size()] = 1;
		}
		
		//for each observation in observation sequence
		for(int i = htSymbols.size()-1; i >= 0 ; i--){
						
			//for next_state in states array
			for(int j = 0; j < noofstates; j++){
				double total = 0;
									
				//for current state in states array
				for(int k = 0; k < noofstates; k++){
				
					//double p = bckprobs[k][i+1] * emission_probs[k][i] * transi_probs[k][j];
					double p = bckprobs[k][i+1] * probs_for_obs[k][j][i];
					total += p;
					
				}//end for k
				bckprobs[j][i] = total;
			}//end for j		
		}//end for i
		return bckprobs;
	}
	
	public static void calculateC(int noofstates, double[][] fwdprobs, double[][] bckprobs,
								Hashtable<String, Integer>htSymbols,
								double[][][] probs_for_obs, int lineNoCnt){
		for(int i = 0; i < noofstates; i++){
			for(int j = 0; j < noofstates; j++){
				double countC[][][] = new double[noofstates][noofstates][htSymbols.size()];;
				double summationOfC = 0;
				for(int ts = 0; ts < htSymbols.size(); ts++){
					
					countC = calculateCount(i, ts, fwdprobs, bckprobs, htSymbols, probs_for_obs, noofstates, lineNoCnt);
					//System.out.println("countC => " + df.format(countC));
					
				
				}
			}
			
		}
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
	public static double[][][] calculateCount(int i, int timestamp, double[][] fwdprobs, double[][] bckprobs, 
						Hashtable<String, Integer> htSymbols,
						double[][][] probs_for_obs, int noofstates, int lineNoCnt){
		
		
		double countC[][][] = new double[noofstates][noofstates][htSymbols.size()];

			for(int statei = 0; statei < noofstates; statei++){
				for(int statej = 0; statej < noofstates; statej++){
					for(int symbols = 0; symbols < htSymbols.size(); symbols++){//symbols in ht
						countC[statei][statej][symbols] = 0;
						for(int lineno = 0; lineno < lineNoCnt; lineno++){	//for each line in trng corpus file			
							countC[statei][statej][symbols] += (fwdprobs[statei][timestamp] * 
									probs_for_obs[statei][statej][symbols]
									  * bckprobs[statej][timestamp+1]);
							System.out.println("countC => " + countC[statei][statej][symbols]);
					}
				}
			}
		}	
		return countC;
	}
	
	
	 public static void main(String[] args) throws IOException {
		
	     Hashtable<String, Integer> htSymbols = new Hashtable<String, Integer>();
	     Hashtable<Integer, String> htIndices = new Hashtable<Integer, String>();
	     int noofstates = 0, lineNoCnt = 0;
	     boolean firstLine = true;
	     double[][] fwdprobs;
	     double[][] bckprobs;
	     try{
	    	  
	    	 //File file = new File("test_corpus.txt");
	    	 File file = new File("test1.txt");
	         int c;
	         String data = new String();
	         FileReader fileReader = new FileReader(file);
	         
	         LineNumberReader lnReader = new LineNumberReader(fileReader);
	    
	         //assume first line in the file = no of states
	         while(lnReader.getLineNumber() == 0){
	        	 String linenoofstates = lnReader.readLine();
	        	 noofstates = Integer.parseInt(linenoofstates);
	        	 System.out.println("First Line => " + String.valueOf(noofstates));
	        	 firstLine = false;
	         }
	         
	         while (lnReader.readLine() != null){
	        	 lineNoCnt++;
	        	 System.out.println("lineNoCnt => " + lineNoCnt);
	         }
	         
	         //read corpus file character by character  
	         BufferedReader reader = new BufferedReader(new InputStreamReader(
	        		 									new FileInputStream(file),
	        		        							Charset.forName("UTF8")));
	         
	         while((data = reader.readLine()) != null){
	        	 //String linenoofstates = lnReader.readLine();
	        	 
	        	 if(firstLine){
	        		 noofstates = Integer.parseInt(data);
		        	 System.out.println("First Line => " + String.valueOf(noofstates));
	        		 firstLine = false;
	        		 continue;	 
	        	 }
	        	 else{
		        	 for(int i = 0; i < file.length(); i++){
		    	    	 if((c = reader.read()) != -1){
		    	       		 char ch = (char)c;
		    		       	 //System.out.println("ch =>  " + ch);
		    		       	 //if character does not exist in hashtable
		    		       	 //add the key as ch and value as index in the hashtable
		    		       	 //else dont add the character in the hashtable
		    		       	 if((!htSymbols.containsKey(ch)) && (String.valueOf(ch) != " "))
		    		       		htSymbols.put(String.valueOf(ch), Integer.valueOf(i));
		    		       	 
		    		       	 //second hashtable key = index, value = symbol
		    		       	if((!htIndices.containsValue(ch)) && (String.valueOf(ch) != " "))
		    		       		htIndices.put(Integer.valueOf(i), String.valueOf(ch));
		    		       	
		    	       	 }
		    	     }
	        	 }
	         }
	         	         
	         Enumeration<String> keysHT1 = htSymbols.keys();
	         while(keysHT1.hasMoreElements() ) {
	           Object key = keysHT1.nextElement();
	           System.out.println("Hashtable1 ch =>  " + key);
	           Object value = htSymbols.get(key);
	           System.out.println("Hashtable1 index =>  " + value);
	         }
	         
	         Enumeration<Integer> keysHT2 = htIndices.keys();
	         while(keysHT2.hasMoreElements() ) {
	           Object key = keysHT2.nextElement();
	           System.out.println("Hashtable2 index =>  " + key);
	           Object value = htIndices.get(key);
	           System.out.println("Hashtable2 ch =>  " + value);
	         }

	         //declare transition probabilities array 
	         double[][][] transi_probs = new double[noofstates][noofstates][htSymbols.size()];
	         
	         //initialize transition probabilities array
	         for(int i = 0; i < noofstates; i++){
	        	 for(int j = 0; j < noofstates; j++){
	        		 for(int k = 0; k < htSymbols.size(); k++){
	        			 transi_probs[i][j][k] = (1 / (noofstates * htSymbols.size()));
	        		 }
	        	 }
	         }
	         
	         //calculate forward probabilities
	         fwdprobs = forward_viterbi(noofstates, htSymbols, transi_probs);
	         
	         //calculate backward probabilities
	         bckprobs = backward_viterbi(noofstates, htSymbols, transi_probs);
	         
	         //calculate C
	         calculateC(noofstates, fwdprobs, bckprobs, htSymbols, transi_probs, lineNoCnt);
	         
	         
	         
	         reader.close();
	         
	     }catch(UnsupportedEncodingException ue){
	         System.out.println("Not supported : ");
	     }catch(FileNotFoundException fnfe){
	    	 System.out.println("File test_corpus.txt not found.");
	     }catch(IOException ioe){
	    	 System.out.println("Unable to read from test_corpus.txt");
	     }
	 }    
}
