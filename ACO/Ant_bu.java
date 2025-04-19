package ACO;

import GA.City;
import org.example.DataUtils;
import org.example.Utility;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

public class Ant_bu extends Thread{

    private final DataUtils data;
    private final CyclicBarrier cyclicBarrier;
    private final AntColony antColony;
    private double objectiveValue = 0.0;
    private int[] tour;
    // Array or tours
    private int [][] routes;
    private Vector<Integer> notJetVisited = null;

    public Ant_bu(DataUtils data, AntColony antColony, CyclicBarrier cyclicBarrier) {
        this.data = data;
        this.antColony = antColony;
        this.cyclicBarrier = cyclicBarrier;
    }

    public void run(){
        try{
            cyclicBarrier.await();
        } catch(Exception e){
            e.printStackTrace();
        }
        try{
            cyclicBarrier.await();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public double getDistance() {
        int count = data.getNumberOfCities();
        double distance = 0;
        for (int i = 0; i < count - 1; i++) {
            distance += data.getDistance(tour[i], tour[i + 1]);
        }
        distance += data.getDistance(tour[count - 1], tour[0]);
        return distance;
    }
    public double getObjectiveValue() {
        if (objectiveValue == 0.0) {
            int count = data.getNumberOfCities();
            for (int i = 0; i < count - 1; i++) {
                objectiveValue += data.getDistance(tour[i], tour[i + 1]);
            }
            objectiveValue += data.getDistance(tour[count - 1], tour[0]);
            double capacityCost = Math.abs(Utility.calcCapacityCost(build(tour)));
            double timeCost = Utility.calcTimeDelay(build(tour));
            System.out.println(" OV "+objectiveValue +" CC "+ capacityCost + " TC "+timeCost + " D "+getDistance());

            objectiveValue += objectiveValue + capacityCost + timeCost;
        }
        return objectiveValue;
    }
    // New method
    public double getCostOfRoute(){
        double currentDistance = 0;
        int currentDemand;
        if (objectiveValue == 0.0) {
            int count = data.getNumberOfCities();
            for(int [] tour: routes){
                //int
            }
        }
        return 0.0;
    }

    public void newRound() {
        objectiveValue = 0.0;
        tour = new int[data.getNumberOfCities()];


        notJetVisited = new Vector<>();

        for (int i = 1; i <= data.getNumberOfCities(); i++) {
            notJetVisited.addElement(i);
        }
    }

    public void layPheromone() {
        double pheromone = Configuration.INSTANCE.decayFactor / objectiveValue;
        int count = data.getNumberOfCities();

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- Ant.layPheromone()");
            Configuration.INSTANCE.logEngine.write("decay factor   : " + Configuration.INSTANCE.decayFactor);
            Configuration.INSTANCE.logEngine.write("objectiveValue : " + objectiveValue);
            Configuration.INSTANCE.logEngine.write("pheromone      : " + pheromone);
        }

        for (int i = 0; i < count - 1; i++) {
            antColony.addPheromone(tour[i], tour[i + 1], pheromone);
            antColony.addPheromone(tour[i + 1], tour[i], pheromone);
        }

        antColony.addPheromone(tour[count - 1], tour[0], pheromone);
        antColony.addPheromone(tour[0], tour[count - 1], pheromone);

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    public void lookForWay() {
        DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- Ant.lookForWay");
        }

        int numberOfCities = data.getNumberOfCities();
        int randomIndexOfTownToStart = (int) (numberOfCities * Configuration.INSTANCE.randomGenerator.nextDouble() + 1);

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("numberOfCities           : " + numberOfCities);
            Configuration.INSTANCE.logEngine.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
        }

        tour[0] = randomIndexOfTownToStart;
        notJetVisited.removeElement(randomIndexOfTownToStart);

        for (int i = 1; i < numberOfCities; i++) {
            double sum = 0.0;

            if (Configuration.INSTANCE.isDebug) {
                Configuration.INSTANCE.logEngine.write("i : " + i + " - notJetVisited : " + notJetVisited);
            }
            //Sum of pheromones of not visited cities
            for (int j = 0; j < notJetVisited.size(); j++) {
                int position = notJetVisited.elementAt(j);
                sum += antColony.getPheromone(tour[i - 1], position) / data.getDistance(tour[i - 1], position);
            }

            double selectionProbability = 0.0;
            double randomNumber = Configuration.INSTANCE.randomGenerator.nextDouble();

            if (Configuration.INSTANCE.isDebug) {
                Configuration.INSTANCE.logEngine.write("i : " + i + " - sum : " + decimalFormat.format(sum) +
                        " - randomNumber : " + decimalFormat.format(randomNumber));
                Configuration.INSTANCE.logEngine.write("-");
            }

            //  Moving through the list of not visited cities
            for (int j = 0; j < notJetVisited.size(); j++) {
                int position = notJetVisited.elementAt(j);

                selectionProbability += antColony.getPheromone(tour[i - 1], position) /
                        data.getDistance(tour[i - 1], position) /
                        sum;

                if (Configuration.INSTANCE.isDebug)
                    if (position < 10) {
                        Configuration.INSTANCE.logEngine.write("position : 0" + position +
                                " - selectionProbability : " + decimalFormat.format(selectionProbability));
                    } else {
                        Configuration.INSTANCE.logEngine.write("position : " + position +
                                " - selectionProbability : " + decimalFormat.format(selectionProbability));
                    }

                if (randomNumber < selectionProbability) {
                    randomIndexOfTownToStart = position;
                    break;
                }
            }

            if (Configuration.INSTANCE.isDebug) {
                Configuration.INSTANCE.logEngine.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
            }

            tour[i] = randomIndexOfTownToStart;
            notJetVisited.removeElement(randomIndexOfTownToStart);

            if (Configuration.INSTANCE.isDebug) {
                Configuration.INSTANCE.logEngine.write("-");
            }
        }

        getObjectiveValue();

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    public List<List<City>> build(int[] tour) {
        int allocation = (int) Math.ceil((double) Configuration.INSTANCE.countCities / Configuration.INSTANCE.vehicleQuantity);
        routes = new int[Configuration.INSTANCE.vehicleQuantity][];
        Map<Integer, City> cities = Configuration.INSTANCE.cities;
        int[][] subroute = splitArray(tour, allocation);
        List<List<City>> routes = new ArrayList<>();
        for (int row = 0; row < Configuration.INSTANCE.vehicleQuantity; row++) {
            List<City> subList = new ArrayList<>();
            for (int geneIndex = 0; geneIndex < allocation; geneIndex++) {
                subList.add(cities.get(subroute[row][geneIndex]));
            }
            routes.add(subList);
        }
        return routes;
    }

    public static int[][] splitArray(int[] inputArray, int chunkSize) {
        return IntStream.iterate(0, i -> i + chunkSize)
                .limit((int) Math.ceil((double) inputArray.length / chunkSize))
                .mapToObj(j -> Arrays.copyOfRange(inputArray, j, Math.min(inputArray.length, j + chunkSize)))
                .toArray(int[][]::new);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int numberOfCities = data.getNumberOfCities();

        stringBuilder.append("tour : ");

        for (int i = 0; i < numberOfCities; i++) {
            stringBuilder.append(tour[i]).append(" ");
        }

        stringBuilder.append("\n");
        //stringBuilder.append("objectiveValue : ").append(objectiveValue);
        stringBuilder.append("Distance : ").append(getDistance());
        stringBuilder.append("Objective Value : ").append(objectiveValue);


        return stringBuilder.toString();
    }
    public String getBestRoute() {
        StringBuilder routeString = new StringBuilder();
        for (int city : tour) {
            routeString.append(city).append(" -> ");
        }
        // Add the return to start city to complete the route
        routeString.append(tour[0]);

        return routeString.toString();
    }

}
