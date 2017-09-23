package schmitt.joao.aco;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represent the solution space. Contains the vertex and edges
 * of the graph to be optimized and the pheromone let by the ants.
 */
public class Environment {

    private double initialTrail;

    private double[][] graph;

    private int[][] NNList;

    private double[][] pheromone;

    private double[][] choiceInfo;

    private Ant[] ants;

    public Environment(double[][] graph) {
        super();
        this.graph = graph;
    }

    /**
     * Create a list with the nn nearest neighbors of each vertex and
     * keep they in a separate structure of dimension n x nn where
     * n = population size, and nn = nearest neighbors size.
     */
    public void generateNearestNeighborList() {
        NNList = new int[getNodesSize()][getNNSize()];
        // For each node of the graph, sort the nearest neighbors by distance
        // and cut the list by the size nn.
        for(int i = 0; i < getNodesSize(); i++) {
            Integer[] nodeIndex = new Integer[getNodesSize()];
            Double[] nodeData = new Double[getNodesSize()];
            for(int j = 0; j < getNodesSize(); j++) {
                nodeIndex[j] = j;
                nodeData[j] = getCost(i, j);
            }
            // The edge of the current vertex with himself is let as last
            // option to be selected to nearest neighbors list
            nodeData[i] = Collections.max(Arrays.asList(nodeData));
            Arrays.sort(nodeIndex, new Comparator<Integer>() {
                public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(nodeData[o1], nodeData[o2]);
                }
            });
            for(int r = 0; r < getNNSize(); r++) {
                NNList[i][r] = nodeIndex[r];
            }
        }
    }

    /**
     * Create a population of k ants to search solutions in the environment,
     * where k is the number of ants.
     */
    public void generateAntPopulation() {
        ants = new Ant[getAntPopSize()];
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k] = new Ant(getNodesSize(), this);
        }
    }

    /**
     * Create pheromone and choice info structure:
     * -> Pheromone is used to represent the quality of the edges used to build solutions.
     * -> ChoiceInfo is calculated with the pheromone and the quality of routes, to be
     *    used by the ants as decision rule and index to speed up the algorithm.
     *
     * To generate the environment the pheromone is initialized taken in account the cost
     * of the nearest neighbor tour.
     */
    public void generateEnvironment() {
        pheromone = new double[getNodesSize()][getNodesSize()];
        choiceInfo = new double[getNodesSize()][getNodesSize()];
        initialTrail = 1.0 / (Parameters.rho * ants[0].calculateNearestNeighborTour());
        for(int i = 0; i < getNodesSize(); i++) {
            for(int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = initialTrail;
                pheromone[j][i] = initialTrail;
                choiceInfo[i][j] = initialTrail;
                choiceInfo[j][i] = initialTrail;
            }
        }
        calculateChoiceInformation();
    }

    /**
     * Calculate the proportional probability of an ant at vertex i select a neighbor
     * j based on the (i->j) edge cost (taken the inverse cost of the edge) and
     * (i->j) edge pheromone amount. The parameters alpha and beta control the
     * balance between heuristic and pheromone.
     */
    public void calculateChoiceInformation() {
        for(int i = 0; i < getNodesSize(); i++) {
            for(int j = 0; j < i; j++) {
                double heuristic = (1.0 / (getCost(i, j) + 0.1));
                choiceInfo[i][j] = Math.pow(pheromone[i][j], Parameters.alpha) * Math.pow(heuristic, Parameters.beta);
                choiceInfo[j][i] = choiceInfo[i][j];
            }
        }
    }

    /**
     * Put each ant to construct a solution in the environment.
     */
    public void constructSolutions() {
        // At the first step reset all ants (clearVisited) and put each one
        // in a random vertex of the graph.
        int phase = 0;
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k].clearVisited();
            ants[k].startAtRandomPosition(phase);
        }
        // Make all ants choose the next non visited vertex based in the
        // pheromone trails and heuristic of the edge cost.
        while(phase < getNodesSize() - 1) {
            phase++;
            for(int k = 0; k < getAntPopSize(); k++) {
                ants[k].goToNNListAsDecisionRule(phase);
            }
        }
        // Close the circuit and calculate the total cost
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k].finishTourCircuit();
        }
    }

    public void updatePheromone() {
        evaporatePheromone();
        for(int k = 0; k < getAntPopSize(); k++) {
            depositPheromone(ants[k]);
        }
        calculateChoiceInformation();
    }

    public void evaporatePheromone() {
        for(int i = 0; i < getNodesSize(); i++) {
            for(int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = (1 - Parameters.rho) * pheromone[i][j];
                pheromone[j][i] = pheromone[i][j];
            }
        }
    }

    public void depositPheromone(Ant ant) {
        double dTau = 1.0 / ant.getTourCost();
        for(int i = 0; i < getNodesSize(); i++) {
            int j = ant.getRoutePhase(i);
            int l = ant.getRoutePhase(i + 1);
            pheromone[j][l] = pheromone[j][l] + dTau;
            pheromone[l][j] = pheromone[j][l];
        }
    }

    /**
     * Return the number of nodes
     *
     * @return graphLength
     */
    public int getNodesSize() {
        return graph.length;
    }

    public int getNNSize() { return Parameters.NNSize; }

    public double getCost(int from, int to) {
        return graph[from][to];
    }

    public int getAntPopSize() {
        return Parameters.antPopSize;
    }

    public int getNNNode(int from, int to) {
        return this.NNList[from][to];
    }

    public double getCostInfo(int from, int to) {
        return choiceInfo[from][to];
    }

    public Ant[] getAnts() {
        return ants;
    }
}
