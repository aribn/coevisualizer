package coeviz.representation;

import java.util.Random;

public interface TDMajorityClassifier {
	public int classify(TDMajorityTask task, Random r); 
}
