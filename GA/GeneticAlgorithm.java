package GA;

import org.example.DataUtils;
import org.example.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GeneticAlgorithm implements Cloneable {
    public List<Route> routes;
    public int countCrossover;
    public int countMutation;
    public int populationSize;
    public int generations;
    public double mutationRate;
    public double crossoverRate;
    public int elitismCount;
    public int truncationNumber;
    public GeneticAlgorithm() {
        DataUtils.readData();
    }

    public GeneticAlgorithm(int populationSize, int generations, double mutationRate, double crossoverRate, int elitismCount, int truncationNumber) {

    }

    public void execute() {
        routes = buildInitialPopulation();
        evolve();
    }
    public List<Route> getRoutes() {
        return routes;
    }

    private List<Route> buildInitialPopulation() {
        List<Route> routes = new ArrayList<>();
        List<Integer> cityIndexList = new ArrayList<>();

        for (int i = 1; i <= Configuration.INSTANCE.countCities; i++) {
            cityIndexList.add(i);
        }

        for (int i = 0; i < Configuration.INSTANCE.populationQuantity; i++) {
            Collections.shuffle(cityIndexList, Configuration.INSTANCE.randomGenerator);
            routes.add(Route.build(cityIndexList));
        }

        return routes;
    }

    private void evolve() {
        int elitismCount= 0;
        int currentGeneration = 0;
        int bestFitness = Integer.MAX_VALUE;
        int populationSize = Configuration.INSTANCE.populationQuantity; // Added to avoid repeated calculations

        while (Configuration.INSTANCE.maximumCountGeneration != currentGeneration) {
            currentGeneration++;

            for (Route route : routes) {
                Fitness.evaluate(route);
            }

            sort(routes);

            // Preserve the best routes for elitism
            List<Route> eliteRoutes = new ArrayList<>(routes.subList(0, Configuration.INSTANCE.elitismCount));

            List<Route> matingPool = selectWithTournament(routes, Configuration.INSTANCE.truncationNumber, populationSize);

            List<Route> children = crossover(matingPool);
            mutate(children);

            // Remove elitismCount number of worst individuals
            for (int i = 0; i < Configuration.INSTANCE.elitismCount; i++) {
                routes.remove(routes.size() - 1);
            }

            // Add the elite routes to the population
            routes.addAll(eliteRoutes);

            addChildrenToPopulation(routes, children);

            for (Route route : routes) {
                Fitness.evaluate(route);
            }

            if ((int) Math.round(getFittestChromosome(routes).getFitness()) < Math.round(bestFitness)) {
                bestFitness = (int) Math.round(getFittestChromosome(routes).getFitness());
                System.out.println(currentGeneration + " | bestFitness = " + (int) Math.round(getFittestChromosome(routes).getFitness())
                        + " | nanosecond = " + System.nanoTime());
            }


            sort(routes);
        }

        System.out.println();

        System.out.println("[tour management]");
        for (Gene gene : routes.get(0).getGenes()) {
            System.out.print(gene.getRoute() + " ");
        }

        System.out.println();
        System.out.println();
        System.out.println("[best route]");
        Utility.printRoute(routes.get(0), Configuration.INSTANCE.cities);
        Utility.printRoute(routes.get(0), Configuration.INSTANCE.cities);
        System.out.println(" Distance : " + Utility.calculateRouteDistance(routes.get(0), Configuration.INSTANCE.cities));
        System.out.println(" Capacity : " + Utility.calculateCapacityCost(routes.get(0), Configuration.INSTANCE.cities)[0] + " " + Utility.calculateCapacityCost(routes.get(0), Configuration.INSTANCE.cities)[1]);
        System.out.println(" Total time : " + Utility.calculateTimeCost(routes.get(0), Configuration.INSTANCE.cities));
        System.out.println();
        System.out.println("countCrossover | " + countCrossover);
        System.out.println("countMutation  | " + countMutation);
    }

    private List<Route> selectWithTournament(List<Route> routes, int limit, int populationSize) {
        List<Route> selectedRoutes = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            List<Route> tournament = new ArrayList<>();
            int tournamentSize = 3; // Adjust the tournament size as needed

            for (int j = 0; j < tournamentSize; j++) {
                int randomIndex = (int) (Configuration.INSTANCE.randomGenerator.nextDouble() * populationSize);
                tournament.add(routes.get(randomIndex));
            }

            // Find the best individual in the tournament based on fitness
            Route bestRoute = findBestRoute(tournament);

            // Add the best individual to the selected routes
            selectedRoutes.add(bestRoute);
        }

        return selectedRoutes;
    }

    private Route findBestRoute(List<Route> routes) {
        Route bestRoute = routes.get(0);

        for (Route route : routes) {
            if (route.getFitness() > bestRoute.getFitness()) {
                bestRoute = route;
            }
        }

        return bestRoute;
    }


    private List<Route> crossover(List<Route> routes) {
        Collections.shuffle(routes);
        List<Route> children = new ArrayList<>();

        for (int i = 0; i < routes.size(); i += 2) {
            if (Configuration.INSTANCE.randomGenerator.nextDouble() < Configuration.INSTANCE.crossoverRate) {
                countCrossover++;

                List<Integer> parent01 = new ArrayList<>();
                List<Integer> parent02 = new ArrayList<>();

                for (Gene gene : routes.get(i).getGenes()) {
                    parent01.addAll(gene.getRoute());
                }

                for (Gene gene : routes.get(i + 1).getGenes()) {
                    parent02.addAll(gene.getRoute());
                }

                List<Integer> tempChild01 = new ArrayList<>(Collections.nCopies(Configuration.INSTANCE.countCities, 0));
                List<Integer> tempChild02 = new ArrayList<>(Collections.nCopies(Configuration.INSTANCE.countCities, 0));

                int upperBound = Configuration.INSTANCE.randomGenerator.nextInt(parent01.size());
                int lowerBound = Configuration.INSTANCE.randomGenerator.nextInt(parent01.size() - 1);

                int start = Math.min(upperBound, lowerBound);
                int end = Math.max(upperBound, lowerBound);

                List<Integer> parent01Genes = new ArrayList<>(parent01.subList(start, end));
                List<Integer> parent02Genes = new ArrayList<>(parent02.subList(start, end));

                tempChild01.addAll(start, parent01Genes);
                tempChild02.addAll(start, parent02Genes);

                for (int j = 0; j <= parent01Genes.size() - 1; j++) {
                    parent01.remove(parent02Genes.get(j));
                    parent02.remove(parent01Genes.get(j));
                }

                for (int z = 0; z < parent01.size(); z++) {
                    tempChild01.set(tempChild01.indexOf(0), parent02.get(z));
                    tempChild02.set(tempChild02.indexOf(0), parent01.get(z));
                }

                Route child01CityRoute = Route.build(tempChild01);
                Route child02CityRoute = Route.build(tempChild02);

                children.add(child01CityRoute);
                children.add(child02CityRoute);
            }
        }

        return children;
    }

    private void mutate(List<Route> children) {
        for (Route child : children) {
            List<Integer> currentChromosome = new ArrayList<>();

            for (Gene gene : child.getGenes()) {
                currentChromosome.addAll(gene.getRoute());
            }

            for (Integer city : currentChromosome) {
                if (Configuration.INSTANCE.randomGenerator.nextDouble() < Configuration.INSTANCE.mutationRate) {
                    countMutation++;
                    int tempIndex = currentChromosome.indexOf(city);
                    int tempValue = city;
                    int indexToSwap = (int) (Configuration.INSTANCE.randomGenerator.nextDouble() * Configuration.INSTANCE.countCities);
                    int valueToSwap = currentChromosome.get(indexToSwap);
                    currentChromosome.set(tempIndex, valueToSwap);
                    currentChromosome.set(indexToSwap, tempValue);
                }
            }

            Route mutatedRoute = Route.build(currentChromosome);
            routes.add(mutatedRoute);
        }
    }

    public Route getFittestChromosome(List<Route> routes) {
        return routes.get(0);
    }

    private void addChildrenToPopulation(List<Route> population, List<Route> newChildren) {
        population.addAll(newChildren);
    }

    private void removeLastNChromosomes(List<Route> population, int n) {
        for (int i = 0; i < n; i++) {
            int indexToRemove = (int) ((population.size() - n) + Configuration.INSTANCE.randomGenerator.nextDouble() * n);
            population.remove(indexToRemove);
        }
    }

    private void sort(List<Route> routes) {
        routes.sort(Comparator.comparing(Route::getFitness));
    }

    @Override
    public GeneticAlgorithm clone() {
        try {
            GeneticAlgorithm clone = (GeneticAlgorithm) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}