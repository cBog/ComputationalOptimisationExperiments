package experiments;

import SearchProblem.SimpleAgentSearchProblem;
import algorithms.*;
import interfaces.Algorithm;
import interfaces.Experiment;

import java.util.Vector;

import static utils.RunAndStore.resultsFolder;

/**
 * Created by Mikolaj on 07/05/2018.
 */
public class Task3Experiments extends Experiment {
    public Task3Experiments() {
        super(50, 500, "Task3Experiment1", true, true);
        setNrRuns(15);

        Algorithm a;

        a = new JADEDEWExponentialFreezing();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("p", 0.05);
        a.setParameter("c", 0.08);
        a.setParameter("ArchiveSize", 2000.0);
        a.setParameter("freeze", 2.0);
        add(a);

        a = new JADEDEWExponentialFreezing();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("p", 0.05);
        a.setParameter("c", 0.08);
        a.setParameter("ArchiveSize", 2000.0);
        a.setParameter("freeze", 5.0);
        add(a);

        a = new SimulatedAnnealing();
        a.setParameter("initTemp", 1.0e9);
        a.setParameter("minTemp", 0.00);
        a.setParameter("expCoolingFactor", 0.8);
        a.setParameter("perturbationFactor", 0.4);
        add(a);

        a = new ClassicDifferentialEvolution();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("F", 0.75);
        a.setParameter("CR", 0.9);
        add(a);

        a = new JADE();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("p", 0.05);
        a.setParameter("c", 0.08);
        a.setParameter("ArchiveSize", 2000.0);
        add(a);

        a = new JADEDecodedEvolvingWaypoints();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("p", 0.05);
        a.setParameter("c", 0.08);
        a.setParameter("ArchiveSize", 2000.0);
        add(a);






        add(SimpleAgentSearchProblem.CreateSearchProblem0(70, 500, 500, 50, 300));
        add(SimpleAgentSearchProblem.CreateSearchProblem1(70, 500, 500, 50, 300));
        add(SimpleAgentSearchProblem.CreateSearchProblem2(70, 500, 500, 50, 300));


    }

    public static void main(String[] args) throws Exception
    {

        // make sure that "results" folder exists
        resultsFolder();


        Vector<Experiment> experiments = new Vector<Experiment>();////!< List of problems


        experiments.add(new Task3Experiments());

        for(Experiment experiment : experiments)
        {
            //experiment.setShowPValue(true);
            experiment.startExperiment();
            System.out.println();
            experiment = null;
        }



    }
}
