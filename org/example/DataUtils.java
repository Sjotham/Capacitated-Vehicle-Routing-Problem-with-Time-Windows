package org.example;


import GA.City;
import GA.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataUtils {
    public static Map<Integer, City> readData() {
        try {
            Files.lines(Configuration.INSTANCE.path).forEach(line -> {
                List<Double> coordinatesXY = new ArrayList<>();
                String[] tempStringArray = line.split((";"));
                coordinatesXY.add(Double.parseDouble(tempStringArray[1]));
                coordinatesXY.add(Double.parseDouble(tempStringArray[2]));
                City city = new City(coordinatesXY, Integer.parseInt(tempStringArray[3]), tempStringArray[7], Integer.parseInt(tempStringArray[4]));
                Configuration.INSTANCE.cities.put(Integer.parseInt(tempStringArray[0]), city);
            });

            Configuration.INSTANCE.countCities = Configuration.INSTANCE.cities.size() - 1;
            Configuration.INSTANCE.initDistanceMatrix();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return Configuration.INSTANCE.cities;
    }

    public static int getNumberOfCities() {
        List<List<Double>> tempMatrix = Utility.calculateDistanceMatrix(readData());
        double[][] distanceMatrix = tempMatrix.stream().map(l -> l.stream().mapToDouble(Double::doubleValue).toArray()).toArray(double[][]::new);
        return distanceMatrix.length;
    }

    public static double getDistance(int from, int to) {
        List<List<Double>> tempMatrix = Utility.calculateDistanceMatrix(readData());
        double[][] distanceMatrix = tempMatrix.stream().map(l -> l.stream().mapToDouble(Double::doubleValue).toArray()).toArray(double[][]::new);
        return distanceMatrix[from - 1][to - 1];
    }
}
