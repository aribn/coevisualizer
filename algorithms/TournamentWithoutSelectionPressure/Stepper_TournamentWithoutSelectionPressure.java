package coeviz.algorithms.TournamentWithoutSelectionPressure;

import java.util.*;
import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

public final class Stepper_TournamentWithoutSelectionPressure extends Stepper {

	private int TOURNAMENT_SIZE = 5; 
	
	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = new Hashtable(); 
		ht.put("TOURNAMENT_SIZE", ""+TOURNAMENT_SIZE); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		try { TOURNAMENT_SIZE = Integer.parseInt((String)ht.get("TOURNAMENT_SIZE")); } catch (Exception e) { System.out.println("Using default value for TOURNAMENT_SIZE"); }
	}
	
    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
		PopulationMember[] descendants = runNonTournament(cPrev, tPrev); 
		Candidate[] newCands = new Candidate[descendants.length];
		for (int i=0; i<descendants.length; i++) 
			newCands[i] = (Candidate) descendants[i]; 
        return getCandidateMutations(newCands);
    }
		
    public Test[] nextTests(Candidate[] cPrev, Test[] tPrev) {
	 PopulationMember[] descendants = runNonTournament(tPrev, cPrev); 
        Test[] newTests = new Test[descendants.length];
		for (int i=0; i<descendants.length; i++) 
			newTests[i] = (Test) descendants[i]; 
        return getTestMutations(newTests);
    }
	
	public PopulationMember[] runNonTournament(PopulationMember[] member, PopulationMember[] other) {
		// put all player indices in an array, and randomize order.
		ArrayList wheel = new ArrayList(member.length); 
		for (int i=0; i<member.length; i++) wheel.add(i, new Integer(i));
		Collections.shuffle(wheel, getRandom());
		
		int[] tournamentWinners = new int[member.length/TOURNAMENT_SIZE]; 

		for (int i=0; i<member.length; i++) {
			// add winner to a list, and get ready for next tourney
			if (((i+1) % TOURNAMENT_SIZE)==0) 
				tournamentWinners[i/TOURNAMENT_SIZE] = ((Integer) wheel.get(i)).intValue(); 
			else if (i==(member.length-1)) 
				System.out.println("Warning: Population size should be a multiple of "+TOURNAMENT_SIZE+"!");
		}
		
        PopulationMember[] winners = new PopulationMember[member.length];
		for (int i=0; i<tournamentWinners.length; i++)
			for (int j=0; j<TOURNAMENT_SIZE; j++)
				winners[i*TOURNAMENT_SIZE + j] = member[ tournamentWinners[i] ];
		
		return winners; 
	}		
}