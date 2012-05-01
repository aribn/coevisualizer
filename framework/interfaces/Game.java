package coeviz.framework.interfaces;

public interface Game extends ExperimentalParametersSettable {
	
	// C x T => ordered outcome for cand.
    public int evaluateCandidate(Candidate c, Test t, java.util.Random r);
    
	// C x T => ordered outcome for test.
	public int evaluateTest(Candidate c, Test t, java.util.Random r);
	
	// define space of candidates
	public String getAcceptableCandidateInterface();

    // define space of tests
	public String getAcceptableTestInterface();
	
	// define ordering of outcomes
	public int[] outcomesInOrder();

	// define dichotomous boundary of outcomes (for coevolution)
	public int neutralOutcome(); 

}
