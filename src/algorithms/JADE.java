package algorithms;

import interfaces.Algorithm;
import interfaces.Problem;
import utils.MatLab;
import utils.RunAndStore;
import utils.algorithms.TopNPicker;
import utils.random.RandUtils;

import java.util.ArrayList;

import static utils.algorithms.Misc.saturate;

/**
 * Created by Mikolaj on 06/05/2018.
 */
public class JADE extends Algorithm {
    @Override
    public RunAndStore.FTrend execute(Problem problem, int budget) throws Exception {

        int computations = 0;

        int populationSize = getParameter("PopulationSize").intValue();
        double topPercentage = getParameter("p");
        int archiveSize = getParameter("ArchiveSize").intValue();
        double cAdaptionParameter = getParameter("c");

        int numberTopPercentage = (int)(topPercentage*populationSize);

        double muF = 0.5; double muCR = 0.5;
        ArrayList<double[]> archive = new ArrayList<double[]>();

        double[][] population = new double[populationSize][problem.getDimension()];
        double[] populationFitness = new double[populationSize];
        populationFitness[0] = Double.MAX_VALUE;
        TopNPicker populationBestN = new TopNPicker(numberTopPercentage);

        for (int i=0;i<populationSize;i++) {
            for (int j = 0; j < problem.getDimension(); j++){
                population[i][j] = RandUtils.uniform(problem.getBounds()[j][0], problem.getBounds()[j][1]);
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

                double[] randomBest = population[randomBestIndex];
                double[] randomPopulation = population[randomPopulationIndex];
                double[] randomArchive;

                // get the solution from the archive if the index is larger than population
                if (randomArchiveIndex < populationSize)
                    randomArchive = population[randomArchiveIndex];
                else
                    randomArchive = archive.get(randomArchiveIndex - populationSize);

                double[] mutated = MatLab.sum(population[i],
                        MatLab.sum(MatLab.multiply(F, MatLab.subtract(randomBest,population[i])),
                                MatLab.multiply(F, MatLab.subtract(randomPopulation,randomArchive))));

                mutated = saturate(mutated, problem.getBounds());


                //CROSS OVER

                double[] crossedOver = binomialXO(population[i], mutated, CR);

                double crossedOverFitness = problem.f(crossedOver); computations++;

                if (populationFitness[i] < crossedOverFitness) {
                    newPopulation[i] = population[i];
                    newPopulationFitness[i] = populationFitness[i];
                    newTopN.add(populationFitness[i], i);
                }
                else {
                    newPopulation[i] = crossedOver;
                    newPopulationFitness[i] = crossedOverFitness;
                    newTopN.add(crossedOverFitness, i);

                    archive.add(population[i]);
                    sCR += CR;
                    countCR++;
                    sSqrF += F*F;
                    sF += F;
                }
            }

            while (archive.size() > archiveSize) {
                archive.remove(RandUtils.randomInteger(archive.size()-1));
            }

            population = newPopulation;
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
