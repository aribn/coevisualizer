package coeviz.visualization;

import java.util.*;
import java.io.*;

public class MakePGM {

    public MakePGM (String path_to_run_dir) {
        try {

            // read in this run's properties.
            Properties properties = new Properties();
            properties.load(new FileInputStream(path_to_run_dir + "config.properties"));
            String m_Str		= properties.getProperty("memoryWindow");
            int genCount		= Integer.parseInt(properties.getProperty("generations"));
            int cPopSize		= Integer.parseInt(properties.getProperty("candidatePopulationSize"));
            int tPopSize 		= Integer.parseInt(properties.getProperty("testPopulationSize"));

            int memWin;
            if (m_Str == null)		memWin = genCount;
            else 			memWin = Integer.parseInt(m_Str);

            // prepare the input and output files
            BufferedReader in = new BufferedReader(new FileReader(path_to_run_dir + "candresults.txt"));
            BufferedReader in2 = new BufferedReader(new FileReader(path_to_run_dir + "testresults.txt"));
            PrintStream ps_pgm = new PrintStream(new BufferedOutputStream(new FileOutputStream(path_to_run_dir + "visual.pgm",false)), true);

            // Prepare the PGM header
            ps_pgm.println("P2\n"+genCount+" "+genCount+"\n"+(2*cPopSize*tPopSize+1));


            // hold the data in mem temporarily
            String[][] vals = new String[genCount][genCount];
            for (int i=0; i<genCount; i++)
                for (int j=0; j<genCount; j++)
                    vals[i][j] = "0";
            


            // for each entry in candresults
            for (int i=0; i<genCount; i++) {

                String dataline = in.readLine();

                StringTokenizer st = new StringTokenizer(dataline, "\t");
                String[] strData = new String[st.countTokens()];
                for (int j=0; j<strData.length; j++)
                    strData[j] = st.nextToken();

                /*
                 // pre-blanks
                 if ((i-(memWin-1)) > 0)
                 for (int b=0; b<(i-(memWin-1)); b++)
                 ps_pgm.print("0 ");

                 // data
                 for (int mem_index = 0; mem_index < memWin; mem_index++)
                 if (i - (memWin-1) + mem_index >= 0)
                 ps_pgm.print(strData[mem_index]+" ");

                 // post-blanks
                 if (i+1 < genCount)
                 for (int b = i+1; b < genCount; b++)
                 ps_pgm.print("0 ");

                 ps_pgm.println("");
                 */


                int start = i-(memWin-1);

                // data
                for (int mem_index = 0; mem_index < memWin; mem_index++)
                    if (i - (memWin-1) + mem_index >= 0)
                        vals[i][start + mem_index] = strData[mem_index];

            }

            
            
            // for each entry in testresults
            for (int i=0; i<genCount; i++) {

                String dataline = in2.readLine();

                StringTokenizer st = new StringTokenizer(dataline, "\t");
                String[] strData = new String[st.countTokens()];
                for (int j=0; j<strData.length; j++)
                    strData[j] = st.nextToken();

                int start = i-(memWin-1);

                // data
                for (int mem_index = 0; mem_index < memWin; mem_index++)
                    if (i - (memWin-1) + mem_index >= 0)
                        vals[start + mem_index][i] = strData[mem_index];

            }

             


            // Write to file...
            for (int i=0; i<genCount; i++) {
                for (int j=0; j<genCount; j++) {
                    ps_pgm.print(vals[i][j] + "  ");
                    if (j%10 == 0) ps_pgm.println("");
                }
                ps_pgm.println("");
            }            
            ps_pgm.flush();
            ps_pgm.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public static void main (String args[]) {
        if (args.length != 1)
            System.out.println("Usage: java coeviz.visualizer.MakePGM <path_to_a_run_dir>");
        else new MakePGM(args[0]);
    }
}


