package coeviz.representation.TD2ChallengeRep;

import coeviz.framework.interfaces.*;
import coeviz.representation.TD2Challenge;

import java.util.Random;
import java.util.Hashtable;


public class TD2Challenge_ScalarDifficulty implements TD2Challenge, Test {

	private double difficulty; 

	// EXPERIMENTAL VARIABLES: 
	private double range = 10.0; 
	
	
    public TD2Challenge_ScalarDifficulty() {
        super();
    }
	
    public void setDifficulty(double d) {
		difficulty = d; 
    }	
	
    public double getDifficulty() {
		return difficulty; 
    }
	
	public double getScaledDifficulty() {
		return ((difficulty - (-1.0*range/2.0)) / range); // this is correct.
	}
	
	public double getRange() {
		return range;
	}
	
	public void randomize(Random r, Game g) {
		difficulty = (range/2.0) - (range * r.nextDouble());		
	}
	
    // ************* For Test interface ***************

	public void initializeMember (Game g, Random r) {
		randomize(r, g);
    }	
	
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		double newDiff = difficulty; 
		
        if(r.nextDouble() < mRate) {
			// change ranges from -0.5 mSize to 0.5 mSize
			double change = ((mSize/2.0) - (mSize * (r.nextDouble())));
			// newDiff is modified by something between -0.5*mSize*range and 0.5*mSize*range
			newDiff += (range * change);
			if (newDiff > (range/2.0)) { newDiff = (range/2.0); } else if (newDiff < 0.0-(range/2.0)) { newDiff = 0.0-(range/2.0); }
        }
		
        TD2Challenge_ScalarDifficulty td = new TD2Challenge_ScalarDifficulty();
		td.setDifficulty(newDiff);
		return (PopulationMember) td;
    }
	
    public String toString() {
		return "" + difficulty; 
	}

    public Object clone() {
        TD2Challenge_ScalarDifficulty sa = new TD2Challenge_ScalarDifficulty();
		sa.setDifficulty(difficulty);
        return (PopulationMember) sa;
    }

	public void regenerateFromLog(String toStringRep) {
		setDifficulty(Double.parseDouble(toStringRep)); 
	}
	
	public double getObjectiveFitness() {
		return difficulty; 
	}
	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = new Hashtable(); 
		ht.put("range", "" + range); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		try { range = Double.parseDouble((String)ht.get("range")); } catch (Exception e) { System.out.print("Using default value for range. "); }
	}
	
	public void view() {}
    
}