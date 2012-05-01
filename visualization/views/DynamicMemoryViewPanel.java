package coeviz.visualization.views;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import coeviz.visualization.*;
import coeviz.visualization.views.elements.*;
import coeviz.framework.interfaces.*;
import coeviz.visualization.ViewerPanel;


public class DynamicMemoryViewPanel extends ViewerPanel implements RecordableToFile {

    private Graphics bufferGraphics;
    private Image buffer;
	
    private int genCount;
    private int memWin;
	private int memUpdateFreq; 
	
	private Vector logs;
    //private ChildPanel cp;
	private Game game; 

	private ChildPanel[] panelCand = new ChildPanel[3]; 
	private ChildPanel[] panelTest = new ChildPanel[3]; 
	
	private ChildPanel[][] panelMult = new ChildPanel[3][3]; 
		
	private double[][][] evaluateCandResultsNormalized;
	private double[][][] evaluateTestResultsNormalized;
	
	private double[][][] candData;
	private double[][][] testData;
	private double[][][] candData2;
	private double[][][] testData2;
	private boolean[][]  selectedData;
	
	private Dimension smaller = new Dimension(1,1);
	private Dimension max = new Dimension(10000, 10000);
	
	private static final double PRECISION = 6; 
	
    public DynamicMemoryViewPanel(String runDir, Game game, int genCount, int memWin, int memUpdateFreq) {
        super(runDir);
		
		
        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[8] Dynamic Memory Graphs");
        border.setTitleColor(Color.black);
        this.setBorder(border);
        
        this.setOpaque(false);
        this.genCount = genCount;
        this.memWin = memWin;
		this.memUpdateFreq = memUpdateFreq; 
		this.game = game; 

		logs = new Vector(); 
		
		evaluateCandResultsNormalized = new double[genCount][genCount][game.outcomesInOrder().length];
		evaluateTestResultsNormalized = new double[genCount][genCount][game.outcomesInOrder().length];
		
		candData   = new double[genCount][memWin][3]; 
		testData   = new double[genCount][memWin][3];
		candData2   = new double[genCount][memWin][3]; 
		testData2   = new double[genCount][memWin][3];
		selectedData = new boolean[genCount][memWin];
		
		for (int j=0; j<genCount; j++) {
			for (int k=0; k<memWin; k++) {
				for (int i=0; i<3; i++) {
					//if (k>j) {
						// outside of triangle
						candData[j][k][i] =  -2;
						testData[j][k][i] =  -2;
						candData2[j][k][i] =  -2;
						testData2[j][k][i] =  -2;
						selectedData[j][k] = false;
					/*
					}
					else {
						// inside of triangle
						candData[j][k][i] =  -1;
						testData[j][k][i] =  -1;
						candData2[j][k][i] =  -1;
						testData2[j][k][i] =  -1;
						selectedData[j][k] = false;
					}
					 */
				}
			}
		}
		
		Dimension graphDataOnly = new Dimension(genCount, memWin*2); 

		int widthOneGraph = genCount + Rule.SIZE; 
		int heightOneGraph = 2*memWin + Rule.SIZE; 
		
		Dimension bigger = new Dimension(3*widthOneGraph + 100, 2*heightOneGraph + 100);
       
		
		JPanel test1 = new JPanel();
		JPanel test2 = new JPanel(); 
				
		for (int i=0; i<3; i++) {
			
			panelCand[i] = new ChildPanel(graphDataOnly, "Cand"+i); 
			
			Rule columnView = new Rule(Rule.HORIZONTAL, true);
			columnView.setPreferredWidth(Rule.SIZE);
			
			Rule rowView = new Rule(Rule.VERTICAL, true);
			rowView.setPreferredHeight(Rule.SIZE);
			
			JScrollPane pictureScrollPane = new JScrollPane(panelCand[i]);
			//pictureScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			//pictureScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			
			pictureScrollPane.setSize(graphDataOnly);
			pictureScrollPane.setPreferredSize(graphDataOnly);
			pictureScrollPane.setMinimumSize(smaller);
			pictureScrollPane.setMaximumSize(max);
			
			pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
			pictureScrollPane.setColumnHeaderView(columnView);
			pictureScrollPane.setRowHeaderView(rowView);
			pictureScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,  new Corner());
			pictureScrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER,  new Corner());
			pictureScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());
			pictureScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new Corner());
			pictureScrollPane.setWheelScrollingEnabled(true); 
			pictureScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
			pictureScrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);

			test1.add(pictureScrollPane); 
		}
		for (int i=0; i<3; i++) {
			
			panelTest[i] = new ChildPanel(graphDataOnly, "Test"+i);  
			
			Rule columnView = new Rule(Rule.HORIZONTAL, true);
			columnView.setPreferredWidth(Rule.SIZE);
			
			Rule rowView = new Rule(Rule.VERTICAL, true);
			rowView.setPreferredHeight(Rule.SIZE);
			
			JScrollPane pictureScrollPane = new JScrollPane(panelTest[i]);
			//pictureScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			//pictureScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			
			pictureScrollPane.setSize(graphDataOnly);
			pictureScrollPane.setPreferredSize(graphDataOnly);
			pictureScrollPane.setMinimumSize(smaller);
			pictureScrollPane.setMaximumSize(max);
			
			pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
			pictureScrollPane.setColumnHeaderView(columnView);
			pictureScrollPane.setRowHeaderView(rowView);
			pictureScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,  new Corner());
			pictureScrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER,  new Corner());
			pictureScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());
			pictureScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new Corner());
			pictureScrollPane.setWheelScrollingEnabled(true); 
			pictureScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
			pictureScrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);
			
			test2.add(pictureScrollPane);
		}
		
		// THIS IS THE KEY TO RESIZING AFFECTING SCROLLPANE.
		test1.setLayout(new BoxLayout(test1, BoxLayout.X_AXIS));
		test2.setLayout(new BoxLayout(test2, BoxLayout.X_AXIS));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		
		this.add(test1); 
		this.add(test2); 
		

		this.setSize(bigger);
        this.setPreferredSize(bigger);
        this.setMinimumSize(smaller);
        this.setMaximumSize(max);
		
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		for (int i=0; i<3; i++) {
			panelCand[i].revalidate(); 
			panelTest[i].revalidate();
		}
		
		
		JPanel gp = new JPanel(); 
		gp.setLayout( new GridLayout(3,3) );
		for (int i=0; i<3; i++) 
			for (int j=0; j<3; j++) {
				panelMult[i][j] = new ChildPanel(graphDataOnly, "multi"+i+","+j); 
				gp.add(panelMult[i][j]); 
			}
		// pop up in a new frame.
		JFrame jf = new JFrame("Multiplications"); 
		jf.getContentPane().add(gp); 
		jf.setVisible(true);
	}

    
    public void prepare() {
		for (int i=0; i<3; i++) {
			panelCand[i].prepare(this.createImage(genCount, memWin*2)); 
			panelTest[i].prepare(this.createImage(genCount, memWin*2));
		}
		for (int i=0; i<3; i++) 
			for (int j=0; j<3; j++)
				panelMult[i][j].prepare(this.createImage(genCount, memWin*2)); 
    }

    
	
	public void autoSaveImage() {
		for (int i=0; i<3; i++) {
			panelCand[i].autoSavePNG(); 
			panelTest[i].autoSavePNG();
		}
		for (int i=0; i<3; i++) 
			for (int j=0; j<3; j++) 
				panelMult[i][j].autoSavePNG();
				
		// add data save here!
		writeLastColumnData(); 
	}
	
	public void writeLastColumnData() {
		// just the last generation... 
		int g = genCount-1; 
		
		for (int i=0; i<memWin; i++) {
			System.out.print("" + i + "\t"); 
			for (int t=0; t<3; t++)
				System.out.print(candData[g][i][t] + "\t"); 
			for (int t=0; t<3; t++)
				System.out.print(testData[g][i][t] + "\t"); 
			System.out.println(""); 
		}
	}
	
    public String getName() {
        return "Dynamic Memory Map";
    }
    public boolean initiallyVisible() {
        return true;
    }
	
	public void doPaint() {
		for (int i=0; i<3; i++) {
			panelCand[i].repaint(); 
			panelTest[i].repaint();
		}
		for (int i=0; i<3; i++) 
			for (int j=0; j<3; j++)
				panelMult[i][j].repaint(); 
    }
	
	public void srtv(Rectangle r) {
		for (int i=0; i<3; i++) {
			panelCand[i].scrollRectToVisible(r); 
			panelTest[i].scrollRectToVisible(r);
		}
	}
		 

    public void colorRect(int x, int y, double color, int width) {
		if (color >= 0) {
			// in case of round-off errors
			if (color > 1) color = 1.0; 
			float c = new Float(1.0-color).floatValue();
			bufferGraphics.setColor(new Color(c,c,c));
			bufferGraphics.fillRect(x, y, width, 1);
		}
	}
    
	// This turns a list of dists like (5,10,5) to a list like (0.25, 0.5, 0.25) 
	public static double[][] normalizeDistributions(int[][] distributions) {
		
		double[][] dists = new double[distributions.length][distributions[distributions.length-1].length]; 
		for (int i=0; i<distributions.length; i++) {
			int[] aDist = distributions[i]; 
			if (aDist != null) {
				int sum = 0; 
				for (int j=0; j<aDist.length; j++) 
					sum += aDist[j]; 
				for (int j=0; j<aDist.length-1; j++) 
					dists[i][j] = (1.0 * aDist[j] / sum);
				
				// do the last one manually, to guarantee that they all sum 1.
				double theRest = 0; 
				for (int j=0; j<aDist.length-1; j++) 
					theRest += dists[i][j]; 
				dists[i][aDist.length-1] = 1.0 - theRest; 				
			}
			else {
				dists[i] = null; 
			}
		}
		return dists; 
	}
	
	
	// This turns a dist like (5,10,5) into a normalized dist like (0.25, 0.5, 0.25) 
	public static double[] normalizeSingleDistribution(int[] aDist) {
		
		if (aDist == null) return null; 
		double[] dist = new double[aDist.length]; 
				
		int sum = 0; 
		for (int j=0; j<aDist.length; j++) 
			sum += aDist[j]; 
		for (int j=0; j<aDist.length-1; j++) 
			dist[j] = limitPrecision(1.0 * aDist[j] / sum);
		
		// do the last one manually, to guarantee that they all sum 1.
		double theRest = 1; 
		for (int j=0; j<aDist.length-1; j++) 
			theRest -= dist[j]; 
		dist[aDist.length-1] = limitPrecision(theRest); 				
		
		return dist; 
	}
	
	
	
	public static double limitPrecision(double d) {
		return Math.round(d * Math.pow(10.0, PRECISION)) / (Math.pow(10.0, PRECISION)) ;
	}
	
	
	// BEFORE: (0.5 worst outcome, 0, 0.25, 0.25 best outcome). NOW: (0.25, 0.25, 0.3, 0.2) 
	// DIFFERENCE IS: (0.05 worse, 0.7 same, 0.25 better)
	// order is always from worst outcome to best outcome. 
	public static double[] difference(double[] older, double[] newer) {
		
		// an array of all 2 x outcome boundary values (0.25, 0.5, 0.5, 0.5, 0.75, 0.8, 1.0, 1.0) 
		double[] allnums = new double[2 * older.length]; 
		
		// an array of difference percentages. (% worse, %same, %better) 
		double[] difference = new double[3]; 
		for (int i=0; i<difference.length; i++) 
			difference[i] = 0; 
		
		// pointer to a cursor in each list
		int older_index = 0; 
		int newer_index = 0; 
		
		// sum of list elements from beginning to the cursor
		double older_count = 0; 
		double newer_count = 0; 
		
		// pointer to a cursor in the combined list. 
		int allnums_index = 0; 
		
		for (int i=0; i<allnums.length; i++) {
			
			// if one list is exhausted, go straight to the other. 
			// Otherwise, take from the list that will give the next lowest value for the allnums joint list.
			boolean takeOlder; 
			if (older_index>older.length-1) takeOlder = false; 
			else if (newer_index>newer.length-1) takeOlder = true; 
			else takeOlder = (older[older_index]+older_count <= (newer[newer_index]+newer_count)); 
			
			// the next segment will come from the older dist. 
			if (takeOlder) {
				// add the running tally to the allnums
				allnums[allnums_index] = older[older_index]+older_count; 
				// increment the running sum for this list. 
				older_count += older[older_index]; 
				// see what's changed, compared to the previous allnum. 
				double diff; 
				if (allnums_index==0) diff = allnums[allnums_index];
				else diff = (allnums[allnums_index] - allnums[allnums_index-1]); 
				
				// add that change to the appropriate tally. 0=worse, 1=same, 2=better. 
				if (older_index >  newer_index) difference[0] += diff; 
				if (older_index == newer_index) difference[1] += diff; 
				if (older_index <  newer_index) difference[2] += diff; 
				// increment the tally for this list. 
				older_index++; 
			}
			else {
				allnums[allnums_index] = newer[newer_index]+newer_count; 
				newer_count += newer[newer_index]; 
				
				double diff;
				if (allnums_index==0) diff = allnums[allnums_index];
				else diff = (allnums[allnums_index] - allnums[allnums_index-1]); 
				
				if (older_index >  newer_index) difference[0] += diff; 
				if (older_index == newer_index) difference[1] += diff; 
				if (older_index <  newer_index) difference[2] += diff; 
				
				newer_index++; 
			}
			allnums_index++; 
		}
		
		return difference; 
	}
	
	
	public void calcData(int gen, int windowSize) {
		
		if (gen-windowSize >= 0) {
			
			double[] pastCand    = evaluateCandResultsNormalized[gen-windowSize][gen];
			double[] currentCand = evaluateCandResultsNormalized[gen           ][gen];

			double[] pastTest    = evaluateTestResultsNormalized[gen][gen-windowSize];
			double[] currentTest = evaluateTestResultsNormalized[gen][gen           ];
			
			double[] differenceCandBack = difference(pastCand, currentCand); 
			double[] differenceTestBack = difference(pastTest, currentTest); 
			
			for (int table=0; table<3; table++) {
				candData[gen][windowSize][table] = differenceCandBack[table]; 
				testData[gen][windowSize][table] = differenceTestBack[table]; 
			}
		}
		
		
		if ((gen-windowSize) > 0) {
			
			double[] currentCand = evaluateCandResultsNormalized[gen-windowSize][gen-windowSize];
			double[] futureCand  = evaluateCandResultsNormalized[gen           ][gen-windowSize];
			
			double[] currentTest = evaluateTestResultsNormalized[gen-windowSize][gen-windowSize];
			double[] futureTest  = evaluateTestResultsNormalized[gen-windowSize][gen           ];
			
			double[] differenceCandForward = reverseArray(difference(currentCand, futureCand)); 
			double[] differenceTestForward = reverseArray(difference(currentTest, futureTest)); 
			
			for (int table=0; table<3; table++) {
				candData2[gen-windowSize][windowSize][table] = differenceCandForward[table]; 
				testData2[gen-windowSize][windowSize][table] = differenceTestForward[table]; 
			}
		}
		
		
		/*
		 double[] candDist = new double[] {0,0,0}; 
		 double[] testDist = new double[] {0,0,0}; 
		 
		 // Originally, as in window box. 
		 for (int i=0; i<windowSize; i++) {
			 
			 // Compare CandPop [gen-windowSize] to CandPop [gen] over TestPops [gen-windowSize] through [gen]
			 double[] older = evaluateCandResultsNormalized[gen-windowSize][gen-windowSize+i];
			 double[] newer = evaluateCandResultsNormalized[gen           ][gen-windowSize+i];
			 
			 // compute this difference, and add it to a running tally 
			 double[] difference = difference(older, newer); 
			 for (int table=0; table<3; table++) 
				 candDist[table] += difference[table];
		 }
		 
		 for (int i=0; i<windowSize; i++) {
			 
			 // Compare TestPop [gen-windowSize] to TestPop [gen] over CandPops [gen-windowSize] through [gen]
			 // remember that this table still indexes candidates and then tests. 
			 double[] older = evaluateTestResultsNormalized[gen-windowSize+i][gen-windowSize];
			 double[] newer = evaluateTestResultsNormalized[gen-windowSize+i][gen           ];
			 
			 // compute this difference, and add it to a running tally 
			 double[] difference = difference(older, newer); 
			 for (int table=0; table<3; table++) 
				 testDist[table] += difference[table]; 
		 }
		 
		 
		 // divide to get the averages. 
		 for (int table=0; table<3; table++) {
			 candDist[table] /= windowSize;
			 testDist[table] /= windowSize;
		 }
		 
		 candData[gen][windowSize] = candDist; 
		 testData[gen][windowSize] = testDist; 
		 */
	}
	
	
	
	public static double[] reverseArray(double[] start) {
		double[] end = new double[start.length]; 
		for (int i=0; i<start.length; i++) 
			end[(start.length-1)-i] = start[i]; 
		return end; 
	}
	
	
	
	// newestCand_vs_TestMem_WRTCandEval[test memory index][game outcomes in increasing order]
	public void updateState(int gen, Game game, int[][] newestCand_vs_TestMem_WRTCandEval, int[][] newestTest_vs_CandMem_WRTCandEval) {
				
		if ((gen % memUpdateFreq) == 0) {
	
			// for each test in the memory
			for (int i=0; i<newestCand_vs_TestMem_WRTCandEval.length; i++) {
				
				// get the outcome tally for newest cand vs testMem[i]
				int[] s = newestCand_vs_TestMem_WRTCandEval[i]; 
				
				// this reflects on Cand generation (gen) and Test generation (gen - (memWin-1) + i) 
				if (s != null) {
					double[] normal = normalizeSingleDistribution(s); 
					
					evaluateCandResultsNormalized[gen][gen - (memWin-1) + i] = normal;
					// A HACK! THIS HARD-CODES THE ASSUMPTION OF A ZERO-SUM GAME. 
					evaluateTestResultsNormalized[gen][gen - (memWin-1) + i] = reverseArray(normal);
				}
			}
			for (int i=0; i<newestTest_vs_CandMem_WRTCandEval.length; i++) {
				
				// get the outcome tally for ith element in candMem vs newest test gen. 
				int[] s = newestTest_vs_CandMem_WRTCandEval[i]; 
				
				// this reflects on Cand generation (gen - (memWin-1) + i) and Test generation (gen) 
				if (s != null) {
					double[] normal = normalizeSingleDistribution(s); 
					
					evaluateCandResultsNormalized[gen - (memWin-1) + i][gen] = normal; 
					// A HACK! THIS HARD-CODES THE ASSUMPTION OF A ZERO-SUM GAME. 
					evaluateTestResultsNormalized[gen - (memWin-1) + i][gen] = reverseArray(normal); 
				}
			}
			
			// at this generation, update the candData for each memory window size (up to the max)
			for (int m=0; m<memWin; m++) {
					
				calcData(gen, m);
				
				for (int table=0; table<3; table++) {
					
					// draw the past data. 
					if (gen >= m) {
						panelCand[table].colorRect(gen, memWin+m, candData[gen][m][table], memUpdateFreq);
						panelTest[table].colorRect(gen, memWin+m, testData[gen][m][table], memUpdateFreq); 
						
						for (int j=0; j<3; j++)
							panelMult[table][j].colorRect(gen, memWin+m, (candData[gen][m][table] * testData[gen][m][j]), memUpdateFreq); 
					}
					
					// draw the future data. 
					if (gen >= m) {
						panelCand[table].colorRect(gen-m, memWin-m, candData2[gen-m][m][table], memUpdateFreq);
						panelTest[table].colorRect(gen-m, memWin-m, testData2[gen-m][m][table], memUpdateFreq); 

						for (int j=0; j<3; j++)
							panelMult[table][j].colorRect(gen-m, memWin-m, (candData2[gen-m][m][table] * testData2[gen-m][m][j]), memUpdateFreq); 
					}
				}
				
			}
			
			setLogFile(candData[gen], testData[gen], candData2[gen], testData2[gen]); 
		}
		else {
			double d = -1; 
			double[][] nullstr = new double[][] {{d},{d},{d}}; 
			setLogFile(nullstr,nullstr,nullstr,nullstr); 
		}

	}


	// here we will write to file
	public String[] getFileNames() { 
		return new String[] {"candCompareBack.txt", "testCompareBack.txt", "candCompareForward.txt", "testCompareForward.txt"}; 
	}
	public Vector getLogFile() {
		return logs;
	}
	public void setLogFile(double[][] c, double[][] t, double[][] c2, double[][] t2) {
		logs = new Vector(); 
		
		String[] candStrings = new String[c.length];
		for (int i=0; i<candStrings.length; i++) 
			candStrings[i] = "" + c[i][0]+","+c[i][1]+","+c[i][2]; 
		
		String[] testStrings = new String[t.length];
		for (int i=0; i<testStrings.length; i++) 
			testStrings[i] = "" + t[i][0]+","+t[i][1]+","+t[i][2]; 
		
		String[] cand2Strings = new String[c2.length];
		for (int i=0; i<cand2Strings.length; i++) 
			cand2Strings[i] = "" + c2[i][0]+","+c2[i][1]+","+c2[i][2]; 
		
		String[] test2Strings = new String[t2.length];
		for (int i=0; i<test2Strings.length; i++) 
			test2Strings[i] = "" + t2[i][0]+","+t2[i][1]+","+t2[i][2]; 
		
		logs.add(candStrings); 
		logs.add(testStrings);
		logs.add(cand2Strings); 
		logs.add(test2Strings);
	}
	
	
	
    public void doClick(MouseEvent e, String imgtype, boolean recordColumnData) {
		
        int x = e.getX();
        int y = e.getY();
		
		int g = x; 
		String type = imgtype;
		
		// see if we clicked in the past or future. 
		boolean past = false; 
		if (y > memWin) past = true; 
		
		int distance = Math.abs(y-memWin); 
		
		String candtest = type.substring(0,4);
		String table    = type.substring(4,5); 
		
		System.out.println("generation="+g + ", past="+past+", type="+type+", distance="+distance); 
		
		
		if (recordColumnData) {
				
			System.out.println("# Col data for gen="+g); 

			for (int i=memWin-1; i>0; i--) {
				if (candData2[g][i] != null) {
					System.out.print("+" + i + "\t"); 
					
					if (candData2[g][i][0] == -2) System.out.print("0\t0\t0\t0\t0\t0\t");
					else {
						for (int t=0; t<3; t++)   System.out.print(candData2[g][i][t] + "\t");
						for (int t=0; t<3; t++)   System.out.print(testData2[g][i][t] + "\t");
					}
				} 
				else  System.out.print("-" + i + "\t0\t0\t0\t0\t0\t0\t"); 
				System.out.println(""); 
			}
			
			for (int i=0; i<candData[g].length; i++) {
				if (candData[g][i] != null) {
					System.out.print("-" + i + "\t"); 
					
					if (candData[g][i][0] == -2)  System.out.print("0\t0\t0\t0\t0\t0\t"); 
					else {
						for (int t=0; t<3; t++)   System.out.print(candData[g][i][t] + "\t"); 
						for (int t=0; t<3; t++)   System.out.print(testData[g][i][t] + "\t"); 
					}
				} 
				else  System.out.print("" + i + "\t0\t0\t0\t0\t0\t0\t"); 
				System.out.println(""); 
			}
			
		}
		else {

			System.out.println("# Row data for past="+past+", distance="+distance); 

			if (past) {
				
				for (int i=0; i<candData.length; i++) {
					if (candData[i][distance] != null) {
						System.out.print("" + i + "\t"); 
						
						if (candData[i][distance][0] == -2)  System.out.print("0\t0\t0\t0\t0\t0\t"); 
						else {
							for (int t=0; t<3; t++)   System.out.print(candData[i][distance][t] + "\t"); 
							for (int t=0; t<3; t++)   System.out.print(testData[i][distance][t] + "\t"); 
						}
					} 
					else  System.out.print("" + i + "\t0\t0\t0\t0\t0\t0\t"); 
					System.out.println(""); 
				}	
			}
			else {
				
				for (int i=0; i<candData2.length; i++) {
					if (candData2[i][distance] != null) {
						System.out.print("" + i + "\t"); 
						
						if (candData2[i][distance][0] == -2)  System.out.print("0\t0\t0\t0\t0\t0\t"); 
						else {
							for (int t=0; t<3; t++)   System.out.print(candData2[i][distance][t] + "\t"); 
							for (int t=0; t<3; t++)   System.out.print(testData2[i][distance][t] + "\t"); 
						}
					} 
					else  System.out.print("" + i + "\t0\t0\t0\t0\t0\t0\t"); 
					System.out.println(""); 
				}	
				
			}
			
		}
		
		
    }
	
	
	// ************* Child panels ***************


    public class ChildPanel extends PanelWithContextualImageSave implements Scrollable, MouseMotionListener {
		
		private Graphics buffGraphics;
		private Image buff;
        Dimension size;
        boolean prepared = false;
		private int maxUnitIncrement = (int)( (double)Toolkit.getDefaultToolkit().getScreenResolution() / (double)2.54 );
		private String type; 
		private MouseEvent lastEvent; 
		
        public ChildPanel(Dimension s, String type) {
            super();
			setLoc(getCurrentImageDir());

			this.type = type; 
			
            this.setOpaque(false);
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setAlignmentY(Component.CENTER_ALIGNMENT);
			
            size = s;
			this.setSize(s);
			this.setPreferredSize(s);
			
            this.setBorder(new LineBorder(Color.gray));
			
            //Let the user scroll by dragging to outside the window.
            setAutoscrolls(true);
            addMouseMotionListener(this);
			
			
			JMenuItem addCol = new JMenuItem("Write column data to System.out");
			addCol.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) { if (!e.isPopupTrigger()) doClick(lastEvent, getType(), true); }
			});
			addMenuItem(addCol);
			
			JMenuItem addRow = new JMenuItem("Write row data to System.out");
			addRow.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) { if (!e.isPopupTrigger()) doClick(lastEvent, getType(), false); }
			});
			addMenuItem(addRow);
			
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) { lastEvent = e; }
			});
			
			/*
			JMenuItem gecco = new JMenuItem("Write column data to System.out");
			gecco.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) { 
					if (!e.isPopupTrigger()) doClick(e, getType()); 
				}
			});
			//popup.add(gecco);
			addMenuItem(gecco);
			*/
			
			
        }
        public void prepare(Image buff) {
			this.buff = buff; 
            buffGraphics = buff.getGraphics();
            prepared = true;
        }
		public final String getType() {
			return type;
		}
        protected void paintComponent(Graphics g) {
            if (prepared) {
                super.paintComponent(g);
                g.drawImage(buff,0,0,this);
            }
        }
        public Dimension getPreferredSize() {
            return size;
        }
		
		
		public void colorRect(int x, int y, double color, int width) {
			if (color >= 0) {
				// in case of round-off errors
				if (color > 1) color = 1.0; 
				float c = new Float(1.0-color).floatValue();
				//System.out.println("" + c); 
				buffGraphics.setColor(new Color(c,c,c));
				buffGraphics.fillRect(x, y, width, 1);
			}
		}
		
		
		// ************* For PanelWithContextualImageSave ***************
		
		public String getImageType() {
			return "DynamicMemoryViewPanel"+type;
		}
		public Image getImage() {
			return buff;
		}
		public int getGeneration() {
			return getGen();
		}
		
		
		// ************* For Scrollable interface ***************
		
		
		
		
        public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
        }
		
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            //Get the current position.
            int currentPosition = 0;
            if (orientation == SwingConstants.HORIZONTAL) {
                currentPosition = visibleRect.x;
            } else {
                currentPosition = visibleRect.y;
            }
			
            //Return the number of pixels between currentPosition
            //and the nearest tick mark in the indicated direction.
            if (direction < 0) {
                int newPosition = currentPosition -
                (currentPosition / maxUnitIncrement)
                * maxUnitIncrement;
                return (newPosition == 0) ? maxUnitIncrement : newPosition;
            } else {
                return ((currentPosition / maxUnitIncrement) + 1)
                * maxUnitIncrement
                - currentPosition;
            }
        }
		
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            if (orientation == SwingConstants.HORIZONTAL) {
                return visibleRect.width - maxUnitIncrement;
            } else {
                return visibleRect.height - maxUnitIncrement;
            }
        }
		
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }
		
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
		
        public void setMaxUnitIncrement(int pixels) {
            maxUnitIncrement = pixels;
        }
		
		
		// ************* For MouseMotionListener interface ***************
		
		
		
		
        //Methods required by the MouseMotionListener interface:
        public void mouseMoved(MouseEvent e) { }
		
		public void mousePressed(MouseEvent e) { 
			if (!e.isPopupTrigger()) 
				srtv(new Rectangle(e.getX(), e.getY(), 1, 1)); 
		}
		
        public void mouseDragged(MouseEvent e) {
            //The user is dragging us, so scroll!
            Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
            //scrollRectToVisible(r);
			srtv(r); 
        }
	}		
}



// failed attempt at a 3d plot of middle data...
/*
 for (int gener=0; gener<genCount; gener++) {
	 for (int i=memWin-1; i>0; i--) {
		 if (candData2[gener][i] != null) {
			 if (candData2[gener][i][1] == -2)  
				 System.out.println("0"); 
			 else System.out.println(candData2[gener][i][1]);
		 } 
		 else System.out.println("0");
	 }
	 for (int i=0; i<candData[g].length; i++) {
		 if (candData[g][i] != null) {
			 if (candData[gener][i][1] == -2)  
				 System.out.println("0"); 
			 else System.out.println(candData[gener][i][1]); 
		 } 
		 else  System.out.println("0"); 
	 }
	 System.out.println(""); 
	 System.out.println(""); 
 }
 */