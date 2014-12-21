package hmmalgo;

public class ViterbiT {
	
	public double start_p;//start probability
	public String state;//viterbi path
	ViterbiT(double start_p, String state){
		this.start_p = start_p;
		this.state = state;
	}
}
