package coeviz.representation.ANumberRep;

import java.util.Random;
import java.util.Hashtable;

import coeviz.framework.interfaces.*;
import coeviz.representation.ANumber;
import coeviz.domain.common.NumbersGames.Game_ngNd;

public class ANumber_IntegersFaster implements ANumber, Test, Candidate {

    private int[] n;
    private final int RESOLUTION = 100;


    public ANumber_IntegersFaster() {
        super();
    }

    public void setVals(int v[]) {
		n = (int[]) v.clone(); 
    }


    /************* For ANumber interface ***************/

	
    public int[] getVals() {
        return n;
    }

    public void setVals(int randCount, Random r) {
        n = new int[randCount];
        for (int i=0; i<randCount; i++)
            n[i] = (int) Math.round(r.nextGaussian()*10);
    }

	
    /************* For PopulationMember interface ***************/

	
	public void initializeMember (Game g, Random r) {
		setVals(((Game_ngNd)g).getNumberOfDimensions(), r);
    }	
	
    public PopulationMember getMutation (Random r, double mRate, int mBias, double mSize) {
		
        int[] mutation = new int[n.length];
        
		// TRIPLE THE MUTATION RATE
		mSize = mSize * 3; 
		
        for (int i=0; i<mutation.length; i++) {
            mutation[i] = n[i];
            if(r.nextDouble() < mRate) {
                double bias = (double) (mBias * 2 * (RESOLUTION/2 - mutation[i])/RESOLUTION);
                mutation[i] += (int) Math.round(r.nextGaussian()*mSize + bias);
            }
        }
		
        ANumber_IntegersFaster ani = new ANumber_IntegersFaster();
        ani.setVals(mutation);
        return (PopulationMember) ani;
    }
	
    public String toString() {
        String s = "[";
        for (int i=0; i<n.length; i++) s += n[i] + " ";
        return s + "]";
    }

    public Object clone() {
        int[] ncopy = (int[]) ((int[]) n).clone();
        ANumber_IntegersFaster ani = new ANumber_IntegersFaster();
        ani.setVals(ncopy);
        return (PopulationMember) ani;
    }

	
	public void regenerateFromLog(String toStringRep) {
		String nums = toStringRep.substring(1, toStringRep.length()-1); 
		String[] numArr = nums.split(" ");
		int[] n = new int[numArr.length];
		for (int i=0; i<n.length; i++) n[i] = Integer.parseInt(numArr[i]); 
		setVals(n);
	}
	
	
	public double getObjectiveFitness() {
		double sumOfCoordinates = 0; 
        for (int i=0; i<n.length; i++) 
			sumOfCoordinates += n[i];
		return sumOfCoordinates; 
	}
	
	public Hashtable getExperimentalVariables() { return new Hashtable(); }
	public void setExperimentalVariables(Hashtable ht) {}

	
	public void view() {}


    /************* that's it! ***************/


}