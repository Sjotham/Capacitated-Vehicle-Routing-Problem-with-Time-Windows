package ACO;

import GA.City;
import org.example.DataUtils;
import org.example.MersenneTwisterFast;
import org.example.Utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public enum Configuration {
    INSTANCE;

    // New lines
    public final Path path = Paths.get("src/main/data.txt");
    public final Map<Integer, City> cities = new TreeMap<>();
    public List<List<Double>> distanceMatrix;
    public int countCities = 0;
    public void initDistanceMatrix() {
        distanceMatrix = Utility.calculateDistanceMatrix(cities);
    }
    public final int vehicleQuantity =9;
    public final int vehicleCapacity = 200;
    // common
    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String dataDirectory = userDirectory + fileSeparator + "data" + fileSeparator;
    public final String logDirectory = userDirectory + fileSeparator + "log" + fileSeparator;
    public final LogEngine logEngine = new LogEngine(logDirectory + "debug.log");
    public final boolean isDebug = true;
    public final DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
    public final MersenneTwisterFast randomGenerator = new MersenneTwisterFast(System.currentTimeMillis());
    // ant colony optimization
    public final double decayFactor = 0.3;
    public final double startPheromoneValue = 0.00005;
    public final int numberOfAnts = 10;
    public final int numberOfIterations = 10000;
    public DataUtils data;
}
