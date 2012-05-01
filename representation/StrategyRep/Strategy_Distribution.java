package coeviz.representation.StrategyRep;

import java.util.Random;
import java.util.Hashtable;

import coeviz.framework.interfaces.*;
import coeviz.representation.Strategy;
import coeviz.domain.common.StrategyGames.Game_sgNs;

public class Strategy_Distribution implements Strategy, Test, Candidate {
	private static final double PRECISION = 2; 
    	
	// distribution over all possible (prob.length) strategies. Sum 1. 
    private double[] prob; 

	
	
	
	
    public Strategy_Distribution() {
        super();
    }
    
    public void setStrategy(double[] newprob) {
		/*
		prob = new double[newprob.length]; 
		for (int i=0; i<prob.length; i++) 
			prob[i] = newprob[i];
		 */
		prob = (double[]) newprob.clone(); 
    }
    
	public double[] normalize(double[] prenormal) {
		
		// first, force back into bounds. 
		for (int i=0; i<prob.length; i++)   if (prenormal[i]<0) prenormal[i]=0;
		for (int i=0; i<prob.length; i++)   if (prenormal[i]>1) prenormal[i]=1;
		
		// then, normalize
		double sum = 0; 
		double[] normal = new double[prenormal.length];
		for (int i=0; i<prob.length; i++)   sum += prenormal[i];
		for (int i=0; i<prob.length; i++)   normal[i] = prenormal[i] / sum; 
		for (int i=0; i<prob.length; i++)   normal[i] = limitPrecision(normal[i]); 
		
		// and guarantee that the sum 1. 
		double s = 1.0; 
		for (int i=0; i<prob.length-1; i++)   
			s -= normal[i]; 
		normal[prob.length-1] = limitPrecision(s); 
		
		return normal;
	}
	
	public double limitPrecision(double d) {
		return Math.round(d * Math.pow(10.0, PRECISION)) / (Math.pow(10.0, PRECISION)) ;
	}
	
    /************* For Strategy interface ***************/
    
    public int getStrategy(Random r) {
		double randDouble = r.nextDouble();
		double sum = 0;
		for (int i=0; i<prob.length; i++) {
			sum += prob[i]; 
			if (randDouble <= sum) return i;
		}
		return -1; 
    }
    
    public void randomizeStrategy(Random r, String[] strategyNames) {
		prob = new double[strategyNames.length]; 

		double[] d = new double[prob.length]; 
		for (int i=0; i<d.length; i++) 
			d[i] = r.nextDouble(); 
		
        double[] rnd = normalize(d); 
		for (int i=0; i<prob.length; i++) 
			prob[i]=rnd[i];
    }
    
    
    /************* For PopulationMember interface ***************/
    
	public void initializeMember (Game g, Random r) {
		randomizeStrategy(r, ((Game_sgNs)g).getStrategyNames());
    }	
	
	// Mutation, as defined by cartlidge and bullock:
	// 10% chance per locus. up to +/- 30% change per probability, then re-normalize. 
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		
		double[] mutation = new double[prob.length];
		for (int i=0; i<prob.length; i++) 
			mutation[i] = prob[i];
		
		// 0.1 chance of mutation per locus, mutated up to +/- 0.3
		int changeThis = r.nextInt(10);
		if (changeThis < prob.length)
			mutation[changeThis] = prob[changeThis] + ((0.6 * r.nextDouble()) - 0.3);
		
		double[] mutated = normalize(mutation); 
		
		Strategy_Distribution strategy = new Strategy_Distribution();
		strategy.setStrategy(mutated);
		return (PopulationMember) strategy;
    }
	
    public String toString() {
		String str = "[";
		for (int i=0; i<prob.length; i++) 
			str += Double.toString(prob[i]) + " "; 
		str += "]";
		return str;
    }
	
	public void regenerateFromLog(String toStringRep) {
		String nums = toStringRep.substring(1, toStringRep.length()-1); 
		String[] numArr = nums.split(" ");
		double[] n = new double[numArr.length];
		for (int i=0; i<n.length; i++) n[i] = Double.parseDouble(numArr[i]); 
        setStrategy(n);
	}
	
    public Object clone() {
		double[] prob_clone = new double[prob.length]; 
		for (int i=0; i<prob.length; i++) 
			prob_clone[i] = prob[i]; 
        Strategy_Distribution sd = new Strategy_Distribution();
        sd.setStrategy(prob_clone);
        return (PopulationMember) sd;
    }
	
	public double getObjectiveFitness() {
		return 0;  
	}

	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}

	public void view() {}

    
    /************* that's it! ***************/
}