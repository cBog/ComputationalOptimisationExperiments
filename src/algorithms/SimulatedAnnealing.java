package algorithms;

import interfaces.Algorithm;
import interfaces.Problem;
import utils.RunAndStore.FTrend;
import utils.algorithms.Misc;
import utils.random.RandUtils;

/**
 * Created by Mikolaj on 24/03/2018.
 * Simulated annealing implementation
 */
public class SimulatedAnnealing extends Algorithm {

    @Override
    public FTrend execute(Problem problem, int maxEvaluations) throws Exception
    {
        // Get parameters for the algorithm...
        double initialTemperature = getParameter("initTemp");
        double minimumTemperature = getParameter("minTemp");
        double expCoolingFactor = getParameter("expCoolingFactor");
        double perturbationFactor = getParameter("perturbationFactor");

        // Create the FTrend to store the fitness trend
        FTrend fTrend = new FTrend();

        // Get the problem settings
        int problemDimension = problem.getDimension();
        double[][] bounds = problem.getBounds();

        // particle (the solution, i.e. "x")
        double[] particle = new double[problemDimension];
        double particleFitness; //fitness value, i.e. "f(x)"
        int fitnessCalculationCount = 0;

        // get initial solution
        if (initialSolution != null)
        {
            particle = initialSolution;
            particleFitness = initialFitness;
        }
        else//random intitial guess
        {
            particle = Misc.generateRandomSolution(bounds, problemDimension);
            particleFitness = problem.f(particle);
        }

        //store the initital guess
        fTrend.add(0, particleFitness);
        fitnessCalculationCount++;

        // execution parameters
        double currentTemperature = initialTemperature;
        double[] newParticle = new double[problemDimension];
        double newParticleFitness;

        while (fitnessCalculationCount < maxEvaluations && currentTemperature > minimumTemperature)
        {
            //currentTemperature = initialTemperature*(1.0-((double)fitnessCalculationCount/(double)maxEvaluations));

            newParticle = randomNeighbour(particle, bounds, perturbationFactor);//Misc.generateRandomSolution(bounds, problemDimension);

            newParticleFitness = problem.f(newParticle);
            fitnessCalculationCount++;

            double fitnessDelta = particleFitness - newParticleFitness;
            double uniformRandom = RandUtils.uniform(0,1.0);

            if (uniformRandom < Math.exp(fitnessDelta / currentTemperature)) {
                particleFitness = newParticleFitness;
                particle = newParticle;
                //fTrend.add(fitnessCalculationCount, particleFitness);

            }

            if(fitnessCalculationCount%problemDimension==0)
                fTrend.add(fitnessCalculationCount, particleFitness);

            currentTemperature = currentTemperature * expCoolingFactor;
        }

        finalBest = particle; //save the final best

        return fTrend;
    }

    private double[] randomNeighbour(double[] particle, double[][]bounds, double perturbationFactor) {
        double[] newParticle = new double[particle.length];

        for (int i=0; i<particle.length; i++)
            newParticle[i] = particle[i] + (perturbationFactor*RandUtils.uniform(bounds[i][0], bounds[i][1]));

        return Misc.toro(newParticle, bounds);
    }
}
