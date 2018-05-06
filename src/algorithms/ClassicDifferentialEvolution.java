package algorithms;

import interfaces.Algorithm;
import interfaces.Problem;
import utils.MatLab;
import utils.RunAndStore.FTrend;
import utils.algorithms.Misc;
import utils.random.RandUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mikolaj on 06/05/2018.
 */
public class ClassicDifferentialEvolution extends Algorithm {
    @Override
    public FTrend execute(Problem problem, int budget) throws Exception {

        int computations = 0;

        int populationSize = getParameter("PopulationSize").intValue();
        double fScalar = getParameter("F");
        double crScalar = getParameter("CR");

        ArrayList<Integer> indexes = new ArrayList<Integer>(populationSize);

        for (int i = 0; i < populationSize; i++)
        {
            indexes.add(i);
        }

        double[][] population = new double[populationSize][problem.getDimension()];
        double[] populationFitness = new double[populationSize];
        populationFitness[0] = Double.MAX_VALUE;
        int populationBest = 0;

        for (int i=0;i<populationSize;i++) {
            for (int j = 0; j < problem.getDimension(); j++){
                population[i][j] = RandUtils.uniform(problem.getBounds()[j][0],problem.getBounds()[j][1]);
            }
            populationFitness[i] = problem.f(population[i]); computations++;

            if (populationFitness[i]<populationFitness[populationBest])
                populationBest = i;
        }

        // Create the FTrend to store the fitness trend
        FTrend fTrend = new FTrend();
        int fitnessTrendCount = 0;
        fTrend.add(fitnessTrendCount++, populationFitness[populationBest]);

        while (computations < budget)
        {
            double[][] newPopulation = new double[population.length][problem.getDimension()];
            double[] newPopulationFitness = new double[populationSize];
            populationFitness[0] = Double.MAX_VALUE;
            int newPopulationbest = 0;

            double[] mutation;
            double[] crossover;

            for (int i = 0; i < population.length; i++)
            {
                mutation = rand1Mutation(population, i, fScalar, indexes);

                //TODO: saturate instead??!
                mutation = Misc.saturate(mutation, problem.getBounds());

                crossover = binomialXO(mutation, population[i], crScalar);

                //TODO: saturate instead??!
                //crossover = Misc.toro(crossover, problem.getBounds());

                newPopulationFitness[i] = problem.f(crossover); computations++;

                if (newPopulationFitness[i] < populationFitness[i]) {
                    newPopulation[i] = crossover;
                }
                else {
                    newPopulation[i] = population[i];
                }

                if (newPopulationFitness[i] < newPopulationFitness[newPopulationbest])
                    newPopulationbest = i;
            }

            population = newPopulation;
            populationFitness = newPopulationFitness;
            populationBest = newPopulationbest;
            fTrend.add(fitnessTrendCount++, populationFitness[populationBest]);
        }

        finalBest = population[populationBest];

        return fTrend;
    }


    public double[] rand1Mutation(double[][] population, int current, double fScalar, ArrayList<Integer> indexes){
        double[] mutation = new double[population[current].length];

        Collections.shuffle(indexes);

        int count= 0;
        int xr1 = indexes.get(count++);
        if (xr1==current)
            xr1 = indexes.get(count++);

        int xr2 = indexes.get(count++);
        if (xr2==current)
            xr2 = indexes.get(count++);

        int xr3 = indexes.get(count++);
        if (xr3==current)
            xr3 = indexes.get(count++);


        mutation = MatLab.sum(population[xr1], MatLab.multiply(fScalar, MatLab.subtract(population[xr2],population[xr3])));

        return mutation;
    }

    public double[] binomialXO(double[] old, double[] mutated, double crScalar){
        double[] xo = new double[old.length];

        int selectedIndex = RandUtils.randomInteger(old.length);

        for (int i = 0; i < old.length; i++){
            if (i == selectedIndex || RandUtils.random() < crScalar){
                xo[i] = old[i];
            }
            else {
                xo[i] = mutated[i];
            }
        }

        return  xo;
    }
}
