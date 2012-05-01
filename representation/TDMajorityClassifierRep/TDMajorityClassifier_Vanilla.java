package coeviz.representation.TDMajorityClassifierRep;

import coeviz.framework.interfaces.*;
import coeviz.representation.TDMajorityClassifier;
import coeviz.representation.TDMajorityTask;

import java.util.Random;
import java.util.Hashtable;


public class TDMajorityClassifier_Vanilla implements TDMajorityClassifier, Candidate {

	// EXPERIMENTAL VARIABLES: 
	private int timesteps = 320; 
	private int radius = 2; // 3
	private boolean debug = false; 
	
	private int[] rule; 
	
    public TDMajorityClassifier_Vanilla() {
        super();
    }
	

	private int[] applyRulesTo(int[] prevState) {
		int len = prevState.length; 
		int[] newState = new int[len];
		
		// for each cell, update it based on the window in the previous state. 
		for (int i=0; i<len; i++) {
		
			int[] window = new int[2 * radius + 1];
			for (int j=0; j<window.length; j++) {
				int index = i+j-radius; 
				// wrap around CA
				if (index<0) index += len; 
				if (index>=len) index -= len;
				// record it. 
				window[j] = prevState[index];
			}			
			newState[i] = applyRule(window); 
		}
		return newState; 
	}
	
	// To find rule for 0110001, convert it to decimal notation, and look up that rule index. 
	public int applyRule(int[] window) {
		int index = 0; 
		for (int i=0; i<window.length; i++) {
			index += (window[i] * (int) Math.pow(2.0, (window.length-1-i)));
		}
		// if (debug) printIntArray("\tvalue: "+index+"\t"+rule[index]+"\t", window); 
		return rule[index]; 
	}
	
	private String stringIntArray(int[] x) {
		String s = ""; 
		for (int i=0; i<x.length; i++) {
			if (x[i]==0) 
				s += "-";  
			else 
				s += "*";  
		}
		return s;
	}
	
	private void printIntArray(String name, int[] x) {
		System.out.println(name+" :\t" + stringIntArray(x)); 
	}
	
	public int classify(TDMajorityTask task, Random r) { 
		
		Hashtable ht = new Hashtable(); 
		
		// grab the initial condition from the challenge task
		// int[] ic = task.getInitialCondition(r); 
		int[] ic = task.getIC(); 
		
		// set up the CA
		int[][] ca = new int[timesteps][ic.length];
		for (int i=0; i<ic.length; i++) {
			ca[0][i] = ic[i]; 
		}
		if (debug) printIntArray("classifier - array ca[0]", ca[0]);  

		for (int time=0; time<(timesteps-1); time++) {
			
				ca[time+1] = applyRulesTo(ca[time]); 
				if (debug) printIntArray("classifier - array ca["+(time+1)+"]", ca[time+1]);  

				String currStateStr = stringIntArray(ca[time+1]); 
				String prevStateStr = stringIntArray(ca[time+0]); 
				String oldPrev = (String) ht.put(currStateStr, prevStateStr);
				
				// check if we have visited this state before. 
				if (oldPrev != null) {
					// if we have... 
					if (debug) System.out.println("revisited state: "+currStateStr); 
				
					// and if we were there for two consecutive states, we have converged. 
					if (currStateStr.equals(prevStateStr)) { 
						if (debug) System.out.println("classifier - converged in "+time+" steps to : " + currStateStr);
						
						// check if all bits are the same. 
						int convergedClass = ca[time+1][0]; 
						for (int i=0; i<ca[time+1].length; i++) {
							if (ca[time+1][i] != convergedClass) {
								// we have converged to a inconsistent classification. 
								if (debug) System.out.println("classifier - inconsistent converged state: "+ currStateStr);
								return -1; 
							}
						}
						
						// success! We've successfully converged to a consistent classification.  
						if (debug) System.out.println("classifier - classified task as: " + convergedClass + " ("+(convergedClass==1)+")"); 
						return convergedClass; 
						
					} else {
					// else, we are just spinning cycles. break the cycle! 
						if (debug) System.out.println("classifier began cycling before converging.");
						return -1; 
					}
				}
				
		}
		
		return -2; 
	}
	
	public void setRule(int[] newRule) {
		rule = new int[newRule.length];
		for (int i=0; i<newRule.length; i++) 
			rule[i] = newRule[i];  
	}
	
	public void randomize(Random r) {
		rule = new int[(int) Math.pow(2.0, (2 * radius + 1))];
		for (int i=0; i<rule.length; i++) 
			rule[i] = r.nextInt(2); 
		if (debug) System.out.println("classifier - random rule: " + toString()); 

	}
	
    // ************* For Test interface ***************

	public void initializeMember (Game g, Random r) {
		randomize(r);
    }	
	
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		int[] newRule = new int[rule.length]; 
		
		for (int i=0; i<rule.length; i++) { 
			newRule[i] = rule[i]; 
			if(r.nextDouble() < mRate) 
				newRule[i] = (1-rule[i]);
		}
			
        TDMajorityClassifier_Vanilla td = new TDMajorityClassifier_Vanilla();
		td.setRule(newRule);
		return (PopulationMember) td;
    }
	
    public String toString() {
		String s = "";
		for (int i=0; i<rule.length; i++) 
			s += rule[i]; 
		return s; 
	}

    public Object clone() {
        TDMajorityClassifier_Vanilla sa = new TDMajorityClassifier_Vanilla();
		sa.setRule(rule);
        return (PopulationMember) sa;
    }

	public void regenerateFromLog(String toStringRep) {
		int[] oldRule = new int[toStringRep.length()];
		for (int i=0; i<oldRule.length; i++) 
			oldRule[i] = Integer.parseInt(toStringRep.substring(i,i+1)); 
		setRule(oldRule); 
	}
	
	// meaningless here... 
	public double getObjectiveFitness() {
		return 0; 
	}
	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = new Hashtable(); 
		ht.put("timesteps", ""+timesteps); 
		ht.put("radius", ""+radius); 
		ht.put("debug", ""+debug); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		try { timesteps = Integer.parseInt((String)ht.get("timesteps")); } catch (Exception e) { System.out.print("Using default value for timesteps. "); }
		try { radius = Integer.parseInt((String)ht.get("radius")); } catch (Exception e) { System.out.print("Using default value for radius. "); }
		try { debug = ((String)ht.get("debug")).equals("true"); } catch (Exception e) { System.out.print("Using default value for debug. "); }
		if (debug) System.out.println("classifier - experimental params: " +getExperimentalVariables()); 
	}
	
	public void view() {}
    
}