package coeviz.framework;

import java.util.Random;
import java.util.Hashtable;

import java.security.SecureRandom;
import coeviz.framework.interfaces.*;

public abstract class Stepper implements ExperimentalParametersSettable {

    private SecureRandom random;
    private Game game;

    public int cSize, tSize;

    public double 	MUTATION_RATE;
    public int 	    MUTATION_BIAS;
    public double 	MUTATION_SIZE;
	
	public boolean isCandStepper = true; 
	public boolean isTestStepper = true; 
	
	// EXPERIMENTAL VARIABLES: 
	public int candsPerMetacand = 0; 
	public int testsPerMetatest = 0; 
	
	// Each Stepper must implement these. 
	
    public abstract Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev);
    public abstract Test[]      nextTests(Candidate[] cPrev, Test[] tPrev);

	
    
	public Game getGame() { return game; }
    public Random getRandom() { return random; }
    
	public void setAsCandStepper() { 
		isCandStepper = true; 
		isTestStepper = false; 
	}
	public void setAsTestStepper() { 
		isCandStepper = false; 
		isTestStepper = true; 
	}
	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = new Hashtable(); 
	/*
		if (isCandStepper) 
			ht.put("candsPerMetacand", ""+candsPerMetacand); 
		if (isTestStepper) 
			ht.put("testsPerMetatest", ""+testsPerMetatest); 
	*/
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		/*
		try { 
			if (isCandStepper) 
				candsPerMetacand = Integer.parseInt((String)ht.get("candsPerMetacand")); } catch (Exception e) { System.out.print("Using default value for candsPerMetacand. "); }
		try { 
			if (isTestStepper) 
				testsPerMetatest = Integer.parseInt((String)ht.get("testsPerMetatest")); } catch (Exception e) { System.out.print("Using default value for testsPerMetatest. "); }
		 */
		 System.out.println("stepper - experimental params: " +getExperimentalVariables()); 
	}
	
	
	public void prepare(long seed, int candidatePopSize, int testPopSize,
                        double rate, int bias, double size, Game g) {
		
        cSize 		    = candidatePopSize;
        tSize 		    = testPopSize;
        MUTATION_RATE 	= rate;
        MUTATION_BIAS 	= bias;
        MUTATION_SIZE 	= size;
        game		    = g;
		
        try {
            random = SecureRandom.getInstance("SHA1PRNG","SUN");
            random.setSeed(seed);
        } catch (Exception e) { e.printStackTrace(); }
    }
	
	// an alternative that allows both candidate and test steppers to share the 
	// same random-number generator. This allows for backward compatability with 
	// properties files from before the change. 
	public void prepare(SecureRandom sr, int candidatePopSize, int testPopSize,
                        double rate, int bias, double size, Game g) {
		
        cSize 		    = candidatePopSize;
        tSize 		    = testPopSize;
        MUTATION_RATE 	= rate;
        MUTATION_BIAS 	= bias;
        MUTATION_SIZE 	= size;
        game		    = g;
		random          = sr; 
    }
    
    public Candidate[] getInitialCandidates(Class candClass, Hashtable experimentalProperties) {
        Candidate[] cands = new Candidate[cSize];
        for (int i=0; i<cSize; i++) {
			try {
				Object c = candClass.newInstance();
				// The stepper/domain class variables are set in Launcher()
				((ExperimentalParametersSettable)c).setExperimentalVariables(experimentalProperties); 
				((PopulationMember)c).initializeMember(game, random);
				cands[i] = (Candidate) c; 
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
		}
        return cands;
    }
    public Test[] getInitialTests(Class testClass, Hashtable experimentalProperties) {
        Test[] tests = new Test[tSize];
        for (int i=0; i<tSize; i++) {
			try {
				Object t = testClass.newInstance();
				// The stepper/domain class variables are set in Launcher()
				((ExperimentalParametersSettable)t).setExperimentalVariables(experimentalProperties); 
				((PopulationMember)t).initializeMember(game, random);
				tests[i] = (Test) t; 
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
		}
        return tests;
    }
    
    public Candidate[] getCandidateMutations(Candidate[] orig) {
        Candidate[] cMutant = new Candidate[orig.length];
        for (int i=0; i<cMutant.length; i++) {
			cMutant[i] = (Candidate) orig[i].getMutation(random, MUTATION_RATE, MUTATION_BIAS, MUTATION_SIZE );
			cMutant[i].setExperimentalVariables(orig[i].getExperimentalVariables()); 
		}
		return cMutant;
    }
    public Test[] getTestMutations(Test[] orig) {
        Test[] tMutant = new Test[orig.length];
        for (int i=0; i<tMutant.length; i++) {
            tMutant[i] = (Test) orig[i].getMutation(random, MUTATION_RATE, MUTATION_BIAS, MUTATION_SIZE );
			tMutant[i].setExperimentalVariables(orig[i].getExperimentalVariables()); 
		}
		return tMutant;
    }
}