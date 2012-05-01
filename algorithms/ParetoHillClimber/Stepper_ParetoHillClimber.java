package coeviz.algorithms.ParetoHillClimber;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

import java.util.Random;

public final class Stepper_ParetoHillClimber extends Stepper {

    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
        Candidate[] cMutant = getCandidateMutations(cPrev);
        Candidate[] cTarget = new Candidate[cPrev.length];
        for (int i=0; i<cPrev.length; i++) {
            if (cDominates(cMutant[i], cPrev[i], tPrev))
                cTarget[i] = cMutant[i];
            else cTarget[i] = cPrev[i];
        }
        return cTarget;
    }

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

    public boolean cDominates(Candidate mutant, Candidate parent, Test[] tests) {
        Game g = getGame();
        int c1dom = 0;
        int c2dom = 0;
        for(int i = 0 ; i < tests.length; i++) {
			if (g.evaluateCandidate(mutant, tests[i], getRandom()) > g.evaluateCandidate(parent, tests[i], getRandom()))
                c1dom++;
            if (g.evaluateCandidate(parent, tests[i], getRandom()) > g.evaluateCandidate(mutant, tests[i], getRandom()))
                c2dom++;
        }
        return ((c1dom > 0) && (c2dom == 0)) ? true : false;
    }

    public boolean tInforms (Test t1, Test t2, Candidate[] cands) {
        Game g = getGame();
        int eq1 = 0;
        int eq2 = 0;
        for(int i=0 ; i<cands.length; i++) {
            for(int j=0 ; j<cands.length ; j++) {
                if(g.evaluateTest(cands[i],t1, getRandom()) == g.evaluateTest(cands[j],t1, getRandom())) eq1++;
                if(g.evaluateTest(cands[i],t2, getRandom()) == g.evaluateTest(cands[j],t2, getRandom())) eq2++;
            }
        }
        return (eq1 <= eq2);
    }
}

/*
 public boolean cDominates(Candidate mutant, Candidate parent, Test[] tests) {
	 Game g = getGame();
	 int c1dom = 0;
	 int c2dom = 0;
	 for(int i = 0 ; i < tests.length; i++) {
		  if ((g.evaluateCandidate(mutant, tests[i], getRandom()) == g.CANDIDATE_PASSES() )
			  && (g.evaluateCandidate(parent, tests[i], getRandom()) != g.CANDIDATE_PASSES() ))
		  c1dom++;
		  if ((g.evaluateCandidate(parent, tests[i], getRandom()) == g.CANDIDATE_PASSES() )
			  && (g.evaluateCandidate(mutant, tests[i], getRandom()) != g.CANDIDATE_PASSES() ))
		  c2dom++;
	 }
	 return ((c1dom > 0) && (c2dom == 0)) ? true : false;
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
 
 */