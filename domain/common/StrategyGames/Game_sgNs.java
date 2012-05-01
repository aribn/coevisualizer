package coeviz.domain.common.StrategyGames;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import coeviz.framework.interfaces.*;
import coeviz.visualization.ViewerPanel;
import coeviz.representation.Strategy;
import coeviz.representation.common.Strategy.OpponentDriven_FSM;

public abstract class Game_sgNs implements Game {

	private int genCount;
	
	private int candCount_wins   = 0;  
	private int candCount_ties   = 0;  
	private int candCount_fails  = 0;  
	
	// SHOWING THAT THE ORDER, BUT NOT THE VALUES, MATTERS. 
    public static final int CANDIDATE_PASSES	=  12;
    public static final int CANDIDATE_TIES      =  10;
    public static final int CANDIDATE_FAILS     =  -5;
	
	public int[] outcomesInOrder() {
		return new int[] {
			CANDIDATE_FAILS, 
			CANDIDATE_TIES, 
			CANDIDATE_PASSES
		}; 
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

	
	
	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}
	
	
	
	public int evaluateTest(Candidate c, Test t, Random r) {
		return evaluateCandidate(c,t,r); 
	}
	
	
    public int evaluateCandidate(Candidate c, Test t, Random r) {
		
		if ((c instanceof OpponentDriven_FSM) || (t instanceof OpponentDriven_FSM)) {
			
			candCount_wins   = 0;  
			candCount_ties   = 0;  
			candCount_fails  = 0;  
			
			// reset to initial state before bouts. 
			if (c instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) c).resetStrategy();
			if (t instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) t).resetStrategy();
			
			// 10 comparisons. 
			for (int i=0; i<10; i++) {
				int cStrategy = ((Strategy) c).getStrategy(r);
				int tStrategy = ((Strategy) t).getStrategy(r);
				
				String[] strategyNames = getStrategyNames(); 
				
				String candStrat = strategyNames[cStrategy];
				String testStrat = strategyNames[tStrategy];
				
				// record opponent's strategy. 
				if (c instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) c).opponentUsedStrategy(tStrategy);
				if (t instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) t).opponentUsedStrategy(cStrategy);
				
				int result = evaluateCandidate_sg(candStrat, testStrat); 
				
				if      (result == CANDIDATE_PASSES) candCount_wins++; 
				else if (result == CANDIDATE_TIES)   candCount_ties++; 
				else if (result == CANDIDATE_FAILS)  candCount_fails++; 
			}
			
			// reset to initial state after bouts. 
			if (c instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) c).resetStrategy();
			if (t instanceof OpponentDriven_FSM) ((OpponentDriven_FSM) t).resetStrategy();
			
			
			// and then return the winner of the bouts.
			if      (candCount_fails >  candCount_wins) return CANDIDATE_FAILS; 
			else if (candCount_fails <  candCount_wins) return CANDIDATE_PASSES; 
			else if (candCount_fails == candCount_wins) return CANDIDATE_TIES; 
			else return -1; 
			
		}
		else {
			return evaluateCandidate_sg(getStrategyNames()[((Strategy) c).getStrategy(r)], 
										getStrategyNames()[((Strategy) t).getStrategy(r)]);
			
		}
	}
	
	public int[] getLastOutcomeTally() {
		return new int[] {candCount_wins, candCount_ties, candCount_fails}; 
	}
	
	public abstract int evaluateCandidate_sg(String candStrat, String testStrat);
	public abstract String[] getStrategyNames();
	
	public String getAcceptableCandidateInterface() {	return "Strategy";	}
    public String getAcceptableTestInterface() {	    return "Strategy";	}
	
	
	
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
		
        int[] candStrategyCounts = new int[getStrategyNames().length];
        int[] testStrategyCounts = new int[getStrategyNames().length];
        for (int i=0; i<candStrategyCounts.length; i++) candStrategyCounts[i]=0;
        for (int i=0; i<testStrategyCounts.length; i++) testStrategyCounts[i]=0;
		
        int barHeight = height/2;
		
        for (int i=0; i<cands.length; i++) {
            int strategy = ((Strategy)cands[i]).getStrategy(viewerSpecificRandom);
            candStrategyCounts[strategy]++;
        }
		
        for (int i=0; i<tests.length; i++) {
            int strategy = ((Strategy)tests[i]).getStrategy(viewerSpecificRandom);
            testStrategyCounts[strategy]++;
        }
		
        // draw candidates in blue
        int xStart = 0;
        bg.setColor(Color.blue.brighter());
        for (int i=0; i<candStrategyCounts.length; i++) {
            int stratBar = (int) Math.round(candStrategyCounts[i] * (1.0*width/cands.length));
            bg.fillRect(xStart, (height/2)-barHeight, stratBar, barHeight);
            //bg.drawString(getStrategyNames()[i].substring(0,1)+"="+candStrategyCounts[i], xStart, height/2-barHeight);
            Color c = bg.getColor(); 
			bg.setColor(Color.white);
			bg.drawString(getStrategyNames()[i].substring(0,1), xStart+stratBar/2, height/2); // -barHeight
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
            //bg.drawString(getStrategyNames()[i].substring(0,1)+"="+testStrategyCounts[i], xStart, height/2+barHeight+8);
			Color c = bg.getColor(); 
			bg.setColor(Color.white); 
            bg.drawString(getStrategyNames()[i].substring(0,1), xStart+stratBar/2, height/2+8); // +barHeight
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
		
        int[] candStrategyCounts = new int[getStrategyNames().length];
        int[] testStrategyCounts = new int[getStrategyNames().length];
        for (int i=0; i<candStrategyCounts.length; i++) candStrategyCounts[i]=0;
        for (int i=0; i<testStrategyCounts.length; i++) testStrategyCounts[i]=0;
		
        for (int i=0; i<cands.length; i++) {
            int strategy = ((Strategy)cands[i]).getStrategy(viewerSpecificRandom);
            candStrategyCounts[strategy]++;
        }
		
        for (int i=0; i<tests.length; i++) {
            int strategy = ((Strategy)tests[i]).getStrategy(viewerSpecificRandom);
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