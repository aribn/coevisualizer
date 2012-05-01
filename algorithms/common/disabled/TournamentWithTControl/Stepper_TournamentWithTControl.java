package coeviz.algorithms.TournamentWithTControl;

import java.util.*;
import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import coeviz.algorithms.common.TournamentAlgorithms.*;


public final class Stepper_TournamentWithTControl extends Tournament {

    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
		
		PopulationMember[] descendants = getDescendantsFromTournament(true, cPrev, tPrev); 
		Candidate[] newCands = new Candidate[descendants.length];
		
		for (int i=0; i<descendants.length; i++) 
			newCands[i] = (Candidate) descendants[i]; 
       
		return getCandidateMutations(newCands);
    }
		
	// NOTE THAT THIS WILL NEVER MODIFY ANY OF THE TESTS. 
    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
		return tPrev;
    }
		
}
