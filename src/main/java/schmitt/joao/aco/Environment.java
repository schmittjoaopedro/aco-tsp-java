package schmitt.joao.aco;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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

    public void generateNearestNeighborList() {
        NNList = new int[getNodesSize()][getNNSize()];
        for(int i = 0; i < getNodesSize(); i++) {
            Integer[] index = new Integer[getNodesSize()];
            Double[] data = new Double[getNodesSize()];
            for(int j = 0; j < getNodesSize(); j++) {
                index[j] = j;
                data[j] = getCost(i, j);
            }
            data[i] = Collections.max(Arrays.asList(data));
            Arrays.sort(index, new Comparator<Integer>() {
                public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(data[o1], data[o2]);
                }
            });
            for(int r = 0; r < getNNSize(); r++) {
                NNList[i][r] = index[r];
            }
        }
    }

    public void generateAntPopulation() {
        ants = new Ant[getAntPopSize()];
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k] = new Ant(getNodesSize(), this);
        }
    }

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

    public void calculateChoiceInformation() {
        for(int i = 0; i < getNodesSize(); i++) {
            for(int j = 0; j < i; j++) {
                double heuristic = (1.0 / (getCost(i, j) + 0.1));
                choiceInfo[i][j] = Math.pow(pheromone[i][j], Parameters.alpha) * Math.pow(heuristic, Parameters.beta);
                choiceInfo[j][i] = choiceInfo[i][j];
            }
        }
    }

    public void constructSolutions() {
        int phase = 0;
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k].clearVisited();
            ants[k].startAtRandomPosition(phase);
        }
        while(phase < getNodesSize() - 1) {
            phase++;
            for(int k = 0; k < getAntPopSize(); k++) {
                ants[k].goToNNListAsDecisionRule(phase);
            }
        }
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
