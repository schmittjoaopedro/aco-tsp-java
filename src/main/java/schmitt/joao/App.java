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

    public static String tspFile = "/home/joao/projects/master-degree/aco-tsp-algorithm/tsp/att532.tsp";

    public static void main(String[] args) {
        initializeData();
        for(int t = 0; t < 300; t++) {
            constructSolution();
            //localSearch();
            updateStatistics(t);
            updatePheromoneTrails();
        }
    }

    public static void initializeData() {
        double[][] distances = Reader.getDistances(tspFile);
        ProblemData.rho = 0.5;
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

        ProblemData.generate_nn_list();
        ProblemData.ants = new Ant[ProblemData.m];
        for(int m = 0; m < ProblemData.m; m++) {
            ProblemData.ants[m] = new Ant();
            ProblemData.ants[m].tour = new int[ProblemData.n + 1];
            ProblemData.ants[m].visited = new boolean[ProblemData.n];
        }
        ProblemData.pheromone = new double[ProblemData.n][ProblemData.n];
        ProblemData.choice_info = new double[ProblemData.n][ProblemData.n];
        Ant.init_trial(ProblemData.ants[0]);
        ProblemData.init_pheromone(Ant.trail0);
        computeChoiceInformation();

        System.out.println("Data initialized!");
    }

    public static void constructSolution() {
        int step = 0;
        for(int k = 0; k < ProblemData.m; k++) {
            ProblemData.ants[k].clear_ant_visited();
            ProblemData.ants[k].place_ant_start(step);
        }
        while(step < ProblemData.n - 1) {
            step++;
            for(int k = 0; k < ProblemData.m; k++) {
                neighborListASDecisionRule(k, step);
            }
        }
        step = ProblemData.n;
        for(int k = 0; k < ProblemData.m; k++) {
            ProblemData.ants[k].tour[ProblemData.n] = ProblemData.ants[k].tour[0];
            ProblemData.ants[k].tour_length = ProblemData.ants[k].computeTourLength();
        }
    }

    public static void ASDecisionRule(int k, int i) {
        int c = ProblemData.ants[k].tour[i - 1];
        double sum_probabilities = 0;
        double[] selection_probability = new double[ProblemData.n];
        for(int j = 0; j < ProblemData.n; j++) {
            if(ProblemData.ants[k].visited[j]) {
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
        ProblemData.ants[k].visited[j] = true;
    }

    public static void neighborListASDecisionRule(int k, int i) {
        int current_city = ProblemData.ants[k].tour[i - 1];
        double sum_probabilities = 0.0;
        double[] selection_probability = new double[ProblemData.nn];
        for(int j = 0; j < ProblemData.nn; j++) {
            if(ProblemData.ants[k].visited[ProblemData.nn_list[current_city][j]]) {
                selection_probability[j] = 0.0;
            } else {
                assert (ProblemData.nn_list[current_city][i] >= 0 && ProblemData.nn_list[current_city][i] < ProblemData.n);
                selection_probability[j] = ProblemData.choice_info[current_city][ProblemData.nn_list[current_city][j]];
                sum_probabilities += selection_probability[j];
            }
        }
        if(sum_probabilities <= 0) {
            Ant.choose_best_next(ProblemData.ants[k], i);
        } else {
            double r = Math.random() * sum_probabilities;
            int j = 0;
            double p = selection_probability[j];
            while(p <= r) {
                j++;
                p += selection_probability[j];
            }
            if(j == ProblemData.nn) {
                ProblemData.ants[k].neighbour_choose_best_next(i);
                return;
            }
            ProblemData.ants[k].tour[i] = ProblemData.nn_list[current_city][j];
            ProblemData.ants[k].visited[ProblemData.nn_list[current_city][j]] = true;
        }
    }

    public static void chooseBestNext(int k, int i) {
        double v = 0.0;
        int nc = 0;
        int c = ProblemData.ants[k].tour[i - 1];
        for(int j = 0; j < ProblemData.n; j++) {
            if(!ProblemData.ants[k].visited[j] && ProblemData.choice_info[c][j] > v) {
                nc = j;
                v = ProblemData.choice_info[c][j];
            }
        }
        ProblemData.ants[k].tour[i] = nc;
        ProblemData.ants[k].visited[nc] = true;
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
                ProblemData.pheromone[i][j] = (1 - ProblemData.rho) * ProblemData.pheromone[i][j];
                ProblemData.pheromone[j][i] = ProblemData.pheromone[i][j];
            }
        }
    }

    public static void depositPheromone(int k) {
        double delta_tau = 1.0 / (double) ProblemData.ants[k].tour_length;
        for(int i = 0; i < ProblemData.n; i++) {
            int j = ProblemData.ants[k].tour[i];
            int l = ProblemData.ants[k].tour[i + 1];
            ProblemData.pheromone[j][l] = ProblemData.pheromone[j][l] + delta_tau;
            ProblemData.pheromone[l][j] = ProblemData.pheromone[j][l];
        }
    }

    public static void computeChoiceInformation() {
        for(int i = 0; i < ProblemData.n; i++) {
            for(int j = 0; j < i; j++) {
                double heuristic = (1.0 / ((double) ProblemData.dist[i][j] + 0.1));
                ProblemData.choice_info[i][j] = Math.pow(ProblemData.pheromone[i][j], ProblemData.alpha) * Math.pow(heuristic, ProblemData.beta);
                ProblemData.choice_info[j][i] = ProblemData.choice_info[i][j];
            }
        }
    }

    public static void updateStatistics(int t) {
        int best_tour = 0;
        int worst_tour = 0;
        double mean = ProblemData.ants[best_tour].tour_length;
        for(int k = 1; k < ProblemData.m; k++) {
            mean += ProblemData.ants[k].tour_length;
            if(ProblemData.ants[k].tour_length < ProblemData.ants[best_tour].tour_length) {
                best_tour = k;
            }
            if(ProblemData.ants[k].tour_length > ProblemData.ants[best_tour].tour_length) {
                worst_tour = k;
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
            System.out.println(t +
                    " -> Best  = " + Statistics.route_length + "\t" +
                    " -> Mean  = " + (mean / ProblemData.m) + "\t" +
                    " -> Worst = " + ProblemData.ants[worst_tour].tour_length + "\t" +
                    " Tour = " + tour);
            //System.out.println(t + "," + Statistics.route_length);
        }
    }

}
