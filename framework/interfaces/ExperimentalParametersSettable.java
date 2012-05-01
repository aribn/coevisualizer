package coeviz.framework.interfaces;

import java.util.Hashtable; 

// Allows for domain, algorithm, and representation classes to get and set 
// experimental variables for the GUI editor or properties file. 
public interface ExperimentalParametersSettable {
	public Hashtable getExperimentalVariables(); 
	public void setExperimentalVariables(Hashtable ht);
}
