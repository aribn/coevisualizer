package coeviz.algorithms.GeccoInformative;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import coeviz.algorithms.common.GeccoAlgorithms.*;

import java.util.*;


public final class Stepper_GeccoInformative extends NoTestMutation_CandDom {

	
    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
        Test[] tMutant = getTestMutations(tPrev);
        Test[] tTarget = new Test[tPrev.length];
        for (int i=0; i<tPrev.length; i++) {
            if (tInforms(tMutant[i], tPrev[i], cPrev))
                tTarget[i] = tMutant[i];
            else tTarget[i] = tPrev[i];
        }
        return tTarget;
    }
	
    public boolean tInforms (Test t1, Test t2, Candidate[] cands) {
        Game g = getGame();
        int eq1 = 0;
        int eq2 = 0;
        for(int i=0 ; i<cands.length; i++) {
            for(int j=0 ; j<cands.length ; j++) {
                if(g.evaluateCandidate(cands[i],t1, getRandom()) == g.evaluateCandidate(cands[j],t1, getRandom())) eq1++;
                if(g.evaluateCandidate(cands[i],t2, getRandom()) == g.evaluateCandidate(cands[j],t2, getRandom())) eq2++;
            }
        }
        return (eq1 <= eq2);
    }
	
}
