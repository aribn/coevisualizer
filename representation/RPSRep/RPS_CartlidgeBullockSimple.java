package coeviz.representation.RPSRep;

/*
import java.util.Random;
import coeviz.framework.interfaces.*;
import coeviz.representation.RPS;

public class RPS_CartlidgeBullockSimple implements RPS, Test, Candidate {
    
    private double[] prob = new double[3]; 
	double PRECISION = 2.0; 

    public RPS_CartlidgeBullockSimple() {
        super();
		for (int i=0; i<prob.length; i++) prob[i]=0;
    }
    
    public void setStrategy(double[] newprob) {
		for (int i=0; i<prob.length; i++) prob[i] = newprob[i];
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
		
		normal[0] = limitPrecision(normal[0]);
		normal[1] = limitPrecision(normal[1]);
		normal[2] = limitPrecision(1 - normal[0] - normal[1]); 
		
		return normal;
	}
	
	public double limitPrecision(double d) {
		return Math.round(d * Math.pow(10.0, PRECISION)) / (Math.pow(10.0, PRECISION)) ;
	}
	
	// Mutation, as defined by cartlidge and bullock:
	// 10% chance per locus. up to +/- 30% change per probability, then re-normalize. 
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		
		double[] mutation = new double[3];
		for (int i=0; i<prob.length; i++) 
			mutation[i] = prob[i];
		
		// 0.1 chance of mutation per locus, mutated up to +/- 0.3
		int changeThis = r.nextInt(10);
		if (changeThis < STRATEGIES.length)
			mutation[changeThis] = prob[changeThis] + ((0.6 * r.nextDouble()) - 0.3);
		
		double[] mutated = normalize(mutation); 
				
		RPS_CartlidgeBullockSimple rps = new RPS_CartlidgeBullockSimple();
		rps.setStrategy(mutated);
		return (PopulationMember) rps;
    }
    
    // *********** For RPS interface ***************
    
    public int getStrategy(Random r) {
		double randDouble = r.nextDouble();
		if (randDouble < (prob[ROCK])) return ROCK;
		else if (randDouble < (prob[ROCK] + prob[PAPER])) return PAPER;
		else if (randDouble < (prob[ROCK] + prob[PAPER] + prob[SCISSORS])) return SCISSORS;
		else return -1;
    }
    
    public void randomizeStrategy(Random r) {
		double[] d = {r.nextDouble(), r.nextDouble(), r.nextDouble()};
        double[] rnd = normalize(d); 
		for (int i=0; i<prob.length; i++) 
			prob[i]=rnd[i];
    }
    
    // *********** For Candidate interface **************
        
    public Candidate getCandidateMutation (Random r, double mutation_rate, int mutation_bias, double mutation_size) {
        return (Candidate) getMutation(r, mutation_rate, mutation_bias, mutation_size);
    }
    
	public void initializeCandidate (Game g, Random r) {
		randomizeStrategy(r);
    }
	
    // *********** For Test interface **************
    
    public Test getTestMutation (Random r, double mutation_rate, int mutation_bias, double mutation_size) {
        return (Test) getMutation(r, mutation_rate, mutation_bias, mutation_size);
    }
	
	public void initializeTest (Game g, Random r) {
		randomizeStrategy(r);
    }
    
    // *********** For PopulationMember interface **************
    
    public String toString() {
		String str = "[";
		for (int i=0; i<prob.length; i++) str += Double.toString(prob[i]) + " "; 
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
		for (int i=0; i<prob.length; i++) prob_clone[i] = prob[i]; 
        RPS_CartlidgeBullockSimple rps = new RPS_CartlidgeBullockSimple();
        rps.setStrategy(prob_clone);
        return (PopulationMember) rps;
    }
	
	public double getObjectiveFitness() {
		return 0;  
	}
	
	public void view() {}

    
    // *********** that's it! **************
}

*/