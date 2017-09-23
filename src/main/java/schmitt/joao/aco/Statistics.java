package schmitt.joao.aco;

/**
 * Created by root on 21/09/17.
 */
public class Statistics {

    private Environment environment;

    private double bestSoFar = Double.MAX_VALUE;

    private int[] bestTourSoFar;

    private Visualizer visualizer;

    public Statistics(Environment environment, double[][] coordinates) {
        this.environment = environment;
        this.visualizer = new Visualizer(coordinates);
    }

    public void calculateStatistics(int phase) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0.0;
        Ant bestAnt = null;
        for(Ant ant : environment.getAnts()) {
            if(ant.getTourCost() < min) {
                min = ant.getTourCost();
                bestAnt = ant;
            }
            if(ant.getTourCost() > max) {
                max = ant.getTourCost();
            }
            total += ant.getTourCost();
        }
        if(min < bestSoFar) {
            bestSoFar = min;
            bestTourSoFar = bestAnt.getTour().clone();
            System.out.printf("Min(%.1f) Phase(%d) Max(%.1f) Mean(%.1f)\n", min, phase, max, (total / environment.getAntPopSize()));
            String message = "[" + bestTourSoFar[0];
            for(int i = 1; i < bestTourSoFar.length - 1; i++) {
                message += "->" + bestTourSoFar[i];
            }
            message += "]";
            System.out.println(message);
            visualizer.draw(bestTourSoFar);
            try { Thread.sleep(1000); } catch (Exception ex) {}
        }
    }

    public int[] getBestTourSoFar() {
        return bestTourSoFar;
    }

}
