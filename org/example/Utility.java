package org.example;

import GA.City;
import GA.Configuration;
import GA.Gene;
import GA.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utility {
    public Utility() {
    }

    public static List<List<Double>> calculateDistanceMatrix(Map<Integer, City> cities) {
        Set<Integer> tempCities = cities.keySet();
        List<List<Double>> distanceMatrix = new ArrayList<>();

        for (Integer city01 : tempCities) {
            List<Double> tempDistance = new ArrayList<>();
            for (Integer city02 : tempCities) {
                if (!city01.equals(city02)) {
                    List<Double> city01Coordinates = cities.get(city01).getCoordinates();
                    List<Double> city02Coordinates = cities.get(city02).getCoordinates();
                    tempDistance.add(calculateEuclideanDistance(city01Coordinates.get(0), city02Coordinates.get(0), city01Coordinates.get(1), city02Coordinates.get(1)));
                    //    tempDistance.add(calculateEuclideanDistance(city01Coordinates.get(0), city02Coordinates.get(0), city01Coordinates.get(1), city02Coordinates.get(1));
                } else {
                    tempDistance.add(0.0);
                }
            }
            distanceMatrix.add(tempDistance);
        }

        return distanceMatrix;
    }

    public static double calculateEuclideanDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static double calculateTimeCost(Route route, Map<Integer, City> cities) {
        double timeDelay = 0;
        double timeWeight = 0.05;
        double travelSpeed = 1;

        for (Gene gene : route.getGenes()) {
            int listSize = gene.getRoute().size() - 1;
            double arrivalTime = 0;
            double distance = 0;

            for (int i = 0; i < listSize; i++) {
                int cityReadyTime = cities.get(gene.getRoute().get(i)).getReady_time();

                if (i == gene.getRoute().size() - 1) {
                    distance = calculateEuclideanDistance(cities.get(gene.getRoute().get(i)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(0)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(i)).getCoordinates().get(1),
                            cities.get(gene.getRoute().get(0)).getCoordinates().get(1));
                } else {
                    distance = calculateEuclideanDistance(cities.get(gene.getRoute().get(i)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(i + 1)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(i)).getCoordinates().get(1),
                            cities.get(gene.getRoute().get(i + 1)).getCoordinates().get(1));
                }

                arrivalTime += distance * travelSpeed;
                timeDelay += Math.abs(arrivalTime - cityReadyTime);
            }
        }

        return timeDelay;
    }

    public static double calculateRouteDistance(Route route, Map<Integer, City> cities) {
        double totalDistance = 0;

        for (Gene gene : route.getGenes()) {
            for (int i = 0; i < gene.getRoute().size(); i++) {
                if (i == gene.getRoute().size() - 1) {
                    totalDistance += calculateEuclideanDistance(cities.get(gene.getRoute().get(i)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(0)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(i)).getCoordinates().get(1),
                            cities.get(gene.getRoute().get(0)).getCoordinates().get(1));
                } else {
                    totalDistance += calculateEuclideanDistance(cities.get(gene.getRoute().get(i)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(i + 1)).getCoordinates().get(0),
                            cities.get(gene.getRoute().get(i)).getCoordinates().get(1),
                            cities.get(gene.getRoute().get(i + 1)).getCoordinates().get(1));
                }
            }
        }

        return totalDistance;
    }

    public static double calcCapacityCost(City[][] subroute) {
        double cost = 0.0;

        for (int row = 0; row < subroute.length; row++) {
            double tempCapacity = 200.00;
            //cost = 0.0;
            for (int col = 0; col < subroute[row].length; col++) {
                if (subroute[row][col] != null) {
                    tempCapacity -= subroute[row][col].getDemand();
                    if (tempCapacity < 0) {
                        cost += tempCapacity;
                        //System.out.print(" cost :"+cost);
                    }
                }
            }
            //System.out.println("Cost :"+ cost + " tempCapacity :"+tempCapacity);
        }
        return cost;
    }

    public static double calcTimeDelay(List<List<City>> subroute) {
        double timeDelay = 0;
        double time_weight = 0.05;
        //double travel_speed = 0.01;
        double travel_speed = 1;
        for (List<City> cities : subroute) {
            int arrivalTime = 0;
            int listSize = cities.size() - 1;
            double distance = 0;
            for (int i = 0; i < listSize; i++) {
                if (cities.get(i) != null) {
                    int ready_time = cities.get(i).getReady_time();
                    if (i == listSize - 1) {
                        //double distance = Route.calcDistance(subroute[row][col],cities[0]);
                        distance = cities.get(i).distanceFrom(Configuration.INSTANCE.cities.get(0));
                    } else {
                        distance = cities.get(i).distanceFrom(Configuration.INSTANCE.cities.get(i + 1));
                    }
                    arrivalTime += (int) (distance * travel_speed);
                    timeDelay += Math.abs(arrivalTime - ready_time);
                }
            }
            //System.out.println("Cost :"+ cost + " tempCapacity :"+tempCapacity);
        }
        return timeDelay;
    }

    public static void printRoute(Route route, Map<Integer, City> cities) {
        int vehicleID = 0;

        for (Gene gene : route.getGenes()) {
            vehicleID++;
            System.out.print("vehicle #" + vehicleID + " | route = [ " + cities.get(0).getName() + " -> ");

            for (int i = 0; i < gene.getRoute().size(); i++) {
                if (i == gene.getRoute().size() - 1) {
                    System.out.print(cities.get(gene.getRoute().get(i)).getName() + " -> depot]");
                } else {
                    System.out.print(cities.get(gene.getRoute().get(i)).getName() + " -> ");
                }
            }
        }
        System.out.println(calculateRouteDistance(route, cities));
    }

    public static int[] calculateCapacityCost(Route route, Map<Integer, City> cities) {
        int vehiclesOverCapacity = 0;
        int totalCapacityExceeded = 0;

        for (Gene gene : route.getGenes()) {
            int tempCapacity = Configuration.INSTANCE.vehicleCapacity;
            int listSize = gene.getRoute().size() - 1;

            for (int i = 0; i < listSize; i++) {
                tempCapacity -= cities.get(gene.getRoute().get(i)).getDemand();
            }

            if (tempCapacity < 0) {
                vehiclesOverCapacity++;
                totalCapacityExceeded += Math.abs(tempCapacity);
            }
        }

        return new int[]{vehiclesOverCapacity, totalCapacityExceeded};
    }

    public static double calcCapacityCost(List<List<City>> subroute) {
        double cost = 0.0;

        for (List<City> cities : subroute) {
            int tempCapacity = Configuration.INSTANCE.vehicleCapacity;
            int listSize = cities.size() - 1;
            for (int i = 0; i < listSize; i++) {
                if (cities.get(i) != null) {
                    tempCapacity -= cities.get(i).getDemand();
                    if (tempCapacity < 0) {
                        cost += Math.abs(tempCapacity);
                    }
                }
            }
            //System.out.println("Cost :"+ cost + " tempCapacity :"+tempCapacity);
        }
        return cost;
    }
}
