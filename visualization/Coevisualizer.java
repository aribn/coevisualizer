package coeviz.visualization;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;

import coeviz.visualization.views.*;
import coeviz.framework.Launcher;
import coeviz.framework.interfaces.*;
import coeviz.visualization.ViewerPanel;


public class Coevisualizer {
	
    private static Coevisualizer vis;
    private static JFileChooser chooser;
    //private static ResourceBundle resbundle;
    private static Properties defaultProperties;
    private static String currentTitle = "";
    private static boolean paused = false;
    private static Vector runs, runJFrames;
	
    private static final String fileMenuS	= "File";
    private static final String editMenuS	= "Edit";
    private static final String viewMenuS	= "Views";
    
    private static final String newItem		= "New Editor";
    private static final String openItem	= "Open in Editor...";
    private static final String closeItem	= "Close";
    private static final String runItem		= "Run/Pause Simulations";
    private static final String quitItem    = "Quit";
	
    private static final String editItem	= "Edit...";
    private static final String copyItem	= "Copy...";
    
    private JCheckBoxMenuItem[] viewToggles;
    private JPanel main;
	
	
    public Coevisualizer() {
		
        //Construct a file filter for param files.
        PropertiesFileFilter filter = new PropertiesFileFilter();
        filter.addExtension("properties");
        filter.setDescription("Coevisualizer File");
		
        // Find the "runs" directory and preset the Open File window to it.
        File dir = new File(System.getProperty("user.dir"));
        dir = new File(dir.getParent());
        dir = new File(dir, "runs");
        chooser = new JFileChooser(dir);
        chooser.setFileFilter(filter);
        chooser.setDragEnabled(true);
    }
	
    public void setMenus(JComponent[] panels) {
		
        Toolkit.getDefaultToolkit();
        //resbundle = ResourceBundle.getBundle ("asdfstrings", Locale.getDefault());
        int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
        int[] vks = {
            /* KeyEvent.VK_0,*/ KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
            KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9,
            KeyEvent.VK_F, KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_I, KeyEvent.VK_J
        };
        
        viewToggles = new JCheckBoxMenuItem[panels.length];
        for (int i=0; i<panels.length; i++) {
            ViewerPanel vv = (ViewerPanel) panels[i];
            viewToggles[i] = new JCheckBoxMenuItem(new updateClass(vv.getName(), KeyStroke.getKeyStroke(vks[i], shortcutKeyMask)));
            viewToggles[i].setState(vv.initiallyVisible());
        }
    }
	
    
    public void addPanels(String title, JComponent[] panels) {
		
        // create a run panel.
        JPanel runPanel = new JPanel();
        runPanel.setLayout(new BoxLayout(runPanel, BoxLayout.Y_AXIS));
        for (int i=0; i<panels.length; i++) runPanel.add(panels[i]);  
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        final JFrame jf = new JFrame(title);        
        jf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jf.getContentPane().add(runPanel, BorderLayout.CENTER);
        jf.addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) { }
            public void windowGainedFocus(WindowEvent e) {
                setCurrentFocusTitle(jf.getTitle());
            }
        });
        jf.addWindowListener(new WindowListener() {
            boolean alreadyDisposed = false;
            public void windowClosed(WindowEvent e) { }
            public void windowOpened(WindowEvent e) {  }
            public void windowIconified(WindowEvent e) { }
            public void windowDeiconified(WindowEvent e) { }
            public void windowActivated(WindowEvent e) { }
            public void windowDeactivated(WindowEvent e) { }
            public void windowClosing(WindowEvent e) {
                if (!alreadyDisposed) {
                    alreadyDisposed = true;
                    String disposedTitle = jf.getTitle();
                    runJFrames.remove(jf);
                    for (int i=0; i<getWinCount(); i++) {
                        Launcher l = (Launcher) runs.elementAt(i);
                        if (l.getTitle().equals(disposedTitle)) {
                            try { l.close(); } catch (Exception ex) {ex.printStackTrace();}
                            runs.remove(l);
                        }
                    }
                    jf.dispose();
                    if (getWinCount() == 0) openPropertiesEditor();
                } 
            }
        });
        jf.show();
        jf.setJMenuBar(addMenus());
        runJFrames.add(jf);
        updatePanelDisplayables();
        jf.setVisible(true);
    }
	
	
    public JMenuBar addMenus() {
		
        JMenuBar mainMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(fileMenuS);
        JMenu editMenu = new JMenu(editMenuS);
        JMenu viewMenu = new JMenu(viewMenuS);
        int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
        fileMenu.add(new JMenuItem(new generalActionClass( newItem,	KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutKeyMask) )));
        fileMenu.add(new JMenuItem(new generalActionClass( openItem, 	KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKeyMask) )));
        fileMenu.add(new JMenuItem(new generalActionClass( closeItem, 	KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcutKeyMask) )));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(new generalActionClass( runItem,	KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, shortcutKeyMask) )));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(new generalActionClass( quitItem,	KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutKeyMask) )));
		
        //editMenu.add(new JMenuItem(new generalActionClass( editItem, 	KeyStroke.getKeyStroke(KeyEvent.VK_E, shortcutKeyMask) )));
        editMenu.add(new JMenuItem(new generalActionClass( copyItem, 	KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutKeyMask) )));
		
        for (int i=0; i<viewToggles.length; i++)
            viewMenu.add(viewToggles[i]);
        
        mainMenuBar.add(fileMenu);
        mainMenuBar.add(editMenu);
        mainMenuBar.add(viewMenu);
        return mainMenuBar;
    }        
	
    
    public void updatePanelDisplayables() {
		
        for (int i=0; i<runJFrames.size(); i++) {
            Container c = ((JFrame)runJFrames.elementAt(i)).getContentPane();
            Container mainPanel = (Container) c.getComponent(0);
            for (int j=0; j<mainPanel.getComponentCount(); j++)
                mainPanel.getComponent(j).setVisible(viewToggles[j].getState());
            mainPanel.validate();
        }
        pack();
    }
	
    
    public void pack() {
        for (int i=0; i<runJFrames.size(); i++)
            ((JFrame)runJFrames.elementAt(i)).pack();
    }
    public static int getWinCount() {
        return runs.size();
    }
    public void setCurrentFocusTitle(String t) {
        currentTitle = t;
    }
    public void closeFrontWindow() {
        for (int i=0; i<runJFrames.size(); i++) {
            JFrame jf = (JFrame) runJFrames.elementAt(i);
            if (jf.getTitle().equals(currentTitle)) {
                try { jf.dispose(); } catch (Exception ex) {ex.printStackTrace();}
                runJFrames.remove(jf);
            }
        }
        for (int i=0; i<getWinCount(); i++) {
            Launcher l = (Launcher) runs.elementAt(i);
            if (l.getTitle().equals(currentTitle)) {
                try { l.close(); } catch (Exception ex) {ex.printStackTrace();}
                runs.remove(l);
            }
        }
        if (getWinCount() == 0) openPropertiesEditor();
    }
    public void doQuit() {
		for (int i=0; i<runJFrames.size(); i++) {
            JFrame jf = (JFrame) runJFrames.elementAt(i);
			try { jf.dispose(); } catch (Exception ex) {ex.printStackTrace();}
			runJFrames.remove(jf);
        }
		for (int i=0; i<getWinCount(); i++) {
			Launcher l = (Launcher) runs.elementAt(i);
			try { l.close(); } catch (Exception ex) {ex.printStackTrace();}
			runs.remove(l);
		}
		System.exit(0);
    }
    public void duplicateFrontWindow() {
        for (int i=0; i<getWinCount(); i++) {
            Launcher l = (Launcher) runs.elementAt(i);
            if (l.getTitle().equals(currentTitle)) {
                defaultProperties = l.getProperties();
                openPropertiesEditor();
            }
        }
    }
    public void editFrontWindow() {
        duplicateFrontWindow();
        //if (getWinCount()>1)
		closeFrontWindow();
    }
    public void doOpenDialog() {
        try {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();

				// THIS LINE IS KEY, without it, previous values seem to get reused.  
                defaultProperties = new Properties(); 
				
				defaultProperties.load(new FileInputStream(path));
                openPropertiesEditor();
            }
        } catch (Exception e) {e.printStackTrace(); }
    }
	// FOR SOME REASON, I have sporadic troubles when calling loadExperimentalFieldValues() before loadProperties(). 
	public static void openPropertiesEditor() {
        try {
            CoevPropertiesEditor cpe = new CoevPropertiesEditor(vis);
		/*
		    cpe.loadExperimentalFieldValues(); 
			cpe.validateIt(); 
			cpe.loadProperties(defaultProperties);
			cpe.validateIt(); 
		*/
			
			// cpe.printVals("after cpe construction"); 
			
			// first, get the menu items set properly. 
			cpe.loadProperties(defaultProperties);
			cpe.validateIt(); 

			// cpe.printVals("after loading in the properites"); 

			// then fill in the class-specified defaults
			cpe.loadExperimentalFieldValues(); 
			cpe.validateIt(); 

			// cpe.printVals("after loading in the class defaults"); 

			// finally, prefer user-specified values when available. 
			cpe.loadProperties(defaultProperties);
			cpe.validateIt(); 

			// cpe.printVals("after loading in the properties"); 

			cpe.display();
			
        } catch (Exception e) {e.printStackTrace(); }
    }
    // Do not call this manually!
    public static void startNewRun (Properties p) {
        defaultProperties = p;
        runs.add(new Launcher(p, vis));
    }
	
	
    
	
    public static void main (String args[]) {
        if (args.length != 1)
            System.out.println("Usage: java coeviz.visualization.Coevisualizer paramFile");
        else {
            try {
                defaultProperties = new Properties();                
                defaultProperties.load(new FileInputStream(args[0]));
                
                runs = new Vector();
                runJFrames = new Vector();
                // instantiate
                vis = new Coevisualizer();
                // and start one. 
                //startNewRun(defaultProperties);
                openPropertiesEditor();
                // until all windows are closed...
                while (true) { //getWinCount()>0
                    if (paused) {
						for (int i=0; i<getWinCount(); i++) {
                            Launcher sim = ((Launcher) runs.elementAt(i));
                            sim.pauseSim();
                        }
						Thread.currentThread().sleep(500);
					}
					else if (getWinCount() == 0) {
						Thread.currentThread().sleep(500);
					}
                    else {
						
						boolean someRunIsStillStepping = false; 
						for (int i=0; i<getWinCount(); i++) 
							if (((Launcher) runs.elementAt(i)).isDoneRunning() == false) 
								someRunIsStillStepping = true; 

						if (someRunIsStillStepping) {
						
							for (int i=0; i<getWinCount(); i++) {
								if (runs.size() > 0) {
									Launcher sim = ((Launcher) runs.elementAt(i));
									boolean done = sim.stepPopulation();
									if (done) {
										sim.close();
										sim.pauseSim(); 
									}
								} 
							}
						} else {
							Thread.currentThread().sleep(500);
						}
                    }
                }
                //System.exit(0);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
	
    
    public class PropertiesFileFilter extends FileFilter {
        String description;
        Vector acceptableSuffixes;
        public PropertiesFileFilter() {
            acceptableSuffixes = new Vector();
            description = "";
        }
        public boolean accept (File f) {
            if (f.isDirectory()) return true;
            String name = f.getName();
            boolean acceptable = false;
            for (int i=0; i<acceptableSuffixes.size(); i++)
                if (name.endsWith((String)acceptableSuffixes.elementAt(i)))
                    return true;
            return false;
        }
        public void addExtension (String ext) { acceptableSuffixes.add(ext); }
        public String getDescription() { return description; }
        public void setDescription(String d) { description=d; }
    }
	
	
    
    public class generalActionClass extends AbstractAction {
        private String type;
        public generalActionClass(String text, KeyStroke shortcut) {
            super(text);
            type = text;
            putValue(ACCELERATOR_KEY, shortcut);
        }
        public void actionPerformed(ActionEvent e) {
            try {
				
                if      (type.equals(newItem))		{ defaultProperties.setProperty(CoevPropertiesEditor.RANDOM_SEED,"0"); openPropertiesEditor(); }
                else if (type.equals(openItem))		doOpenDialog();
                else if (type.equals(closeItem))	closeFrontWindow();
                else if (type.equals(copyItem))		duplicateFrontWindow();
                else if (type.equals(editItem))		editFrontWindow();
                else if (type.equals(runItem))		paused = paused ? false : true;
				else if (type.equals(quitItem))     doQuit();
                else 					            System.out.println("Unrecognized action: " + type);
				
            } catch (Exception exception) { exception.printStackTrace(); }
        }
    }
	
    
    public class updateClass extends AbstractAction {
        public updateClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }
        public void actionPerformed(ActionEvent e) {
            updatePanelDisplayables();
        }
    }
}
