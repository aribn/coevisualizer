package coeviz.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;

import coeviz.framework.interfaces.Game;
import coeviz.framework.interfaces.ExperimentalParametersSettable;
import coeviz.framework.Stepper;


public class CoevPropertiesEditor extends JFrame {

	public static final String PROBLEM_DOMAIN                 = "domain";
	public static final String CANDIDATE_ALGORITHM            = "candidateCoevolutionaryAlgorithm";
	public static final String TEST_ALGORITHM                 = "testCoevolutionaryAlgorithm";
	public static final String CANDIDATE_REPRESENTATION       = "candidateRepresentationClass";
	public static final String TEST_REPRESENTATION            = "testRepresentationClass";
	public static final String MAX_GENERATION_COUNT           = "generations";
	public static final String MEMORY_WINDOW_SIZE             = "memoryWindow";
	public static final String MEMORY_WINDOW_UPDATE_FREQUENCY = "memoryUpdateFrequency";
	public static final String CANDIDATE_POPULATION_SIZE      = "candidatePopulationSize";
	public static final String TEST_POPULATION_SIZE           = "testPopulationSize";
	public static final String MUTATION_RATE                  = "mutation_rate";
	public static final String MUTATION_BIAS                  = "mutation_bias";
	public static final String MUTATION_SIZE                  = "mutation_size";
	public static final String RANDOM_SEED                    = "randomSeed";
	// public static final String FILE_FORMAT_VERSION            = "fileFormatVersion";

	// make sure these values begin with the prefix "exper". I'm matching on it below. 
	public static final String EXPER_VARS_DOMAIN                    = "experimentalVarsDomain";
	public static final String EXPER_VARS_CANDIDATE_ALGORITHM       = "experimentalVarsCandidateAlgorithm";
	public static final String EXPER_VARS_TEST_ALGORITHM            = "experimentalVarsTestAlgorithm";
	public static final String EXPER_VARS_CANDIDATE_REPRESENTATION  = "experimentalVarsCandidateRepresentation";
	public static final String EXPER_VARS_TEST_REPRESENTATION       = "experimentalVarsTestRepresentation";

	// retain for historic purposes
	public static final String ALGORITHM                      = "coevolutionaryAlgorithm";
	
	
    private static final int INVALID 	= 2;
    private static final int EXIT 	    = 3;

	
    public JComponent[] components;
	public JComponent[] forClassSpecificProperties; 

    String[] params = {
        PROBLEM_DOMAIN,
		EXPER_VARS_DOMAIN, 
        //ALGORITHM,
        CANDIDATE_ALGORITHM,
		EXPER_VARS_CANDIDATE_ALGORITHM,
        TEST_ALGORITHM,
		EXPER_VARS_TEST_ALGORITHM, 
        CANDIDATE_REPRESENTATION,
		EXPER_VARS_CANDIDATE_REPRESENTATION,
        TEST_REPRESENTATION,
		EXPER_VARS_TEST_REPRESENTATION,			
        CANDIDATE_POPULATION_SIZE,
        TEST_POPULATION_SIZE,
		MAX_GENERATION_COUNT,
        MEMORY_WINDOW_SIZE,
        MEMORY_WINDOW_UPDATE_FREQUENCY,
		MUTATION_RATE,
        MUTATION_BIAS, 
        MUTATION_SIZE,
		// FILE_FORMAT_VERSION, 
        RANDOM_SEED
    };

    String[] descriptions = {
        "Game",
		"vars", 
        //"Algorithm",
        "Candidate Algorithm",
		"vars", 
        "Test Algorithm",
		"vars", 
        "Candidate Type",
		"vars", 
        "Test Type",
		"vars",
		"Candidate Population Size",
        "Test Population Size",
        "Generations",
        "Memory Window (0 for all, 1 for none)",
        "Memory Update Frequency (1 for all)", 
        "Mutation Rate",
        "Mutation Bias",
        "Mutation Size",
        // "File Format version",
		"Random seed (0 for new seed)"
	};

    boolean alreadyDisposed = false;
    JPanel container = new JPanel();
    private int state = INVALID;
    private Properties p = new Properties();
    private Coevisualizer v;

    JComboBox domainMenu, /*algorithmMenu,*/ candidateAlgorithmMenu, testAlgorithmMenu, candRepMenu, testRepMenu;
	// JTextField versionField; 

    public CoevPropertiesEditor(Coevisualizer c) {
        super();

        // Get the list of currently-available games
        File dir = new File(System.getProperty("user.dir"));
        if (!dir.isDirectory()) throw new IllegalArgumentException("no such directory");
        dir = new File(dir.getParent());
        dir = new File(dir, "build/coeviz/domain");
        String[] domainList = dir.list();
        Vector domainVect = new Vector();
        for (int i=0; i<domainList.length; i++) 
            if (!domainList[i].equals("CVS") && !domainList[i].equals("common") && 
				!domainList[i].equals("disabled") && !domainList[i].equals(".DS_Store"))
                domainVect.add(domainList[i]);

        // Get the list of currently-available algorithms
        File dir2 = new File(System.getProperty("user.dir"));
        if (!dir2.isDirectory()) throw new IllegalArgumentException("no such directory");
        dir2 = new File(dir2.getParent());
        dir2 = new File(dir2, "build/coeviz/algorithms");
        String[] algorithmList = dir2.list();
        
		//Vector algorithmVect = new Vector();
        Vector candidateAlgorithmVect = new Vector();
        Vector testAlgorithmVect = new Vector();
		
        for (int i=0; i<algorithmList.length; i++)
            if (!algorithmList[i].equals("CVS") && !algorithmList[i].equals("common") &&
				!algorithmList[i].equals(".DS_Store")) {
                //algorithmVect.add(algorithmList[i]);
                candidateAlgorithmVect.add(algorithmList[i]);
                testAlgorithmVect.add(algorithmList[i]);
			}

        domainMenu 	            = new JComboBox(domainVect);
        //algorithmMenu 	        = new JComboBox(algorithmVect);
        candidateAlgorithmMenu 	= new JComboBox(candidateAlgorithmVect);
        testAlgorithmMenu     	= new JComboBox(testAlgorithmVect);
        candRepMenu	            = new JComboBox();
        testRepMenu 	        = new JComboBox();

        domainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				String s = (String) domainMenu.getSelectedItem();
                updateExperimentalFieldValue(EXPER_VARS_DOMAIN, s);
                updateRepresentations(s);
            }
        });
		candidateAlgorithmMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String) candidateAlgorithmMenu.getSelectedItem();
                updateExperimentalFieldValue(EXPER_VARS_CANDIDATE_ALGORITHM, s);
            }
        });
        testAlgorithmMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String) testAlgorithmMenu.getSelectedItem();
                updateExperimentalFieldValue(EXPER_VARS_TEST_ALGORITHM, s);
            }
        });
        candRepMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String) candRepMenu.getSelectedItem();
                updateExperimentalFieldValue(EXPER_VARS_CANDIDATE_REPRESENTATION, s);
            }
        });
        testRepMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String) testRepMenu.getSelectedItem();
                updateExperimentalFieldValue(EXPER_VARS_TEST_REPRESENTATION, s);
            }
        });

		// versionField = new JTextField(); 
		// versionField.setEditable(false); 

		
        JComponent[] mycomps = {
            domainMenu,
			new JTextField(), 
            //algorithmMenu,
            candidateAlgorithmMenu,
            new JTextField(), 
            testAlgorithmMenu,
            new JTextField(), 
            candRepMenu,
            new JTextField(), 
            testRepMenu,
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new JTextField(),
			// versionField, 
            new JTextField() 
		};
        components = mycomps;
		
        v = c;
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        GridLayout grid = new GridLayout();
        grid.setColumns(2);
        grid.setRows(params.length+1);
        grid.setHgap(10);
        grid.setVgap(0);
        
        container.setLayout(grid);
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(20,20,20,20));

        Font f = new Font("Monospaced", Font.PLAIN, 12);

        
        for (int i=0; i<params.length; i++) {
            JLabel jl = new JLabel(descriptions[i] + ": ", SwingConstants.RIGHT);
            jl.setFont(f);
            components[i].setFont(f);
            container.add(jl);
            container.add(components[i]);
        }
		this.getContentPane().add(container, BorderLayout.CENTER);
		this.setTitle("Coevisualizer - Untitled");
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent e) { }
            public void windowOpened(WindowEvent e) {  }
            public void windowIconified(WindowEvent e) { }
            public void windowDeiconified(WindowEvent e) { }
            public void windowActivated(WindowEvent e) { }
            public void windowDeactivated(WindowEvent e) { }
            public void windowClosing(WindowEvent e) {
                if (!alreadyDisposed) {
                    alreadyDisposed = true;
                    state = EXIT;
                    (CoevPropertiesEditor.this).dispose();
                    if (v.getWinCount()==0) System.exit(0);
                }
            }
        });


        JButton submit = new JButton("Validate");
        submit.setVerticalTextPosition(AbstractButton.CENTER);
        submit.setHorizontalTextPosition(AbstractButton.LEADING); 
		submit.setDefaultCapable(true);
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentValuesValid()) {
                    if (!alreadyDisposed) {
                        alreadyDisposed = true;
                        state = EXIT;
                        (CoevPropertiesEditor.this).dispose();
                    }
                    v.startNewRun(p);                    
                }
            }
        });
        submit.setToolTipText("Click to validate and load these parameters.");
        
        JButton loadFromFile = new JButton("Load from file...");
        loadFromFile.setVerticalTextPosition(AbstractButton.CENTER);
        loadFromFile.setHorizontalTextPosition(AbstractButton.LEADING); 
        loadFromFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!alreadyDisposed) {
                    alreadyDisposed = true;
                    state = EXIT;
                    (CoevPropertiesEditor.this).dispose();
                }
                v.doOpenDialog();
            }
        });

		
		
        JButton save = new JButton("Save as default");
        save.setVerticalTextPosition(AbstractButton.CENTER);
        save.setHorizontalTextPosition(AbstractButton.LEADING); 
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCurrentPropertiesAsDefault(); 
            }
        });

		
		JPanel buttonPanel = new JPanel(); 
		
        buttonPanel.add(loadFromFile);
        buttonPanel.add(save);

		container.add(buttonPanel); 
		container.add(submit); 
		
		getRootPane().setDefaultButton(submit); 
		
        this.setJMenuBar( addMenus() );
    }

	public void validateIt() {
		container.validate(); 
	}

    public JMenuBar addMenus() {
        int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        JMenuBar mainMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(new generalActionClass( "Close", KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcutKeyMask) )));
        mainMenuBar.add(fileMenu);
        return mainMenuBar;
    }

	

	public void updateExperimentalFieldValue(String propertyName, String forClassName) {
		try { 
			Class c = Class.forName("java.lang.Object"); 
			
			if (propertyName.equals(EXPER_VARS_DOMAIN)) {
				c = Class.forName("coeviz.domain."+forClassName+".Game_"+forClassName);
			} else if (propertyName.equals(EXPER_VARS_CANDIDATE_ALGORITHM) || propertyName.equals(EXPER_VARS_TEST_ALGORITHM)) {
				c = Class.forName("coeviz.algorithms."+forClassName+".Stepper_"+forClassName);
			} else if (propertyName.equals(EXPER_VARS_CANDIDATE_REPRESENTATION) || propertyName.equals(EXPER_VARS_TEST_REPRESENTATION)) {
				c = Class.forName("coeviz.representation."+forClassName.substring(0, forClassName.indexOf("_")) + "Rep."+forClassName);
			}
			String s = ((ExperimentalParametersSettable) c.newInstance()).getExperimentalVariables().toString();
			int index = findValCorrespondingTo(propertyName); 
			setVal(index, s); 
			
		} catch (Exception e) {
			System.out.println("propertyName = " + propertyName + ". forClassName = " + forClassName); 
        }
	}
		
        

	// Takes the name of the game, and re-generates the representation menus accordingly. 
    public void updateRepresentations(String domainName) {
        
        try {
            // instantiate the game class.
            Class domainClass = Class.forName("coeviz.domain."+domainName+".Game_"+domainName);
            Game game = (Game) domainClass.newInstance();

            String candRep = game.getAcceptableCandidateInterface();
            String testRep = game.getAcceptableTestInterface();

            candRepMenu.removeAllItems();
            testRepMenu.removeAllItems();

            // Get the list of currently-available algorithms
            File dir = new File(System.getProperty("user.dir"));
            if (!dir.isDirectory()) throw new IllegalArgumentException("no such directory");
            dir = new File(dir.getParent());
            dir = new File(dir, "build/coeviz/representation/"+candRep+"Rep");
            String[] candRepList = dir.list();
            for (int i=0; i<candRepList.length; i++) {
                if (!candRepList[i].equals("CVS") && !candRepList[i].equals(".DS_Store") && !(candRepList[i].indexOf("$")>=0)) {
                    String s = candRepList[i];
                    s = s.substring(0,s.indexOf("."));
                    candRepMenu.addItem(s);
                }
            }

            // Get the list of currently-available algorithms
            File dir2 = new File(System.getProperty("user.dir"));
            if (!dir2.isDirectory()) throw new IllegalArgumentException("no such directory");
            dir2 = new File(dir2.getParent());
            dir2 = new File(dir2, "build/coeviz/representation/"+testRep+"Rep");
            String[] testRepList = dir2.list();
            for (int i=0; i<testRepList.length; i++) {
                if (!testRepList[i].equals("CVS") && !testRepList[i].equals(".DS_Store") && !(testRepList[i].indexOf("$")>=0)) {
                    String s = testRepList[i];
                    s = s.substring(0,s.indexOf("."));
                    testRepMenu.addItem(s);
                }
            }

            candRepMenu.validate();
            testRepMenu.validate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class generalActionClass extends AbstractAction {
        public generalActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }
        public void actionPerformed(ActionEvent e) {
            if (!alreadyDisposed) {
                alreadyDisposed = true;
                state = EXIT;
                (CoevPropertiesEditor.this).dispose();
                if (v.getWinCount()==0) System.exit(0);
            }
        }
    }

    

    
	
	
    
    public void display() {
        this.show();
        this.pack();
        this.setVisible(true);
    }


	// Updates all experimental field values with the class-specified defaults. 
	public void loadExperimentalFieldValues() {
		updateExperimentalFieldValue(EXPER_VARS_DOMAIN, (String) domainMenu.getSelectedItem());
		updateExperimentalFieldValue(EXPER_VARS_CANDIDATE_ALGORITHM, (String) candidateAlgorithmMenu.getSelectedItem());
		updateExperimentalFieldValue(EXPER_VARS_TEST_ALGORITHM, (String) testAlgorithmMenu.getSelectedItem());
		updateExperimentalFieldValue(EXPER_VARS_CANDIDATE_REPRESENTATION, (String) candRepMenu.getSelectedItem());
		updateExperimentalFieldValue(EXPER_VARS_TEST_REPRESENTATION, (String) testRepMenu.getSelectedItem());
	}
	
	
	// Takes a properties object, and updates the cpe editor field for each defined property value. 
    public void loadProperties(Properties properties) {

		// LEGACY: 
		if (properties.getProperty(PROBLEM_DOMAIN).equals("TD2")) 
			properties.setProperty(PROBLEM_DOMAIN, "RaschModelLearningGame");
		
		// First, select the specified game in the domain menu. 
		setVal(findValCorrespondingTo(PROBLEM_DOMAIN), properties.getProperty(PROBLEM_DOMAIN));
        
        for (int i=1; i<params.length; i++) {
			String prop = properties.getProperty(params[i]); 
			if (prop != null) {
				setVal(i, prop);
			} 
			else { // property wasn't found. Set a default. 
				
				// if (params[i].equals(FILE_FORMAT_VERSION)) setVal(i, "" + 1); 
				
			}
		}

		// LEGACY: 
		// If an algorithm was specified without being specific to candidate
		// or test population, use it for both. 
		String dualAlgorithm = properties.getProperty(ALGORITHM); 
		if (dualAlgorithm != null) {
			setVal(findValCorrespondingTo(CANDIDATE_ALGORITHM), dualAlgorithm);
			setVal(findValCorrespondingTo(TEST_ALGORITHM), dualAlgorithm);
		}
    }
	
	
	
	
	public void saveCurrentPropertiesAsDefault() {
		if (currentValuesValid()) {
			
			try {
				
				File dir = new File(System.getProperty("user.dir"));
				if (!dir.isDirectory())
					throw new IllegalArgumentException("no such directory");
				// move to ../scripts/
				dir = new File(dir.getParent());
				dir = new File(dir, "scripts");
				dir = new File(dir, "config.properties");
				
				FileOutputStream fos = new FileOutputStream( dir,false); 
				BufferedOutputStream bos = new BufferedOutputStream(fos); 
				
				p.setProperty(RANDOM_SEED, "0"); 
				p.store( bos, "");		
				
				fos.close();
				bos.close(); 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void printVals(String str) {
		System.out.println(""); 
		System.out.println(str);  
		for (int i=0; i<params.length; i++) System.out.println("params["+i+"] = "+getVal(i)); 
	}
	
    public String getVal(int i) {
        String type = components[i].getClass().getName();
        if (type.equals("javax.swing.JTextField"))
            return ((JTextField)components[i]).getText();
        else if (type.equals("javax.swing.JComboBox"))
            return (String) ((JComboBox)components[i]).getSelectedItem();
        else {
            System.out.println("JComponent not yet handled...");
            return "";
        }
    }
    public void setVal(int i, String val) {
		if (i != -1) {
			String type = components[i].getClass().getName();
			if (type.equals("javax.swing.JTextField")) {
				JTextField jtf = ((JTextField)components[i]);
				if (val==null) System.out.println("skip this, no value specified."); 
				else jtf.setText(val);
			}
			else if (type.equals("javax.swing.JComboBox")) {
				JComboBox jcb = ((JComboBox)components[i]); 
				jcb.setSelectedItem(val);
			}
		} else {
			System.out.println("setVal failed for "+i + ", "+val); 
		}
    }
	public int findValCorrespondingTo(String name) {
		int index = -1; 
		for (int i=0; i<params.length; i++) 
			if (params[i].equals(name)) 
				index = i; 
		return index; 
	}


    public boolean alert(int i, String field, String msg) {
        components[i].grabFocus();
        JOptionPane.showMessageDialog(this, msg, "Error Parsing Parameter: "+field, JOptionPane.ERROR_MESSAGE);
        return false;
    }


    
    public boolean currentValuesValid() {
    
        for (int i=0; i<params.length; i++) {


            if (MAX_GENERATION_COUNT.equals(params[i])) {
                try {
                    int integer = new Integer(getVal(i)).intValue();
                    if (integer<=0) return alert(i, MAX_GENERATION_COUNT, "Value must be greater than zero.");
                } catch (NumberFormatException nfe) { return alert(i, MAX_GENERATION_COUNT, "\"" + getVal(i) + "\" is not an integer."); }
            }
            else if (MEMORY_WINDOW_SIZE.equals(params[i])) {
                try {
                    int integer = new Integer(getVal(i)).intValue();
                    if (integer<0) return alert(i, MEMORY_WINDOW_SIZE, "Value must be >= 0.");
                } catch (NumberFormatException nfe) { return alert(i, MEMORY_WINDOW_SIZE, "\"" + getVal(i) + "\" is not an integer."); }
            }
            else if (MEMORY_WINDOW_UPDATE_FREQUENCY.equals(params[i])) {
                try {
                    int integer = new Integer(getVal(i)).intValue();
                    if (integer<=0) return alert(i, MEMORY_WINDOW_UPDATE_FREQUENCY, "Value must be > 0.");
                } catch (NumberFormatException nfe) { return alert(i, MEMORY_WINDOW_UPDATE_FREQUENCY, "\"" + getVal(i) + "\" is not an integer."); }                
            }
            else if (CANDIDATE_POPULATION_SIZE.equals(params[i])) {
                try {
                    int integer = new Integer(getVal(i)).intValue();
                    if (integer<=0) return alert(i, CANDIDATE_POPULATION_SIZE, "Value must be > 0.");
                } catch (NumberFormatException nfe) { return alert(i, CANDIDATE_POPULATION_SIZE, "\"" + getVal(i) + "\" is not an integer."); }
            }
            else if (TEST_POPULATION_SIZE.equals(params[i])) {
                try {
                    int integer = new Integer(getVal(i)).intValue();
                    if (integer<=0) return alert(i, TEST_POPULATION_SIZE, "Value must be > 0.");
                } catch (NumberFormatException nfe) { return alert(i, TEST_POPULATION_SIZE, "\"" + getVal(i) + "\" is not an integer."); }
            }
            else if (RANDOM_SEED.equals(params[i])) {
                try {
                    long along = new Long(getVal(i)).longValue();
                    if (along<0) return alert(i, RANDOM_SEED, "Value must be >= 0.");
                } catch (NumberFormatException nfe) { return alert(i, RANDOM_SEED, "\"" + getVal(i) + "\" is not a long."); }
            }
            else if (MUTATION_RATE.equals(params[i])) {
                try {
                    double d = new Double(getVal(i)).doubleValue();
                } catch (NumberFormatException nfe) { return alert(i, MUTATION_RATE, "\"" + getVal(i) + "\" is not a double."); }
            }
            else if (MUTATION_BIAS.equals(params[i])) {
                try {
                    int integer = new Integer(getVal(i)).intValue();
                } catch (NumberFormatException nfe) { return alert(i, MUTATION_BIAS, "\"" + getVal(i) + "\" is not an integer."); }
            }
            else if (MUTATION_SIZE.equals(params[i])) {
                try {
                    double d = new Double(getVal(i)).doubleValue();
                } catch (NumberFormatException nfe) { return alert(i, MUTATION_SIZE, "\"" + getVal(i) + "\" is not a double."); }
            }

            
        }

        
        // It's good! Load into p.
        
        for (int i=0; i<params.length; i++) 
            p.setProperty(params[i], getVal(i));
        
        
        
        return true;
    }


	// How to override a static method: http://faq.javaranch.com/view?OverridingVsHiding
    
}

