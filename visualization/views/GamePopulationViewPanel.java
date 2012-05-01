package coeviz.visualization.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*; 
import java.awt.event.*;

import coeviz.framework.interfaces.*;
import coeviz.visualization.views.elements.*;
import coeviz.visualization.ViewerPanel;


public class GamePopulationViewPanel extends ViewerPanel implements RecordableToFile {

    private Graphics bufferGraphics;
    private Image buffer;
    private ChildPanel cp;
	protected Vector logs;
    protected Game game;
	protected Candidate[] cPop;
	protected Test[] tPop;
	
	
    public GamePopulationViewPanel(String runDir, Game game) {
        super(runDir);

        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[2] Populations");
        border.setTitleColor(Color.black);
        this.setBorder(border);
        
        this.game = game;
        this.setOpaque(false);

		logs = new Vector(); 

        Dimension image_size;
		if (game instanceof Renderable) 
			image_size = ((Renderable)game).getDimension();
		else image_size = new Dimension(0,0); 
		
        Dimension panel_size = new Dimension((int)image_size.getWidth()+20, (int)image_size.getHeight()+50);
        
        cp = new ChildPanel(image_size);
		
        cp.setAlignmentX(Component.CENTER_ALIGNMENT);
        cp.setAlignmentY(Component.CENTER_ALIGNMENT);

        cp.setBorder(new LineBorder(Color.gray));
        
        this.setSize(panel_size);
        this.setPreferredSize(panel_size);
        this.setMinimumSize(panel_size);
        this.setMaximumSize(new Dimension(10000, (int)panel_size.getHeight()));

        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(cp);

        Dimension scroll_size = new Dimension(10+(int)image_size.getWidth(), 10+(int)image_size.getHeight());

        jsp.setSize(scroll_size);
        jsp.setPreferredSize(scroll_size);
        jsp.setMaximumSize(scroll_size);

        this.add(cp);
    }
	
	
	// here we will write to these two files
	public String[] getFileNames() { 
		return new String[] {"cands.txt", "tests.txt"}; 
	}
	public Vector getLogFile() {
		return logs;
	}
	public void setLogFile(Candidate[] cands, Test[] tests) {
		logs = new Vector(); 

		String[] candStrings = new String[cands.length];
		for (int i=0; i<candStrings.length; i++) 
			candStrings[i] = cands[i].toString(); 
		String[] testStrings = new String[tests.length];
		for (int i=0; i<testStrings.length; i++) 
			testStrings[i] = tests[i].toString(); 
		
		logs.add(candStrings); 
		logs.add(testStrings);
	}
	
	
	
	
	
    public void prepare() {
        cp.prepare();
        bufferGraphics.setColor(Color.gray);
		if (game instanceof Renderable) 
			bufferGraphics.fillRect(0,0, 
									(int) ((Renderable) game).getDimension().getWidth(), 
									(int) ((Renderable) game).getDimension().getHeight());
		cp.repaint();
    }

	
    public void updateState(Candidate[] cands, Test[] tests, Random viewerSpecificRandom) {
		if (game instanceof Renderable) {
			bufferGraphics = ((Renderable)game).renderGeneration(cands, tests, bufferGraphics, viewerSpecificRandom);
			cp.repaint();
		}
		cPop = cands;
		tPop = tests; 
		
		setLogFile(cands, tests); 
    }
	
	
	
    
    public String getName() {
        return "Populations";
    }
    public boolean initiallyVisible() {
        return true;
    }
	public void autoSaveImage() {
		//cp.autoSavePNG(); 
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
            
            size = s;
            this.setSize(size);
            this.setPreferredSize(size);
            this.setMaximumSize(size);
            this.setMinimumSize(size);
			
			
			
			JMenuItem gecco = new JMenuItem("Save as Gecco Initial Pop...");
			gecco.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) { recordPops(game, cPop, tPop); } 
            });
			//popup.add(gecco);
			addMenuItem(gecco);
			
				
        }

        public void prepare() {
            buffer = this.createImage((int)size.getWidth(),(int)size.getHeight());
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
			return "GamePopulationView";
		}
		public int getGeneration() {
			return getGen();
		}
    }
}



