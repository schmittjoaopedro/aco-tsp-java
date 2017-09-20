package schmitt.joao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Canonical ACO for TSP problem
 *
 *
 *  procedure ACOforTSP
 *      InitializeData
 *      while (not terminate) do
 *          ConstructSolutions
 *          LocalSearch
 *          UpdateStatistics
 *          UpdatePheromoneTrails
*       end-while
 *  end-procedure
 */
public class App {

    public static String tspFile = "/home/joao/projects/master-degree/aco-tsp-algorithm/tsp/eil51.tsp";

    public static void main(String[] args) {
        initializeData();
        for(int t = 0; t < 1000; t++) {
            constructSolution();
            //localSearch();
            updateStatistics();
            updatePheromoneTrails();
        }
    }

    public static void initializeData() {
        double[][] distances = Reader.getDistances(tspFile);
        ProblemData.p = 0.5;
        ProblemData.alpha = 1.0;
        ProblemData.beta = 2.0;
        ProblemData.n = distances.length;
        ProblemData.m = 20;
        ProblemData.nn = 20;
        ProblemData.dist = new int[ProblemData.n][ProblemData.n];
        for(int i = 0; i < distances.length; i++) {
            for(int j = 0; j < distances[i].length; j++) {
                ProblemData.dist[i][j] = (int) distances[i][j];
            }
        }
        ProblemData.nn_list = new int[ProblemData.n][ProblemData.nn];
        for(int i = 0; i < ProblemData.dist.length; i++) {
            Integer[] index = new Integer[ProblemData.dist[i].length];
            Integer[] data = new Integer[ProblemData.n];
            for(int j = 0; j < ProblemData.dist[i].length; j++) {
                index[j] = j;
                data[j] = ProblemData.dist[i][j];
            }
            data[i] = Collections.max(Arrays.asList(data));
            Arrays.sort(index, new Comparator<Integer>() {
                public int compare(final Integer o1, final Integer o2) {
                    return Integer.compare(data[o1], data[o2]);
                }
            });
            for(int r = 0; r < ProblemData.nn; r++) {
                ProblemData.nn_list[i][r] = index[r];
            }
        }
        ProblemData.pheromone = new double[ProblemData.n][ProblemData.n];
        int count = 0;
        int current = 0;
        double total = 0;
        int[] visited = new int[ProblemData.n];
        while(count < ProblemData.n) {
            for(int j = 0; j < ProblemData.nn_list[current].length; j++) {
                if(visited[ProblemData.nn_list[current][j]] != 1) {
                    current = ProblemData.nn_list[count][j];
                    visited[current] = 1;
                    total += ProblemData.dist[count][current];
                    break;
                }
            }
            count++;
        }
        for(int i = 0; i < ProblemData.pheromone.length; i++) {
            for(int j = i; j < ProblemData.pheromone[i].length; j++) {
                ProblemData.pheromone[i][j] = ProblemData.m / total;
                ProblemData.pheromone[j][i] = ProblemData.m / total;
            }
        }
        ProblemData.choice_info = new double[ProblemData.n][ProblemData.n];
        computeChoiceInformation();
        ProblemData.ants = new Ant[ProblemData.m];
        for(int m = 0; m < ProblemData.m; m++) {
            ProblemData.ants[m] = new Ant();
            ProblemData.ants[m].tour = new int[ProblemData.n + 1];
            ProblemData.ants[m].visited = new int[ProblemData.n];
        }
        System.out.println("Data initialized!");
    }

    public static void constructSolution() {
        for(int k = 0; k < ProblemData.m; k++) {
            for(int i = 0; i < ProblemData.n; i++) {
                ProblemData.ants[k].visited[i] = 0;
            }
        }
        int step = 0;
        for(int k = 0; k < ProblemData.m; k++) {
            int r = (int) (Math.random() * ProblemData.n);
            ProblemData.ants[k].tour[step] = r;
            ProblemData.ants[k].visited[r] = 1;
        }
        while(step < ProblemData.n) {
            step++;
            for(int k = 0; k < ProblemData.m; k++) {
                neighborListASDecisionRule(k, step);
            }
        }
        for(int k = 0; k < ProblemData.m; k++) {
            ProblemData.ants[k].tour[ProblemData.n] = ProblemData.ants[k].tour[0];
            ProblemData.ants[k].tour_length = computeTourLength(k);
        }
    }

    public static int computeTourLength(int k) {
        int tour_length = 0;
        for(int i = 0; i < ProblemData.n; i++) {
            tour_length += ProblemData.dist[ProblemData.ants[k].tour[i]][ProblemData.ants[k].tour[i + 1]];
        }
        return tour_length;
    }

    public static void ASDecisionRule(int k, int i) {
        int c = ProblemData.ants[k].tour[i - 1];
        double sum_probabilities = 0;
        double[] selection_probability = new double[ProblemData.n];
        for(int j = 0; j < ProblemData.n; j++) {
            if(ProblemData.ants[k].visited[j] == 1) {
                selection_probability[j] = 0.0;
            } else {
                selection_probability[j] = ProblemData.choice_info[c][j];
                sum_probabilities += sum_probabilities + selection_probability[j];
            }
        }
        double r = Math.random() * sum_probabilities;
        int j = 0;
        double p = selection_probability[j];
        while(p < r) {
            j++;
            p += selection_probability[j];
        }
        ProblemData.ants[k].tour[i] = j;
        ProblemData.ants[k].visited[j] = 1;
    }

    public static void neighborListASDecisionRule(int k, int i) {
        int c = ProblemData.ants[k].tour[i - 1];
        double sum_probabilities = 0.0;
        double[] selection_probability = new double[ProblemData.nn];
        for(int j = 0; j < ProblemData.nn; j++) {
            if(ProblemData.ants[k].visited[ProblemData.nn_list[c][j]] == 1) {
                selection_probability[j] = 0.0;
            } else {
                selection_probability[j] = ProblemData.choice_info[c][ProblemData.nn_list[c][j]];
                sum_probabilities += selection_probability[j];
            }
        }
        if(sum_probabilities == 0) {
            chooseBestNext(k, i);
        } else {
            double r = Math.random() * sum_probabilities;
            int j = 0;
            double p = selection_probability[j];
            while(p < r) {
                j++;
                p += selection_probability[j];
            }
            ProblemData.ants[k].tour[i] = ProblemData.nn_list[c][j];
            ProblemData.ants[k].visited[ProblemData.nn_list[c][j]] = 1;
        }
    }

    public static void chooseBestNext(int k, int i) {
        double v = 0.0;
        int nc = 0;
        int c = ProblemData.ants[k].tour[i - 1];
        for(int j = 0; j < ProblemData.n; j++) {
            if(ProblemData.ants[k].visited[j] == 0 && ProblemData.choice_info[c][j] > v) {
                nc = j;
                v = ProblemData.choice_info[c][j];
            }
        }
        ProblemData.ants[k].tour[i] = nc;
        ProblemData.ants[k].visited[nc] = 1;
    }

    public static void updatePheromoneTrails() {
        evaporate();
        for(int k = 0; k < ProblemData.m; k++) {
            depositPheromone(k);
        }
        computeChoiceInformation();
    }

    public static void evaporate() {
        for(int i = 0; i < ProblemData.n; i++) {
            for(int j = i; j < ProblemData.n; j++) {
                ProblemData.pheromone[i][j] = (1 - ProblemData.p) * ProblemData.pheromone[i][j];
                ProblemData.pheromone[j][i] = ProblemData.pheromone[i][j];
            }
        }
    }

    public static void depositPheromone(int k) {
        double delta_tau = 1.0 / ProblemData.ants[k].tour_length;
        for(int i = 0; i < ProblemData.n; i++) {
            int j = ProblemData.ants[k].tour[i];
            int l = ProblemData.ants[k].tour[i + 1];
            ProblemData.pheromone[j][l] = ProblemData.pheromone[j][l] + delta_tau;
            ProblemData.pheromone[l][j] = ProblemData.pheromone[j][l];
        }
    }

    public static void computeChoiceInformation() {
        for(int i = 0; i < ProblemData.choice_info.length; i++) {
            double denominator = 0.0;
            for(int j = 0; j < ProblemData.choice_info[i].length; j++) {
                if(i != j)
                    denominator += Math.pow(ProblemData.pheromone[i][j], ProblemData.alpha) + Math.pow(1.0 / ProblemData.dist[i][j], ProblemData.beta);
            }
            for(int j = 0; j < ProblemData.choice_info[i].length; j++) {
                if(i != j) {
                    double numerator = Math.pow(ProblemData.pheromone[i][j], ProblemData.alpha) + Math.pow(1.0 / ProblemData.dist[i][j], ProblemData.beta);
                    ProblemData.choice_info[i][j] = numerator / denominator;
                }
            }
        }
    }

    public static void updateStatistics() {
        int best_tour = 0;
        for(int k = 1; k < ProblemData.m; k++) {
            if(ProblemData.ants[k].tour_length < ProblemData.ants[best_tour].tour_length) {
                best_tour = k;
            }
        }
        if(ProblemData.ants[best_tour].tour_length < Statistics.route_length) {
            Statistics.best_route = ProblemData.ants[best_tour].tour.clone();
            Statistics.route_length = ProblemData.ants[best_tour].tour_length;
            String tour = "[" + ProblemData.ants[best_tour].tour[0];
            for (int i = 1; i < ProblemData.ants[best_tour].tour.length; i++) {
                tour += "->" + ProblemData.ants[best_tour].tour[i];
            }
            tour += "]";
            System.out.println("Length = " + Statistics.route_length + "\t" + " Tour = " + tour);
        }
    }

}
