package schmitt.joao.aco;

public class Program {

    public static void main(String[] args) {

        String tspPath = "/home/joao/projects/master-degree/aco-tsp-algorithm/tsp/";
        String tspFile = "eil51.tsp";

        Program app = new Program();
        for(int i = 0; i < 1; i++) {
            System.out.println("\nIteration " + i);
            app.startApplication(tspPath, tspFile);
        }
    }

    public void startApplication(String path, String file) {
        Environment environment = new Environment(TspReader.getDistances(path, file));
        Statistics statistics = new Statistics(environment, TspReader.getCoordinates(path, file));
        environment.generateNearestNeighborList();
        environment.generateAntPopulation();
        environment.generateEnvironment();
        //Long startTime = System.currentTimeMillis();
        int count = 0;
        //while(System.currentTimeMillis() - startTime < Parameters.time) {
        while(count < Parameters.iterationsMax) {
            environment.constructSolutions();
            environment.updatePheromone();
            statistics.calculateStatistics(count++);
        }
        System.out.println("Finished");
    }

}
