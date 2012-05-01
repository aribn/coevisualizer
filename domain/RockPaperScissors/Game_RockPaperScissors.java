package coeviz.domain.RockPaperScissors;

/*

import coeviz.framework.interfaces.*;
import coeviz.representation.RPS;
import coeviz.representation.RPSRep.*;
import coeviz.visualization.ViewerPanel;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


public class Game_RockPaperScissors implements Game, Renderable {
	
	
	private int genCount;

	// SHOWING THAT THE ORDER, BUT NOT THE VALUES, MATTERS. 
    public static final int CANDIDATE_PASSES	=  12;
    public static final int CANDIDATE_TIES      =  10;
    public static final int CANDIDATE_FAILS     =  -5;
	
	public int[] outcomesInOrder() {
		return new int[] {CANDIDATE_FAILS, CANDIDATE_TIES, CANDIDATE_PASSES}; 
	}
	public Color outcomesInColor(int outcome) {
		if      (outcome == CANDIDATE_FAILS)  return Color.black; 
		else if (outcome == CANDIDATE_TIES)   return Color.gray; 
		else if (outcome == CANDIDATE_PASSES) return Color.white; 
		else return null; 
	}
	public int neutralOutcome() {
		return CANDIDATE_TIES;
	}

	
	
	public static int getIntRepresentingLetter(String str) {
		for (int i=0; i<RPS.STRATEGIES.length; i++) 
			if (str.equals(RPS.STRATEGIES[i].substring(0,1))) 
				return i;
		return -1; 
	}
	


	// ************* For Game interface ***************

	
	public String getAcceptableCandidateInterface() {	return "RPS";  }
    public String getAcceptableTestInterface() {	    return "RPS";  }
	
	
    public int evaluateCandidate(Candidate c, Test t, Random r) {
		
        int cStrategy = ((RPS) c).getStrategy(r);
        int tStrategy = ((RPS) t).getStrategy(r);
		
		// hack to provide these representations access to previous move of other player.
		if (c instanceof RPS_CartlidgeBullockFSM) ((RPS_CartlidgeBullockFSM) c).opponentUsedStrategy(tStrategy);
		if (t instanceof RPS_CartlidgeBullockFSM) ((RPS_CartlidgeBullockFSM) t).opponentUsedStrategy(cStrategy);
		
		// define the cycle. 
        if       ((cStrategy == RPS.ROCK) &&     (tStrategy == RPS.ROCK)) 	    return CANDIDATE_TIES;
        else if  ((cStrategy == RPS.ROCK) &&     (tStrategy == RPS.PAPER))	    return CANDIDATE_FAILS;
        else if  ((cStrategy == RPS.ROCK) &&     (tStrategy == RPS.SCISSORS))	return CANDIDATE_PASSES;
        else if  ((cStrategy == RPS.PAPER) &&    (tStrategy == RPS.ROCK)) 	    return CANDIDATE_PASSES;
        else if  ((cStrategy == RPS.PAPER) &&    (tStrategy == RPS.PAPER)) 	    return CANDIDATE_TIES;
        else if  ((cStrategy == RPS.PAPER) &&    (tStrategy == RPS.SCISSORS))	return CANDIDATE_FAILS;
        else if  ((cStrategy == RPS.SCISSORS) && (tStrategy == RPS.ROCK)) 	    return CANDIDATE_FAILS;
        else if  ((cStrategy == RPS.SCISSORS) && (tStrategy == RPS.PAPER))     	return CANDIDATE_PASSES;
        else if  ((cStrategy == RPS.SCISSORS) && (tStrategy == RPS.SCISSORS)) 	return CANDIDATE_TIES;
        else									                                return CANDIDATE_TIES;
    }
	
	
	
	
	// ************ For Renderable interface ***************
	
	
	
	public Dimension getDimension() {			        return new Dimension(200, 40);		        } 
    public Dimension getObjectiveFitDimension() {	    return new Dimension(genCount, 120);    	}
    public void setGenCount(int g) { 			        genCount = g;                               }	
	
	
	
	public Graphics renderObjectivePrep(Graphics bg) {

		int rulerHeight = 12; 

        int width = (int) getObjectiveFitDimension().getWidth();
        int height = (int) getObjectiveFitDimension().getHeight();
        
		// clear the screen
        bg.setColor(Color.white);
        bg.fillRect(0, 0, width, height);
		
		
        // draw the coordinate axes
        bg.setColor(Color.black);
        bg.setFont(new Font("sansserif", Font.PLAIN, 8));
		
		bg.drawLine(0, (height/2)-(rulerHeight/2), width, (height/2)-(rulerHeight/2));
		bg.drawLine(0, (height/2)+(rulerHeight/2)-1, width, (height/2)+(rulerHeight/2)-1);
		
		for (int i=0; i<width; i+=50) {
			bg.drawString(""+i, i+1, (height/2)+(rulerHeight/2)-3);
			bg.drawLine(i, (height/2)-(rulerHeight/2), i, (height/2)+(rulerHeight/2)-1);
		}
		
		
		return bg; 
	}
	
    public Graphics renderGeneration(Candidate[] cands, Test[] tests, Graphics bg, Random viewerSpecificRandom) {

        int width = (int) getDimension().getWidth();
        int height = (int) getDimension().getHeight();

        // clear the screen
        bg.setFont(new Font("sansserif", Font.PLAIN, 9));
        bg.setColor(Color.white);
        bg.fillRect(0, 0, width, height+100);

        int[] candStrategyCounts = new int[3];
        int[] testStrategyCounts = new int[3];
        for (int i=0; i<candStrategyCounts.length; i++) candStrategyCounts[i]=0;
        for (int i=0; i<testStrategyCounts.length; i++) testStrategyCounts[i]=0;

        int barHeight = height/2;

        for (int i=0; i<cands.length; i++) {
            int strategy = ((RPS)cands[i]).getStrategy(viewerSpecificRandom);
            candStrategyCounts[strategy]++;
        }

        for (int i=0; i<tests.length; i++) {
            int strategy = ((RPS)tests[i]).getStrategy(viewerSpecificRandom);
            testStrategyCounts[strategy]++;
        }

        // draw candidates in blue
        int xStart = 0;
        bg.setColor(Color.blue.brighter());
        for (int i=0; i<candStrategyCounts.length; i++) {
            int stratBar = (int) Math.round(candStrategyCounts[i] * (1.0*width/cands.length));
            bg.fillRect(xStart, (height/2)-barHeight, stratBar, barHeight);
            //bg.drawString(RPS.STRATEGIES[i].substring(0,1)+"="+candStrategyCounts[i], xStart, height/2-barHeight);
            Color c = bg.getColor(); 
			bg.setColor(Color.white);
			bg.drawString(RPS.STRATEGIES[i].substring(0,1), xStart+stratBar/2, height/2); // -barHeight
			bg.drawString("" + candStrategyCounts[i], xStart+stratBar/2, height/2-barHeight+10);
			bg.setColor(c); 
            xStart += stratBar;
            bg.setColor(bg.getColor().darker());
        }

        // draw tests in red
        xStart = 0;
        bg.setColor(Color.red.brighter());
        for (int i=0; i<testStrategyCounts.length; i++) {
            int stratBar = (int) Math.round(testStrategyCounts[i] * (1.0*width/tests.length));
            bg.fillRect(xStart, (height/2), stratBar, barHeight);
            //bg.drawString(RPS.STRATEGIES[i].substring(0,1)+"="+testStrategyCounts[i], xStart, height/2+barHeight+8);
			Color c = bg.getColor(); 
			bg.setColor(Color.white); 
            bg.drawString(RPS.STRATEGIES[i].substring(0,1), xStart+stratBar/2, height/2+8); // +barHeight
            bg.drawString(""+testStrategyCounts[i], xStart+stratBar/2, height/2+barHeight-2);
			bg.setColor(c); 
            xStart += stratBar;
            bg.setColor(bg.getColor().darker());
        }

		bg.setColor(Color.black); 
		bg.drawLine(0, height/2, width, height/2); 
		
        return bg;
    }



    public Graphics renderObjectiveFitness(int genNum, Candidate[] cands, Test[] tests, Graphics bg, Random viewerSpecificRandom) {

		int rulerHeight = 12; 

        int width = (int) getObjectiveFitDimension().getWidth();
        int height = (int) getObjectiveFitDimension().getHeight();

        int[] candStrategyCounts = new int[3];
        int[] testStrategyCounts = new int[3];
        for (int i=0; i<candStrategyCounts.length; i++) candStrategyCounts[i]=0;
        for (int i=0; i<testStrategyCounts.length; i++) testStrategyCounts[i]=0;
		
        for (int i=0; i<cands.length; i++) {
            int strategy = ((RPS)cands[i]).getStrategy(viewerSpecificRandom);
            candStrategyCounts[strategy]++;
        }

        for (int i=0; i<tests.length; i++) {
            int strategy = ((RPS)tests[i]).getStrategy(viewerSpecificRandom);
            testStrategyCounts[strategy]++;
        }
        
        // draw candidates in blue
        int xStart = 0;
        bg.setColor(Color.blue.brighter());
        for (int i=0; i<candStrategyCounts.length; i++) {
            int stratBar = (int) Math.round(1.0 * ((height-rulerHeight) / 2) * candStrategyCounts[i] / cands.length);
            bg.fillRect(genNum, xStart, 1, stratBar);
            xStart += stratBar;
            bg.setColor(bg.getColor().darker());
        }

        // draw tests in red
        xStart = 0;
        bg.setColor(Color.red.brighter());
        for (int i=0; i<testStrategyCounts.length; i++) {
            int stratBar = (int) Math.round(1.0 * ((height-rulerHeight) / 2) * testStrategyCounts[i] / tests.length);
            bg.fillRect(genNum, (height-((height-rulerHeight) / 2))+xStart, 1, stratBar);
            xStart += stratBar;
            bg.setColor(bg.getColor().darker());
        }

        return bg;
    }
	
	public JPanel renderControls(ViewerPanel vv) {
        return null;
    }
}
*/
