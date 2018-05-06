/** @file RunExperiments.java
 *  
 *  @author Fabio Caraffini
*/

import experiments.Task2Experiment;
import interfaces.Experiment;

import java.util.Vector;

import static utils.RunAndStore.resultsFolder;

/** 
* This class contains the main method and has to be used for launching experiments.
*/
public class RunExperiments
{
	
	
	/** 
	* Main method.
	* This method has to be modified in order to launch a new experiment.
	*/
	public static void main(String[] args) throws Exception
	{	
		
		// make sure that "results" folder exists
		resultsFolder();
	
	
		Vector<Experiment> experiments = new Vector<Experiment>();////!< List of problems 
	
			
		//@@@ MODIFY THIS PART @@@
		//experiments.add(new Task2Experiment(3));
		experiments.add(new Task2Experiment(10));
		experiments.add(new Task2Experiment(30));
		experiments.add(new Task2Experiment(50));
		//experiments.add(new Test(5));
		//experiments.add(new Test(10));
		//experiments.add(new Test(20));
		//experiments.add(new CEC14(10));
		//experiments.add(new CEC14(50));
		//experiments.add(new CEC14(100));
		//experiments.add(new CEC15(10));
		//experiments.add(new CEC15(50));
		//experiments.add(new CEC15(100));
		
		//@@@@@@

//		Problem problem = new BaseFunctions.Michalewicz(2);
//
//		final ProblemSurfaceGenerator surfaceModel = new ProblemSurfaceGenerator(problem, -2.0f, 0.0f);
//		java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new ProblemSurfaceFrame(surfaceModel).setVisible(true);
//            }
//        });
	
		for(Experiment experiment : experiments)
		{
			//experiment.setShowPValue(true);
			experiment.startExperiment();
			System.out.println();
			experiment = null;
		}

		
		
	}
	
	

}
