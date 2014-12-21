package hmmalgo;

public abstract class HMM_FileReader {
	int noofstates;
	int noofobservations;
	public int getNoofstates() {
		return noofstates;
	}
	public void setNoofstates(int noofstates) {
		this.noofstates = noofstates;
	}
	public int getNoofobservations() {
		return noofobservations;
	}
	public void setNoofobservations(int noofobservations) {
		this.noofobservations = noofobservations;
	}
	
	public abstract int getNextCharacter(char c);
	
	
}
