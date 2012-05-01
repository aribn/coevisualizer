package coeviz.domain.RaschModelLearningGame;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import coeviz.representation.TD2Challenge;
import coeviz.representation.TD2SingleResponder;
import coeviz.framework.interfaces.*; 
import coeviz.framework.*; 
import coeviz.visualization.views.*;
import coeviz.visualization.ViewerPanel;

public class Game_RaschModelLearningGame extends TeachersDilemma {

	/*
	private static final String CALC_PAYOFFS_FROM_ITEM_DIFFICULTY                   = "difficulty"; 
	private static final String CALC_PAYOFFS_FROM_HALF_ITEM_DIFFICULTY              = "halfdifficulty"; 
	//private static final String CALC_PAYOFFS_FROM_PROBABILITY_ACCURATE_RESPONSE     = "probability"; 
	//private static final String CALC_PAYOFFS_FROM_GRADE_AGGREGATE_ITEM_DIFFICULTY   = "gradelevel"; 
	private static final String CALC_PAYOFFS_FROM_RANDOM                            = "random"; 
	*/
	
	// EXPERIMENTAL VARIABLES: 
	//private String calcPayoffsFrom = CALC_PAYOFFS_FROM_ITEM_DIFFICULTY;
	//private double epsilon = 0.0;  
	// private int gradeCount = 5; 
	
	private static int genCount;
	private double scalar = 20;
	private double max_range, min_range; 
	private boolean rangeSet = false; 
	private static final boolean debug = false; 
	private static final int barWidth = 10; 
	private static final Color testColor = new java.awt.Color(1.0f, 0.0f, 0.0f, 0.1f); 
	private static final Color candColor = new java.awt.Color(0.0f, 0.0f, 1.0f, 0.6f); 
	private boolean redrawAxis = false; 
	

	public String getAcceptableCandidateInterface() {	return "TD2SingleResponder";	}
    public String getAcceptableTestInterface() {	    return "TD2Challenge";	}
	
	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = super.getExperimentalVariables();
		// 
		// ht.put("gradeCount", ""+gradeCount); 
		//ht.put("epsilon", ""+epsilon); 
		//ht.put("calcPayoffsFrom", ""+calcPayoffsFrom); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		super.setExperimentalVariables(ht);
		//
		// try { epsilon = Double.parseDouble((String)ht.get("epsilon"));} catch (Exception e) { System.out.println("Using default value for epsilon"); }
		// try { gradeCount = Integer.parseInt((String)ht.get("gradeCount"));} catch (Exception e) { System.out.println("Using default value for gradeCount"); }
		// try { calcPayoffsFrom = ((String)ht.get("calcPayoffsFrom"));} catch (Exception e) { System.out.println("Using default value for calcPayoffsFrom"); }
	}
	
	
	public double assessDifficulty(Candidate c, Test t, Random r) {
		TD2Challenge challenge = (TD2Challenge) t; 
		TD2SingleResponder tutee = (TD2SingleResponder) c;
		double difficulty = challenge.getDifficulty(); 
		double scaled_difficulty = challenge.getScaledDifficulty(); 
		//double p_acc = tutee.probabilityOfAccurateResponseToChallengeDiff(difficulty); 
		double x; 
		
		/*
		 if (calcPayoffsFrom.equals(CALC_PAYOFFS_FROM_PROBABILITY_ACCURATE_RESPONSE)) {
			x = p_acc;
		} else if (calcPayoffsFrom.equals(CALC_PAYOFFS_FROM_GRADE_AGGREGATE_ITEM_DIFFICULTY)) {
			x = (Math.floor(p_acc * gradeCount) / gradeCount); 
		} else 
		 */
		/*
		if (calcPayoffsFrom.equals(CALC_PAYOFFS_FROM_ITEM_DIFFICULTY)) { 
			x = scaled_difficulty; 
		} else if (calcPayoffsFrom.equals(CALC_PAYOFFS_FROM_HALF_ITEM_DIFFICULTY)) { 
			x = (scaled_difficulty/2.0); 
		} else if (calcPayoffsFrom.equals(CALC_PAYOFFS_FROM_RANDOM)) {
			x = r.nextDouble(); 
		} else {
			x = -1; 
			new Exception("ERROR!!: calcPayoffsFrom value not recognized: \""+calcPayoffsFrom+"\"").printStackTrace(); 
		}
		
		x += (r.nextDouble() * 2.0 * epsilon) - epsilon; 
		if (x > 1.0) {x=1.0;} else if (x < 0.0) { x = 0.0; }
		*/
		// x = scaled_difficulty; 
		x = 1.0 - tutee.probabilityOfAccurateResponseToChallengeDiff(difficulty); 
		return x; 
	}
    
	public boolean assessAccuracy(Candidate c, Test t, Random r) {
		TD2Challenge challenge = (TD2Challenge) t; 
		TD2SingleResponder tutee = (TD2SingleResponder) c;
		double difficulty = challenge.getDifficulty(); 
		return (r.nextDouble() < tutee.probabilityOfAccurateResponseToChallengeDiff(difficulty));
	}
	
	

	
	
	
	
	
	
	
	
	
	public JPanel renderControls(ViewerPanel vv) {
		if ((vv instanceof GamePopulationViewPanel) || (vv instanceof ObjectivePopulationViewPanel)) {
			
			JPanel controls = new JPanel();
			controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
			
			JLabel label = new JLabel("zoom");
			
			JButton zoom_in = new JButton("+");
			zoom_in.setVerticalTextPosition(AbstractButton.CENTER);
			zoom_in.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
			zoom_in.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) { 
					scalar *= 2; 
					redrawAxis = true; 
				} 
			});
			zoom_in.setToolTipText("Click to zoom in.");
			
			JButton zoom_out = new JButton("-");
			zoom_out.setVerticalTextPosition(AbstractButton.CENTER);
			zoom_out.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
			zoom_out.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) { 
					scalar /= 2; 
					redrawAxis = true; 
				} 
			});
			zoom_out.setToolTipText("Click to zoom out.");
			
			controls.add(zoom_out);
			controls.add(label);
			controls.add(zoom_in);
			
			return controls;
		} 
		else return null; 
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
   
        for (int i=-1*width/2; i<=width/2; i+=width/10) {
            bg.drawString(""+(i/scalar), width/2 + i - 10, height-7);
            bg.drawLine(width/2 + i, height-1, width/2 + i, height-5);
        }
		
        // draw candidate(s) in blue
        bg.setColor(candColor);
        bg.drawLine(0, height/2, width, height/2);

        for (int i=0; i<cands.length; i++) {
            double ability = ((TD2SingleResponder)cands[i]).getAbility();
			bg.fillRect(width/2 + (int) (scalar * ability), height/2 - 2, 2, 4);
        
			double[] alldiffs = new double[tests.length]; 
			for (int j=0; j<tests.length; j++) 
				alldiffs[j] = ((TD2Challenge)tests[j]).getDifficulty(); 
			((TD2SingleResponder) cands[i]).noteAllChallengeDiffs(alldiffs); 
			
			double[][] xy = ((TD2SingleResponder) cands[i]).getDiscretizedIRTCurve(); 
			int lastX = (width/2 + (int) (1.0 * scalar * xy[0][0])); // scalar->width 
			int lastY = (height - ((int) (height * xy[0][1]))); 
			for (int j=0; j<xy.length; j++) {
				int nextX = (width/2 + (int) (1.0 * scalar * xy[j][0])); // scalar->width
				int nextY = (height - ((int) (height * xy[j][1]))); 
				bg.drawLine(lastX,lastY,nextX,nextY);
				lastX = nextX; 
				lastY = nextY; 
			}
		}
		
		
        // draw tests in red
        bg.setColor(testColor);
		double[] diffs = new double[tests.length];
        for (int i=0; i<tests.length; i++) {
            diffs[i] = ((TD2Challenge)tests[i]).getDifficulty();
            double difficulty = diffs[i]; 
			bg.fillRect(width/2 + (int) (scalar * difficulty) - (barWidth/2), 0, barWidth, height);
        }
				
		// gray lines at outside of smaller range. 
		double center = (int) (scalar * min_range); 
		bg.setColor(Color.gray);
		int d = (int) Math.floor((width-center)/2); 
		bg.drawLine(d, 0, d, height); 
		bg.drawLine(width-d, 0, width-d, height); 
        
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
		
	
		bg.setColor(Color.gray);
		int diff = (int)(scalar*min_range/2.0); 
		bg.drawLine(0, (height/2)-diff, genCount, (height/2)-diff); // -barWidth/2
		bg.drawLine(0, (height/2)+diff, genCount, (height/2)+diff); // +barWidth/2
		
		return bg; 
	}
	
	public java.awt.Graphics renderObjectiveFitness(int genNum, Candidate[] cands, Test[] tests, java.awt.Graphics bg, java.util.Random viewerSpecificRandom) {
		
        int width = (int) getObjectiveFitDimension().getWidth();
        int height = (int) getObjectiveFitDimension().getHeight();
		
		
		// do initial setup. can't do it in next method because I need access to tests and cands. 
		if (!rangeSet) { 
			double c_range = (((TD2SingleResponder) cands[0]).getRange());
			double t_range = (((TD2Challenge) tests[0]).getRange()); 
			max_range = c_range; if (t_range>c_range) max_range = t_range; 
			min_range = c_range; if (t_range<c_range) min_range = t_range; 
			scalar = (int) Math.floor(height / max_range); 
			rangeSet = true; 
			bg.setColor(Color.black);
			
			for (int i=0; i<width; i+=50) {
				bg.drawString(""+i, i, height/2 + 15);
				bg.drawLine(i, height/2-2, i, height/2+2);
			}
			for (int i=-1*height/2; i<height/2; i+=height/10) {
				bg.drawString(""+(i/scalar), 10, height/2 - i + 5);
				bg.drawLine(0, height/2 - i, 4, height/2 - i);
			}
		}
		
        // draw tests in red
        bg.setColor(testColor);
		for (int i=0; i<tests.length; i++) {
			double val = tests[i].getObjectiveFitness(); 
			bg.fillRect(genNum, (height/2)-(int)(scalar*val) - (barWidth/2), 1, barWidth); 
		}		
		
        // draw candidates in blue
        bg.setColor(candColor);
		for (int i=0; i<cands.length; i++) {
			double val = cands[i].getObjectiveFitness(); 
			bg.fillRect(genNum, (height/2)-(int)(scalar*val), 1,1); 
		}      
		
		// black out outside the range. 
		bg.setColor(Color.black);
		bg.setFont(new Font("sansserif", Font.PLAIN, 8));
		
		
		// gray lines on smaller range borders. 
		bg.setColor(Color.gray);
		double c_range = (((TD2SingleResponder) cands[0]).getRange());
		double t_range = (((TD2Challenge) tests[0]).getRange()); 
		double min_range = c_range; if (t_range<c_range) min_range = t_range; 		
		int diff = (int)(scalar*min_range/2.0); 
		bg.drawLine(genNum, (height/2)-diff, genNum, (height/2)-diff-1); // -barWidth/2
		bg.drawLine(genNum, (height/2)+diff, genNum, (height/2)+diff+1); // +barWidth/2
		
		// draw a new y axis. 
		if (redrawAxis) {
			for (int i=-1*height/2; i<height/2; i+=height/10) {
				bg.drawString(""+(i/scalar), genNum + 10, height/2 - i + 5);
				bg.drawLine(genNum, height/2 - i, genNum + 4, height/2 - i);
				bg.drawLine(genNum, 0, genNum, height); 
			}
			redrawAxis = false; 
		}
		 
		
		
        return bg;
	}
	
	// to allocate screen space for rendering game panels. 
	public Dimension getDimension() {			        return new Dimension(200, 100);		        } 
    public Dimension getObjectiveFitDimension() {	    return new Dimension(genCount, 200);    	}
    public void setGenCount(int g) { 			        genCount = g;                               }	
	
	}
