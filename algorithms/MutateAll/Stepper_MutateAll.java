package coeviz.algorithms.MutateAll;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

import java.util.Random;


public final class Stepper_MutateAll extends Stepper {

    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
        return getCandidateMutations(cPrev);
    }

    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
        return getTestMutations(tPrev);
    }
    
}
