package GA;

import org.example.Utility;

public class Fitness {
    private Fitness() {

        // Private constructor to prevent instantiation.
    }

    // Static method to evaluate the fitness of a given Route.
    public static void evaluate(Route route) {
        double currentDistance = 0; // Initialize the current distance to 0.
        int currentDemand; // Initialize the current demand.

        // Iterate through the genes in the route to calculate distance and fitness.
        for (Gene gene : route.getGenes()) {
            int tempVehicleCapacity = Configuration.INSTANCE.vehicleCapacity;
            int listSize = gene.getRoute().size() - 1;

            for (int i = 0; i < listSize; i++) {
                if (i == 0) {
                    // Calculate distance from the depot to the first city.
                    currentDistance += Configuration.INSTANCE.distanceMatrix.get(0).get(gene.getRoute().get(0));
                } else {
                    // Calculate distance between consecutive cities.
                    currentDistance += Configuration.INSTANCE.distanceMatrix.get(gene.getRoute().get(i - 1)).get(gene.getRoute().get(i));
                }

                currentDemand = Configuration.INSTANCE.cities.get(i).getDemand();

                // Handle demand and capacity constraints.
                while (currentDemand > 0) {
                    if (tempVehicleCapacity - currentDemand < 0) {
                        currentDemand -= tempVehicleCapacity;
                        currentDistance += Configuration.INSTANCE.distanceMatrix.get(gene.getRoute().get(i)).get(0);
                        currentDistance += Configuration.INSTANCE.distanceMatrix.get(0).get(gene.getRoute().get(i));
                        tempVehicleCapacity = Configuration.INSTANCE.vehicleCapacity;
                    } else {
                        tempVehicleCapacity -= currentDemand;
                        currentDemand = 0;
                    }
                }
            }

            // Calculate distance from the last city back to the depot.
            currentDistance += Configuration.INSTANCE.distanceMatrix.get(gene.getRoute().get(listSize)).get(0);
        }

        // Calculate time cost, capacity cost, and route distance using utility methods.
        double timeCost = Utility.calculateTimeCost(route, Configuration.INSTANCE.cities);
        int[] capacityCost = Utility.calculateCapacityCost(route, Configuration.INSTANCE.cities);
        double distance = Utility.calculateRouteDistance(route, Configuration.INSTANCE.cities);

        // Calculate the fitness of the route based on a combination of costs.
        route.setFitness((capacityCost[0] * 5) + distance + timeCost);
    }
}
