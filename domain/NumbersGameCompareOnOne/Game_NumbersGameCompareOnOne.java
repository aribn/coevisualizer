package coeviz.domain.NumbersGameCompareOnOne;

import java.util.Random; 

import coeviz.framework.interfaces.*;
import coeviz.domain.common.NumbersGames.Game_ng2d;
import coeviz.representation.ANumber;

public class Game_NumbersGameCompareOnOne extends Game_ng2d {
            
	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}

	
    public int evaluateCandidate(Candidate c, Test t, Random r) {
      
		int[] cVal = ((ANumber) c).getVals();
        int[] tVal = ((ANumber) t).getVals();

        int greatestTest_index = 0;
        int greatestTest_val   = 0;

        for (int i=0; i<tVal.length; i++) {
            if (tVal[i] > greatestTest_val) {
                greatestTest_index = i;
                greatestTest_val = tVal[i];
            }
        }
        
        if (cVal[greatestTest_index] > tVal[greatestTest_index]) 		    return CANDIDATE_PASSES;
        else if (cVal[greatestTest_index] == tVal[greatestTest_index]) 		return CANDIDATE_TIES;
        else 									                            return CANDIDATE_FAILS;
    }


    
}
