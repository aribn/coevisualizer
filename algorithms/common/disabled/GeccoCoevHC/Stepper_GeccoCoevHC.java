package coeviz.algorithms.GeccoCoevHC;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import coeviz.algorithms.common.GeccoAlgorithms.*;

import java.util.*;


public final class Stepper_GeccoCoevHC extends NoTestMutation_CandDom {

    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
        Test[] tMutant = getTestMutations(tPrev);
        Test[] tTarget = new Test[tPrev.length];
        // Select tests.
        for (int i=0; i<tPrev.length; i++) {
            if (t_coevDominates(tMutant[i], tPrev[i], cPrev, getGame()))
                tTarget[i] = tMutant[i];
            else tTarget[i] = tPrev[i];
        }
        return tTarget;
    }
	
	    public boolean t_coevDominates(Test t1, Test t2, Candidate[] cands, Game g) {
        int t1wins = 0;
        int t2wins = 0;
        for(int i = 0 ; i < cands.length ; i++) {
			if (g.evaluateCandidate(cands[i], t1, getRandom()) < g.neutralOutcome() ) t1wins++;
            if (g.evaluateCandidate(cands[i], t2, getRandom()) < g.neutralOutcome() ) t2wins++;
        }
        return (t1wins > t2wins);
    }
	
}
