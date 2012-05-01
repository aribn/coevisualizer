package coeviz.representation.RPSRep;

/*
import java.util.Random;
import coeviz.framework.interfaces.*;
import coeviz.representation.RPS;

public class RPS_Vanilla implements RPS, Test, Candidate {

    private int strategy;

    
    public RPS_Vanilla() {
        super();
    }

    public void setStrategy(int newStrategy) {
        strategy = newStrategy;
    }

    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {

        int mutation = strategy;

        if(r.nextDouble() < mRate) {
            mutation = (mutation + r.nextInt(3)) % 3;
        }

        RPS_Vanilla rps = new RPS_Vanilla();
        rps.setStrategy(mutation);
        return (PopulationMember) rps;
    }
    

    // ************* For ANumber interface ***************


    public int getStrategy(Random r) {
        return strategy;
    }

    public void randomizeStrategy(Random r) {
        strategy = r.nextInt(3);
    }
    

    // *********** For Candidate interface ***************


    
    public Candidate getCandidateMutation (Random r, double mutation_rate, int mutation_bias, double mutation_size) {
        return (Candidate) getMutation(r, mutation_rate, mutation_bias, mutation_size);
    }

	
	public void initializeCandidate (Game g, Random r) {
		randomizeStrategy(r);
    }

    
    // *********** For Test interface ***************

    

    public Test getTestMutation (Random r, double mutation_rate, int mutation_bias, double mutation_size) {
        return (Test) getMutation(r, mutation_rate, mutation_bias, mutation_size);
    }

	
	public void initializeTest (Game g, Random r) {
		randomizeStrategy(r);
    }

    
    // *********** For PopulationMember interface ***************

    
        
    public String toString() {
        return STRATEGIES[strategy];
    }

    public Object clone() {
        RPS_Vanilla rps = new RPS_Vanilla();
        rps.setStrategy(strategy);
        return (PopulationMember) rps;
    }

	
	public void regenerateFromLog(String toStringRep) {
		for (int i=0; i<3; i++) 
			if (STRATEGIES[i].equals(toStringRep))
				setStrategy(i); 
	}
	
    
	public double getObjectiveFitness() {
		return 0;  
	}
	
	public void view() {}

	
    // ************* that's it! ***************

    
}
*/