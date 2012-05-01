package coeviz.visualization.views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import coeviz.visualization.views.elements.*;
import coeviz.visualization.ViewerPanel;


public class ProgressBarViewPanel extends ViewerPanel {

    private JProgressBar bar;
    private int genCount;
    private int currentCount;

    public ProgressBarViewPanel(String runDir, int genCount) {
        super(runDir);

        currentCount = 0;
        this.genCount = genCount;
        
        bar = new JProgressBar(0, genCount);
        bar.setValue(currentCount);
        bar.setStringPainted(true);
        bar.setString("(Paused)");
        bar.setFont(new Font("SansSerif", Font.PLAIN, 10));


		int barHeight = 30; 
        Dimension size = new Dimension(150,barHeight);
        Dimension minSize = new Dimension(20,barHeight);
        Dimension maxSize = new Dimension(10000,barHeight);
		
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMinimumSize(minSize);
        this.setMaximumSize(maxSize);
        this.setOpaque(false);

        bar.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridLayout grid = new GridLayout();
        grid.setColumns(1);
        grid.setRows(1);
        this.setLayout(grid);
        this.add(bar);
    }


    public void updateProgressBar(int i) {
		currentCount = i;

        if (currentCount < genCount-1) {
            bar.setValue(i);
            bar.setString("Completed " + i + " of " + genCount + " generations...");
        }
        else {
            bar.setValue(0);
            bar.setString("Done.");
        }
    }
	
	public void pause() {
		bar.setValue(0); 
		if (currentCount < (genCount-1))
			bar.setString("PAUSED: at generation " + currentCount + " of " + genCount); 
		else 
			bar.setString("Done"); 
	}
	
	public void prepare() {}
	public void autoSaveImage() {}
    public String getName() {
        return "Progress Bar";
    }
    public boolean initiallyVisible() {
        return true;
    }
}