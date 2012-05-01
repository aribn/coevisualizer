package coeviz.domain.TDMajority;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import coeviz.representation.TDMajorityClassifier;
import coeviz.representation.TDMajorityTask;
import coeviz.framework.interfaces.*; 
import coeviz.framework.*; 
import coeviz.visualization.views.*;
import coeviz.visualization.ViewerPanel;

public class Game_TDMajority extends TeachersDilemma {

	// EXPERIMENTAL VARIABLES: 
	private double rho = 0.5;  
	private boolean debug = false; 
	private int evaluation_trials = 20; 
	private int evaluation_frequency = 10; 
	
	private static int genCount;
	private double scalar = 200;
	private double max_range = 1;
	private double min_range = 1; 
	private boolean rangeSet = false; 
	private static final int barWidth = 10; 
	private static final Color testColor = new java.awt.Color(1.0f, 0.0f, 0.0f, 0.1f); 
	private static final Color candColor = new java.awt.Color(0.0f, 0.0f, 1.0f, 0.6f); 
	private boolean redrawAxis = false; 
	

	public String getAcceptableCandidateInterface() {	return "TDMajorityClassifier";	}
    public String getAcceptableTestInterface() {	    return "TDMajorityTask";    	}

	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = super.getExperimentalVariables();
		ht.put("rho", ""+rho); 
		ht.put("debug", ""+debug); 
		ht.put("evaluation_trials", ""+evaluation_trials); 
		ht.put("evaluation_frequency", ""+evaluation_frequency); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		super.setExperimentalVariables(ht); 
		try { rho = Double.parseDouble((String)ht.get("rho"));} catch (Exception e) { System.out.println("Using default value for rho"); }
		try { evaluation_trials = Integer.parseInt((String)ht.get("evaluation_trials"));} catch (Exception e) { System.out.println("Using default value for evaluation_trials"); }
		try { evaluation_frequency = Integer.parseInt((String)ht.get("evaluation_frequency"));} catch (Exception e) { System.out.println("Using default value for evaluation_frequency"); }
		try { debug = ((String)ht.get("debug")).equals("true"); } catch (Exception e) { System.out.print("Using default value for debug. "); }
		if (debug) System.out.println("game - experimental params: " +getExperimentalVariables()); 
	}
	
	
	public double assessDifficulty(Candidate c,Test t, Random r) {
		TDMajorityTask chal = (TDMajorityTask) t; 
		double density = chal.calculateDensity(); 
		double d = 1.0 - (2.0 * Math.abs(rho - density)); 
		//double difficulty; 
		d = d*d; 
		if (debug) System.out.println("* density is: "+density+", difficulty is: "+d); 
		return d; 
	}
    
	public boolean assessAccuracy(Candidate c, Test t, Random r) {
		boolean classifierAccuracy; 
		TDMajorityTask chal = (TDMajorityTask) t; 
		TDMajorityClassifier resp = (TDMajorityClassifier) c; 
		
		// get the right answer
		int theRightAnswer;
		if (chal.calculateDensity() > rho)
			 theRightAnswer = 1; 
		else theRightAnswer = 0; 
		
		// compare this to the classifier's response. 
		int classifierAnswer = resp.classify(chal,r);
	
		if (classifierAnswer < 0) {
			classifierAccuracy = false; 
			if (debug) System.out.println("* the classifier did not successfully converge."); 
		} else { 
			if (debug) System.out.println("* the classifier's answer is: "+classifierAnswer); 
			classifierAccuracy = (classifierAnswer == theRightAnswer);
		}
		
		if (debug) System.out.println("* the correct answer is: "+theRightAnswer); 
		if (debug) System.out.println("* was the classifier accurate? "+classifierAccuracy); 
		
		return classifierAccuracy; 
	}
	
	
	

	
	
	
	
	
	
	
	
	
	public JPanel renderControls(ViewerPanel vv) {
		return null; 
    }
	
    public java.awt.Graphics renderGeneration(Candidate[] cands, Test[] tests, java.awt.Graphics bg, java.util.Random viewerSpecificRandom) {
        int width = (int) getDimension().getWidth();
        int height = (int) getDimension().getHeight();
        		
        // clear the screen
        bg.setColor(Color.white);
        bg.fillRect(0, 0, width, height);
		
        // draw the coordinate axes
        bg.setColor(Color.black);
        bg.setFont(new Font("sansserif", Font.PLAIN, 8));
        bg.drawLine(0, height-1, width, height-1);
		
		for (int i=0; i<=width; i+=width/10) {
            bg.drawString(""+(i/scalar), i - 10, height-7);
            bg.drawLine(i, height-1, i, height-5);
        }
		
        // draw tests in red
        bg.setColor(testColor);
		double[] densities = new double[tests.length];
        for (int i=0; i<tests.length; i++) {
            densities[i] = ((TDMajorityTask)tests[i]).calculateDensity();
            double density = densities[i]; 
			bg.fillRect((int) (scalar * density) - (barWidth/2), 0, barWidth, height);
        }
		
		return bg; 
	}
	
	
	public Graphics renderObjectivePrep(Graphics bg) {
		int width = (int) getObjectiveFitDimension().getWidth();
        int height = (int) getObjectiveFitDimension().getHeight();

		bg.setColor(Color.white);
		bg.fillRect(0, 0, width, height);
		
		// draw the coordinate axes
		bg.setColor(Color.black);
		bg.setFont(new Font("sansserif", Font.PLAIN, 8));
		bg.drawLine(0, height/2, width, height/2);
		
		for (int i=0; i<width; i+=50) {
			bg.drawString(""+i, i, height/2 + 15);
			bg.drawLine(i, height/2-2, i, height/2+2);
		}

		return bg; 
	}
	
	public java.awt.Graphics renderObjectiveFitness(int genNum, Candidate[] cands, Test[] tests, java.awt.Graphics bg, java.util.Random viewerSpecificRandom) {
        int width = (int) getObjectiveFitDimension().getWidth();
        int height = (int) getObjectiveFitDimension().getHeight();
		
		// draw tests in red
        bg.setColor(testColor);
		for (int i=0; i<tests.length; i++) {
			System.out.println("Placeholder below! Note that always using first candidate.");
			double val = assessDifficulty(cands[0], tests[i], viewerSpecificRandom); 
			// double val = tests[i].getObjectiveFitness(); 
			System.out.println("Difficulty Val: "+val); 
			bg.fillRect(genNum, (height/2)-(int)(scalar*val) - (barWidth/2), 1, barWidth); 
		}		
		
		
        // draw candidates in blue
        bg.setColor(candColor);
		if ((genNum % evaluation_frequency) == 0) {
			for (int i=0; i<cands.length; i++) {
	
				int successCount = 0; 
				for (int j=0; j<evaluation_trials; j++) {
					// first get a uniformly sampled test. 
					TDMajorityTask task = (TDMajorityTask) tests[0].clone();
					task.randomize(viewerSpecificRandom); 
					Test uniformlySampledTest = (Test) task; 
					successCount += (assessAccuracy(cands[i], uniformlySampledTest, viewerSpecificRandom))?1:0;  
				}
				
				double val = 1.0 * successCount / evaluation_trials;  
				// System.out.println("Classification Accuracy: "+successCount + " / " + evaluation_trials); 
				bg.fillRect(genNum, (height)-(int)(scalar*val/2) - (barWidth/2), evaluation_frequency, barWidth); 
			}
		}
		
		

        return bg;
	}
	
	// to allocate screen space for rendering game panels. 
	public Dimension getDimension() {			        return new Dimension(200, 100);		        } 
    public Dimension getObjectiveFitDimension() {	    return new Dimension(genCount, 200);    	}
    public void setGenCount(int g) { 			        genCount = g;                               }	
	
	}
