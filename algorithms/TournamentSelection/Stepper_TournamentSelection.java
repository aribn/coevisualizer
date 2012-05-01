package coeviz.algorithms.TournamentSelection;

import java.util.*;
import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;
import coeviz.representation.common.Strategy.OpponentDriven_FSM;
import coeviz.domain.common.StrategyGames.Game_sgNs;
import coeviz.representation.Strategy;


public final class Stepper_TournamentSelection extends Stepper {

	
	private int TOURNAMENT_SIZE = 5; 

	public Hashtable getExperimentalVariables() { 
		Hashtable ht = super.getExperimentalVariables(); 
		ht.put("TOURNAMENT_SIZE", ""+TOURNAMENT_SIZE); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		super.setExperimentalVariables(ht); 
		try { TOURNAMENT_SIZE = Integer.parseInt((String)ht.get("TOURNAMENT_SIZE")); } catch (Exception e) { System.out.println("Using default value for TOURNAMENT_SIZE"); }
	}

	
    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
		PopulationMember[] descendants = getDescendantsFromTournament(true, cPrev, tPrev); 
		Candidate[] newCands = new Candidate[descendants.length];
		for (int i=0; i<descendants.length; i++) 
			newCands[i] = (Candidate) descendants[i]; 
        return getCandidateMutations(newCands);
    }
		
    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
	 PopulationMember[] descendants = getDescendantsFromTournament(false, tPrev, cPrev); 
        Test[] newTests = new Test[descendants.length];
		for (int i=0; i<descendants.length; i++) 
			newTests[i] = (Test) descendants[i]; 
        return getTestMutations(newTests);
    }
	
	
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
				
				Candidate c;
				Test t; 
				
				if (membersAreCandidates) {
					c = (Candidate) member[m];
					t = (Test) other[o];
				}
				else {
					c = (Candidate) other[o];
					t = (Test) member[m];
				}
				
				// if we have a FSA representation, reset to initial state before bouts. 
				if ((c instanceof OpponentDriven_FSM) || (t instanceof OpponentDriven_FSM)) {
					if (g instanceof Game_sgNs) {
						
						if (membersAreCandidates) {
							// do the ten-round comparison, but ignore the 3-outcome result
							g.evaluateCandidate((Candidate) c, (Test) t, getRandom() ); 
							// instead, reach inside of the result for the outcome tally. 
							int[] wins_ties_losses = ((Game_sgNs)g).getLastOutcomeTally(); 
							memberScores[m] += (2*wins_ties_losses[0] + 1*wins_ties_losses[1] + 0*wins_ties_losses[2]); 
						}
						else {
							// do the ten-round comparison, but ignore the 3-outcome result
							// g.evaluateCandidate((Candidate) c, (Test) t, getRandom() ); 
							g.evaluateTest((Candidate) c, (Test) t, getRandom() ); 
							// instead, reach inside of the result for the outcome tally. 
							int[] wins_ties_losses = ((Game_sgNs)g).getLastOutcomeTally(); 
							memberScores[m] += (0*wins_ties_losses[0] + 1*wins_ties_losses[1] + 2*wins_ties_losses[2]); 
						}
						
					
					}
					else {
						new Exception("c or t is fsm, but game is not strategy-based!").printStackTrace(); 
					}
				}
				else {
					
					// do a 10-round tournament. 
					
					for (int i=0; i<10; i++) {
						if (membersAreCandidates) {
							int result = g.evaluateCandidate( (Candidate) c, (Test) t, getRandom() );
							if      (result >  g.neutralOutcome())     memberScores[m] += 2; 
							else if (result == g.neutralOutcome())     memberScores[m] += 1; 
							else if (result <  g.neutralOutcome())     memberScores[m] += 0;
						
						}
						else {
							// int result = g.evaluateCandidate( (Candidate) c, (Test) t, getRandom() );
							int result = g.evaluateTest( (Candidate) c, (Test) t, getRandom() );
							if      (result  > g.neutralOutcome())      memberScores[m] += 0; 
							else if (result == g.neutralOutcome())      memberScores[m] += 1; 
							else if (result  < g.neutralOutcome())      memberScores[m] += 2;
						}
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
				indexOfBestInTourn = 0; 
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
