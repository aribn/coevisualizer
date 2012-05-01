package coeviz.domain.StrategyRPSF;

import coeviz.framework.interfaces.*;
import coeviz.visualization.ViewerPanel;
import coeviz.domain.common.StrategyGames.Game_sgNs;
import coeviz.representation.Strategy;

import java.util.Hashtable; 
import java.util.Random;

public class Game_StrategyRPSF extends Game_sgNs implements Game, Renderable {

	private static final String ROCK     = "ROCK";
	private static final String PAPER    = "PAPER";
	private static final String SCISSORS = "SCISSORS"; 
	private static final String FIRE     = "FIRE"; 
	
	private static final String[] STRATEGIES = {ROCK, PAPER, SCISSORS, FIRE};

	public String[] getStrategyNames() { return STRATEGIES; }

	public int evaluateCandidate_sg(String candStrat, String testStrat) {
		
        if       (candStrat.equals(testStrat))                                      return CANDIDATE_TIES;
        else if  ((candStrat.equals(ROCK)) &&     (testStrat.equals(PAPER)))	    return CANDIDATE_FAILS;
        else if  ((candStrat.equals(ROCK)) &&     (testStrat.equals(SCISSORS)))	    return CANDIDATE_PASSES;
        else if  ((candStrat.equals(ROCK)) &&     (testStrat.equals(FIRE)))	        return CANDIDATE_PASSES;
        else if  ((candStrat.equals(PAPER)) &&    (testStrat.equals(ROCK))) 	    return CANDIDATE_PASSES;
        else if  ((candStrat.equals(PAPER)) &&    (testStrat.equals(SCISSORS)))	    return CANDIDATE_FAILS;
        else if  ((candStrat.equals(PAPER)) &&    (testStrat.equals(FIRE)))	        return CANDIDATE_FAILS;
        else if  ((candStrat.equals(SCISSORS)) && (testStrat.equals(ROCK))) 	    return CANDIDATE_FAILS;
        else if  ((candStrat.equals(SCISSORS)) && (testStrat.equals(PAPER)))     	return CANDIDATE_PASSES;
        else if  ((candStrat.equals(SCISSORS)) && (testStrat.equals(FIRE)))      	return CANDIDATE_PASSES;   // is this right? 
		else if  ((candStrat.equals(FIRE)) &&     (testStrat.equals(ROCK)))         return CANDIDATE_FAILS;  
        else if  ((candStrat.equals(FIRE)) &&     (testStrat.equals(PAPER)))        return CANDIDATE_PASSES; 
        else if  ((candStrat.equals(FIRE)) &&     (testStrat.equals(SCISSORS)))     return CANDIDATE_FAILS;   // is this right? 
		else     {new Exception().printStackTrace(); return CANDIDATE_TIES; }
    }
	
	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}

}
