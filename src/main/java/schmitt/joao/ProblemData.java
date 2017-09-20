package schmitt.joao;

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
    public static double p;

    //Pheromone influence
    public static double alpha;

    //Heuristic influence
    public static double beta;

}
