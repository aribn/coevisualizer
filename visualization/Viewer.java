package coeviz.visualization;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.security.SecureRandom;
import coeviz.framework.interfaces.*;
import coeviz.visualization.views.*;
import coeviz.framework.*;

public class Viewer {
	
    private GamePopulationViewPanel 	 gamePopulationView;
    private ProgressViewPanel		     progressView;
    private PropertiesViewPanel		     propertiesView;
    private ProgressLocalViewPanel	     progressLocalView;
    private WinsViewPanel		         winsView;
    private ProgressBarViewPanel	     progressBarView;
    private ObjectivePopulationViewPanel objectivePopulationView;
    private MemoryBasedFitnessViewPanel  memoryBasedFitnessView;
	// private DynamicMemoryViewPanel       dynamicMemoryView; 
	
	// private JComponent[] comps = new JComponent[9];
	private JComponent[] comps = new JComponent[8];
	
	private static boolean evaluateCandidateNotTest = true; 
	
    private long startTime;
    private Game game;
    private Coevisualizer vis;
    private int memWin;
	private int currentGen; 
    private boolean useGui;
	private SecureRandom viewerOnlyRandom; 
	private Class candRepClass; 
	private Class testRepClass; 
	private ArrayList cand_memory, test_memory;
    private int memUpdateFreq; 

	private static Hashtable reverseMappingOfOutcomeValuesToIndices; 
	
	private static final boolean debug = false;
	
    public Viewer(Coevisualizer vis, int genCount, Properties properties, 
				  Game game, int cCount, int tCount, int memoryWindow, 
                  long startTime, String title, boolean useGui, long seed, 
				  Class candRepClass, Class testRepClass, int memUpdateFreq) {
		
        if (useGui) {
			
			// Make a special random number generator only for use 
			try {
				viewerOnlyRandom = SecureRandom.getInstance("SHA1PRNG","SUN");
				viewerOnlyRandom.setSeed(seed+1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
            this.startTime 		= startTime;
            this.game			= game;
            this.vis			= vis;
            this.memWin			= memoryWindow;
            this.useGui			= useGui;
			this.currentGen     = 0; 
			this.candRepClass   = candRepClass;
			this.testRepClass   = testRepClass;	
			this.memUpdateFreq  = memUpdateFreq; 
			
            File dir			= ViewerPanel.getRunDir(""+startTime);
			String runDir       = "" + startTime; 
			
			setReverseMapping(game); 
			
			// prepare the memory
            test_memory = new ArrayList(memWin);
            cand_memory = new ArrayList(memWin);
            for (int i=0; i<memWin; i++) {
                test_memory.add(null);
                cand_memory.add(null);
            }
			
			if (game instanceof Renderable) 
				((Renderable) game).setGenCount(genCount);
			
			// switch each of these to only requiring (properties, this). 
			
            progressBarView		    = new ProgressBarViewPanel(runDir, genCount);
            propertiesView 		    = new PropertiesViewPanel(runDir, properties);
            gamePopulationView 		= new GamePopulationViewPanel(runDir, game);
            progressView		    = new ProgressViewPanel(runDir, this, genCount, (2*cCount*tCount+1), memWin, memUpdateFreq );
            progressLocalView 		= new ProgressLocalViewPanel(runDir, memWin, (2*cCount*tCount+1), memUpdateFreq);
            winsView 			    = new WinsViewPanel(runDir, cCount,tCount,game);
            objectivePopulationView = new ObjectivePopulationViewPanel(runDir, game, this);
            memoryBasedFitnessView 	= new MemoryBasedFitnessViewPanel(runDir, memWin, (2*cCount*tCount+1), genCount, this, memUpdateFreq);
			// dynamicMemoryView       = new DynamicMemoryViewPanel(runDir, game, genCount, memWin, memUpdateFreq); 
			
			int x=0; 
			comps[x] = propertiesView;           x++; 
			comps[x] = gamePopulationView;       x++; 
			comps[x] = progressLocalView;        x++; 
			comps[x] = progressView;             x++; 
			comps[x] = winsView;                 x++; 
			comps[x] = objectivePopulationView;  x++; 
			comps[x] = memoryBasedFitnessView;   x++; 
			// comps[x] = dynamicMemoryView;        x++; 
			comps[x] = progressBarView;          x++; 
			
			
			if (game instanceof Renderable) 
				for (int i=0; i<comps.length; i++) 
					((ViewerPanel) comps[i]).setController(((Renderable)game).renderControls((ViewerPanel) comps[i]));
			
            vis.setMenus(comps);
            vis.addPanels(title, comps);
			for (int i=0; i<comps.length; i++) 
				((ViewerPanel) comps[i]).prepare(); 
            vis.pack();
        }
    }
	
	
	public void closeFiles() throws Exception {
		for (int i=0; i<comps.length; i++) 
			((ViewerPanel) comps[i]).closeFile();
	}
	
	public void saveFinalImageFiles() {
		for (int i=0; i<comps.length; i++) 
			((ViewerPanel) comps[i]).autoSaveImage(); 
	}
	
	public void renderWins(int candGen, int testGen) {
		try {
			if ((candGen <= currentGen) && (testGen <= currentGen)) {
				
				String forGeccoCands = "";
				String forGeccoTests = "";

				// read in the cands from candGen 
				String[] candStrs = gamePopulationView.getLine(candGen, 0);
				Candidate[] requestGenOfCandidates = new Candidate[candStrs.length];
				for (int i=0; i<requestGenOfCandidates.length; i++) {
					try {
						Object c = candRepClass.newInstance();
						((PopulationMember)c).initializeMember(game, viewerOnlyRandom);
						requestGenOfCandidates[i] = (Candidate) c; 
						((PopulationMember) requestGenOfCandidates[i]).regenerateFromLog(candStrs[i]);
						forGeccoCands += candStrs[i] + "\t"; 
					} catch (Exception e) { 
						e.printStackTrace(); 
					}
				}
				
				// read in the tests from testGen
				String[] testStrs = gamePopulationView.getLine(testGen, 1); 
				Test[] requestedGenOfTests = new Test[testStrs.length];
				for (int i=0; i<requestedGenOfTests.length; i++) {
					try {
						Object t = testRepClass.newInstance();
						((PopulationMember)t).initializeMember(game, viewerOnlyRandom);
						requestedGenOfTests[i] = (Test) t; 
						((PopulationMember) requestedGenOfTests[i]).regenerateFromLog(testStrs[i]);
						forGeccoTests += testStrs[i] + "\t"; 
					} catch (Exception e) { 
						e.printStackTrace(); 
					}
				}			
				
				// DO UPDATE ON ALL VIEWS THAT implement GenerationChangeListener
				
				winsView.update(requestGenOfCandidates, requestedGenOfTests, candGen, testGen, viewerOnlyRandom); // need to fix this to input both candGen and testGen
				gamePopulationView.updateState(requestGenOfCandidates, requestedGenOfTests, viewerOnlyRandom);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void pauseViewer() { 
		if (useGui) progressBarView.pause(); 
	}
    
	
	public void recordToView(int gen, Candidate[] candPop, Test[] testPop) {
		
		currentGen = gen; 

		boolean modifyMemory = false; 
		if ((gen % memUpdateFreq) == 0) modifyMemory=true;
		if (memWin == 1) modifyMemory = false; 

		if (modifyMemory) {
			// Drop the oldest in memory and add the newest.
			test_memory.remove(0);	test_memory.add(testPop);
			cand_memory.remove(0);	cand_memory.add(candPop);
		}
		 
		// note the oldest and newest generations
		int oldest = 0;
		try {
		while (cand_memory.get(oldest) == null) 
			oldest++;
		} catch (Exception e) { 
			// a hack, along with the memWin == 1 test above, to turn off memory for =1. 
		}
		int newest = memWin-1;

		// give each ViewerPanel the current generation number
		for (int i=0; i<comps.length; i++) 
			((ViewerPanel) comps[i]).setGen(gen); 
				
		int[] oldCandRes=new int[0]; int[] newCandRes=new int[0]; 
		int[] oldTestRes=new int[0]; int[] newTestRes=new int[0];
		int[][] oldCandDist=new int[0][0]; int[][] newCandDist=new int[0][0]; 
		int[][] oldTestDist=new int[0][0]; int[][] newTestDist=new int[0][0];
		
		if (modifyMemory) {
			
			// oldest candidate versus all tests. 
			oldCandDist = Viewer.getResultsDist(true, game, ((Candidate[]) cand_memory.get(oldest)),  test_memory, viewerOnlyRandom);
			newCandDist = Viewer.getResultsDist(true, game, ((Candidate[]) cand_memory.get(newest)),  test_memory, viewerOnlyRandom);
			oldTestDist = Viewer.getResultsDist(false, game, ((Test[]) test_memory.get(oldest)),       cand_memory, viewerOnlyRandom);
			newTestDist = Viewer.getResultsDist(false, game, ((Test[]) test_memory.get(newest)),       cand_memory, viewerOnlyRandom);

			oldCandRes = Viewer.flattenDistributions(true,  oldCandDist, game); 
			newCandRes = Viewer.flattenDistributions(true,  newCandDist, game); 
			oldTestRes = Viewer.flattenDistributions(false, oldTestDist, game); 
			newTestRes = Viewer.flattenDistributions(false, newTestDist, game); 
		}	
				
		if (useGui) {
			
			// update gui on each ViewerPanel
            progressBarView.updateProgressBar(gen);
            gamePopulationView.updateState(candPop, testPop, viewerOnlyRandom);
			progressLocalView.updateState(gen, newCandRes, newTestRes); 
            progressView.updateState(gen, newCandRes, newTestRes); 
			winsView.update(candPop, testPop, gen, gen, viewerOnlyRandom);
            objectivePopulationView.updateState(gen, candPop, testPop, viewerOnlyRandom);
			memoryBasedFitnessView.updateState(gen, oldest, oldCandRes, newCandRes, oldTestRes, newTestRes); 
			// dynamicMemoryView.updateState(gen, game, newCandDist, newTestDist); 

			
			// refresh the graphics.
			for (int i=0; i<comps.length; i++) 
				((ViewerPanel) comps[i]).doPaint(); 
			
			// write logs to file, if necessary. 
			for (int i=0; i<comps.length; i++) 
				if (comps[i] instanceof RecordableToFile) 
					((ViewerPanel) comps[i]).writeToFile(((RecordableToFile) comps[i]).getLogFile()); 
			
        }
		
    }
		
	// each int[] corresponds to the result tally of one CxT. 
	// e.g. {4,3,0} = (4 losses, 3 ties, 0 wins) or {1,4} = (1 loss, 4 wins) 
	public static int[][] getResultsDist(boolean popIsCand, Game g, PopulationMember[] pop, ArrayList other_memory, Random r) {
		
		// one result[] for each Cand[]xTest[]
		int[][] results = new int[other_memory.size()][g.outcomesInOrder().length];
		
		// get ordered list of possible game outcomes. 
		int[] possibleGameOutcomes = g.outcomesInOrder(); 
		
		for (int i=0; i<results.length; i++) {
			
			// if memWin=200, but gen=50, need to skip memory items 0-150 
			if (other_memory.get(i) == null) {
				results[i] = null; 
			}
			else {
				// get test population from memory. 
				PopulationMember[] otherPop = (PopulationMember[]) other_memory.get(i); 
				
				// I will tally outcomes of this Cand[]xTest[] in here. 
				int[] gameOutcomeCounts = new int[possibleGameOutcomes.length]; 
				for (int j=0; j<gameOutcomeCounts.length; j++) 
					gameOutcomeCounts[j] = 0; 
				
				// evaluate each Cand x Test and tally results. 
				for (int c=0; c<pop.length; c++) {
					for (int t=0; t<otherPop.length; t++) {
						int eval;
						
						///////// I BELIEVE THAT THIS IS WHERE THE PROBLEM IS. 
						// Cand x Test -> outcome
						if (popIsCand) {
							//if (evaluateCandidateNotTest) 
								eval = g.evaluateCandidate( (Candidate) pop[c], (Test) otherPop[t], r); 
							// else 
							// eval = g.evaluateTest( (Candidate) pop[c], (Test) otherPop[c], r); 
						}
						else {
							//if (evaluateCandidateNotTest)
								eval = g.evaluateCandidate( (Candidate) otherPop[t], (Test) pop[c], r); 
							//else
							//	eval = g.evaluateTest( (Candidate) otherPop[t], (Test) pop[c], r); 
						}
						
						// if (debug) System.out.println("eval["+c+","+t+"] = " + eval); 
						
						//////////////////////////////////////////////////
						
						// if (evaluateCandidateNotTest) {
						
						/*
						for (int o=0; o<possibleGameOutcomes.length; o++) 
							if (eval == possibleGameOutcomes[o]) 
								gameOutcomeCounts[o]++;
						*/
						
						if (debug) System.out.println("Eval is " + eval); 
						
						int index = getReverseMapping(eval); 
						gameOutcomeCounts[index]++; 
						
						/*
						} else {
							// ????????????????????????????????? another try... 
							for (int o=0; o<possibleGameOutcomes.length; o++) 
								if (eval == possibleGameOutcomes[o]) 
									gameOutcomeCounts[possibleGameOutcomes.length-1-o]++; 
						}
						 */
						
					}
				}
				results[i] = gameOutcomeCounts; 
			}
		}
		return results; 
	}
	
	private static void setReverseMapping(Game g) { 
		reverseMappingOfOutcomeValuesToIndices = new Hashtable(); 
		int[] outcomeVals = g.outcomesInOrder(); 
		for (int i=0; i<outcomeVals.length; i++) 
			reverseMappingOfOutcomeValuesToIndices.put("" + outcomeVals[i], "" + i); 
	}
	private static int getReverseMapping(int outcomeVal) { 
		if (debug) System.out.println("size is "+reverseMappingOfOutcomeValuesToIndices.size());
		if (debug) System.out.println("" + outcomeVal + " -> " + reverseMappingOfOutcomeValuesToIndices.get("" + outcomeVal));
		return Integer.parseInt((String) reverseMappingOfOutcomeValuesToIndices.get("" + outcomeVal)); 
	}
	
	
	
	public static int[] flattenDistributions(boolean popIsCand, int[][] distResults, Game g) {
		
		
		int[] results = new int[distResults.length];
		int[] orderedOutcome = g.outcomesInOrder(); 
		int[] possibleOutcome = orderedOutcome; 
		
		int even = g.neutralOutcome(); 
		
		// for each entry in memory
		for (int i=0; i<distResults.length; i++) {
			// get this dist over outcomes
			int[] aDistResult = distResults[i]; 
			if (aDistResult == null) {
				// 0 for empty memory
				results[i] = 0; 
			}
			else {
				int offset = 0; 
				int val = 0; 
				for (int index=0; index<possibleOutcome.length; index++) {
					// flattening results. 
					if (aDistResult[index] > 0) {
						if      (possibleOutcome[index]  > even)    val += ( 1 * aDistResult[index]); 
						else if (possibleOutcome[index]  < even)    val += (-1 * aDistResult[index]); 
						// else if (possibleOutcome[index] == even)    val += ( 0 * aDistResult[index]); 

						// keep track of the total number of evaluations. 
						offset += aDistResult[index]; 
					}
				}
				// for grayscaling: 
				// offset will be the number of evaluations. 
				// val will be (-1 * offset) for all losses, 0 for even split, and (1 * offset for all wins)
				// so results will be (0) for all losses, (offset) for even split, and (2 * offset for all wins)
				results[i] = offset + val; 
			}
		}
		return results; 
	}
}