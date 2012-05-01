package coeviz.algorithms.FitnessProportionalCoevolutionWithLookahead;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

import java.util.*;


public final class Stepper_FitnessProportionalCoevolutionWithLookahead extends Stepper {
	
	public int ELITE_COUNT = 0;
	
	public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
		
		Candidate[] cMutant = getCandidateMutations(cPrev);
		Candidate[] cTarget = new Candidate[cPrev.length];
		
		cTarget = (Candidate[]) competeCand(true, cMutant, cTarget, tPrev, getGame());
		return cTarget;
	}
	
	
	public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
		
		Test[] tMutant = getTestMutations(tPrev);
		Test[] tTarget = new Test[tPrev.length];
		
		tTarget = (Test[]) competeTest(false, tMutant, tTarget, cPrev, tPrev, getGame());
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
	
	public PopulationMember[] competeCand (boolean membersAreCandidates, 
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
			for(int j=0 ; j<members.length; j++) {
				
				if (cDominates((Candidate) members[i], (Candidate) members[j], (Test[]) compare)) {
					wheel.add(members[i]);
					wins[i]++;
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
	
	
	
	public boolean tInforms (Test t1, Test t2, Candidate[] cands) {
		Game g = getGame();
		int eq1 = 0;
		int eq2 = 0;
		for(int i=0 ; i<cands.length; i++) {
			for(int j=0 ; j<cands.length ; j++) {
			/*
			    if(g.evaluateCandidate(cands[i],t1, getRandom()) == g.evaluateCandidate(cands[j],t1, getRandom())) eq1++;
				if(g.evaluateCandidate(cands[i],t2, getRandom()) == g.evaluateCandidate(cands[j],t2, getRandom())) eq2++;
			*/	
				if(g.evaluateTest(cands[i],t1, getRandom()) == g.evaluateTest(cands[j],t1, getRandom())) eq1++;
				if(g.evaluateTest(cands[i],t2, getRandom()) == g.evaluateTest(cands[j],t2, getRandom())) eq2++;
			}
		}
		return (eq1 <= eq2);
	}
	
	public PopulationMember[] competeTest (boolean membersAreCandidates, 
										   PopulationMember[] members,
										   PopulationMember[] target,
										   PopulationMember[] tPrev, 
										   PopulationMember[] compare, Game g) {
		
		PopulationMember[] membersCopy = new Test[members.length + tPrev.length];
		for (int i=0; i<members.length; i++) membersCopy[i] = (Test) members[i]; 
		for (int i=0; i<tPrev.length;   i++) membersCopy[members.length+i] =(Test) tPrev[i]; 
		
		// get a next-generation of candidates. 
		Candidate[] compare2 = new Candidate[compare.length]; 
		compare2 = (Candidate[]) competeCand(true, compare, (PopulationMember[]) compare2, membersCopy, g); 
		
		ArrayList wheel = new ArrayList();
		
		// keep track of relative fitness
		int[] wins = new int[membersCopy.length];
		for (int i=0; i<wins.length; i++)
			wins[i]=0;
		
		// play everybody against everybody
		for(int i=0; i<membersCopy.length; i++) {
			// everybody gets in a little bit
			wheel.add(membersCopy[i]);
			wheel.add(membersCopy[i]);
			//wheel.add(membersCopy[i]);
			for(int j=0 ; j<membersCopy.length; j++) {
				if (tInforms((Test) membersCopy[i], (Test) membersCopy[j], (Candidate[]) compare2)) {
					wheel.add(membersCopy[i]);
					wins[i]++;
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
			target[e] = membersCopy[bestIndex];
			wins[bestIndex] = 0; // take them out of the running.
		}
		
		return target;
		}
	}
