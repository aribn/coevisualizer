package coeviz.domain.StrategyRPS;

import java.util.Random;
import java.util.Hashtable; 

import coeviz.framework.interfaces.*;
import coeviz.domain.common.StrategyGames.Game_sgNs;
import coeviz.visualization.ViewerPanel;
import coeviz.representation.Strategy;

public class Game_StrategyRPS extends Game_sgNs implements Game, Renderable {

	private static final String ROCK     = "ROCK";
	private static final String PAPER    = "PAPER";
	private static final String SCISSORS = "SCISSORS"; 
	
	private static final String[] STRATEGIES = {ROCK, PAPER, SCISSORS};
	public String[] getStrategyNames() { return STRATEGIES; }
		
	public int evaluateCandidate_sg(String candStrat, String testStrat) {
		if       (candStrat.equals(testStrat))                                      return CANDIDATE_TIES; 
		else if  ((candStrat.equals(ROCK)) &&     (testStrat.equals(PAPER)))	    return CANDIDATE_FAILS;
		else if  ((candStrat.equals(ROCK)) &&     (testStrat.equals(SCISSORS)))	    return CANDIDATE_PASSES;
		else if  ((candStrat.equals(PAPER)) &&    (testStrat.equals(ROCK))) 	    return CANDIDATE_PASSES;
		else if  ((candStrat.equals(PAPER)) &&    (testStrat.equals(SCISSORS)))	    return CANDIDATE_FAILS;
		else if  ((candStrat.equals(SCISSORS)) && (testStrat.equals(ROCK))) 	    return CANDIDATE_FAILS;
		else if  ((candStrat.equals(SCISSORS)) && (testStrat.equals(PAPER)))     	return CANDIDATE_PASSES;
		else     {new Exception().printStackTrace(); return CANDIDATE_TIES; }
	}

	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}

}
