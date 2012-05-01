package coeviz.algorithms.GeccoTournament;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import coeviz.algorithms.common.GeccoAlgorithms.*;

import java.util.*;


public final class Stepper_GeccoTournament extends NoTestMutation_CandDom {

	public int TOURNAMENT_SIZE = 5; 
	
	
    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
		PopulationMember[] descendants = getDescendantsFromTournament(tPrev, cPrev); 
        Test[] newTests = new Test[descendants.length];
		for (int i=0; i<descendants.length; i++) 
			newTests[i] = (Test) descendants[i]; 
        return getTestMutations(newTests);
    }
	
	public PopulationMember[] getDescendantsFromTournament(PopulationMember[] member, 
														   PopulationMember[] other) {
		
		// initialize all scores to 0.
		int[] memberScores = new int[member.length]; 
		for (int i=0; i<member.length; i++) 
			memberScores[i]=0; 
		
		// then play each candidate against each test for ten rounds, and tally the results. 
		for (int m=0; m<member.length; m++) {
			for (int o=0; o<other.length; o++) {
				for (int i=0; i<10; i++) {
					Game g = getGame(); 
					int result = g.evaluateCandidate( (Candidate) other[o], (Test) member[m], getRandom() );
					if      (result  > g.neutralOutcome())      memberScores[m] += 0; 
					else if (result == g.neutralOutcome())      memberScores[m] += 1; 
					else if (result  < g.neutralOutcome())      memberScores[m] += 2;
				} 
			}
		}
		
		// put all player indices in an array, and randomize order.
		ArrayList wheel = new ArrayList(member.length); 
		for (int i=0; i<member.length; i++) wheel.add(i, new Integer(i));
		Collections.shuffle(wheel, getRandom());
		
		int[] tournamentWinners = new int[member.length/TOURNAMENT_SIZE]; 
		int indexOfBestInTourn = 0; 
		int scoreOfBestInTourn = 0; 
		
		for (int i=0; i<member.length; i++) {
			int nextIndex = ((Integer) wheel.get(i)).intValue(); 
			int nextScore = memberScores[nextIndex]; 
			if (nextScore >= scoreOfBestInTourn) {
				indexOfBestInTourn = nextIndex; 
				scoreOfBestInTourn = nextScore;
			}
			
			// add winner to a list, and get ready for next tourney
			if (((i+1) % TOURNAMENT_SIZE)==0) {
				tournamentWinners[i/TOURNAMENT_SIZE] = indexOfBestInTourn; 
				scoreOfBestInTourn = 0; 
			}
			else if (i==(member.length-1)) {
				System.out.println("Warning: Population size should be a multiple of "+TOURNAMENT_SIZE+"!");
			}
		}
		
        PopulationMember[] winners = new PopulationMember[member.length];
		for (int i=0; i<tournamentWinners.length; i++)
			for (int j=0; j<TOURNAMENT_SIZE; j++)
				winners[i*TOURNAMENT_SIZE + j] = member[ tournamentWinners[i] ];
		
		return winners; 
	}	
	
	



}
