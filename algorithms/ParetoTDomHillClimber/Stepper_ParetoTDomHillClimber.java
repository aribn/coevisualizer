package coeviz.algorithms.ParetoTDomHillClimber;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

import java.util.Random;

public final class Stepper_ParetoTDomHillClimber extends Stepper {

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
            if (tMutantIsNotDominant(tMutant[i], tPrev[i], cPrev))
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

    public boolean tMutantIsNotDominant (Test mutant, Test parent, Candidate[] cands) {
		Game g = getGame();
        int mutant_dom = 0;
        int parent_dom = 0;
        for(int i = 0 ; i < cands.length; i++) {
		/*
		    if (g.evaluateCandidate(cands[i], mutant, getRandom()) > g.evaluateCandidate(cands[i], parent, getRandom()))
                mutant_dom++;
            if (g.evaluateCandidate(cands[i], parent, getRandom()) > g.evaluateCandidate(cands[i], mutant, getRandom()))
                parent_dom++;
		*/	
			if (g.evaluateTest(cands[i], mutant, getRandom()) > g.evaluateTest(cands[i], parent, getRandom()))
                mutant_dom++;
            if (g.evaluateTest(cands[i], parent, getRandom()) > g.evaluateTest(cands[i], mutant, getRandom()))
                parent_dom++;
        }
        //return ((mutant_dom > 0) && (parent_dom == 0)) ? false : true;
        return ((mutant_dom > 0) && (parent_dom > 0)) ? false : true;
    }
}