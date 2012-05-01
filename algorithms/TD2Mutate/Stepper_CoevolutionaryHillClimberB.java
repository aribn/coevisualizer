package coeviz.algorithms.CoevolutionaryHillClimberB;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import java.util.*;

public final class Stepper_CoevolutionaryHillClimberB extends Stepper {
	
    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
        Candidate[] cMutant = getCandidateMutations(cPrev);
        Candidate[] cTarget = new Candidate[cPrev.length];
        // Select candidates
        for (int i=0; i<cPrev.length; i++) {
            if (c(cMutant[i], cPrev[i], tPrev, getGame()))
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
            if (t(tMutant[i], tPrev[i], cPrev, getGame()))
                tTarget[i] = tMutant[i];
            else tTarget[i] = tPrev[i];
        }
        return tTarget;
    }
	
    public boolean c(Candidate c1, Candidate c2, Test[] tests, Game g) {
        int c1wins = 0;
        int c2wins = 0;
        for(int i = 0 ; i < tests.length; i++) {
			
			
			double c1w = g.evaluateCandidate(c1, tests[i], getRandom());
			double c2w = g.evaluateCandidate(c2, tests[i], getRandom()); 
			
			/*
			long theSeed = getRandom().nextLong(); 
			double c1w = g.evaluateCandidate(c1, tests[i], new Random(theSeed));
			double c2w = g.evaluateCandidate(c2, tests[i], new Random(theSeed)); 
			*/
			
			if (c1w > c2w)
				c1wins++;
            else c2wins++;
        }
        return (c1wins > c2wins);
    }
	
	public boolean t(Test t1, Test t2, Candidate[] cands, Game g) {
        int t1wins = 0;
        int t2wins = 0;
        for(int i = 0 ; i < cands.length ; i++) {
			
			/*
			double t1 = g.evaluateTest(cands[i], t1, getRandom());
			double t2 = g.evaluateTest(cands[i], t2, getRandom()); 
			*/
			long theSeed = getRandom().nextLong(); 
			double t1w = g.evaluateTest(cands[i], t1, new Random(theSeed));
			double t2w = g.evaluateTest(cands[i], t2, new Random(theSeed)); 
			
			if (t1w < t2w) 
				t1wins++;
			else t2wins++;
		}
		return (t1wins > t2wins);
    }
}
