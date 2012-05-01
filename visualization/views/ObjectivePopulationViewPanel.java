package coeviz.visualization.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*; 
import java.awt.event.*;

import coeviz.visualization.*;
import coeviz.framework.interfaces.*;
import coeviz.visualization.views.elements.*;
import coeviz.visualization.ViewerPanel;


public class ObjectivePopulationViewPanel extends ViewerPanel implements RecordableToFile {

    private Graphics bufferGraphics;
    private Game game;
    private Image buffer;
    private ChildPanel cp;
	private Vector logs; 
	private Viewer v; 

    public ObjectivePopulationViewPanel(String runDir, Game game, Viewer v) {
        super(runDir);

        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[6] Objective Fitnesses");
        border.setTitleColor(Color.black);
        this.setBorder(border);

		this.v = v; 
        this.game = game;
        this.setOpaque(false);
		logs = new Vector();

        Dimension image_size;
		
		if (game instanceof Renderable) 
			image_size = ((Renderable) game).getObjectiveFitDimension();
		else image_size = new Dimension(0,0); 
		
        Dimension imagePlus_size = new Dimension((int)image_size.getWidth(),    (int)image_size.getHeight());
        Dimension panel_size     = new Dimension((int)image_size.getWidth()+20, (int)image_size.getHeight()+40);

        cp = new ChildPanel(imagePlus_size);

        cp.setAlignmentX(Component.CENTER_ALIGNMENT);
        cp.setAlignmentY(Component.CENTER_ALIGNMENT);
        cp.setBorder(new LineBorder(Color.gray));

        this.setSize(panel_size);
        this.setPreferredSize(panel_size);
        this.setMinimumSize(panel_size);
        this.setMaximumSize(new Dimension(10000, (int) panel_size.getHeight()));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		this.add(cp);
    }

	
    public void doClick(MouseEvent e) {
        int i = e.getX();
		v.renderWins(i,i); 
    }
	
    public String getName() {
        return "Objective Fitness";
    }
    public boolean initiallyVisible() {
        return true;
    }
	
	// here we will write to file
	public String[] getFileNames() { 
		return new String[] {"objFitnessCands.txt", "objFitnessTests.txt"}; 
	}
	public Vector getLogFile() {
		return logs;
	}
	public void setLogFile(Candidate[] cands, Test[] tests) {
		logs = new Vector(); 
		
		String[] candVals = new String[cands.length];
		for (int i=0; i<candVals.length; i++) 
			candVals[i] = "" + cands[i].getObjectiveFitness(); 
		logs.add(candVals); 
		
		String[] testVals = new String[tests.length];
		for (int i=0; i<testVals.length; i++) 
			testVals[i] = "" + tests[i].getObjectiveFitness(); 
		logs.add(testVals); 
	}
	
    public void prepare() {
        cp.prepare();
        
		if (game instanceof Renderable) {
			bufferGraphics = ((Renderable)game).renderObjectivePrep(bufferGraphics); 
			cp.repaint();
		}
    }

    public void updateState(int gen, Candidate[] cands, Test[] tests, Random viewerSpecificRandom) {
		if (game instanceof Renderable) {
			bufferGraphics = ((Renderable)game).renderObjectiveFitness(gen, cands, tests, bufferGraphics, viewerSpecificRandom);
			cp.repaint();
		}
		setLogFile(cands, tests); 
    }

	
	public void autoSaveImage() {
		cp.autoSavePNG(); 
	}
	
	/************* Double-buffered child panel can save snapshot image to disk ***************/

	
	
    public class ChildPanel extends PanelWithContextualImageSave {
        Dimension size;
        boolean prepared = false;

        public ChildPanel(Dimension s) {
            super();
			setLoc(getCurrentImageDir());

            this.setOpaque(false);
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setAlignmentY(Component.CENTER_ALIGNMENT);

            size = s;
            this.setSize(size);
            this.setPreferredSize(size);
            this.setMaximumSize(size);
            this.setMinimumSize(size);
			
			this.setBorder(new LineBorder(Color.gray));
			
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) { 
					if (!e.isPopupTrigger()) doClick(e); 
				}
			});
        }

        public void prepare() {
            buffer = this.createImage((int)size.getWidth(),(int)size.getHeight()+2);
            bufferGraphics = buffer.getGraphics();
            prepared = true;
        }

        protected void paintComponent(Graphics g) {
            if (prepared) {
                super.paintComponent(g);
                g.drawImage(buffer,0,0,this);
            }
        }
		
		
		/************* For PanelWithContextualImageSave ***************/

		
		public Image getImage() {
			return buffer;
		}
		public String getImageType() {
			return "ObjectivePopulationView";
		}
		public int getGeneration() {
			return getGen();
		}
    }
}



