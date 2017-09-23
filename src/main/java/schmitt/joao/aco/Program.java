package schmitt.joao.aco;

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

    public static void main(String[] args) {

        String tspPath = "/home/joao/projects/master-degree/aco-tsp-algorithm/tsp/";
        String tspFile = "lin318.tsp";

        Program app = new Program();
        // Test more simulations
        for(int i = 0; i < 1; i++) {
            System.out.println("\nIteration " + i);
            app.startApplication(tspPath, tspFile);
        }
    }

    // Main part of the algorithm
    public void startApplication(String path, String file) {

        // Create a TSP instance from file with .tsp extension
        Environment environment = new Environment(TspReader.getDistances(path, file));
        Statistics statistics = new Statistics(environment, TspReader.getCoordinates(path, file));

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
        System.out.println("Finished");
    }

}
