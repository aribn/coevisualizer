package coeviz.domain.NumbersGameLINT;

import java.util.Random; 

import coeviz.framework.interfaces.*;
import coeviz.domain.common.NumbersGames.Game_ng1d;
import coeviz.representation.ANumber;



public class Game_NumbersGameLINT extends Game_ng1d {

    
	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}

	
    // The candidate wins if it is larger than the test.

    public int evaluateCandidate(Candidate c, Test t, Random r) {
        int[] cVal = ((ANumber) c).getVals();
        int[] tVal = ((ANumber) t).getVals();

        int nhood = 5;
        
        int candAdvan = cVal[0] - tVal[0];
        
        if       (candAdvan  < -1*nhood) 			 	 	 	return CANDIDATE_FAILS;
        else if ((candAdvan >= -1*nhood) && (candAdvan < 0)) 	return CANDIDATE_PASSES;
        else if  (candAdvan == 0) 				 	 	 	 	return CANDIDATE_TIES;
        else if ((candAdvan >  0) && (candAdvan <= nhood)) 	 	return CANDIDATE_FAILS;
        else if  (candAdvan >  nhood) 			 	 	 		return CANDIDATE_PASSES;
        else							 	 	 	 	 	 	return CANDIDATE_TIES;
    }


    
}
