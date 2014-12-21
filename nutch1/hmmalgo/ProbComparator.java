package hmmalgo;

import java.util.Comparator;
import java.util.Map;

class ProbComparator implements Comparator
{
//	char ch;
//	double prob;
//
//	public char getCh() {
//		return ch;
//	}
//
//	public void setCh(char currentObs) {
//		this.ch = currentObs;
//	}
//
//	public double getProb() {
//		return prob;
//	}
//
//	public void setProb(double prob) {
//		this.prob = prob;
//	}
//
//	public int compare(ProbComparator o1, ProbComparator o2) {
//		if(o1.prob < o2.prob)
//			return -1;
//		else
//		if(o1.prob > o2.prob) 
//			return 1;
//		else
//			return 0;	
//	}

//	public int compare(Double o1, Double o2) {
//		if(o1 > o2)
//			return 1;
//		else
//		if(o1 < o2)
//			return -1;
//		else
//			return 0;
//	}
	
	
    public int compare(Object o1,Object o2)
    {
        if( ((Double)((Map.Entry)o1).getValue()).doubleValue() > ((Double)((Map.Entry)o2).getValue()).doubleValue() ){
              return(1);
        }else if( ((Double)((Map.Entry)o1).getValue()).doubleValue() < ((Double)((Map.Entry)o2).getValue()).doubleValue() ){
              return(-1);
        }else{
              return(0);
        }
    }

	
//	public int compare(double o1, double o2) {
//		//return o1.compareTo(o2);
//		if(o1 > o2){
//          return(1);
//		}else if(o1 < o2){
//          return(-1);
//		}else{
//          return(0);
//		}
//	}
	
//	public int compareTo(Object o1) {
//        if (this.probVal == Double.valueOf(o1.toString()).doubleValue())
//            return 0;
//        else if ((this.probVal) > Double.valueOf(o1.toString()).doubleValue())
//            return 1;
//        else
//            return -1;
//    }

//	public int compare(Double o1, Double o2) {
//		if(o1 > o2)
//			return 1;
//		else
//		if(o1 < o2)
//			return -1;
//		else
//			return 0;
//	}
}