package coeviz.domain.NumbersGame2D_FirstBigger;

import java.util.Random; 

import coeviz.framework.interfaces.*;
import coeviz.domain.common.NumbersGames.Game_ng2d;
import coeviz.representation.ANumber;


// The candidate wins if it is larger than the test.
public class Game_NumbersGame2D_FirstBigger extends Game_ng2d {
	
	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}

	
    public int evaluateCandidate(Candidate c, Test t, Random r) {
        int[] cVal = ((ANumber) c).getVals();
        int[] tVal = ((ANumber) t).getVals();

        if (tVal[0] > 50)		    return CANDIDATE_PASSES;
        else if (tVal[0] == 50) 	return CANDIDATE_TIES;
        else 					    return CANDIDATE_FAILS;
    }


}