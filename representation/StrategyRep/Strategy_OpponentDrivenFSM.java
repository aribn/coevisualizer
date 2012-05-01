package coeviz.representation.StrategyRep;

import java.util.Random;
import coeviz.framework.interfaces.*;
import coeviz.representation.Strategy;
import coeviz.domain.common.StrategyGames.Game_sgNs;
import coeviz.representation.common.Strategy.*;

// for grappa
import java.awt.geom.*;
import java.net.*;
import att.grappa.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random; 
import java.util.Hashtable;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class Strategy_OpponentDrivenFSM implements OpponentDriven_FSM, Strategy, Test, Candidate {
    
	// each node n in the FSM is represented by an int[]:
	// nodeTable[n][0]   is the node's id number
	// nodeTable[n][1]   is the node's state (an index into GAME_STRATEGIES) 
	// nodeTable[n][2+o] is the node's out-link target given opponent strategy o (an index into GAME_STRATEGIES) 
    private int[][] nodeTable; 
	
	// index in nodeTable of the current state
	private int currentNode;
	
	// a list of all strategy names in this game
	private String[] GAME_STRATEGIES;

	
	
	
	
    public Strategy_OpponentDrivenFSM() {
        super();
    }
	
	public void opponentUsedStrategy(int opponentStrategy) {
		currentNode = nodeTable[currentNode][2+opponentStrategy];
	}
	public void resetStrategy() {
		currentNode = 0; 
	}
	
    public void setStrategy(int[][] newNodeTable, String[] strategyNames) {
		GAME_STRATEGIES = (String[]) strategyNames.clone(); 
		nodeTable = new int[newNodeTable.length][newNodeTable[0].length];
		for (int i=0; i<newNodeTable.length; i++) 
			for (int j=0; j<newNodeTable[0].length; j++) 
				nodeTable[i][j] = newNodeTable[i][j];
		currentNode = 0; 
    }
	
	public String toDot() {
		String str = "digraph Player { \r\trankdir=LR; \r\tsize=\"8,5\"; \r\tnode [shape = circle];";
		for (int i=0; i<nodeTable.length; i++) {
			for (int j=0; j<GAME_STRATEGIES.length; j++) {
				str += "\r\t";
				str += GAME_STRATEGIES[ nodeTable[i][1] ].substring(0,1) + "_" + nodeTable[i][0] + " -> ";
				str += GAME_STRATEGIES[ nodeTable[ nodeTable[i][j+2] ][1] ].substring(0,1) + "_" + nodeTable[i][j+2] + " [ label = \"" +GAME_STRATEGIES[j].substring(0,1)+ "\" ]; ";
			}
		}
		str += "\r}";
		return str;
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
	
	
    // ************* For Strategy interface ***************
    
    public int getStrategy(Random r) {
		return nodeTable[currentNode][1];
    }
    
    public void randomizeStrategy(Random r, String[] strategyNames) {
		
		currentNode = 0;
		GAME_STRATEGIES = strategyNames; 
				
		nodeTable = new int[1][2 + GAME_STRATEGIES.length]; 		
		nodeTable[0][0] = 0; // This is node number 0
		nodeTable[0][1] = r.nextInt(GAME_STRATEGIES.length); // random strategy
		
		for (int i=0; i<strategyNames.length; i++) 
			nodeTable[0][2+i] = 0; // regardless of strategy, add a self-link
	}
	
	
    // ************* For PopulationMember interface ***************
    
    
	public void initializeMember (Game g, Random r) {
		randomizeStrategy(r, ((Game_sgNs)g).getStrategyNames());
    }	
	
	
	// Mutation, as defined by cartlidge and bullock:
	// 10% chance per locus. up to +/- 30% change per probability, then re-normalize. 
    //
	// To replicate cartlidge and bullock style experiments, mRate should be 1.0
	// 
	public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
				
		int[][] newNodeTable = new int[nodeTable.length][nodeTable[0].length];
		for (int i=0; i<nodeTable.length; i++) 
			for (int j=0; j<nodeTable[0].length; j++) 
				newNodeTable[i][j] = nodeTable[i][j];
		
		
		
		if (r.nextDouble() < mRate) {
			
			// node mutation: prob 0.03
			if (r.nextDouble() < 0.03) {
				
				// 0.5 probability, add a new node
				if (r.nextDouble() < 0.5) {
					
					// only if there are less than 100 nodes. 
					if (nodeTable.length<100) {
						
						newNodeTable = new int[nodeTable.length+1][nodeTable[0].length];
						for (int i=0; i<nodeTable.length; i++) 
							for (int j=0; j<nodeTable[0].length; j++) 
								newNodeTable[i][j] = nodeTable[i][j];
						// then add an additional node at the end of the table. 
						newNodeTable[newNodeTable.length-1][0] = newNodeTable.length-1;
						newNodeTable[newNodeTable.length-1][1] = r.nextInt(GAME_STRATEGIES.length);
						// give it all random out-links
						for (int i=0; i<GAME_STRATEGIES.length; i++) 
							newNodeTable[newNodeTable.length-1][2+i] = r.nextInt(newNodeTable.length); 
						
						// and for the fun of it, give me a few (3) random in-links
						for (int i=0; i<3; i++) 
							newNodeTable[ r.nextInt(newNodeTable.length) ][ 2+r.nextInt(GAME_STRATEGIES.length) ] = newNodeTable.length-1;
						
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
										newNodeTable[i][0] -= 1;
								}
								else if (j >= 2) {
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
					newNodeTable[i][1] = r.nextInt(GAME_STRATEGIES.length); 
			
			// edge mutation: prob 0.02 per edge
			for (int i=0; i<newNodeTable.length; i++) 
				for (int j=0; j<GAME_STRATEGIES.length; j++) 
					if (r.nextDouble() < 0.02) 
						newNodeTable[i][2+j] = r.nextInt(newNodeTable.length); 
			
		}
		
		Strategy_OpponentDrivenFSM so = new Strategy_OpponentDrivenFSM();
		so.setStrategy(newNodeTable, GAME_STRATEGIES);
		return (PopulationMember) so;
    }
    
    public String toString() {
		String str = "[";
		for (int i=0; i<nodeTable.length; i++) {
			str += "<" + nodeTable[i][0] + "-" + GAME_STRATEGIES[ nodeTable[i][1] ].substring(0,1) + ":";
			for (int j=0; j<GAME_STRATEGIES.length; j++) {
				str += nodeTable[i][2+j];
				if (j < GAME_STRATEGIES.length-1) str += "|"; else str += "> "; 
			}
		}
		str += "]";
		return str;
    }
	
	public void regenerateFromLog(String toStringRep) {
		String[] aList;
		String remaining; 
		
		//System.out.println(toStringRep); 
		
		// remove []s
		remaining = toStringRep.substring(1, toStringRep.length()-1); 
		// separate string on " " : "node1 node2 node3 etc"
		aList = remaining.split(" ");
		
		int[][] newNodeTable = new int[aList.length][ 2 + aList[0].split("\\|").length ]; 
		
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
			newNodeTable[i][1] = getIntRepresentingLetter(strategyAndMore[0]); 
			remaining = strategyAndMore[1];
			
			// get the edges: separate string on "|" - "R_edge|P_edge|S_edge"
			String[] edges = remaining.split("\\|");
			for (int j=2; j<newNodeTable[0].length; j++)
				newNodeTable[i][j] = Integer.parseInt(edges[j-2]); 
		}
		
        setStrategy(newNodeTable, GAME_STRATEGIES);
		currentNode = 0; 
	}
	
	public int getIntRepresentingLetter(String str) {
		for (int i=0; i<GAME_STRATEGIES.length; i++) 
			if (str.equals(GAME_STRATEGIES[i].substring(0,1))) return i;
		return -1; 
	}
	
    
    public Object clone() {
		int[][] newNodeTable = new int[nodeTable.length][nodeTable[0].length];
		for (int i=0; i<nodeTable.length; i++) 
			for (int j=0; j<nodeTable[0].length; j++) 
				newNodeTable[i][j] = nodeTable[i][j];
        Strategy_OpponentDrivenFSM so = new Strategy_OpponentDrivenFSM();
        so.setStrategy(newNodeTable, GAME_STRATEGIES);
        return (PopulationMember) so;
    }
    
	public double getObjectiveFitness() {
		return 0;  
	}
	
	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}
	
	public void view() {
		System.out.println(toString()); 
		System.out.println(toDot()); 
		new Thread(new DotRenderThread(toDot())).start();
	}
}