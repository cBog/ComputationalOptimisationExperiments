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
    public Task3Experiments(int ScenarioNumber, int numWaypoints) {
        super(numWaypoints, 4000, "Task3Experiment-"+ScenarioNumber, true, true);
        setNrRuns(15);

        Algorithm a;

        a = new JADEDEWExponentialFreezing();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("p", 0.05);
        a.setParameter("c", 0.08);
        a.setParameter("ArchiveSize", 2000.0);
        a.setParameter("freeze", 2.0);
        add(a);

//        a = new JADEDEWExponentialFreezing();
//        a.setParameter("PopulationSize", 500.0);
//        a.setParameter("p", 0.05);
//        a.setParameter("c", 0.08);
//        a.setParameter("ArchiveSize", 2000.0);
//        a.setParameter("freeze", 2.0);
//        add(a);
//
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
        a = new JADEDecodedEvolvingWaypoints();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("p", 0.05);
        a.setParameter("c", 0.08);
        a.setParameter("ArchiveSize", 2000.0);
        add(a);

        a = new JADE();
        a.setParameter("PopulationSize", 500.0);
        a.setParameter("p", 0.05);
        a.setParameter("c", 0.08);
        a.setParameter("ArchiveSize", 2000.0);
        add(a);






        if (ScenarioNumber ==1)
            add(SimpleAgentSearchProblem.CreateSearchProblem0(60, 500, 500, numWaypoints, 1000));
        else if (ScenarioNumber == 2)
            add(SimpleAgentSearchProblem.CreateSearchProblem1(60, 500, 500, numWaypoints, 1000));
        else if (ScenarioNumber == 3)
            add(SimpleAgentSearchProblem.CreateSearchProblem2(60, 500, 500, numWaypoints, 1000));


    }

    public static void main(String[] args) throws Exception
    {

        // make sure that "results" folder exists
        resultsFolder();


        Vector<Experiment> experiments = new Vector<Experiment>();////!< List of problems


        experiments.add(new Task3Experiments(1,50));
        experiments.add(new Task3Experiments(2,100));
        experiments.add(new Task3Experiments(3,100));

        for(Experiment experiment : experiments)
        {
            //experiment.setShowPValue(true);
            experiment.startExperiment();
            System.out.println();
            experiment = null;
        }



    }
}
