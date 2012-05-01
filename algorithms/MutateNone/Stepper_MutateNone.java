package coeviz.algorithms.MutateNone;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

import java.util.Random;


public final class Stepper_MutateNone extends Stepper {

    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
        return cPrev;
    }

    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
        return tPrev;
    }
    
}
