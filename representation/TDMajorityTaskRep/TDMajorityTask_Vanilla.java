package coeviz.representation.TDMajorityTaskRep;

import coeviz.framework.interfaces.*;
import coeviz.representation.TDMajorityTask;

import java.util.Random;
import java.util.Hashtable;


public class TDMajorityTask_Vanilla implements TDMajorityTask, Test {

	// private double density; 
	private int[] ic; 
	
	// EXPERIMENTAL VARIABLES: 
	private double mutation_sd = 0.05; 
	private double mutation_mean = 0; 
	private int lattice_bits = 13; // 149 
	private boolean debug = false; 
	
	
    public TDMajorityTask_Vanilla() {
        super();
    }
	
	/*
    public void setDensity(double d) {
		density = d; 
    }	
	
    public double getDensity() {
		return density; 
    }
	*/
	
	public int[] getIC() {
		return ic; 
	}
	
	public void setIC(int[] newIC) {
		ic = newIC; 
	}
	
	/*
	public int[] getInitialCondition(Random r) {
		int[] ic = new int[lattice_bits];
		for (int i=0; i<ic.length; i++) 
			ic[i] = 0; 
		int bitFlipCount = (int) Math.round(density * lattice_bits);
		int bitsFlipped = 0; 
		while (bitsFlipped < bitFlipCount) {
			int tryToFlipIndex = r.nextInt(lattice_bits); 
			if (ic[tryToFlipIndex] == 0) {
				ic[tryToFlipIndex] = 1; 
				bitsFlipped++;
			}
		}
		
		if (debug) {
			System.out.print("task - IC: ");
			for (int i=0; i<ic.length; i++) {
				System.out.print(ic[i]);
			}
			System.out.println(""); 
		}
		
		return ic; 
	}
*/
	
	public double calculateDensity() {
		int count = 0; 
		for (int i=0; i<ic.length; i++) 
			if (ic[i] == 1) 
				count++; 
		double density = 1.0 * count / ic.length; 
		if (debug) System.out.println("task - random density: " + density); 
		return density; 
	}
	
	public void randomize(Random r) {
		double d = r.nextDouble();	
		if (debug) System.out.println("task - random density: " + d); 
		generateICFromDensity(r, d);
	}
	
	public void generateICFromDensity(Random r, double density) {
		ic = new int[lattice_bits];
		for (int i=0; i<ic.length; i++) 
			ic[i] = 0; 
		int bitFlipCount = (int) Math.round(density * lattice_bits);
		int bitsFlipped = 0; 
		while (bitsFlipped < bitFlipCount) {
			int tryToFlipIndex = r.nextInt(lattice_bits); 
			if (ic[tryToFlipIndex] == 0) {
				ic[tryToFlipIndex] = 1; 
				bitsFlipped++;
			}
		}
		
		if (debug) {
			System.out.print("task - IC: ");
			for (int i=0; i<ic.length; i++) {
				System.out.print(ic[i]);
			}
			System.out.println(""); 
		}
	}
	
    // ************* For Test interface ***************

	public void initializeMember (Game g, Random r) {
		randomize(r);
    }	
	

	
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		double newDensity = calculateDensity(); 
			
        if(r.nextDouble() < mRate) {
			double change = r.nextGaussian() * mutation_sd + mutation_mean;
			
			newDensity += change;
			if (newDensity > 1) { newDensity = 1; } else if (newDensity < 0) { newDensity = 0; }
        }
		
        TDMajorityTask_Vanilla td = new TDMajorityTask_Vanilla();
		// td.setDensity(newDensity);
		td.setExperimentalVariables(getExperimentalVariables()); 
		td.generateICFromDensity(r, newDensity);

		if (debug) System.out.println("mutant task - IC: " + td.getIC()); 
		if (debug) System.out.println("mutant task - density: " + td.calculateDensity()); 
		return (PopulationMember) td;
    }
	
    public String toString() {
		// return "" + density; 
		String s = "";
		for (int i=0; i<ic.length; i++) 
			s += ic[i]; 
		return s; 
	}

    public Object clone() {
        TDMajorityTask_Vanilla sa = new TDMajorityTask_Vanilla();
		// sa.setDensity(density);
		sa.setIC(ic); 
		return (PopulationMember) sa;
    }

	public void regenerateFromLog(String toStringRep) {
		// setDensity(Double.parseDouble(toStringRep)); 
		int[] theIC = new int[toStringRep.length()];
		for (int i=0; i<theIC.length; i++) 
			theIC[i] = Integer.parseInt(toStringRep.substring(i,i+1)); 
		setIC(theIC); 
	}
	
	public double getObjectiveFitness() {
		// return density;
		return calculateDensity(); 
	}
	
	public Hashtable getExperimentalVariables() { 
		Hashtable ht = new Hashtable();
		ht.put("mutation_sd", ""+mutation_sd); 
		ht.put("mutation_mean", ""+mutation_mean); 
		ht.put("lattice_bits", ""+lattice_bits); 
		ht.put("debug", ""+debug); 
		return ht; 
	}
	public void setExperimentalVariables(Hashtable ht) {
		try { mutation_sd = Double.parseDouble((String)ht.get("mutation_sd"));} catch (Exception e) { System.out.println("Using default value for mutation_sd"); }
		try { mutation_mean = Double.parseDouble((String)ht.get("mutation_mean"));} catch (Exception e) { System.out.println("Using default value for mutation_mean"); }
		try { lattice_bits = Integer.parseInt((String)ht.get("lattice_bits"));} catch (Exception e) { System.out.println("Using default value for lattice_bits"); }
		try { debug = ((String)ht.get("debug")).equals("true"); } catch (Exception e) { System.out.print("Using default value for debug. "); }
		if (debug) System.out.println("task - experimental params: " +getExperimentalVariables()); 
	}
	
	public void view() {}
    
}