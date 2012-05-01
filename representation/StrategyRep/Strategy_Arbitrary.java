package coeviz.representation.StrategyRep;

import java.util.Random;
import java.util.Hashtable;

import coeviz.framework.interfaces.*;
import coeviz.representation.Strategy;
import coeviz.domain.common.StrategyGames.Game_sgNs;

public class Strategy_Arbitrary implements Strategy, Test, Candidate {

	// index in GAME_STRATEGIES representing the current strategy
    private int strategy;
	
	// a list of all strategy names in this game
	private String[] GAME_STRATEGIES;

	
	
	
    
    public Strategy_Arbitrary() {
        super();
    }
	
    public void setStrategy(int newStrategy, String[] strategyNames) {
        strategy = newStrategy;
		GAME_STRATEGIES = (String[]) strategyNames.clone(); 
    }
    

    // ************* For Strategy interface ***************


    public int getStrategy(Random r) {
        return strategy;
    }

    public void randomizeStrategy(Random r, String[] strategyNames) {
		GAME_STRATEGIES = strategyNames; 
        strategy = r.nextInt(GAME_STRATEGIES.length);
    }
    


    
    // ************* For PopulationMember interface ***************

	public void initializeMember (Game g, Random r) {
		randomizeStrategy(r, ((Game_sgNs)g).getStrategyNames());
    }	
	
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		
        int mutation = strategy;
		
        if(r.nextDouble() < mRate) {
            mutation = (mutation + r.nextInt(GAME_STRATEGIES.length)) % GAME_STRATEGIES.length;
        }
		
        Strategy_Arbitrary strategy = new Strategy_Arbitrary();
        strategy.setStrategy(mutation, GAME_STRATEGIES);
        return (PopulationMember) strategy;
    }
	
    public String toString() {
        return GAME_STRATEGIES[strategy];
    }

    public Object clone() {
        Strategy_Arbitrary sa = new Strategy_Arbitrary();
        sa.setStrategy(strategy, GAME_STRATEGIES);
        return (PopulationMember) sa;
    }

	
	public void regenerateFromLog(String toStringRep) {
		for (int i=0; i<GAME_STRATEGIES.length; i++) 
			if (GAME_STRATEGIES[i].equals(toStringRep))
				setStrategy(i, GAME_STRATEGIES); 
	}
	
    
	public double getObjectiveFitness() {
		return 0;  
	}
	
	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}

	
	public void view() {}
    
}