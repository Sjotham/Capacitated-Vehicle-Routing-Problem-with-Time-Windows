package ACO;


import java.io.IOException;
public class Colony implements Runnable {
    private final double[][] pheromoneMatrix;
    private final Ant[] antArray;
    private int bestAnt;

    public Colony() throws IOException {
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony()");
        }

        int count = Configuration.INSTANCE.data.getNumberOfCities();
        pheromoneMatrix = new double[count][count];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                pheromoneMatrix[i][j] = Configuration.INSTANCE.startPheromoneValue;
            }
        }

        antArray = new Ant[Configuration.INSTANCE.numberOfAnts];


        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    public Colony(Ant[] antArray) throws IOException {
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony()");
        }

        int count = Configuration.INSTANCE.data.getNumberOfCities();
        pheromoneMatrix = new double[count][count];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                pheromoneMatrix[i][j] = Configuration.INSTANCE.startPheromoneValue;
            }
        }

        this.antArray = antArray;

//        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
//            antArray[i] = new Ant(Configuration.INSTANCE.data, this,);
//        }

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    public void run() {
        //for(int i = 0; i<antArray.length; i++){

        //}
    }

    public void addPheromone(int from, int to, double pheromoneValue) {
        pheromoneMatrix[from - 1][to - 1] += pheromoneValue;
    }

    public double getPheromone(int from, int to) {
        return pheromoneMatrix[from - 1][to - 1];
    }

    public void doDecay() {
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony.doDecay()");
        }

        int count = Configuration.INSTANCE.data.getNumberOfCities();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                pheromoneMatrix[i][j] *= (1.0 - Configuration.INSTANCE.decayFactor);
            }
        }

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    private Ant getBestAnt() {
        int indexOfAntWithBestObjectiveValue = 0;
        double objectiveValue = Double.MAX_VALUE;
        // Best Ant route
        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
            double currentObjectiveValue = antArray[i].getObjectiveValue();
            if (currentObjectiveValue < objectiveValue) {
                objectiveValue = currentObjectiveValue;
                indexOfAntWithBestObjectiveValue = i;
            }
        }

        return antArray[indexOfAntWithBestObjectiveValue];
    }

    public void solve() {

        int iteration = 0;
        Ant bestAnt = getBestAnt();


        while (iteration < Configuration.INSTANCE.numberOfIterations) {
            Configuration.INSTANCE.logEngine.write("*** iteration - " + iteration);
            printPheromoneMatrix();
            iteration++;

            for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
                antArray[i].newRound();
                antArray[i].lookForWay();
            }
            doDecay();
            getBestAnt().layPheromone();

            printPheromoneMatrix();
            Configuration.INSTANCE.logEngine.write("***");
            //executor.shutdown();
            System.out.println("Best ant's route:");
            for (int i = 0; i < bestAnt.getDistance(); i++) {
                System.out.print(bestAnt.getRoute()[i] + " ");
            }
            System.out.println();

        }
    }

    public void printPheromoneMatrix() {
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony.printPheromoneMatrix()");
        }

        int n = pheromoneMatrix.length;
        for (double[] matrix : pheromoneMatrix) {
            for (int j = 0; j < n; j++) {
                System.out.print(Configuration.INSTANCE.decimalFormat.format(matrix[j]) + " ");
            }
            System.out.println();
        }

        System.out.println("---");
    }

    public String toString() {
        return getBestAnt().toString();
    }

}
