package coeviz.algorithms.GeccoFitProp;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import coeviz.algorithms.common.GeccoAlgorithms.*;

import java.util.*;


public final class Stepper_GeccoFitProp extends NoTestMutation_CandDom {

    
	
	public int ELITE_COUNT = 0;
	
	
    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
		
        Test[] tMutant = getTestMutations(tPrev);
        Test[] tTarget = new Test[tPrev.length];
		
		tTarget = (Test[]) compete(false, tMutant, tTarget, cPrev, getGame());
		
        return tTarget;
    }
	
	
    public PopulationMember[] compete (boolean membersAreCandidates, 
									   PopulationMember[] members,
                                       PopulationMember[] target,
                                       PopulationMember[] compare, Game g) {
		
        ArrayList wheel = new ArrayList();
		
        // keep track of relative fitness
        int[] wins = new int[members.length];
        for (int i=0; i<wins.length; i++)
            wins[i]=0;
		
        // play everybody against everybody
        for(int i=0; i<members.length; i++) {
            // everybody gets in a little bit
            wheel.add(members[i]);
            wheel.add(members[i]);
            wheel.add(members[i]);
            for(int j=0 ; j<compare.length; j++) {
				
				if (membersAreCandidates) {
					
					// winners get a spot on the wheel
					Candidate c = (Candidate) members[i];
					Test t = (Test) compare[j];
					//if( g.evaluateCandidate(c,t, getRandom()) == g.CANDIDATE_PASSES() ) {
					if( g.evaluateCandidate(c,t, getRandom()) > g.neutralOutcome() ) {
						// c won, so it gets a spot
						wheel.add(members[i]);
						wins[i]++;
					}
					}
				else { // members are tests. 
					
					// winners get a spot on the wheel
					Candidate c = (Candidate) compare[j];
					Test t = (Test) members[i];
					//if( g.evaluateCandidate(c,t, getRandom()) == g.CANDIDATE_FAILS() ) {
					if( g.evaluateCandidate(c,t, getRandom()) < g.neutralOutcome() ) {
						// t won, so it gets a spot
						wheel.add(members[i]);
						wins[i]++;
					}
					}
				
				}
				}
		
        // now everybody has a chunk of the wheel roughly proportional
        // to how many times they won.  so shuffle it and pluck off a few
        Collections.shuffle(wheel, getRandom());
        for (int i=ELITE_COUNT; i<target.length; i++) {
            target[i] = (PopulationMember) wheel.get(i);
        }
		
        // elitism
        for (int e=0; e<ELITE_COUNT; e++) {
            int bestScore = 0;
            int bestIndex = 0;
            for (int i=0; i<wins.length; i++) {
                if (wins[i]>bestScore) {
                    bestIndex = i;
                    bestScore = wins[i];
                }
            }
            target[e] = members[bestIndex];
            wins[bestIndex] = 0; // take them out of the running.
        }
        
        return target;
			}
	
}
