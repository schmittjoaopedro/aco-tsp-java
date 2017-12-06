package schmitt.joao.aco;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * ACO - Ant Colony Optimization Meta-heuristic
 *
 * Reference book: Ant Colony Optimization.
 * Authors: Marco Dorigo and Thomas Stützle
 * Links:
 *  -> https://mitpress.mit.edu/books/ant-colony-optimization
 *  -> http://www.aco-metaheuristic.org/
 *
 * This algorithm present the implementation of ACO for TSP problems.
 */
public class Program {

    public static void main(String[] args) throws Exception {

        String tspPath = (new File(".")).getCanonicalPath();
        tspPath = Paths.get(tspPath, "tsp").toAbsolutePath().toString();
        String tspFiles[] = {"lin318.tsp", "att532.tsp", "eil51.tsp", "pcb1173.tsp", "pr2392.tsp"};

        Program app = new Program();
        // Test more simulations
        for(String tspFile : tspFiles) {
            System.out.println("\nProblem: " + tspFile);
            app.startApplication(tspPath, tspFile);
        }
    }

    // Main part of the algorithm
    public void startApplication(String path, String file) {

        // Create a TSP instance from file with .tsp extension
        Environment environment = new Environment(TspReader.getDistances(path, file));
        Statistics statistics = new Statistics(file, environment, TspReader.getCoordinates(path, file));

        // Startup part
        environment.generateNearestNeighborList();
        environment.generateAntPopulation();
        environment.generateEnvironment();

        // Repeat the ants behavior by n times
        int n = 0;
        while(n < Parameters.iterationsMax) {
            environment.constructSolutions();
            environment.updatePheromone();
            statistics.calculateStatistics(n);
            n++;
        }
        try { Thread.sleep(3000); } catch (Exception ex) {}
        statistics.close();
        System.out.println("Finished");
    }

}
