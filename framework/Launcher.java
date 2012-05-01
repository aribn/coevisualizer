package coeviz.framework;

import java.util.*;
import java.io.*;
import java.util.*;
import java.security.SecureRandom;

import coeviz.framework.interfaces.*;
import coeviz.visualization.*;


public class Launcher {

    // private Recorder r;
    private Viewer v;

    //private Stepper stepper;
    private Stepper candStepper, testStepper;
    private Game game;
    private Candidate[] cands;
    private Test[] tests;

    private Properties p;
    private int genCount, memUpdateFreq;
    private int i=0;
    private long startTime;
    private boolean useGui = true;
	private boolean closedForGood = false; 
	private boolean doneRunning = false; 
    
    public Launcher (Properties properties, Coevisualizer coevis) {

        long seed;
        p = properties;
        int memoryWindow;
        if (coevis == null) useGui = false;
        startTime = System.currentTimeMillis();

        try {

            //String algName		= p.getProperty(CoevPropertiesEditor.ALGORITHM);
			String candidateAlgName = p.getProperty(CoevPropertiesEditor.CANDIDATE_ALGORITHM);
            String testAlgName		= p.getProperty(CoevPropertiesEditor.TEST_ALGORITHM);

            String domainName		= p.getProperty(CoevPropertiesEditor.PROBLEM_DOMAIN);
            String candClassName	= p.getProperty(CoevPropertiesEditor.CANDIDATE_REPRESENTATION);
            String testClassName	= p.getProperty(CoevPropertiesEditor.TEST_REPRESENTATION);
            String m_Str		    = p.getProperty(CoevPropertiesEditor.MEMORY_WINDOW_SIZE);
            String mUpdate_Str		= p.getProperty(CoevPropertiesEditor.MEMORY_WINDOW_UPDATE_FREQUENCY);
            String r_Str		    = p.getProperty(CoevPropertiesEditor.RANDOM_SEED);

            int g 			        = Integer.parseInt(p.getProperty(CoevPropertiesEditor.MAX_GENERATION_COUNT));
            int c 			        = Integer.parseInt(p.getProperty(CoevPropertiesEditor.CANDIDATE_POPULATION_SIZE));
            int t 			        = Integer.parseInt(p.getProperty(CoevPropertiesEditor.TEST_POPULATION_SIZE));
            
            int bias			    = Integer.parseInt(p.getProperty(CoevPropertiesEditor.MUTATION_BIAS));
            double rate			    = Double.parseDouble(p.getProperty(CoevPropertiesEditor.MUTATION_RATE));
            double size			    = Double.parseDouble(p.getProperty(CoevPropertiesEditor.MUTATION_SIZE));

            // Some string parsing
            String candClassRepDir 	= candClassName.substring(0,candClassName.indexOf("_"));
            String testClassRepDir 	= testClassName.substring(0,testClassName.indexOf("_"));

            // Dynamically loading
            //Class stepperClass	= Class.forName("coeviz.algorithms."+algName+".Stepper_"+algName);
            Class candStepperClass	= Class.forName("coeviz.algorithms."+candidateAlgName+".Stepper_"+candidateAlgName);
            Class testStepperClass	= Class.forName("coeviz.algorithms."+testAlgName+".Stepper_"+testAlgName);
			
            Class domainClass		= Class.forName("coeviz.domain."+domainName+".Game_"+domainName);
            Class candRepClass		= Class.forName("coeviz.representation."+candClassRepDir+"Rep."+candClassName);
            Class testRepClass		= Class.forName("coeviz.representation."+testClassRepDir+"Rep."+testClassName);

            // Dynamic instantiation
            //stepper			    = (Stepper) stepperClass.newInstance();
            candStepper			    = (Stepper) candStepperClass.newInstance();
            testStepper			    = (Stepper) testStepperClass.newInstance();
            game			        = (Game) domainClass.newInstance();
		
			candStepper.setAsCandStepper(); 
			testStepper.setAsTestStepper();
			
			///////////////////////////////////////////////
			// WORKING HERE
			
			String expCandStepper = p.getProperty(CoevPropertiesEditor.EXPER_VARS_CANDIDATE_ALGORITHM);
			String expTestStepper = p.getProperty(CoevPropertiesEditor.EXPER_VARS_TEST_ALGORITHM);
			String expDomain      = p.getProperty(CoevPropertiesEditor.EXPER_VARS_DOMAIN);
			String expCandRep     = p.getProperty(CoevPropertiesEditor.EXPER_VARS_CANDIDATE_REPRESENTATION);
			String expTestRep     = p.getProperty(CoevPropertiesEditor.EXPER_VARS_TEST_REPRESENTATION); 
			
			((ExperimentalParametersSettable)candStepper).setExperimentalVariables(toHashTable(expCandStepper)); 
			((ExperimentalParametersSettable)testStepper).setExperimentalVariables(toHashTable(expTestStepper)); 
			((ExperimentalParametersSettable)game).setExperimentalVariables(toHashTable(expDomain)); 
			// The cand/test rep class variables are set in Stepper.getInitial*()

			
			/*
			 // This was from when I tried to use static methods. 
			Hashtable h1 = toHashTable(expCandStepper); 
			Hashtable h2 = toHashTable(expTestStepper); 
			Hashtable h3 = toHashTable(expDomain); 
			Hashtable h4 = toHashTable(expCandRep); 
			Hashtable h5 = toHashTable(expTestRep); 
			Object[] o1 = new Object[]{h1}; 
			Object[] o2 = new Object[]{h2};
			Object[] o3 = new Object[]{h3};
			Object[] o4 = new Object[]{h4};
			Object[] o5 = new Object[]{h5};
			candStepperClass.getMethod("setExperimentalVariables", new Class[]{h1.getClass()}).invoke(candStepperClass, o1); 
			testStepperClass.getMethod("setExperimentalVariables", new Class[]{h2.getClass()}).invoke(testStepperClass, o2); 
			domainClass.getMethod("setExperimentalVariables", new Class[]{h3.getClass()}).invoke(domainClass, o3); 
			*/
			
			
			///////////////////////////////////////////////
			
			
            // generate max window size
            if (m_Str.equals("0"))	{
                memoryWindow = g;
                p.setProperty(CoevPropertiesEditor.MEMORY_WINDOW_SIZE, ""+memoryWindow);
            }
            else memoryWindow = Integer.parseInt(m_Str);

            // generate a random seed
            if (r_Str.equals("0")) {
                seed = startTime;
                p.setProperty(CoevPropertiesEditor.RANDOM_SEED, ""+seed);
            }
            else seed = Long.parseLong(r_Str);

			// no longer accept null
            if (mUpdate_Str==null) {
                memUpdateFreq = 1;
                p.setProperty(CoevPropertiesEditor.MEMORY_WINDOW_UPDATE_FREQUENCY, ""+1);
            }
            else memUpdateFreq = Integer.parseInt(mUpdate_Str); 

			
			// set the seed property
			p.setProperty(CoevPropertiesEditor.RANDOM_SEED, ""+seed);
			
            genCount = g;

            //stepper.prepare(seed, c, t, rate, bias, size, game);
			
			try {
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG","SUN");
				random.setSeed(seed);
			
				candStepper.prepare(random, c, t, rate, bias, size, game);
				testStepper.prepare(random, c, t, rate, bias, size, game);
			} catch (Exception e) { e.printStackTrace(); }
			
			
			try { 	
				String runId = "" + startTime; 

				// make the directories
				File run = ViewerPanel.getRunDir(runId); 
				File img = new File(run, "images"); 
				File log = new File(run, "logs"); 
				run.mkdirs(); // make it!
				img.mkdirs(); // make it!
				log.mkdirs(); // make it!
				
				// store the last run id.
				File dir = new File(System.getProperty("user.dir"));
				File f = new File(dir, "lastrun.txt");
				PrintStream ps_info = new PrintStream(new BufferedOutputStream( new FileOutputStream(f, false)), true);
				ps_info.println(""+startTime);
				ps_info.flush();
				ps_info.close(); 
				
				// Store the properties as a file
				File f2 = new File(ViewerPanel.getRunDir(runId), "config.properties");
				p.store( new BufferedOutputStream( new FileOutputStream(f2 ,false)), "");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
            v = new Viewer(coevis, g, p, game, c, t, memoryWindow, startTime, 
						   getTitle(), useGui, seed, candRepClass, testRepClass, memUpdateFreq);

			cands = candStepper.getInitialCandidates(candRepClass, toHashTable(expCandRep));
            tests = testStepper.getInitialTests(testRepClass, toHashTable(expTestRep));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
	public static Hashtable toHashTable(String str1) {
		Hashtable ht2 = new Hashtable(); 
		try {
			str1 = str1.substring(1,str1.length()-1); 
			
			if (!str1.equals("")) { 
				boolean done = false; 
				int lastCommaAt = 0; 
				while (!done) {
					int commaAt = str1.indexOf(", ", lastCommaAt); 
					String line; 
					if (commaAt == -1) {
						line = str1.substring(lastCommaAt,str1.length());
						done = true; 
					} else {
						line = str1.substring(lastCommaAt,commaAt); 
						lastCommaAt = commaAt+2;
					}
					ht2.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=")+1)); 
				}
			} 
		} catch (Exception e) {
			System.out.println("str1="+str1); 
			e.printStackTrace(); 
		}
		
		return ht2; 
	}
	
	
	// evolutionary step
    public boolean stepPopulation() throws Exception {
        if (i<genCount) {
		
			if (!useGui) displayUpdate(i, genCount);

			v.recordToView(i, cands, tests);
            
            // cands = stepper.nextCands(cands, tests);
            // tests = stepper.nextTests(cands, tests);
			
			if (candStepper.candsPerMetacand == 0) {
				
				// System.out.println("as before");
				cands = candStepper.nextCands(cands, tests);
				
			} else {
				int candsPerMetacand = candStepper.candsPerMetacand; 
				int metacandCount = ((int) Math.ceil(1.0 * cands.length / candsPerMetacand));			
				// System.out.println("candsPerMetacand: "+candsPerMetacand+", metacandCount"+metacandCount); 
				
				for (int finishedMetacands = 0; finishedMetacands < metacandCount; finishedMetacands++) {
					
					Candidate[] currentCands = new Candidate[candsPerMetacand];  
					
					for (int i=0; i < candsPerMetacand; i++) 
						if (finishedMetacands * candsPerMetacand + i < cands.length) 
							currentCands[i] = cands[finishedMetacands * candsPerMetacand + i];
					
					currentCands = candStepper.nextCands(currentCands, tests); 
					
					for (int i=0; i < candsPerMetacand; i++) 
						if (finishedMetacands * candsPerMetacand + i < cands.length) 
							cands[finishedMetacands * candsPerMetacand + i] = currentCands[i];
					
				}
			}
			
			if (testStepper.testsPerMetatest == 0) { 

				// System.out.println("as before");
				tests = testStepper.nextTests(cands, tests);
			
			} else {
				
				int testsPerMetatest = testStepper.testsPerMetatest; 				
				int metatestCount = ((int) Math.ceil(1.0 * tests.length / testsPerMetatest)); 
				// System.out.println("testsPerMetatest: "+testsPerMetatest+", metatestCount"+metatestCount); 

				for (int finishedMetatests = 0; finishedMetatests < metatestCount; finishedMetatests++) {
					
					Test[] currentTests = new Test[testsPerMetatest];  
					
					for (int i=0; i < testsPerMetatest; i++) 
						if (finishedMetatests * testsPerMetatest + i < tests.length) 
							currentTests[i] = tests[finishedMetatests * testsPerMetatest + i];
					
					currentTests = testStepper.nextTests(cands, currentTests); 
					
					for (int i=0; i < testsPerMetatest; i++) 
						if (finishedMetatests * testsPerMetatest + i < tests.length) 
							tests[finishedMetatests * testsPerMetatest + i] = currentTests[i];
					
				}
				
			}
			
			
            i++;
            return false;
        }
        else {
			if (!closedForGood) {
				saveFinal();
				doneRunning = true; 
			}
			return true;
		}
    }


	public boolean isDoneRunning() {
		return doneRunning; 
	}
	
	public void pauseSim() {
		v.pauseViewer(); 
	}



    public String getTitle() {
        return "runs/temp/"+startTime;
    }
    public void displayUpdate(int i, int genCount) {
        if (i>0) if ((1.0*i/(genCount/10)) == (1.0*(i/(genCount/10))))
            System.out.println("\t"+(1.0*i/(genCount/100))+"% complete. ("+i+" of "+genCount+" generations.)");
    }
    public Properties getProperties() {
        return p;
    }
    public void close() throws Exception {
        if (!closedForGood) {
			System.out.println("closing"); 
			v.closeFiles();
			//v.saveFinalImageFiles(); 
			closedForGood = true; 
		}
	}
	public void saveFinal() throws Exception {
		v.saveFinalImageFiles(); 
		System.out.println("saving"); 
	}

    public static void main(String[] args) {

        if (args.length != 1)
            System.out.println("Usage: java coeviz.visualization.Coevisualizer");
        else {

            Properties pr = new Properties();
            try {
                // load the properties file
                pr.load(new FileInputStream(args[0]));
                // instantiate the launcher
                Launcher launcher = new Launcher(pr, null);
                // run until done.
                boolean done = false;
                while (!done) 
					done = launcher.stepPopulation();
                launcher.close();

            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
