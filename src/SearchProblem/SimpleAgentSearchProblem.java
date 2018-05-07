package SearchProblem;

import interfaces.Problem;
import utils.random.RandUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikolaj on 29/04/2018.
 */
public class SimpleAgentSearchProblem extends Problem {

    private final double numberOfWaypoints;
    private List<Particle> particleDistribution;
    public double startXPos,startYPos;
    public double waypointDistance = 100;

    public SimpleAgentSearchProblem(int numberOfWaypoints, double[] bounds, int numberOfParticles, double startXPos, double startYPos) {
        super(numberOfWaypoints, bounds);
        this.numberOfWaypoints = numberOfWaypoints;
        this.startXPos = startXPos;
        this.startYPos = startYPos;

        particleDistribution = new ArrayList<Particle>();
        double maxSearchSpace = 1000;

        for (int i=0; i<numberOfParticles/5; i++)
        {
            particleDistribution.add(new Particle(RandUtils.gaussian(maxSearchSpace/2, maxSearchSpace / 20),
                    RandUtils.gaussian(maxSearchSpace/2, maxSearchSpace/20)));
        }

        for (int i=0; i<numberOfParticles/5; i++)
        {
            particleDistribution.add(new Particle(RandUtils.gaussian(maxSearchSpace/4, maxSearchSpace / 20),
                    RandUtils.gaussian(maxSearchSpace/4, maxSearchSpace/16)));
        }

        for (int i=0; i<numberOfParticles/5; i++)
        {
            particleDistribution.add(new Particle(RandUtils.gaussian(maxSearchSpace*3/4, maxSearchSpace / 20),
                    RandUtils.gaussian(maxSearchSpace*3/4, maxSearchSpace/16)));
        }

        for (int i=0; i<numberOfParticles/5; i++)
        {
            particleDistribution.add(new Particle(RandUtils.gaussian(maxSearchSpace/4, maxSearchSpace / 20),
                    RandUtils.gaussian(maxSearchSpace*3/4, maxSearchSpace/20)));
        }

        for (int i=0; i<numberOfParticles/5; i++)
        {
            particleDistribution.add(new Particle(RandUtils.gaussian(maxSearchSpace*3/4, maxSearchSpace / 20),
                    RandUtils.gaussian(maxSearchSpace/4, maxSearchSpace/20)));
        }

//        double gaussXMean = RandUtils.uniform(0,maxSearchSpace);
//        double gaussYMean = RandUtils.uniform(0,maxSearchSpace);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, maxSearchSpace/16),
//                    RandUtils.gaussian(gaussYMean, maxSearchSpace/16)));
//
//        gaussXMean = RandUtils.uniform(0,maxSearchSpace);
//        gaussYMean = RandUtils.uniform(0,maxSearchSpace);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, maxSearchSpace/16),
//                    RandUtils.gaussian(gaussYMean, maxSearchSpace/16)));
//
//        gaussXMean = RandUtils.uniform(0,maxSearchSpace);
//        gaussYMean = RandUtils.uniform(0,maxSearchSpace);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, maxSearchSpace/16),
//                    RandUtils.gaussian(gaussYMean, maxSearchSpace/16)));
//
//        gaussXMean = RandUtils.uniform(0,maxSearchSpace);
//        gaussYMean = RandUtils.uniform(0,maxSearchSpace);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, maxSearchSpace/16),
//                    RandUtils.gaussian(gaussYMean, maxSearchSpace/16)));

        //TODO: think about start and end points!?
    }

//    public SimpleAgentSearchProblem(int numberOfWaypoints, double[][] bounds, int numberOfParticles) {
//        super(numberOfWaypoints*2, bounds);
//        this.numberOfWaypoints = numberOfWaypoints;
//
//        particleDistribution = new ArrayList<Particle>();
//
//        double gaussXMean = RandUtils.uniform(0,bounds[0][1]);
//        double gaussYMean = RandUtils.uniform(0,bounds[1][1]);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, bounds[0][1]/16),
//                    RandUtils.gaussian(gaussYMean, bounds[1][1]/16)));
//
//        gaussXMean = RandUtils.uniform(0,bounds[0][1]);
//        gaussYMean = RandUtils.uniform(0,bounds[1][1]);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, bounds[0][1]/16),
//                    RandUtils.gaussian(gaussYMean, bounds[1][1]/16)));
//
//        gaussXMean = RandUtils.uniform(0,bounds[0][1]);
//        gaussYMean = RandUtils.uniform(0,bounds[1][1]);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, bounds[0][1]/16),
//                    RandUtils.gaussian(gaussYMean, bounds[1][1]/16)));
//
//        gaussXMean = RandUtils.uniform(0,bounds[0][1]);
//        gaussYMean = RandUtils.uniform(0,bounds[1][1]);
//
//        for (int i=0; i< numberOfParticles/4; i++)
//            particleDistribution.add(new Particle(RandUtils.gaussian(gaussXMean, bounds[0][1]/16),
//                    RandUtils.gaussian(gaussYMean, bounds[1][1]/16)));
//
//        //TODO: think about start and end points!?
//    }

    @Override
    public double f(double[] angles) throws Exception {

        double[] waypoints = anglesToWaypoints(angles);

        double cumulativeProbability = 0.0;

        for (Particle p: particleDistribution)
        {
            cumulativeProbability += p.calculateDetectionScore(waypoints);
        }

        return 1 - (cumulativeProbability/(double)particleDistribution.size());
    }

//    public static SimpleAgentSearchProblem CreateSearchProblem(double xMax, double yMax, int numberOfWaypoints, int numberOfParticles)
//    {
//        double[][] bounds = new double[numberOfWaypoints * 2][2];
//        for (int i = 0; i<bounds.length; i++)
//        {
//            bounds[i][0]=0;
//
//            if (i%2==0)
//                bounds[i][1]=xMax;
//            else
//                bounds[i][1]=yMax;
//        }
//        return new SimpleAgentSearchProblem(numberOfWaypoints, bounds, numberOfParticles);
//    }

    public static SimpleAgentSearchProblem CreateSearchProblem(double maxTurnDeg, double startX, double startY, int numberOfWaypoints, int numberOfParticles)
    {
        double maxTurnRad = (maxTurnDeg / 180.0) * Math.PI;

        double[] bounds = new double[]{-maxTurnRad, maxTurnRad};

        return new SimpleAgentSearchProblem(numberOfWaypoints, bounds, numberOfParticles, startX, startY);
    }

    public List<Particle> getParticles() {
        return particleDistribution;
    }

    public double[] anglesToWaypoints(double[] angleSolution) {

        double[] pointList = new double[(angleSolution.length + 1) * 2];
        int i = 0;

        double previousX = startXPos;
        double previousY = startYPos;
        double previousHeading = 0;

        pointList[i++] = previousX;
        pointList[i++] = previousY;

        for (double angle : angleSolution) {
            previousHeading = previousHeading + angle;

            previousX = previousX + (Math.sin(previousHeading) * waypointDistance);
            previousY = previousY + (Math.cos(previousHeading) * waypointDistance);

            pointList[i++] = previousX;
            pointList[i++] = previousY;
        }

        return pointList;
    }

}
