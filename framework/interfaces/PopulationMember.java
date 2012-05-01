package coeviz.framework.interfaces;

public interface PopulationMember extends ExperimentalParametersSettable {

	// constructor
	public void initializeMember(Game g, java.util.Random r);

    // variation operator
	public PopulationMember getMutation (java.util.Random r, double mutation_rate, int mutation_bias, double mutation_size);
	
	// for visualization. 
	public double getObjectiveFitness(); 
	public void view(); 
    
	// for serialization
    public String toString();
	public void regenerateFromLog(String toStringRep);
	public Object clone();
}
