package coeviz.visualization.views;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import coeviz.visualization.*;
import coeviz.visualization.views.elements.*;
import coeviz.framework.interfaces.*;
import coeviz.visualization.ViewerPanel;


public class ProgressLocalViewPanel extends ViewerPanel implements RecordableToFile {

    private Graphics bufferGraphics;
    private Image buffer;
    private int maxPixel;
    private int memWin;
	private int memUpdateFreq; 
	private Vector logs;
    private ChildPanel cp;
    
    public ProgressLocalViewPanel(String runDir, int memWin, int maxPixel, int memUpdateFreq) {
        super(runDir);

        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[3] Current Memory");
        border.setTitleColor(Color.black);
        this.setBorder(border);
        
        this.setOpaque(false);
        this.maxPixel = maxPixel;
        this.memWin = memWin;
		this.memUpdateFreq = memUpdateFreq; 

		logs = new Vector(); 

        Dimension size = new Dimension(memWin, memWin);
        Dimension bigger = new Dimension(memWin+20, memWin+50);
        this.setSize(bigger);
        this.setPreferredSize(bigger);
        this.setMinimumSize(bigger);
        this.setMaximumSize(new Dimension(10000, (int)bigger.getHeight()));

        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);

        cp = new ChildPanel(size);

        cp.setAlignmentX(Component.CENTER_ALIGNMENT);
        cp.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        this.add(cp);
        
    }

    
    public void prepare() {
        cp.prepare();
        bufferGraphics.setColor(Color.red);
        bufferGraphics.fillRect(0,0,memWin,memWin);
        cp.repaint();
    }

    
    public String getName() {
        return "Current Memory";
    }
    public boolean initiallyVisible() {
        return false;
    }
	
	public void doPaint() {
        cp.repaint();
    }
    
    public void colorBottomPixel(int y, int color) {
        float c = (1.0f * color / maxPixel);
        bufferGraphics.setColor(new Color(c,c,c));
        bufferGraphics.fillRect(y, memWin-1, 1,1);
    }
    
    public void colorRightPixel(int x, int color) {
        float c = (1.0f * color / maxPixel);
        bufferGraphics.setColor(new Color(c,c,c));
        bufferGraphics.fillRect(memWin-1, x, 1,1);
    }

    public void shiftPastPixels() {
        bufferGraphics.copyArea(1,1,  memWin-1,memWin-1,  -1,-1);  
    }

	
	public void updateState(int gen, /* Game game, Candidate[] cands, Test[] tests, ArrayList cand_memory, ArrayList test_memory, */
							int[] newest_cand_results, int[] newest_test_results /*, Random r */) {
		
		if (((gen % memUpdateFreq) == 0) && (memWin!=1)) {
			shiftPastPixels();
			int start = gen-(memUpdateFreq*(memWin-1));
			for (int mem_index = 0; mem_index < memWin; mem_index++) {
				if (start+(memUpdateFreq*mem_index) >= 0) {
					colorBottomPixel(mem_index, newest_cand_results[mem_index]);
					colorRightPixel( mem_index, newest_test_results[mem_index]);
				}
			}
		}
		
		/*
		// calculate results
		int[] cand_results = Viewer.getCandResults(game, cands, test_memory, r);
		int[] test_results = Viewer.getTestResults(game, tests, cand_memory, r);
		setLogFile(cand_results, test_results); 
		 */
		
		setLogFile(newest_cand_results, newest_test_results); 
	}
	
	
	// here we will write to file
	public String[] getFileNames() { 
		return new String[] {"candResults.txt", "testResults.txt"}; 
	}
	public Vector getLogFile() {
		return logs;
	}
	public void setLogFile(int[] cand_results, int[] test_results) {
		logs = new Vector(); 
		
		String[] candStrings = new String[cand_results.length];
		for (int i=0; i<candStrings.length; i++) 
			candStrings[i] = "" + cand_results[i]; 
		String[] testStrings = new String[test_results.length];
		for (int i=0; i<testStrings.length; i++) 
			testStrings[i] = "" + test_results[i]; 
		
		logs.add(candStrings); 
		logs.add(testStrings);
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
            this.setAlignmentY(Component.CENTER_ALIGNMENT);

            size = s;
            this.setSize(size);
            this.setPreferredSize(size);
            this.setMaximumSize(size);
            this.setMinimumSize(size);

            this.setBorder(new LineBorder(Color.gray));
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

		public String getImageType() {
			return "ProgressLocalView";
		}
		public Image getImage() {
			return buffer;
		}
		public int getGeneration() {
			return getGen();
		}
    }
}

