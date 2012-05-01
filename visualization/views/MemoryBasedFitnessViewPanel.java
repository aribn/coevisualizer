package coeviz.visualization.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*; 
import java.awt.event.*;

import coeviz.visualization.*;
import coeviz.visualization.views.elements.*;
import coeviz.framework.interfaces.*;
import coeviz.visualization.ViewerPanel;


public class MemoryBasedFitnessViewPanel extends ViewerPanel implements RecordableToFile {

    private Graphics bufferGraphics;
    private Image buffer;

    private int maxPixel;
    private int memWin;
    private Dimension size;
    private ChildPanel cp;
	private Vector logs; 
	private Viewer v; 
	private int memUpdateFreq; 
    int lastComboFitness = 0;
	private double lastCandSubjFitness;
	private double lastTestSubjFitness; 	

	
    public MemoryBasedFitnessViewPanel(String runDir, int memWin, int maxPixel, int genCount, Viewer v, int memUpdateFreq) {
        super(runDir);

        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[7] Subjective Fitness (Memory-Based)");
        border.setTitleColor(Color.black);
        this.setBorder(border);

		this.v = v; 
        this.setOpaque(false);
        this.maxPixel = maxPixel;
        this.memWin = memWin;
		this.memUpdateFreq = memUpdateFreq; 

		lastCandSubjFitness     = 0; 
		lastTestSubjFitness     = 0; 
		
		logs = new Vector(); 

        size = new Dimension(genCount, 100);
		
        Dimension withDists = new Dimension((int)size.getWidth(),    (int)size.getHeight());
        Dimension bigger    = new Dimension((int)size.getWidth()+20, (int)size.getHeight()+40);
        
		this.setSize(bigger);
        this.setPreferredSize(bigger);
        this.setMinimumSize(bigger);
        this.setMaximumSize(new Dimension(10000, (int) bigger.getHeight()));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);

        cp = new ChildPanel(withDists);
        cp.setAlignmentX(Component.CENTER_ALIGNMENT);
        cp.setAlignmentY(Component.CENTER_ALIGNMENT);
        cp.setBorder(new LineBorder(Color.gray));
		
        this.add(cp);
    }

	
    public void doClick(MouseEvent e) {
        int i = e.getX();
		v.renderWins(i,i); 
    }
	
	// here we will write to file
	public String[] getFileNames() { 
		return new String[] {"subjectiveFitness.txt"}; 
	}
	public Vector getLogFile() {
		return logs;
	}
	public void setLogFile(double candFitness, double testFitness) {
		logs = new Vector(); 
		String[] vals = new String[3];
		vals[0] = "" + candFitness; 
		vals[1] = "" + testFitness; 
		vals[2] = "" + ((candFitness+testFitness)/2); 
		logs.add(vals); 
	}
	
	
	
    public void prepare() {
        cp.prepare();

        int width  = (int)size.getWidth();
        int height = (int)size.getHeight();
        
        bufferGraphics.setColor(Color.white);
        bufferGraphics.fillRect(0,0, (int)size.getWidth(), (int)size.getHeight());

		// good and bad zones.
		bufferGraphics.setColor(Color.yellow);
		bufferGraphics.fillRect(0, 0, width, height/4);
		bufferGraphics.fillRect(0, 3*height/4, width, height);
		
        bufferGraphics.setColor(Color.black);
        bufferGraphics.drawLine(0,(int)size.getHeight()/2, (int)size.getWidth(), (int)size.getHeight()/2);

        bufferGraphics.setFont(new Font("sansserif", Font.PLAIN, 8));

        for (int i=-1*height/2; i<height/2; i+=height/4) {
            bufferGraphics.setColor(Color.gray.brighter());
            bufferGraphics.drawLine(0, height/2 - i, width, height/2 - i);
        }
        for (int i=0; i<width; i+=50) {
            bufferGraphics.setColor(Color.black);
            bufferGraphics.drawString(""+i, i, height/2 + 15);
            bufferGraphics.drawLine(i, height/2-2, i, height/2+2);
        }
        
        cp.repaint();
    }

	
	
	
	public void updateState(int gen, int oldest, 
		int[] oldest_cand_results, int[] newest_cand_results, int[] oldest_test_results, int[] newest_test_results) {
		
		double candSubjFitness = lastCandSubjFitness; 
		double testSubjFitness = lastTestSubjFitness; 
		
		if ((gen % memUpdateFreq) == 0) {
			
			int candCount_Better=0; int candCount_Same=0; int candCount_Worse	= 0;
			int testCount_Better=0; int testCount_Same=0; int testCount_Worse	= 0;
			int cand_value=0; int test_value = 0;
			
			for (int i=oldest; i<memWin; i++) {
				
				// if the newest candidate did better than the oldest candidate, increment the candidate score
				if      (newest_cand_results[i] > oldest_cand_results[i]) 	{ cand_value += 1; candCount_Better++;	}
				else if (newest_cand_results[i] < oldest_cand_results[i]) 	{ cand_value -= 1; candCount_Worse++;	}
				else 							                            { candCount_Same++; 			        }
				
				// if the newest test forced candidates to do worse than with the oldest test, increment the test score
				if      (newest_test_results[i] < oldest_test_results[i]) 	{ test_value += 1; testCount_Better++;	}
				else if (newest_test_results[i] > oldest_test_results[i]) 	{ test_value -= 1; testCount_Worse++;	}
				else 							                            { testCount_Same++;           			}
			}
			
			candSubjFitness = (1.0 * cand_value / (memWin - oldest));
			testSubjFitness = (1.0 * test_value / (memWin - oldest));
			
		}
		
		double scale = size.getHeight()/2;
		bufferGraphics.setColor(Color.blue);
		int cf = (int) (scale - (candSubjFitness*scale));
		bufferGraphics.fillRect(gen, cf, 1,1);
		
		bufferGraphics.setColor(Color.red);
		int tf = (int) (scale - (testSubjFitness*scale));
		bufferGraphics.fillRect(gen, tf, 1,1);
		
		bufferGraphics.setColor(Color.black);
		int f = (int) (scale - (((candSubjFitness+testSubjFitness)/2)*scale));
		bufferGraphics.drawLine(gen-1, lastComboFitness, gen, f);
		lastComboFitness = f;
		
		setLogFile(candSubjFitness, testSubjFitness); 
		
		
		lastCandSubjFitness = candSubjFitness; 
		lastTestSubjFitness = testSubjFitness; 
		
	}
	
	
    public void doPaint() {
        cp.repaint();
    }

    public String getName() {
        return "Memory-Based Fitness";
    }
	
    public boolean initiallyVisible() {
        return false;
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

		
		
		public String getImageType() {
			return "MemoryBasedFitnessView";
		}
		public Image getImage() {
			return buffer;
		}
		public int getGeneration() {
			return getGen();
		}
    }
}

