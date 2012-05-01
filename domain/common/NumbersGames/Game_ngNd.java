package coeviz.domain.common.NumbersGames;

import java.awt.Color; 
import java.util.Hashtable; 

import coeviz.framework.interfaces.*;

public abstract class Game_ngNd implements Game {
	
	
	// SHOWING THAT THE ORDER, BUT NOT THE VALUES, MATTERS. 
    public static final int CANDIDATE_PASSES	=  12;
    public static final int CANDIDATE_TIES      =  10;
    public static final int CANDIDATE_FAILS     =  -5;
	
	public int[] outcomesInOrder() {
		return new int[] {
			CANDIDATE_FAILS, 
			CANDIDATE_TIES, 
			CANDIDATE_PASSES
		}; 
	}
	public Color outcomesInColor(int outcome) {
		if      (outcome == CANDIDATE_FAILS)  return Color.black; 
		else if (outcome == CANDIDATE_TIES)   return Color.gray; 
		else if (outcome == CANDIDATE_PASSES) return Color.white; 
		else return null; 
	}
	public int neutralOutcome() {
		return CANDIDATE_TIES;
	}

	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}

	public abstract int getNumberOfDimensions();

	public String getAcceptableCandidateInterface() {	return "ANumber";	}
    public String getAcceptableTestInterface() {	    return "ANumber";	}
	

}