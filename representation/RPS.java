package coeviz.representation;

import java.util.Random;


public interface RPS {

    public static final int ROCK	    = 0;
    public static final int PAPER	    = 1;
    public static final int SCISSORS	= 2;

    public static final String[] STRATEGIES = {"ROCK", "PAPER", "SCISSORS"};
    
    public int getStrategy(Random r);
    public void randomizeStrategy(Random r);

}
