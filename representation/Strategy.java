package coeviz.representation;

import java.util.Random;


public interface Strategy {
	public int getStrategy(Random r);
    public void randomizeStrategy(Random r, String[] strategyNames);

}
