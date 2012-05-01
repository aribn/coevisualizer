package coeviz.algorithms.CoevolutionaryHillClimber;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

import java.util.*;


public final class Stepper_CoevolutionaryHillClimber extends Stepper {


    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
        Candidate[] cMutant = getCandidateMutations(cPrev);
        Candidate[] cTarget = new Candidate[cPrev.length];
        // Select candidates
        for (int i=0; i<cPrev.length; i++) {
            if (c_coevDominates(cMutant[i], cPrev[i], tPrev, getGame()))
                cTarget[i] = cMutant[i];
            else cTarget[i] = cPrev[i];
        }
        return cTarget;
    }

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

    public boolean c_coevDominates(Candidate c1, Candidate c2, Test[] tests, Game g) {
        int c1wins = 0;
        int c2wins = 0;
        for(int i = 0 ; i < tests.length; i++) {
			if (g.evaluateCandidate(c1, tests[i], getRandom()) > g.neutralOutcome() ) c1wins++;
            if (g.evaluateCandidate(c2, tests[i], getRandom()) > g.neutralOutcome() ) c2wins++;
        }
        return (c1wins > c2wins);
    }

    public boolean t_coevDominates(Test t1, Test t2, Candidate[] cands, Game g) {
        int t1wins = 0;
        int t2wins = 0;
        for(int i = 0 ; i < cands.length ; i++) {
			if (g.evaluateTest(cands[i], t1, getRandom()) < g.neutralOutcome() ) t1wins++;
            if (g.evaluateTest(cands[i], t2, getRandom()) < g.neutralOutcome() ) t2wins++;
        }
        return (t1wins > t2wins);
    }


}

/*
 public boolean c_coevDominates(Candidate c1, Candidate c2, Test[] tests, Game g) {
	 int c1wins = 0;
	 int c2wins = 0;
	 for(int i = 0 ; i < tests.length; i++) {
		  if (g.evaluateCandidate(c1, tests[i], getRandom()) == g.CANDIDATE_PASSES() ) c1wins++;
		  if (g.evaluateCandidate(c2, tests[i], getRandom()) == g.CANDIDATE_PASSES() ) c2wins++;
	 }
	 return (c1wins > c2wins);
 }
 public boolean t_coevDominates(Test t1, Test t2, Candidate[] cands, Game g) {
	 int t1wins = 0;
	 int t2wins = 0;
	 for(int i = 0 ; i < cands.length ; i++) {
		  if (g.evaluateCandidate(cands[i], t1, getRandom()) == g.CANDIDATE_FAILS() ) t1wins++;
		  if (g.evaluateCandidate(cands[i], t2, getRandom()) == g.CANDIDATE_FAILS() ) t2wins++;
	 }
	 return (t1wins > t2wins);
 }
 */