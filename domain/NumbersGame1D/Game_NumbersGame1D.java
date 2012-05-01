package coeviz.domain.NumbersGame1D;

import java.util.Random; 

import coeviz.framework.interfaces.*;
import coeviz.domain.common.NumbersGames.Game_ng1d;
import coeviz.representation.ANumber;


public class Game_NumbersGame1D extends Game_ng1d {


	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}
	
    
    // The candidate wins if it is larger than the test.
    
    public int evaluateCandidate(Candidate c, Test t, Random r) {
        int[] cVal = ((ANumber) c).getVals();
        int[] tVal = ((ANumber) t).getVals();
    
        if      (cVal[0]  > tVal[0])	return CANDIDATE_PASSES;
        else if (cVal[0] == tVal[0]) 	return CANDIDATE_TIES;
        else 			 	 	 		return CANDIDATE_FAILS;
    }


}