package coeviz.framework;

import java.io.*;
import java.util.*;
import coeviz.framework.interfaces.*;


public class Recorder {

    private PrintStream ps_cands, ps_tests, ps_candresults, ps_testresults, ps_info;

    public Recorder(long randSeed, Properties p, long recordStartTime) {
        
        File dir = getRunDir(""+recordStartTime);
        dir.mkdirs(); // make it!
        String path = dir.getAbsolutePath() + "/";

        try {
            ps_info		= new PrintStream(new BufferedOutputStream( new FileOutputStream("lastrun.txt", false)), true);
            ps_cands		= new PrintStream(new BufferedOutputStream( new FileOutputStream( path + "cands.txt",   false)), true);
            ps_tests		= new PrintStream(new BufferedOutputStream( new FileOutputStream( path + "tests.txt",   false)), true);
            ps_candresults	= new PrintStream(new BufferedOutputStream( new FileOutputStream( path + "candresults.txt", false)), true);
            ps_testresults	= new PrintStream(new BufferedOutputStream( new FileOutputStream( path + "testresults.txt", false)), true);

            ps_info.println(""+recordStartTime);

            // Store the properties as a file
            p.setProperty("randomSeed", ""+randSeed);
            p.store( new BufferedOutputStream( new FileOutputStream( path + "config.properties",false)), "");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static File getRunDir(String name) {
        // Prepare the output path
        File dir = new File(System.getProperty("user.dir"));
        if (!dir.isDirectory())
            throw new IllegalArgumentException("no such directory");
        // move to ../runs/alg
        dir = new File(dir.getParent());
        dir = new File(dir, "runs");
        String runs_dir = dir.getAbsolutePath();
        dir = new File(dir, "temp");
        // identify new dir and create it
        dir = new File(dir, "" + name);
        return dir;
    }

    

    public void recordToDisk(Candidate[] candPop, Test[] testPop,
                             int[] cand_results, int[] test_results) {
        
        // Write line of cand population
        for (int i=0; i<candPop.length; i++)
            ps_cands.print(candPop[i].toString() + "\t");
        ps_cands.println("");

        // Write line of test population
        for (int i=0; i<testPop.length; i++)
            ps_tests.print(testPop[i].toString() + "\t");
        ps_tests.println("");

        // Write line of cand's results
        for (int i=0; i<cand_results.length; i++) 
            ps_candresults.print(cand_results[i] + "\t");
        ps_candresults.println("");

        // Write line of test's results
        for (int i=0; i<test_results.length; i++) 
            ps_testresults.print(test_results[i] + "\t");
        ps_testresults.println("");

    }

    public void closeFiles() throws Exception {

        ps_info.flush();
        ps_cands.flush();
        ps_tests.flush();
        ps_testresults.flush();
        ps_candresults.flush();

        ps_info.close();
        ps_cands.close();
        ps_tests.close();
        ps_testresults.close();
        ps_candresults.close();

    }
}
