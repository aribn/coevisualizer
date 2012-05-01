package coeviz.framework.interfaces;

public interface Renderable {

	public java.awt.Graphics  renderObjectivePrep(java.awt.Graphics bg); 
    public javax.swing.JPanel renderControls(coeviz.visualization.ViewerPanel vv);
    public java.awt.Graphics  renderGeneration(Candidate[] cands, Test[] tests, java.awt.Graphics bg, java.util.Random viewerSpecificRandom);
    public java.awt.Graphics  renderObjectiveFitness(int genNum, Candidate[] cands, Test[] tests, java.awt.Graphics bg, java.util.Random viewerSpecificRandom);
	
	// to allocate screen space for rendering game panels. 
    public void setGenCount(int g);
	public java.awt.Dimension getDimension();
    public java.awt.Dimension getObjectiveFitDimension();
	
	// for wins view. 
	public java.awt.Color outcomesInColor(int outcome); 
	
}
