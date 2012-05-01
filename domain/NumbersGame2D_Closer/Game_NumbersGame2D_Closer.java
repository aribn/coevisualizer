package coeviz.domain.NumbersGame2D_Closer;

import java.util.Random; 

import coeviz.framework.interfaces.*;
import coeviz.domain.common.NumbersGames.Game_ng2d;
import coeviz.representation.ANumber;

// intransitive numbers game. 
public class Game_NumbersGame2D_Closer extends Game_ng2d {


	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}
	
    // The candidate wins if it is larger than the test.

    public int evaluateCandidate(Candidate c, Test t, Random r) {
        
		int[] cVal = ((ANumber) c).getVals();
        int[] tVal = ((ANumber) t).getVals();

        int closest_index = 0;
        int closest_dist  = Integer.MAX_VALUE;

        for (int i=0; i<cVal.length; i++) {
            if (Math.abs( cVal[i]-tVal[i] ) < closest_dist) {
                closest_index = i;
                closest_dist = Math.abs( cVal[i]-tVal[i] );
            }
        }
        
        if (cVal[closest_index] > tVal[closest_index]) 	 		return CANDIDATE_PASSES;
        else if (cVal[closest_index] == tVal[closest_index]) 	return CANDIDATE_TIES;
        else 							 	 	 	 	 	 	return CANDIDATE_FAILS;
    }


}