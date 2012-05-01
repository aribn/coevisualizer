package coeviz.algorithms.common.GeccoAlgorithms;

import coeviz.framework.interfaces.*;
import coeviz.framework.Stepper;

import java.util.*;
import java.io.*;


public abstract class Stepper_NoTestMutation_CandDom extends Stepper {


    public Candidate[] nextCands(Candidate[] cPrev, Test[] tPrev) {
        Candidate[] cMutant = getCandidateMutations(cPrev);
        Candidate[] cTarget = new Candidate[cPrev.length];
        // Select candidates
        for (int i=0; i<cPrev.length; i++) {
            if (c_coevDominates(cMutant[i], cPrev[i], tPrev, getGame()))
                cTarget[i] = cMutant[i];
            else cTarget[i] = cPrev[i];
        }
        return cTarget;
    }


    public boolean c_coevDominates(Candidate c1, Candidate c2, Test[] tests, Game g) {
        int c1wins = 0;
        int c2wins = 0;
        for(int i = 0 ; i < tests.length; i++) {
			if (g.evaluateCandidate(c1, tests[i], getRandom()) > g.neutralOutcome() ) c1wins++;
            if (g.evaluateCandidate(c2, tests[i], getRandom()) > g.neutralOutcome() ) c2wins++;
        }
        return (c1wins > c2wins);
    }	
	
	private String[] getStringRep(boolean isCand, Class repClass) {
		
		String gameName = getGame().getClass().getName();
		gameName = gameName.substring( 1+gameName.lastIndexOf("_"), gameName.length()); 
		
		String repName = ""; 
		
		if (isCand) {
			repName = repClass.getName();
			repName = repName.substring( 1+repName.lastIndexOf("_"), repName.length()); 
			repName = "" + cSize + "c_" + repName; 
		} 
		else {
			repName = repClass.getName();
			repName = repName.substring( 1+repName.lastIndexOf("_"), repName.length()); 
			repName = "" + cSize + "t_" + repName; 
		}
		
		String filename = "Init_"+gameName+"_"+repName+".txt"; 

		System.out.println("written to: scripts/gecco/"+filename); 
		String line = "";
		
		try {
			
			BufferedReader br; 
			FileReader fr; 
			
			File dir = new File(System.getProperty("user.dir"));
			if (!dir.isDirectory())
				throw new IllegalArgumentException("no such directory");
			
			dir = new File(dir.getParent());
			dir = new File(dir, "scripts");
			dir = new File(dir, "gecco");
			dir = new File(dir, filename);
			
			// create the file reader
			fr = new FileReader(dir); 
			br = new BufferedReader(fr);
			
			line = br.readLine();
			
			fr.close();
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return the lines.
		return line.split("\t"); 
		
	}
								  
								  
	
	
	// I WILL NEED TO OVERWRITE THIS TO ALLOW FOR CANDIDATE INITIALIZATION FROM FILES. 
	
	// For each pair of representations that I plan to do cross-algorithmic experiments with, 
	// I need to first generate a pair of highly engaged populations, save this to disk, and 
	// use these two lines later to regenerate these populations. 
	
    public Candidate[] getInitialCandidates(Class candClass) {
        Candidate[] cands = new Candidate[cSize];
		
		String[] toStringReps = getStringRep(true, candClass); 
        
		for (int i=0; i<cSize; i++) {
			try {
				Object c = candClass.newInstance();
				((PopulationMember)c).initializeMember(getGame(), getRandom());
				System.out.println("before: "+((PopulationMember)c).toString()); 
				((PopulationMember)c).regenerateFromLog(toStringReps[i]); 
				System.out.println("after : "+((PopulationMember)c).toString()); 
				cands[i] = (Candidate) c; 
				System.out.println(""); 
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
		}
        return cands;
    }
    public Test[] getInitialTests(Class testClass) {
        Test[] tests = new Test[tSize];

		String[] toStringReps = getStringRep(false, testClass); 

        for (int i=0; i<tSize; i++) {
			try {
				Object t = testClass.newInstance();
				((PopulationMember)t).initializeMember(getGame(), getRandom());
				((PopulationMember)t).regenerateFromLog(toStringReps[i]); 
				tests[i] = (Test) t; 
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
		}
        return tests;
    }
	

}
