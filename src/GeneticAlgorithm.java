import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GeneticAlgorithm {

    static int POP_SIZE = 150;
    static int ELITE = 2;                 // Best ELITE unchanged
    static double CROSS_RATE = 0.85;
    static double MUT_RATE = 0.01;        // per-gene probability
    static int TOURNAMENT_K = 3;
    static int MAX_GENERATIONS = 20000;
    static final int CHROM_LEN = Parent.CHROM_LEN;

    static final Random random = new Random();


    private void evaluate(Parent parent, int[] passcode) {
        int score = 0;
        for (int i = 0; i < CHROM_LEN; i++) {
            if (parent.genes[i] == passcode[i]) score++;
        }
        parent.fitness = score;
    }

    private void evaluatePop(Parent[] pop, int[] passcode) {
        for (int i = 0; i < pop.length; i++)
            evaluate(pop[i], passcode);
    }

    private Parent bestOfPop(Parent[] pop) {
        Parent best = pop[0];
        for (int i = 1; i < pop.length; i++) {
            if (pop[i].fitness > best.fitness) best = pop[i];
        }
        return best;
    }

    private void sortPopDescByFitness(Parent[] pop) {

        for (int i = 0; i < pop.length - 1; i++) {
            int bestIdx = i;
            for (int j = i + 1; j < pop.length; j++) {
                if (pop[j].fitness > pop[bestIdx].fitness) bestIdx = j;
            }
            if (bestIdx != i) {
                Parent tmp = pop[i];
                pop[i] = pop[bestIdx];
                pop[bestIdx] = tmp;
            }
        }
    }

    private Parent tournamentSelect(Parent[] pop) {
        int bestIdx = random.nextInt(pop.length);
        for (int i = 1; i < TOURNAMENT_K; i++) {
            int idx = random.nextInt(pop.length);
            if (pop[idx].fitness > pop[bestIdx].fitness) bestIdx = idx;
        }
        return pop[bestIdx];
    }

    static Parent[] crossover(Parent p1, Parent p2) {
        Parent c1 = new Parent();
        Parent c2 = new Parent();

        // Single-point crossover
        int cut = 1 + random.nextInt(CHROM_LEN - 1);
        for (int i = 0; i < CHROM_LEN; i++) {
            if (i < cut) {
                c1.genes[i] = p1.genes[i];
                c2.genes[i] = p2.genes[i];
            } else {
                c1.genes[i] = p2.genes[i];
                c2.genes[i] = p1.genes[i];
            }
        }
        return new Parent[]{c1, c2};
    }

    private void mutate(Parent parent) {
        for (int i = 0; i < CHROM_LEN; i++) {
            if (random.nextDouble() < MUT_RATE) {
                parent.genes[i] = 1 - parent.genes[i]; // flip bit
            }
        }
    }

    public  Result runOnce(int[] passcode, String curveCsvPath) throws IOException {
        Parent[] pop = new Parent[POP_SIZE];
        for (int i = 0; i < POP_SIZE; i++)
            pop[i] = Parent.randomParent();
        evaluatePop(pop, passcode);

        long start = System.currentTimeMillis();

        FileWriter curve = null;
        if (curveCsvPath != null) {
            curve = new FileWriter(curveCsvPath);
            curve.write("generation,bestFitness\n");
        }

        for (int gen = 0; gen <= MAX_GENERATIONS; gen++) {
            Parent best = bestOfPop(pop);

            if (curve != null)
                curve.write(gen + "," + best.fitness + "\n");

            if (best.fitness == CHROM_LEN) {
                long end = System.currentTimeMillis();
                if (curve != null) curve.close();

                Result result = new Result(true,gen,end - start);
                return result;
            }


            sortPopDescByFitness(pop);
            Parent[] next = new Parent[POP_SIZE];
            for (int i = 0; i < ELITE; i++) next[i] = pop[i].copy();

            int idx = ELITE;
            while (idx < POP_SIZE) {
                Parent p1 = tournamentSelect(pop);
                Parent p2 = tournamentSelect(pop);

                Parent child1, child2;

                if (random.nextDouble() < CROSS_RATE) {
                    Parent[] kids = crossover(p1, p2);
                    child1 = kids[0];
                    child2 = kids[1];
                } else {
                    child1 = p1.copy();
                    child2 = p2.copy();
                }

                mutate(child1);
                evaluate(child1, passcode);
                next[idx++] = child1;

                if (idx < POP_SIZE) {
                    mutate(child2);
                    evaluate(child2, passcode);
                    next[idx++] = child2;
                }
            }

            pop = next;
        }

        long end = System.currentTimeMillis();
        if (curve != null) curve.close();

        Result result = new Result(false,MAX_GENERATIONS,end - start);
        return result;
    }

    public void runWithDifferentMutationRates(int[] passcode) throws IOException {


        double[] mutRates = {0.001, 0.005, 0.01, 0.02, 0.05};
        int trials = 20;

        FileWriter summary = new FileWriter("mutation_tuning.csv");
        summary.write("mutRate,trials,successes,avgGenerations,avgTimeMs\n");

        for (int m = 0; m < mutRates.length; m++) {
            MUT_RATE = mutRates[m];

            int successes = 0;
            long genSum = 0;
            long timeSum = 0;

            for (int t = 0; t < trials; t++) {
                // Use same passcode for fair comparison
                Result result = runOnce(passcode, null);
                if (result.isSuccess()) {
                    successes++;
                    genSum += result.getGenerations();
                    timeSum += result.getTimeMs();
                }
            }

            double avgGen = (successes > 0) ? ((double) genSum / successes) : -1.0;
            double avgTime = (successes > 0) ? ((double) timeSum / successes) : -1.0;

            summary.write(MUT_RATE + "," + trials + "," + successes + "," + avgGen + "," + avgTime + "\n");
            summary.flush();

            System.out.println("MUT_RATE=" + MUT_RATE + " successes=" + successes + "/" + trials + " avgGen=" + avgGen);
        }

        summary.close();
    }

    public void runWithDifferentCrossoverRate(int[] passcode) throws Exception {

        double[] crossRates = {0.5, 0.6, 0.7, 0.8, 0.9};
        int trials = 20;

        FileWriter fw = new FileWriter("crossover_tuning.csv");
        fw.write("crossRate,trials,successes,avgGenerations,avgTimeMs\n");

        for (int i = 0; i < crossRates.length; i++) {
            CROSS_RATE = crossRates[i];

            int successes = 0;
            long genSum = 0;
            long timeSum = 0;

            for (int t = 0; t < trials; t++) {
                Result rr = runOnce(passcode, null);

                if (rr.isSuccess()) {
                    successes++;
                    genSum += rr.getGenerations();
                    timeSum += rr.getTimeMs();
                }
            }

            double avgGen = (successes > 0) ? (double) genSum / successes : -1;
            double avgTime = (successes > 0) ? (double) timeSum / successes : -1;

            fw.write(CROSS_RATE + "," + trials + "," + successes + "," +
                    avgGen + "," + avgTime + "\n");

            System.out.println("CROSS_RATE=" + CROSS_RATE +
                    " success=" + successes + "/" + trials +
                    " avgGen=" + avgGen);
        }

        fw.close();
    }

    public void runWithDifferentPopulationSize(int[] passcode) throws Exception {

        int[] popSizes = {30, 50, 100, 150, 200};
        int trials = 20;

        FileWriter fw = new FileWriter("population_tuning.csv");
        fw.write("popSize,trials,successes,avgGenerations,avgTimeMs\n");

        for (int p = 0; p < popSizes.length; p++) {
            POP_SIZE = popSizes[p];

            int successes = 0;
            long genSum = 0;
            long timeSum = 0;

            for (int t = 0; t < trials; t++) {
               Result rr = runOnce(passcode, null);

                if (rr.isSuccess()) {
                    successes++;
                    genSum += rr.getGenerations();
                    timeSum += rr.getTimeMs();
                }
            }

            double avgGen = (successes > 0) ? (double) genSum / successes : -1;
            double avgTime = (successes > 0) ? (double) timeSum / successes : -1;

            fw.write(POP_SIZE + "," + trials + "," + successes + "," +
                    avgGen + "," + avgTime + "\n");

            System.out.println("POP_SIZE=" + POP_SIZE +
                    " success=" + successes + "/" + trials +
                    " avgGen=" + avgGen);
        }

        fw.close();
    }



}
