package ACO;

import GA.City;
import org.example.DataUtils;
import org.example.Utility;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;

public class Ant {
    public  double routeLength;
    private final DataUtils data;
    private final AntColony antColony;
    private double objectiveValue = 0.0;
    private int[] tour;
    // Array or tours
    private int [] route;
    private Vector<Integer> notJetVisited = null;

    public Ant(DataUtils data, AntColony antColony) {
        this.data = data;
        this.antColony = antColony;
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
            double capacityCost = Utility.calcCapacityCost(buildRoute(tour));
            double timeCost = Utility.calcTimeDelay(buildRoute(tour));
            System.out.println(" OV "+objectiveValue +" CC "+ capacityCost + " TC "+timeCost + " D "+getDistance());

            objectiveValue += objectiveValue + capacityCost*10 + timeCost*700;
        }
        return objectiveValue;
    }

    public void newRound() {
        objectiveValue = 0.0;
        tour = new int[data.getNumberOfCities()];


        notJetVisited = new Vector<>();

        for (int i = 1; i < data.getNumberOfCities(); i++) {
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

        //getObjectiveValue();

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }
    public int[] getRoute() {
        return this.route;
    }
    //Splitting route into subtours
    // Implementing Array of tours
    public City [][] build(int [] tour){
        int allocation =  (int)Math.ceil( (double) Configuration.INSTANCE.countCities /Configuration.INSTANCE.vehicleQuantity);
        System.out.println(Arrays.toString(tour));

        route = new int[Configuration.INSTANCE.vehicleQuantity];
        Map<Integer, City> cities = Configuration.INSTANCE.cities;
        int subroute[][] = splitArray(tour, allocation);
        City routes [][] = new City [Configuration.INSTANCE.vehicleQuantity][allocation];
        for (int row = 0; row < Configuration.INSTANCE.vehicleQuantity; row++){
            for (int geneIndex=0; geneIndex < allocation ;geneIndex++){
                routes[row][geneIndex] = cities.get(subroute[row][geneIndex]);
                System.out.print(" "+ cities.get(subroute[row][geneIndex]).getName());
            }
            System.out.println();
        }
        return routes;
    }

    public List<List<City>> buildRoute (int[] tour){
        List<Integer> cityIndexList = new ArrayList<>();

        int allocation =  (int)Math.ceil( (double) Configuration.INSTANCE.countCities /Configuration.INSTANCE.vehicleQuantity);
        List<List<City>> route = new ArrayList<>();
        //for (int i = 1; i <= Configuration.INSTANCE.countCities; i++) {
        for (int i :tour){
            cityIndexList.add(i);
        }
        Map<Integer, City> cities = Configuration.INSTANCE.cities;

        for (int row = 0; row < Configuration.INSTANCE.vehicleQuantity; row++){
            List<City> subroute= new ArrayList<>();
            // Null pointer problem
            for (int geneIndex=0; geneIndex < allocation+1 ;geneIndex++) {
                if (geneIndex == allocation - 1 && row == Configuration.INSTANCE.vehicleQuantity - 1) {
                    for (int l = 0; l < Configuration.INSTANCE.countCities % Configuration.INSTANCE.vehicleQuantity; l++) {
                        subroute.add(cities.get(cityIndexList.get(0)));
                        int index = cityIndexList.remove(0);
                        cityIndexList.add(index);
                    }
                }
                subroute.add(cities.get(cityIndexList.get(0)));
                int num = cityIndexList.remove(0);
                cityIndexList.add(num);
            }

            route.add(subroute);

        }
        return route;
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
        stringBuilder.append(" Capacity : ").append(Math.abs(Utility.calcCapacityCost(buildRoute(tour))));

        stringBuilder.append(" Time Cost : ").append(Math.abs(Utility.calcTimeDelay(buildRoute(tour))));

        stringBuilder.append(" Objective Value : ").append(objectiveValue);
        //printRoute();
        return stringBuilder.toString();
    }

    public void printRoute() {
        int vehicleID = 0;
        City [][] route = build(tour);
        List <List<City>> cityRoute = buildRoute(tour);
        cityRoute.forEach((list) -> {
            list.forEach((City) -> System.out.println(" "+City.getName()+" "));
        });
        for ( City [] subroute : route) {
            vehicleID++;
            System.out.print("vehicle #" + vehicleID + " | route = [ " + Configuration.INSTANCE.cities.get(0).getName() + " -> ");

            for (int i = 0; i < subroute.length; i++) {
                if (i == subroute.length - 1) {
                    //System.out.print(Configuration.INSTANCE.cities.get(subroute[i]).getName() + " -> depot]");
                    System.out.print(subroute[i].getName() + " -> depot]");
                } else {
                    //System.out.print(Configuration.INSTANCE.cities.get(subroute[i]).getName() + " -> ");
                    System.out.print((subroute[i].getName()) + " -> ");
                }
            }
            System.out.println();
        }

        //System.out.println(calculateRouteDistance(route, cities));
    }

}
