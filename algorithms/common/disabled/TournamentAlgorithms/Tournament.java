package coeviz.algorithms.common.TournamentAlgorithms;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import coeviz.representation.common.Strategy.OpponentDriven_FSM;
import coeviz.representation.Strategy;

import java.util.*;


public abstract class Tournament extends Stepper {
	
	
	public int TOURNAMENT_SIZE = 5; 

	
	public PopulationMember[] getDescendantsFromTournament(boolean membersAreCandidates, 
														   PopulationMember[] member, 
														   PopulationMember[] other) {
		
		Game g = getGame(); 
		
		// initialize all scores to 0.
		int[] memberScores = new int[member.length]; 
		for (int i=0; i<member.length; i++) 
			memberScores[i]=0; 
		
		
		// the play each candidate against each test for ten rounds, and tally the results. 
		for (int m=0; m<member.length; m++) {
			for (int o=0; o<other.length; o++) {
				
				Candidate c = (Candidate) member[m]; 
				Test t = (Test) other[o]; 
				
				// if we have a FSA representation, reset to initial state before bouts. 
				if (c instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) c).resetStrategy();
				if (t instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) t).resetStrategy();
				
				for (int i=0; i<10; i++) {
					if (membersAreCandidates) {
						int result = g.evaluateCandidate( (Candidate) c, (Test) t, getRandom() );
						if      (result >  g.neutralOutcome())     memberScores[m] += 2; 
						else if (result == g.neutralOutcome())     memberScores[m] += 1; 
						else if (result <  g.neutralOutcome())     memberScores[m] += 0;
						
					}
					else {
						int result = g.evaluateCandidate( (Candidate) c, (Test) t, getRandom() );
						if      (result  > g.neutralOutcome())      memberScores[m] += 0; 
						else if (result == g.neutralOutcome())      memberScores[m] += 1; 
						else if (result  < g.neutralOutcome())      memberScores[m] += 2;
					}
					
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
