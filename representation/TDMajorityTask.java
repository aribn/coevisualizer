package coeviz.representation;

import java.util.Random; 


public interface TDMajorityTask {
	// public int[] getInitialCondition(Random r); 
	// public double getDensity(); 
	public int[] getIC(); 
	public double calculateDensity(); 
	
	public Object clone();
	public void randomize(Random r); 
}
