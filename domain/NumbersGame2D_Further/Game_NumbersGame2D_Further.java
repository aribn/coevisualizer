package coeviz.domain.NumbersGame2D_Further;

import java.util.Random; 

import coeviz.framework.interfaces.*;
import coeviz.domain.common.NumbersGames.Game_ng2d;
import coeviz.representation.ANumber;


public class Game_NumbersGame2D_Further extends Game_ng2d {

    
	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}

	
    // The candidate wins if it is larger than the test.

    public int evaluateCandidate(Candidate c, Test t, Random r) {
      
		int[] cVal = ((ANumber) c).getVals();
        int[] tVal = ((ANumber) t).getVals();

        int furthest_index = 0;
        int furthest_dist  = 0;

        for (int i=0; i<cVal.length; i++) {
            if (Math.abs( cVal[i]-tVal[i] ) > furthest_dist) {
                furthest_index = i;
                furthest_dist = Math.abs( cVal[i]-tVal[i] );
            }
        }

        if      (cVal[furthest_index]  > tVal[furthest_index]) 	return CANDIDATE_PASSES;
        else if (cVal[furthest_index] == tVal[furthest_index]) 	return CANDIDATE_TIES;
        else 							 	 	 	 	 	 	return CANDIDATE_FAILS;
        
    }
    
}