package coeviz.representation.TD2SingleResponderRep;

import coeviz.framework.interfaces.*;
import coeviz.representation.TD2SingleResponder;

import java.util.Random;
import java.util.Hashtable;


public class TD2SingleResponder_ScalarIRTAbility implements TD2SingleResponder, Candidate {

	public static final String RASCH_MODEL = "RaschModel"; 
	public static final String LINEAR_MODEL = "LinearModel"; 
	
	// EXPERIMENTAL VARIABLES: 
	public double range = 10.0; 
	public double learningRate = 0.001; 
	public String studentModel = RASCH_MODEL;
	
	int stepCount = 20; 
	
	
	private double ability; 
	private double[] allChallengeDiffs; 
	
    public TD2SingleResponder_ScalarIRTAbility() {
        super();
    }
	
	public void randomize(Random r, Game g) {
		ability = (range/2.0) - (range * r.nextDouble());
	}
	
	public double getAbility() {
		return ability; 
	}
	
	public double getRange() {
		return range;
	}
	
	public void setAbility(double newAbility) {
		ability = newAbility;
	}
		
	public void noteAllChallengeDiffs(double[] alldiffs) {
		allChallengeDiffs = alldiffs; 
	}
	
	public double probabilityOfAccurateResponseToChallengeDiff(double diff) {
		double p = -1.0; 
		if (studentModel.equals(RASCH_MODEL)) 
			return (Math.exp(ability-diff) / (1.0 + Math.exp(ability-diff))); 
		else if (studentModel.equals(LINEAR_MODEL)) 
			return (1.0-diff); 
		return p; 
	}
	
	public double[][] getDiscretizedIRTCurve() {
		double[][] xy = new double[stepCount+1][2]; 
		for (int i=0; i<=stepCount; i++) {
			xy[i][0] = (-0.5*range) + ((1.0*i/stepCount)*range);
			xy[i][1] = probabilityOfAccurateResponseToChallengeDiff(xy[i][0]); 
		}
		return xy; 
	}
	
    // ************* For Candidate interface ***************

	public void initializeMember (Game g, Random r) {
		randomize(r, g);
    }	
	
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		
		double mutation = ability; 
		double problearning[] = new double[allChallengeDiffs.length]; 

		for (int i=0; i<problearning.length; i++) {
			double p = probabilityOfAccurateResponseToChallengeDiff(allChallengeDiffs[i]); 
			double p_learning = ((1.0 - p) * p); 
			// learning happens in learningRate increments. 
			if(r.nextDouble() < p_learning) mutation += learningRate;
		}
		// ceiling and floor.
		if (mutation > (range/2.0)) { mutation = (range/2.0); } else if (mutation < 0.0-(range/2.0)) { mutation = 0.0-(range/2.0); }

        TD2SingleResponder_ScalarIRTAbility td = new TD2SingleResponder_ScalarIRTAbility();
		td.setAbility(mutation);
        return (PopulationMember) td;
    }
	
    public String toString() {
		return "" + ability; 
	}

    public Object clone() {
        TD2SingleResponder_ScalarIRTAbility sa = new TD2SingleResponder_ScalarIRTAbility();
        sa.setAbility(ability);
        return (PopulationMember) sa;
    }

	public void regenerateFromLog(String toStringRep) {
		setAbility(Double.parseDouble(toStringRep));
	}
	
	public double getObjectiveFitness() {
		return ability; 
	}
	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = new Hashtable(); 
		ht.put("learningRate", "" + learningRate); 
		ht.put("studentModel", "" + studentModel); 
		ht.put("range", "" + range); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		try { learningRate = Double.parseDouble((String)ht.get("learningRate")); } catch (Exception e) { System.out.print("Using default value for learningRate. "); }
		try { studentModel = (String)ht.get("studentModel"); } catch (Exception e) { System.out.print("Using default value for studentModel. "); }
		try { range = Double.parseDouble((String)ht.get("range")); } catch (Exception e) { System.out.print("Using default value for range. "); }
	}
	
	public void view() {}
    
}