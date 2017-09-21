package schmitt.joao;

public class Ant {

    //The ant's tour length
    public int tour_length;

    //Ant's memory storing (partial) tours (n+1)
    public int tour[];

    //Visited cities
    public boolean visited[];

    public static double trail0;

    //Calculate the first trial
    public static void init_trial(Ant ant) {
        trail0 = 1. / ((ProblemData.rho) * Ant.nn_tour(ant));
    }

    //Calculate nearest neighbor tour
    public static double nn_tour(Ant ant) {
        int phase, help;
        ant.clear_ant_visited();
        phase = 0;
        ant.place_ant_start(phase);
        while(phase < ProblemData.n - 1) {
            phase++;
            choose_best_next(ant, phase);
        }
        phase = ProblemData.n;
        ant.tour[ProblemData.n] = ant.tour[0];
        ant.tour_length = ant.computeTourLength();
        help = ant.tour_length;
        ant.clear_ant_visited();
        return help;
    }

    public static void choose_best_next(Ant a, int phase) {
        int next_city = ProblemData.n;
        int current_city = a.tour[phase - 1];
        int min_distance = Integer.MAX_VALUE;
        for(int city = 0; city < ProblemData.n; city++) {
            if(!a.visited[city] && ProblemData.dist[current_city][city] < min_distance) {
                next_city = city;
                min_distance = ProblemData.dist[current_city][city];
            }
        }
        a.tour[phase] = next_city;
        a.visited[next_city] = true;
    }

    public void clear_ant_visited() {
        for(int i = 0; i < this.visited.length; i++) {
            this.visited[i] = false;
        }
    }

    public void place_ant_start(int step) {
        this.tour[step] = (int) (Math.random() * ProblemData.n);
        this.visited[this.tour[step]] = true;
    }

    public int computeTourLength() {
        int tour_length = 0;
        for(int i = 0; i < ProblemData.n; i++) {
            tour_length += ProblemData.dist[this.tour[i]][this.tour[i + 1]];
        }
        return tour_length;
    }

    public void neighbour_choose_best_next(int phase) {
        int help_city;
        int next_city = ProblemData.n;
        int current_city = this.tour[phase - 1];
        double value_best = -1.0;
        double help;
        for(int i = 0; i < ProblemData.nn; i++) {
            help_city = ProblemData.nn_list[current_city][i];
            if(!this.visited[help_city]) {
                help = ProblemData.choice_info[current_city][help_city];
                if(help > value_best) {
                    value_best = help;
                    next_city = help_city;
                }
            }
        }
        if(next_city == ProblemData.n) {
            choose_best_next(this, phase);
        } else {
            this.tour[phase] = next_city;
            this.visited[next_city] = true;
        }
    }

}
