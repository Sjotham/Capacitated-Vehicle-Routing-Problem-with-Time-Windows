package GA;

import org.example.MersenneTwisterFast;
import org.example.Utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public enum Configuration {
    INSTANCE;

    // random generator
    public final MersenneTwisterFast randomGenerator = new MersenneTwisterFast(System.nanoTime());

    // data management
    public final Path path = Paths.get("src/main/data.txt");
    public final Map<Integer, City> cities = new TreeMap<>();
    // depot
    public final int vehicleQuantity =9;
    public final int vehicleCapacity = 200;
    // genetic algorithm
    public final int populationQuantity = 100;
    public final int maximumCountGeneration = 10000;
    public final double crossoverRate = 0.7;
    public final double mutationRate = 0.0003;
    public final int truncationNumber = 250;
    public int countCities = 0;
    public final int elitismCount = 5;
    public List<List<Double>> distanceMatrix;
    public boolean isDebug;

    public void initDistanceMatrix() {
        distanceMatrix = Utility.calculateDistanceMatrix(cities);
    }
}