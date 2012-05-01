package coeviz.representation;

import java.util.Random;

public interface TD2SingleResponder {
	public void noteAllChallengeDiffs(double[] alldiffs); 
	public double getAbility();
	public double probabilityOfAccurateResponseToChallengeDiff(double diff); 
	public double getRange();
	public double[][] getDiscretizedIRTCurve(); 
}
