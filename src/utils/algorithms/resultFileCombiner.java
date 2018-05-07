package utils.algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Mikolaj on 07/05/2018.
 */
public class ResultFileCombiner {
    ArrayList<ArrayList<Double>> results;
    ArrayList<ArrayList<Double>> bestPaths;

    public ResultFileCombiner(String pathToResultsFolder) throws FileNotFoundException {
        results = new ArrayList<ArrayList<Double>>();
        bestPaths = new ArrayList<ArrayList<Double>>();

        File dir = new File(pathToResultsFolder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (!child.isDirectory() && !child.isHidden())
                    parseFile(child);
            }
        }

        File resultsFile = new File(pathToResultsFolder+"/combinedResults.csv");

        writeResults(resultsFile);


    }

    private void parseFile(File file) throws FileNotFoundException {

        ArrayList<Double> newResultSet = new ArrayList<Double>();
        ArrayList<Double> newBestPath = new ArrayList<Double>();

        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("\n");
        if (scanner.hasNext()) {
            String line = scanner.next();
            String[] angleStrings = line.split(" ");

            for (String angle: angleStrings)
                if (!angle.equals("#"))
                    newBestPath.add(Double.parseDouble(angle));
        }

        while(scanner.hasNext()){
            String line = scanner.next();
            String[] resultLine = line.split("\t");
            newResultSet.add(Double.parseDouble(resultLine[1]));
        }
        scanner.close();

        results.add(newResultSet);
        bestPaths.add(newBestPath);
    }


    public void writeResults(File resultsFile) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(resultsFile);
        StringBuilder sb = new StringBuilder();
        int numberOfGenerations = results.get(0).size();

//        sb.append("generation");
//        for (int i = 0; i<results.size(); i++)
//        {
//            sb.append(",");
//            sb.append("run" + i);
//        }

        for (int i = 0; i < numberOfGenerations; i++){

            sb.append("\n");

            sb.append(i);

            String prefix = "\t";
            for (ArrayList<Double> run:results)
            {
                sb.append(prefix);
                sb.append(run.get(i));
            }
        }

        pw.write(sb.toString());
        pw.close();
    }

    public static void main(String[] args)
    {
        try {
            ResultFileCombiner fileCombiner = new ResultFileCombiner("/Users/Mikolaj/Google Drive/MSc_New/ComputationalIntelligenceOptimisation-IMAT5232/Labs/SOS Software/SOS/results/Task2AlgorithmComparison: 10D/SimulatedAnnealing/benchmarks.BaseFunctions.Michalewicz-10");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
