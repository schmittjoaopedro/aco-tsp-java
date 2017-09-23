package schmitt.joao.aco;

public class Ant {

    private double tourCost;

    private int[] tour;

    private boolean[] visited;

    private Environment environment;

    public Ant(int tourSize, Environment environment) {
        super();
        this.tour = new int[tourSize + 1];
        this.visited = new boolean[tourSize];
        this.environment = environment;
    }

    public double calculateNearestNeighborTour() {
        int phase = 0;
        clearVisited();
        startAtRandomPosition(phase);
        while (phase < environment.getNodesSize() - 1) {
            phase++;
            goToBestNext(phase);
        }
        tour[environment.getNodesSize()] = tour[0];
        tourCost = computeTourCost();
        clearVisited();
        return this.tourCost;
    }

    public void clearVisited() {
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }
    }

    public void startAtRandomPosition(int phase) {
        tour[phase] = (int) (Math.random() * environment.getNodesSize());
        visited[tour[phase]] = true;
    }

    public void goToBestNext(int phase) {
        int nextCity = environment.getNodesSize();
        int currentCity = tour[phase - 1];
        double minDistance = Double.MAX_VALUE;
        for (int city = 0; city < environment.getNodesSize(); city++) {
            if (!visited[city] && environment.getCost(currentCity, city) < minDistance) {
                nextCity = city;
                minDistance = environment.getCost(currentCity, city);
            }
        }
        tour[phase] = nextCity;
        visited[nextCity] = true;
    }

    public double computeTourCost() {
        double tourCost = 0.0;
        for (int i = 0; i < environment.getNodesSize(); i++) {
            tourCost += environment.getCost(tour[i], tour[i + 1]);
        }
        return tourCost;
    }

    public void finishTourCircuit() {
        tour[environment.getNodesSize()] = tour[0];
        tourCost = computeTourCost();
    }

    public void goToNNListAsDecisionRule(int phase) {
        int currentCity = this.tour[phase - 1];
        double sumProbabilities = 0.0;
        double[] selectionProbabilities = new double[environment.getNNSize() + 1];
        for(int j = 0; j < environment.getNNSize(); j++) {
            if(visited[environment.getNNNode(currentCity, j)]) {
                selectionProbabilities[j] = 0.0;
            } else {
                selectionProbabilities[j] = environment.getCostInfo(currentCity, environment.getNNNode(currentCity, j));
                sumProbabilities += selectionProbabilities[j];
            }
        }
        if(sumProbabilities <= 0) {
            goToBestNext(phase);
        } else {
            double rand = Math.random() * sumProbabilities;
            int j = 0;
            double probability = selectionProbabilities[j];
            while(probability <= rand) {
                j++;
                probability += selectionProbabilities[j];
            }
            if(j == environment.getNNSize()) {
                goToBestNeighbor(phase);
                return;
            }
            tour[phase] = environment.getNNNode(currentCity, j);
            visited[this.tour[phase]] = true;
        }
    }

    public void goToBestNeighbor(int phase) {
        int helpCity;
        int nextCity = environment.getNodesSize();
        int currentCity = this.tour[phase - 1];
        double valueBest = -1.0;
        double help;
        for(int i = 0; i < environment.getNNSize(); i++) {
            helpCity = environment.getNNNode(currentCity, i);
            if(!this.visited[helpCity]) {
                help = environment.getCostInfo(currentCity, helpCity);
                if(help > valueBest) {
                    valueBest = help;
                    nextCity = helpCity;
                }
            }
        }
        if(nextCity == environment.getNodesSize()) {
            goToBestNext(phase);
        } else {
            tour[phase] = nextCity;
            visited[this.tour[phase]] = true;
        }
    }

    public double getTourCost() {
        return tourCost;
    }

    public int getRoutePhase(int phase) {
        return tour[phase];
    }

    public int[] getTour() {
        return tour;
    }
}
