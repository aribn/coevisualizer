package coeviz.representation.RPSRep;
/*
import java.util.Random;
import coeviz.framework.interfaces.*;
import coeviz.representation.RPS;
import coeviz.domain.RockPaperScissors.*;

// for grappa
import java.awt.geom.*;
import java.net.*;
import att.grappa.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random; 
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class RPS_CartlidgeBullockFSM implements RPS, Test, Candidate {
    
    private int[][] nodeTable; 
	private int currentNode;
	
    public RPS_CartlidgeBullockFSM() {
        super();
		
		currentNode = 0;
		
		nodeTable = new int[1][5]; 		
		nodeTable[0][0] = 0; // This is node number 0
		nodeTable[0][1] = 0; // current strategy -- WILL BE OVER-WRITTEN
		nodeTable[0][2] = 0; // if rock, return to state 0
		nodeTable[0][3] = 0; // if paper, return to state 0
		nodeTable[0][4] = 0; // if scissors, return to state 0
    }
	
	
	public void opponentUsedStrategy(int opponentStrategy) {
		currentNode = nodeTable[currentNode][2+opponentStrategy];
	}
	
    public void setStrategy(int[][] newNodeTable ) {
		nodeTable = new int[newNodeTable.length][newNodeTable[0].length];
		for (int i=0; i<newNodeTable.length; i++) 
			for (int j=0; j<newNodeTable[0].length; j++) 
				nodeTable[i][j] = newNodeTable[i][j];
		currentNode = 0; 
    }
    
	// Mutation, as defined by cartlidge and bullock:
	// 10% chance per locus. up to +/- 30% change per probability, then re-normalize. 
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		
		int[][] newNodeTable = new int[nodeTable.length][nodeTable[0].length];
		for (int i=0; i<nodeTable.length; i++) 
			for (int j=0; j<nodeTable[0].length; j++) 
				newNodeTable[i][j] = nodeTable[i][j];
		
		// node mutation: prob 0.03
		if (r.nextDouble() < 0.03) {
			
			// 0.5 probability, add a new node
			if (r.nextDouble() < 0.5) {
				// if max node-count of 100 hasn't been reached
				if (nodeTable.length<100) {
					newNodeTable = new int[nodeTable.length+1][nodeTable[0].length];
					for (int i=0; i<nodeTable.length; i++) 
						for (int j=0; j<nodeTable[0].length; j++) 
							newNodeTable[i][j] = nodeTable[i][j];
					
					// then add an additional node at the end of the table. 
					newNodeTable[newNodeTable.length-1][0] = newNodeTable.length-1;
					newNodeTable[newNodeTable.length-1][1] = r.nextInt(STRATEGIES.length);
					newNodeTable[newNodeTable.length-1][2] = r.nextInt(newNodeTable.length); // or should these all be self-loops?
					newNodeTable[newNodeTable.length-1][3] = r.nextInt(newNodeTable.length); // or should these all be self-loops?
					newNodeTable[newNodeTable.length-1][4] = r.nextInt(newNodeTable.length); // or should these all be self-loops?
					
					// and for the fun of it, give me a few random in-links
					newNodeTable[r.nextInt(newNodeTable.length)][2+r.nextInt(STRATEGIES.length)] = newNodeTable.length-1;
					newNodeTable[r.nextInt(newNodeTable.length)][2+r.nextInt(STRATEGIES.length)] = newNodeTable.length-1;
					newNodeTable[r.nextInt(newNodeTable.length)][2+r.nextInt(STRATEGIES.length)] = newNodeTable.length-1;
					
				}
			}
			else { // remove a node
				
				// leaving min 1. 
				if (nodeTable.length>1) {
					
					int removeNode = r.nextInt(nodeTable.length);
					
					// create the new nodeTable.
					newNodeTable = new int[nodeTable.length-1][nodeTable[0].length];
					for (int i=0; i<nodeTable.length; i++) {
						for (int j=0; j<nodeTable[0].length; j++) {
							if (i<removeNode) 
								newNodeTable[i][j] = nodeTable[i][j];
							else if (i==removeNode) {
								// don't assign this row.
							}
							else if (i>removeNode) 
								newNodeTable[i-1][j] = nodeTable[i][j];
						}
					}
					
					// now remove the node from edge tables and replace with self-loops
					for (int i=0; i<newNodeTable.length; i++) {
						for (int j=0; j<newNodeTable[0].length; j++) {
							if (j==0) {
								// decrement the index of shifted nodes.
								if (i >= removeNode)
									newNodeTable[i][j] -= 1;
							}
							else if (j==2 || j==3 || j==4) {
								// reassign missing edge targets to edge originators.
								if (newNodeTable[i][j] == removeNode) {
									newNodeTable[i][j] = newNodeTable[i][0];
								}
								else if (newNodeTable[i][j] > removeNode) {
									// decrement targets of later links 
									newNodeTable[i][j] -= 1; 
								}
							}
						}
					}
				}
			}
		}
		
		// state mutation: prob 0.02 per node
		for (int i=0; i<newNodeTable.length; i++) 
			if (r.nextDouble() < 0.02) 
				newNodeTable[i][1] = r.nextInt(STRATEGIES.length); 
		
		// edge mutation: prob 0.02 per edge
		for (int i=0; i<newNodeTable.length; i++) 
			for (int j=0; j<STRATEGIES.length; j++) 
				if (r.nextDouble() < 0.02) 
					newNodeTable[i][2+j] = r.nextInt(newNodeTable.length); 
		
		RPS_CartlidgeBullockFSM rps = new RPS_CartlidgeBullockFSM();
		rps.setStrategy(newNodeTable);
		return (PopulationMember) rps;
    }
    
    // ********** For RPS interface **************
    
    public int getStrategy(Random r) {
		return nodeTable[currentNode][1];
    }
    
    public void randomizeStrategy(Random r) {
		nodeTable[0][1] = r.nextInt(STRATEGIES.length); // random strategy
    }
    
    // ********** For Candidate interface **************
	
    public Candidate getCandidateMutation (Random r, double mutation_rate, int mutation_bias, double mutation_size) {
        return (Candidate) getMutation(r, mutation_rate, mutation_bias, mutation_size);
    }
    
	public void initializeCandidate (Game g, Random r) {
		randomizeStrategy(r);
    }
	
    // ********** For Test interface **************
    
    public Test getTestMutation (Random r, double mutation_rate, int mutation_bias, double mutation_size) {
        return (Test) getMutation(r, mutation_rate, mutation_bias, mutation_size);
    }
    
	public void initializeTest (Game g, Random r) {
		randomizeStrategy(r);
    }
	
    // ********** For PopulationMember interface **************
    
    public String toString() {
		String str = "[";
		for (int i=0; i<nodeTable.length; i++) {
			str += "<" + nodeTable[i][0] + "-" + STRATEGIES[ nodeTable[i][1] ].substring(0,1) + ":";
			str += nodeTable[i][2]+"|"+nodeTable[i][3]+"|"+nodeTable[i][4]+"> ";
		}
		str += "]";
		return str;
    }
	
	
	
	public String toDot() {
		String str = "digraph Player { \r\trankdir=LR; \r\tsize=\"8,5\"; \r\tnode [shape = doublecircle]; ";
		str += STRATEGIES[ nodeTable[0][1] ].substring(0,1) + "_0; \r\tnode [shape = circle];";
		for (int i=0; i<nodeTable.length; i++) {
			for (int j=0; j<STRATEGIES.length; j++) {
				str += "\r\t";
				str += STRATEGIES[ nodeTable[i][1] ].substring(0,1) + "_" + nodeTable[i][0] + " -> ";
				str += STRATEGIES[ nodeTable[ nodeTable[i][j+2] ][1] ].substring(0,1) + "_" + nodeTable[i][j+2] + " [ label = \"" +STRATEGIES[j].substring(0,1)+ "\" ]; ";
			}
		}
		str += "\r}";
		return str;
    }
	
	
	public void regenerateFromLog(String toStringRep) {
		String[] aList;
		String remaining; 
		
		// remove []s
		remaining = toStringRep.substring(1, toStringRep.length()-1); 
		// separate string on " " : "node1 node2 node3 etc"
		aList = remaining.split(" ");
		
		// create a node table of the appropriate size. 
		int[][] newNodeTable = new int[aList.length][5];
		
		// for each node, 
		for (int i=0; i<aList.length; i++) {
			// remove the <>s
			remaining = aList[i].substring(1, aList[i].length()-1 ); 
			
			// get the node id: separate string on "-" : "id-more"
			String[] idAndMore = remaining.split("-"); 
			newNodeTable[i][0] = Integer.parseInt(idAndMore[0]); 
			remaining = idAndMore[1];
			
			// get the strategy: separate string on ":" - "strategy:more"
			String[] strategyAndMore = remaining.split(":"); 
			newNodeTable[i][1] = Game_RockPaperScissors.getIntRepresentingLetter(strategyAndMore[0]); 
			remaining = strategyAndMore[1];
			
			// get the edges: separate string on "|" - "R_edge|P_edge|S_edge"
			String[] edges = remaining.split("\\|");
			newNodeTable[i][2] = Integer.parseInt(edges[0]); 
			newNodeTable[i][3] = Integer.parseInt(edges[1]); 
			newNodeTable[i][4] = Integer.parseInt(edges[2]); 
		}
		
        setStrategy(newNodeTable);
		currentNode = 0; 
	}
	
    
    public Object clone() {
		int[][] newNodeTable = new int[nodeTable.length][nodeTable[0].length];
		for (int i=0; i<nodeTable.length; i++) 
			for (int j=0; j<nodeTable[0].length; j++) 
				newNodeTable[i][j] = nodeTable[i][j];
        RPS_CartlidgeBullockFSM rps = new RPS_CartlidgeBullockFSM();
        rps.setStrategy(newNodeTable);
        return (PopulationMember) rps;
    }
    
	public double getObjectiveFitness() {
		return 0;  
	}
	
	public void view() {
		System.out.println(toString()); 
		System.out.println(toDot()); 
		//showGraph(toDot()); 
		new Thread(new DotRenderThread(toDot())).start();
	}
	
	
	
	class DotRenderThread extends Thread {
		String dotformatted;
		
		public DotRenderThread(String dotString) {
			dotformatted = dotString;
		}
		
		public void run() {
			try {
				//Parser parser = new Parser(new FileInputStream(dotfile), System.out);
				Parser parser = new Parser((java.io.Reader) new StringReader(dotformatted));
				parser.parse();
				Graph graph = parser.getGraph();
				//graph.setErrorWriter(new PrintWriter(System.out,true));
				
				Object connector; 
				
				connector = (new URL("http://www.research.att.com/~john/cgi-bin/format-graph")).openConnection();
				URLConnection urlConn = (URLConnection) connector;
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				
				if (connector != null) {
					if(!GrappaSupport.filterGraph(graph,connector)) {
						System.out.println("Error: Could not connect to internet for webdot");
					}
					
					if(connector instanceof Process) {
						int code = ((Process)connector).waitFor();
						if(code != 0) System.out.println("WARNING: proc exit code is: " + code);
					}
					connector = null;
				}
				graph.repaint();
				
				GrappaPanel gp = new GrappaPanel(graph);
				gp.setScaleToFit(false);
				
				Dimension size = new Dimension(100 + (int) graph.getBoundingBox().getWidth(), 
											   100 + (int) graph.getBoundingBox().getHeight());
				
				gp.setSize(size);
				gp.setPreferredSize(size);
				gp.setMinimumSize(size);
				gp.setMaximumSize(size);
				gp.setOpaque(false);
				
				// pop up in a new frame.
				JFrame jf = new JFrame("FSM"); 
				jf.getContentPane().add(gp); 
				jf.setSize(gp.getSize());
				jf.setVisible(true);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
    // ********** that's it! **************
}
*/