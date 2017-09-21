package schmitt.joao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ProblemData {

    //Number of points
    public static int n;

    //Number of neighbors
    public static int nn;

    //Number of ants
    public static int m;

    //Distance matrix
    public static int dist[][];

    //Matrix with nearest neighbor list of depth nn
    public static int nn_list[][];

    //Pheromone matrix
    public static double pheromone[][];

    //Combined pheromone and heuristic information
    public static double choice_info[][];

    //Structure of ants
    public static Ant[] ants;

    //Pheromone evaporation
    public static double rho;

    //Pheromone influence
    public static double alpha;

    //Heuristic influence
    public static double beta;

    //Generate nearest neighbor list
    public static void generate_nn_list() {
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
    }

    //Generate initial pheromone
    public static void init_pheromone(double initialTrial) {
        for(int i = 0; i < ProblemData.pheromone.length; i++) {
            for(int j = i; j < ProblemData.pheromone[i].length; j++) {
                ProblemData.pheromone[i][j] = initialTrial;
                ProblemData.pheromone[j][i] = initialTrial;
                ProblemData.choice_info[i][j] = initialTrial;
                ProblemData.choice_info[j][i] = initialTrial;
            }
        }
    }
}
