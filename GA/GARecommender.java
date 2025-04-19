package GA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GARecommender {
    public int capacity;
    public int generations;
    public float mutationRate;
    public float crossoverRate;
    public String crossover;
    public String selectWithTournament;
    public String mutate;

    public GARecommender(int capacity, int generations, float mutationRate, float crossoverRate, float elitismRatio, String crossover, String selectWithTournament,
                         String mutate) {
        this.capacity = capacity;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismRatio = elitismRatio;
        this.crossover = crossover;
    }

    public float elitismRatio;

    public GARecommender(boolean best)
    {
        if(best)
            readFile("GA_BEST.xml");
        else
            readFile("GA_DEFAULT.xml");
    }
    public void readFile(String file)
    {
        try {
            Scanner scFile = new Scanner(new File("./data/Recommenders/" + file));
            capacity = Integer.parseInt(scFile.nextLine().replaceAll("<capacity>","").replaceAll("</capacity>",""));
            generations = Integer.parseInt(scFile.nextLine().replaceAll("<generations>","").replaceAll("</generations>",""));
            mutationRate = Float.parseFloat(scFile.nextLine().replaceAll("<mutationRate>","").replaceAll("</mutationRate>",""));
            crossoverRate = Float.parseFloat(scFile.nextLine().replaceAll("<crossoverRate>","").replaceAll("</crossoverRate>",""));
            elitismRatio = Float.parseFloat(scFile.nextLine().replaceAll("<elitismRatio>","").replaceAll("</elitismRatio>",""));
            crossover = scFile.nextLine().replaceAll("<crossoverFunc>","").replaceAll("</crossoverFunc>","");
            mutate = scFile.nextLine().replaceAll("<mutateFunc>","").replaceAll("</mutateFunc>","");
            selectWithTournament = scFile.nextLine().replaceAll("<selectionFunc>","").replaceAll("</selectionFunc>","");
            scFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " +  file + " not found. Could not load recommender.");
        }
    }
    public void writeFile(boolean best)
    {
        String file = best ? "GA_BEST.xml" : "GA_DEFAULT.xml";
        try {
            FileWriter fw = new FileWriter("./data/Recommenders/" + file);
            fw.write("<capacity>" + capacity + "</capacity>\n");
            fw.write("<generations>" + generations + "</generations>\n");
            fw.write("<mutationRate>" + mutationRate + "</mutationRate>\n");
            fw.write("<crossoverRate>" + crossoverRate + "</crossoverRate>\n");
            fw.write("<elitismRatio>" + elitismRatio + "</elitismRatio>\n");
            fw.write("<crossoverFunc>" + crossover+ "</crossoverFunc>\n");
            fw.write("<mutateFunc>" + mutate + "</mutateFunc>\n");
            fw.write("<selectionFunc>" + selectWithTournament + "</selectionFunc>");
            fw.close();
        } catch (IOException e) {
            System.out.println("File " +  file + " not found. Could not load recommender.");
        }
    }
//
//    public static void main(String[] args) {
//        int populationSize = 100;
//        int generations = 100;
//        double mutationRate = 0.02;
//        double crossoverRate = 0.7;
//        int elitismCount = 2;
//        int truncationNumber = 10;
//        int numberOfRuns = 10;
//
//        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(populationSize, generations, mutationRate, crossoverRate, elitismCount, truncationNumber);
//
//        recommendBestParameters(geneticAlgorithm, numberOfRuns);
   // }
}

