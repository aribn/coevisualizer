package coeviz.domain.common.NumbersGames;

import coeviz.framework.interfaces.*;
import coeviz.representation.ANumber;
import coeviz.visualization.views.*;
import coeviz.visualization.ViewerPanel;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


public abstract class Game_ng2d extends Game_ngNd implements Renderable {

    public int getNumberOfDimensions() { return 2; }
	
	
	/************* For Renderable interface ***************/

	
	

    public int genCount;
	public double scalar = 1;
   
	
	public Dimension getDimension() {			    return new Dimension(200,200);		    }
    public Dimension getObjectiveFitDimension() {	return new Dimension(genCount,300);	    }
    public void setGenCount(int g) { 			    genCount = g;			              	}
	
	
    public Graphics renderGeneration(Candidate[] cands, Test[] tests, Graphics bg, Random viewerSpecificRandom) {

        int width = (int) getDimension().getWidth();
        int height = (int) getDimension().getHeight();
        
        // clear the screen
        bg.setColor(Color.white);
        bg.fillRect(0, 0, width, height+100);

        // draw the coordinate axes
        bg.setColor(Color.black);
        bg.setFont(new Font("sansserif", Font.PLAIN, 8));
        bg.drawLine(width/2, 0, width/2, height);
        bg.drawLine(0, height/2, width, height/2);

        for (int i=-1*width/2; i<width/2; i+=width/10) {
            bg.drawString(""+(int)((double)i/scalar), width/2 + i, height/2-10);
            bg.drawLine(width/2 + i, height/2-2, width/2 + i, height/2+2);
        }
        for (int i=-1*height/2; i<height/2; i+=height/10) {
            bg.drawString(""+(int)((double)i/scalar), width/2+10, height/2 - i);
            bg.drawLine(width/2-2, height/2 - i, width/2+2, height/2 - i);
        }


        // draw candidates in blue
        bg.setColor(Color.blue);
        for (int i=0; i<cands.length; i++) {
            int[] coords = ((ANumber)cands[i]).getVals();
            bg.fillRect(width/2 + (int) (scalar * coords[0]), height/2 - (int) (scalar * coords[1]), 3, 3);
        }

        // draw tests in red
        bg.setColor(Color.red);
        for (int i=0; i<tests.length; i++) {
            int[] coords = ((ANumber)tests[i]).getVals();
            bg.fillRect(width/2 + (int) (scalar * coords[0]), height/2 - (int) (scalar * coords[1]), 3, 3);
        }

        return bg;
    }

	
	
    public Graphics renderObjectiveFitness(int genNum, Candidate[] cands, Test[] tests, Graphics bg, Random viewerSpecificRandom) {
        
        int width = (int) getObjectiveFitDimension().getWidth();
        int height = (int) getObjectiveFitDimension().getHeight();
		
        // draw candidates in blue
        bg.setColor(Color.blue);
		for (int i=0; i<cands.length; i++) {
			double val = cands[i].getObjectiveFitness(); 
			bg.fillRect(genNum, (height/2)-(int)(scalar*val), 1,1); 
		}
      
        // draw tests in red
        bg.setColor(Color.red);
		for (int i=0; i<tests.length; i++) {
			double val = tests[i].getObjectiveFitness(); 
			bg.fillRect(genNum, (height/2)-(int)(scalar*val), 1,1); 
		}		
        return bg;
    }

	
	
	public Graphics renderObjectivePrep(Graphics bg) {
		
        int width = (int) getObjectiveFitDimension().getWidth();
        int height = (int) getObjectiveFitDimension().getHeight();
		
        // clear the screen
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
        for (int i=-1*height/2; i<height/2; i+=height/10) {
            bg.drawString(""+(int)((double)i/scalar), 10, height/2 - i);
            bg.drawLine(0, height/2 - i, 4, height/2 - i);
        }
		
		return bg; 
	}

    public JPanel renderControls(ViewerPanel vv) {
		
		if ((vv instanceof GamePopulationViewPanel) || (vv instanceof ObjectivePopulationViewPanel)) {
			
			JPanel controls = new JPanel();
			controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

			JLabel label = new JLabel("zoom");
			
			JButton zoom_in = new JButton("+");
			zoom_in.setVerticalTextPosition(AbstractButton.CENTER);
			zoom_in.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
			zoom_in.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { scalar *= 2; } });
			zoom_in.setToolTipText("Click to zoom in.");
			
			JButton zoom_out = new JButton("-");
			zoom_out.setVerticalTextPosition(AbstractButton.CENTER);
			zoom_out.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
			zoom_out.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { scalar /= 2; } });
			zoom_out.setToolTipText("Click to zoom out.");
			
			controls.add(zoom_out);
			controls.add(label);
			controls.add(zoom_in);
			
			return controls;
		} 
		else return null; 
    }
}
