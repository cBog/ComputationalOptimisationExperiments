package experiments;

import algorithms.CMAES;
import algorithms.ISPO;
import algorithms.SimulatedAnnealing;
import benchmarks.BaseFunctions.*;
import benchmarks.BaseFunctions.Michalewicz;
import benchmarks.BaseFunctions.Rastrigin;
import benchmarks.BaseFunctions.Schwefel;
import benchmarks.BaseFunctions.Sphere;
import interfaces.Algorithm;
import interfaces.Experiment;

/**
 * Created by Mikolaj on 29/03/2018.
 */
public class Task2Experiment extends Experiment
{
    public Task2Experiment(int probDim) throws Exception
    {
        super(probDim, 5000, "Task2AlgorithmComparison2: " + probDim + "D", true, true);
        setNrRuns(30);

        Algorithm a;

        a = new SimulatedAnnealing();
        a.setParameter("initTemp", 1.0e9);
        a.setParameter("minTemp", 0.00);
        a.setParameter("expCoolingFactor", 0.8);
        a.setParameter("perturbationFactor", 0.4);
        add(a);

        a = new ISPO();
        a.setParameter("p0", 1.0);
        a.setParameter("p1", 10.0);
        a.setParameter("p2", 2.0);
        a.setParameter("p3", 4.0);
        a.setParameter("p4", 1e-5);
        a.setParameter("p5", 30.0);
        add(a);

        a = new CMAES();
        add(a);

        add(new Sphere(probDim, new double[]{-5.12, 5.12}));
        add(new Schwefel(probDim, new double[]{-500, 500}));
        add(new Rastrigin(probDim, new double[]{-5.12, 5.12}));
        add(new Michalewicz(probDim, new double[]{0.0,Math.PI}));
        add(new Ackley(probDim));//add it to the list
        add(new Alpine(probDim));
        add(new Rosenbrock(probDim));
    }
}
