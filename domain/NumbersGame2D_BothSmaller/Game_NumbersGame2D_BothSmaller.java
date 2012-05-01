package coeviz.domain.NumbersGame2D_BothSmaller;

import java.util.Random; 

import coeviz.framework.interfaces.*;
import coeviz.representation.ANumber;
import coeviz.domain.common.NumbersGames.Game_ng2d;


// The candidate wins if it is larger than the test.
public class Game_NumbersGame2D_BothSmaller extends Game_ng2d {

	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}

	
    public int evaluateCandidate(Candidate c, Test t, Random r) {
        
		int[] cVal = ((ANumber) c).getVals();
        int[] tVal = ((ANumber) t).getVals();

        if ((cVal[0] < tVal[0]) && (cVal[1] < tVal[1])) 	    return CANDIDATE_PASSES;
        else if ((cVal[0] == tVal[0]) || (cVal[1] == tVal[1]))	return CANDIDATE_TIES;
        else 							                        return CANDIDATE_FAILS;
    }
}