package coeviz.domain.StrategyHD;

import coeviz.framework.interfaces.*;
import coeviz.representation.Strategy;
import coeviz.representation.StrategyRep.*;
import coeviz.visualization.ViewerPanel;
import coeviz.domain.common.StrategyGames.Game_sgNs;

import java.util.*;
import java.awt.Color;

public class Game_StrategyHD extends Game_sgNs implements Game, Renderable {

	private static final String HAWK = "HAWK";
	private static final String DOVE = "DOVE";
	private static final String[] STRATEGIES = {HAWK, DOVE};
	
	// SHOWING THAT THE ORDER, BUT NOT THE VALUES, MATTERS. 
    public static final int HAWK_STEALS	     =  12;  //  2
    public static final int DOVES_SHARE      =  10;  //  1
    public static final int NEUTRAL          =  9;  
    public static final int DOVE_LOSES       =  8;   //  0	
    public static final int HAWKS_CLASH      =  6;   // -2
   	
	public int[] outcomesInOrder() {
		return new int[] {
			HAWKS_CLASH, 
			DOVE_LOSES, 
			DOVES_SHARE, 
			HAWK_STEALS
		}; 
	}
	public int neutralOutcome() {
		return NEUTRAL;
	}
	public Color outcomesInColor(int outcome) {
		if      (outcome == HAWKS_CLASH)  return Color.red; 
		else if (outcome == DOVE_LOSES)   return Color.yellow; 
		else if (outcome == DOVES_SHARE)  return Color.white; 
		else if (outcome == HAWK_STEALS)  return Color.black; 
		else return null; 
	}
	
	public String[] getStrategyNames() { return STRATEGIES; }
	
	public int evaluateCandidate_sg(String candStrat, String testStrat) {
		
		if       ((candStrat.equals(DOVE)) &&     (testStrat.equals(DOVE)))	    return DOVES_SHARE;
        else if  ((candStrat.equals(DOVE)) &&     (testStrat.equals(HAWK)))	    return DOVE_LOSES;
        else if  ((candStrat.equals(HAWK)) &&    (testStrat.equals(DOVE))) 	    return HAWK_STEALS;
        else if  ((candStrat.equals(HAWK)) &&    (testStrat.equals(HAWK)))	    return HAWKS_CLASH;
		else     {new Exception().printStackTrace(); return CANDIDATE_TIES; }

	}
	
	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}

}
