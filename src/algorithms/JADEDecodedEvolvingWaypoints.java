package algorithms;

import SearchProblem.SimpleAgentSearchProblem;
import interfaces.Algorithm;
import interfaces.Problem;
import utils.MatLab;
import utils.RunAndStore;
import utils.algorithms.TopNPicker;
import utils.random.RandUtils;

import java.util.ArrayList;

import static utils.MatLab.max;
import static utils.MatLab.min;

/**
 * Created by Mikolaj on 06/05/2018.
 */
public class JADEDecodedEvolvingWaypoints extends Algorithm {
    @Override
    public RunAndStore.FTrend execute(Problem problem, int budget) throws Exception {

        int computations = 0;
        SimpleAgentSearchProblem searchProblem = (SimpleAgentSearchProblem)problem;

        int populationSize = getParameter("PopulationSize").intValue();
        double topPercentage = getParameter("p");
        int archiveSize = getParameter("ArchiveSize").intValue();
        double cAdaptionParameter = getParameter("c");

        int numberTopPercentage = (int)(topPercentage*populationSize);

        double muF = 0.5; double muCR = 0.5;
        ArrayList<double[]> archive = new ArrayList<double[]>();
        ArrayList<double[][]> archiveWaypoints = new ArrayList<double[][]>();

        double[][] population = new double[populationSize][problem.getDimension()];
        double[][][] populationWaypoints = new double[populationSize][problem.getDimension()][2];
        double[] populationFitness = new double[populationSize];
        populationFitness[0] = Double.MAX_VALUE;
        TopNPicker populationBestN = new TopNPicker(numberTopPercentage);

        for (int i=0;i<populationSize;i++) {
            //TODO: get start points from problem definition?
            double[] previousLocation = new double[]{searchProblem.startXPos,searchProblem.startYPos};
            double previousHeading = 0;

            for (int j = 0; j < problem.getDimension(); j++){
                population[i][j] = RandUtils.uniform(problem.getBounds()[j][0], problem.getBounds()[j][1]);
                previousHeading = previousHeading + population[i][j];

                populationWaypoints[i][j][0] = previousLocation[0] + (Math.sin(previousHeading) * searchProblem.waypointDistance);
                populationWaypoints[i][j][1] = previousLocation[1] + (Math.cos(previousHeading) * searchProblem.waypointDistance);

                previousLocation = populationWaypoints[i][j];
            }
            populationFitness[i] = problem.f(population[i]); computations++;

            populationBestN.add(populationFitness[i], i);
        }

        // Create the FTrend to store the fitness trend
        RunAndStore.FTrend fTrend = new RunAndStore.FTrend();
        int fitnessTrendCount = 0;
        fTrend.add(fitnessTrendCount++, populationBestN.getBestFitness());

        while (computations < budget)
        {
            double sCR = 0.0;
            double countCR = 0.0;
            double sSqrF = 0.0;
            double sF = 0.0;

            double[][] newPopulation = new double[populationSize][problem.getDimension()];
            double[][][] newPopulationWaypoints = new double[populationSize][problem.getDimension()][2];
            double[] newPopulationFitness = new double[populationSize];
            TopNPicker newTopN = new TopNPicker(numberTopPercentage);

            for (int i = 0; i < populationSize; i++) {

                double CR = RandUtils.gaussian(muCR, 0.1);
                double F = RandUtils.cauchy(muF, 0.1);

                //MUTATION STEPS

                int randomBestIndex = populationBestN.getRandomTopIndex();
                int randomPopulationIndex;
                int randomArchiveIndex;

                do {
                    randomPopulationIndex = RandUtils.randomInteger(populationSize-1);
                } while (randomPopulationIndex == randomBestIndex || randomPopulationIndex == i);

                do {
                    randomArchiveIndex = RandUtils.randomInteger(populationSize+archive.size()-1);
                } while (randomArchiveIndex == randomPopulationIndex || randomArchiveIndex == randomBestIndex || randomArchiveIndex == i);

//                double[] randomBest = population[randomBestIndex];
//                double[] randomPopulation = population[randomPopulationIndex];
//                double[] randomArchive;
                double[][] randomBestWaypoints = populationWaypoints[randomBestIndex];
                double[][] randomPopulationWaypoints = populationWaypoints[randomPopulationIndex];
                double[][] randomArchiveWaypoints;

                // get the solution from the archive if the index is larger than population
                if (randomArchiveIndex < populationSize)
                    randomArchiveWaypoints = populationWaypoints[randomArchiveIndex];
                else
                    randomArchiveWaypoints = archiveWaypoints.get(randomArchiveIndex - populationSize);

                double[][] mutatedwaypoints = MatLab.sum(populationWaypoints[i],
                        MatLab.sum(MatLab.multiply(F, MatLab.subtract(randomBestWaypoints,populationWaypoints[i])),
                                MatLab.multiply(F, MatLab.subtract(randomPopulationWaypoints,randomArchiveWaypoints))));


                //TODO: put the crossover in here and do something smart
                double[] mutated = new double[mutatedwaypoints.length];
                double[] previousLocation = new double[]{searchProblem.startXPos,searchProblem.startYPos};
                double previousHeading = 0;
                for (int w = 0; w < mutated.length; w++)
                {
                    double dX = mutatedwaypoints[w][0] - previousLocation[0];
                    double dY = mutatedwaypoints[w][1] - previousLocation[1];

                    mutated[w] = min(max(Math.atan2(dX,dY) - previousHeading, problem.getBounds()[w][0]), problem.getBounds()[w][1]);

                    previousHeading = mutated[w] + previousHeading;

                    previousLocation[0] = previousLocation[0] + (Math.sin(previousHeading) * searchProblem.waypointDistance);
                    previousLocation[1] = previousLocation[1] + (Math.cos(previousHeading) * searchProblem.waypointDistance);
                }

                //TODO: don't cross over based on waypoints if better but far away! Something like that..?
                //mutated = saturate(mutated, problem.getBounds());


                //CROSS OVER

                double[] crossedOver = binomialXO(population[i], mutated, CR);

                double[][] crossedOverWaypoints = new double[crossedOver.length][2];
                previousLocation = new double[]{searchProblem.startXPos,searchProblem.startYPos};
                previousHeading = 0;

                for (int w = 0; w < problem.getDimension(); w++){
                    previousHeading = previousHeading + crossedOver[w];

                    crossedOverWaypoints[w][0] = previousLocation[0] + (Math.sin(previousHeading) * searchProblem.waypointDistance);
                    crossedOverWaypoints[w][1] = previousLocation[1] + (Math.cos(previousHeading) * searchProblem.waypointDistance);

                    previousLocation = crossedOverWaypoints[w];
                }


                double crossedOverFitness = problem.f(crossedOver); computations++;

                if (populationFitness[i] < crossedOverFitness) {
                    newPopulation[i] = population[i];
                    newPopulationWaypoints[i] = populationWaypoints[i];
                    newPopulationFitness[i] = populationFitness[i];
                    newTopN.add(populationFitness[i], i);
                }
                else {
                    newPopulation[i] = crossedOver;
                    newPopulationWaypoints[i] = crossedOverWaypoints;
                    newPopulationFitness[i] = crossedOverFitness;
                    newTopN.add(crossedOverFitness, i);

                    archive.add(population[i]);
                    archiveWaypoints.add(populationWaypoints[i]);
                    sCR += CR;
                    countCR++;
                    sSqrF += F*F;
                    sF += F;
                }
            }

            while (archive.size() > archiveSize) {
                int randomIndex = RandUtils.randomInteger(archive.size()-1);
                archive.remove(randomIndex);
                archiveWaypoints.remove(randomIndex);
            }

            population = newPopulation;
            populationWaypoints = newPopulationWaypoints;
            populationFitness = newPopulationFitness;
            populationBestN = newTopN;

            // UPDATE MU PARAMS
            double meanSuccessfulCR;
            if (sCR > 0)
                meanSuccessfulCR = sCR/countCR;
            else
                meanSuccessfulCR = 0.0;

            double lehmerMeanSuccessfulF;
            if (sSqrF > 0)
                lehmerMeanSuccessfulF = sSqrF/sF;
            else
                lehmerMeanSuccessfulF = 0.0;

            muCR = ((1-cAdaptionParameter)*muCR)+(cAdaptionParameter*meanSuccessfulCR);
            muF = ((1-cAdaptionParameter)*muCR)+(cAdaptionParameter*lehmerMeanSuccessfulF);

            fTrend.add(fitnessTrendCount++, populationBestN.getBestFitness());
        }


        finalBest = population[populationBestN.getBestIndex()];

        return fTrend;
    }

    public double[] binomialXO(double[] old, double[] mutated, double crScalar){
        double[] xo = new double[old.length];

        int selectedIndex = RandUtils.randomInteger(old.length);

        for (int i = 0; i < old.length; i++){
            if (i == selectedIndex || RandUtils.random() < crScalar){
                xo[i] = mutated[i];
            }
            else {
                xo[i] = old[i];
            }
        }

        return  xo;
    }
}
