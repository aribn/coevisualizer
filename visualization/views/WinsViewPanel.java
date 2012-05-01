package coeviz.visualization.views;

import java.awt.*;
import java.awt.event.*;
import java.util.Random; 
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import coeviz.framework.interfaces.*;
import coeviz.visualization.views.elements.*;
import coeviz.visualization.ViewerPanel;


public class WinsViewPanel extends ViewerPanel {


    private Graphics bufferGraphics;
    private Image buffer;
    private ChildPanel cp;
    
    private Game game;

    private Candidate[] cPop;
    private Test[] tPop;

    // int generation;
	int cGeneration, tGeneration; 
    private int candCount, testCount;

    private JLabel cGen, tGen, cand, test, cIndex, tIndex;
    
    
    private static final int SIZE = 8;
    
    public WinsViewPanel(String runDir, int candCount, int testCount, Game game) {
        super(runDir);

        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[5] Evaluation of Candidate against Test");
        border.setTitleColor(Color.black);
        this.setBorder(border);
        this.setOpaque(false);
		
        this.candCount = candCount;
        this.testCount = testCount;
        this.game = game;

        Dimension size = new Dimension(testCount*SIZE, candCount*SIZE);
        Dimension bigger = new Dimension(testCount*SIZE+200, candCount*SIZE+150);
		
        cp = new ChildPanel(size);
        cp.setAlignmentX(Component.CENTER_ALIGNMENT);
		
        
        
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));

		Font f = new Font("Monospaced", Font.PLAIN, 10);

        cGen    = new JLabel("Cand Generation: ", SwingConstants.RIGHT);
		cand    = new JLabel("     Cand Index: ", SwingConstants.RIGHT);
		cIndex  = new JLabel("      Candidate: ", SwingConstants.RIGHT);

        tGen    = new JLabel("Test Generation: ", SwingConstants.RIGHT);
		test    = new JLabel("     Test Index: ", SwingConstants.RIGHT);
		tIndex  = new JLabel("           Test: ", SwingConstants.RIGHT);
		
		JLabel instr = new JLabel("Pause and click in grid to explore..."); 
		
		instr.setFont(f); 
        tGen.setFont(f);
        cGen.setFont(f);
        cand.setFont(f);
        test.setFont(f);
        cIndex.setFont(f);
        tIndex.setFont(f);

		jp.add(instr);
		jp.add(new JLabel(" ")); 
        jp.add(cGen);
		jp.add(cIndex);
        jp.add(cand);
		jp.add(new JLabel(" ")); 
        jp.add(tGen);
        jp.add(tIndex);
        jp.add(test);	

		/*
		JPanel outcomeOrd = new JPanel(); 
		int[] outcomes = game.outcomesInOrder();
		JLabel q = new JLabel("  Cand outcomes: ", SwingConstants.RIGHT); 
		q.setFont(f);
		outcomeOrd.add(q); 
		for (int i=0; i<outcomes.length; i++) {
			outcomeOrd.add(new PixelPanel(game.outcomesInColor(outcomes[i]))); 
			if (i<outcomes.length-1) outcomeOrd.add(new JLabel(" < ")); 
		}
		jp.add(outcomeOrd); 
		*/
		
		JPanel p = new JPanel(); 
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(cp);
		p.add(new JLabel("     ")); 
        p.add(jp);
		
        this.setSize(bigger);
        this.setPreferredSize(bigger);
        this.setMinimumSize(bigger);
        this.setMaximumSize(new Dimension(10000, candCount*SIZE+150));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(p); 
    }


    public void update(Candidate[] candPop, Test[] testPop, int cg, int tg, Random viewerSpecificRandom) {

        bufferGraphics.setColor(Color.gray);
        bufferGraphics.fillRect(0,0, testCount*SIZE, candCount*SIZE);
        
        cGeneration = cg;
        tGeneration = tg;
        cPop = candPop;
        tPop = testPop;
        
        int[][] wins = new int[testPop.length][candPop.length];
        
        for (int i=0; i<candPop.length; i++) {
            for (int j=0; j<testPop.length; j++) {
				int winval = game.evaluateCandidate( candPop[i], testPop[j], viewerSpecificRandom);
                bufferGraphics.setColor(lookupColor(winval));
                bufferGraphics.fillRect(j*SIZE, i*SIZE, SIZE-1, SIZE-1);
			}
        }
		
		
        cGen.setText("Cand Generation: " + cGeneration);
        tGen.setText("Test Generation: " + tGeneration);
        cIndex.setText("");
        tIndex.setText("");
        cand.setText("" );
        test.setText("");
		
        cp.repaint();
    }

    public Color lookupColor(int winType) {
     	return ((Renderable)game).outcomesInColor(winType); 
    }

    public void prepare() {
        cp.prepare();
        // make a grid...
        bufferGraphics.setColor(Color.gray);
        bufferGraphics.fillRect(0,0, testCount*SIZE, candCount*SIZE);
		cp.repaint();
		}

    public void doPaint() {
        cp.repaint();
	}


    public void doClick(MouseEvent e) {

        int i = e.getX() / SIZE;
        int j = e.getY() / SIZE;

        Candidate thisCand = cPop[j];
        Test thisTest = tPop[i];

        cGen.setText("Cand Generation: " + cGeneration);
        tGen.setText("Test Generation: " + tGeneration);
        cIndex.setText("     Cand Index: " + j);
        tIndex.setText("     Test Index: " + i);
        cand.setText("      Candidate: " + thisCand.toString());
        test.setText("           Test: " + thisTest.toString());

        bufferGraphics.setColor(Color.red);
        bufferGraphics.drawRect(i*SIZE-1, j*SIZE-1, SIZE, SIZE);
		
        cp.repaint();
		
		thisCand.view();
		thisTest.view(); 
    }
	
    public String getName() {
        return "Wins";
    }
    public boolean initiallyVisible() {
        return false;
    }
	
	public void autoSaveImage() {
		//cp.autoSavePNG(); 
	}
	
	
	/************* Double-buffered child panel can save snapshot image to disk ***************/

	public class PixelPanel extends JPanel {
		Color c; 
		public PixelPanel(Color c) {
			super();
			this.c = c; 
			this.setSize(new Dimension(10,10)); 
		}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.white);
			g.fillRect(0, 0, 10,10);
			g.setColor(c); 
			g.fillRect(0,0,10,10); 
		}
	}
	
	
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
                public void mousePressed(MouseEvent e) { if (!e.isPopupTrigger()) doClick(e); }
            });
            
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
			return "WinsView";
		}
		public Image getImage() {
			return buffer;
		}
		public int getGeneration() {
			return getGen();
			// return 10000 * wvp.getCandGeneration() + wvp.getTestGeneration(); 
		}
    }
}
