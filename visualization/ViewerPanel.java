package coeviz.visualization;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

import coeviz.visualization.views.elements.*;
import coeviz.framework.interfaces.*;

public abstract class ViewerPanel extends JPanel {

	int g; 
	private PrintStream[] ps;
	private String runId; 
	
	public ViewerPanel( String runId) {
		super();
		g = 0;
		ps = null; 
		this.runId = runId;
		
		if (this instanceof RecordableToFile) {
			try { 
				String[] names = ((RecordableToFile)this).getFileNames(); 
				
				if (names != null) {
					ps = new PrintStream[names.length];
					
					for (int i=0; i<names.length; i++) {
						File f = new File(getCurrentLogsDir(), names[i]);
						ps[i] = new PrintStream(new BufferedOutputStream(new FileOutputStream(f,false)),true);
					}
				}
			} catch (Exception e) { 
				e.printStackTrace();  
			}		
		}
	}
	
	
	
	
	
	
	
	public abstract String getName();
	public abstract boolean initiallyVisible();
    public abstract void prepare();
	public abstract void autoSaveImage(); 
	
	
	
	
	
	
	
	public void doPaint() {}
	public void setGen(int gen) { g = gen;   }
	public int getGen() {         return g;  }

    public void setController(JComponent controller) {
		if (controller != null) {
			add(controller);
			validate();
		}
    }
	
	// INTERNAL USE. DO NOT CALL DIRECTLY. 
	// INSTEAD, implement RecordableToFile, and it will be automatically written.
	
	// Write strings corresponding to one generation to one line of the file
    public void writeToFile(Vector fileLines) {
		
		if (this instanceof RecordableToFile) {
			
			// for each file that we are writing
			for (int f=0; f<ps.length; f++) {
				
				// get this file's set of new values
				String[] vals = (String[]) fileLines.elementAt(f); 
				
				// write all values and print a new line
				for (int i=0; i<vals.length; i++) 
					ps[f].print(vals[i] + "\t");
				ps[f].println("");
			}
		}
	}

	public void closeFile() throws Exception {
		if (this instanceof RecordableToFile) {
			for (int i=0; i<ps.length; i++) {
				ps[i].flush();
				ps[i].close();
			}
		}
	}
	
	
	public String[] getLine(int lineNumber, int logIndex) {
		if (this instanceof RecordableToFile) {
			BufferedReader br; 
			FileReader fr; 
			String line = null; 
			
			try {				
				File dir = new File(getCurrentLogsDir(), ((RecordableToFile)this).getFileNames()[logIndex]);
				
				// create the file reader
				fr = new FileReader(dir); 
				br = new BufferedReader(fr);
				
				// loop until the requested line
				for (int i=0; i<lineNumber; i++) 
					line = br.readLine();
				
				// close the files after accessing them. 
				fr.close();
				br.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			// return the lines.
			return line.split("\t"); 
		}
		else return null;
	}
	
	
    public static File getRunDir(String name) {
        File dir = new File(System.getProperty("user.dir"));
        if (!dir.isDirectory())
            throw new IllegalArgumentException("no such directory");
		
        dir = new File(dir.getParent());
        dir = new File(dir, "runs");
        dir = new File(dir, "temp");
        dir = new File(dir, name);
        return dir;
    }
	
	public File getCurrentImageDir() { 
		File rundir = ViewerPanel.getRunDir(runId);  
		return new File(rundir, "images"); 
	}
	
	
	public File getCurrentLogsDir() { 
		File rundir = ViewerPanel.getRunDir(runId);  
		return new File(rundir, "logs"); 
	}

}
