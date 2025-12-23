import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception {
        GeneticAlgorithm ga=new GeneticAlgorithm();

        //int[] passcode=Passcode.randomPasscode();
        int[] passcode={ 0,1,0,0,0,0,0,1,
                0,1,1,0,0,1,1,0,
                0,0,1,0,1,1,0,1,
                0,0,1,1,0,1,1,0};
        System.out.println("Target Passcode (32 bits): " + Passcode.bitsToString(passcode));

        // 1) One normal run + convergence curve file
        Result one = ga.runOnce(passcode, "convergence_curve.csv");
        System.out.println("Success: " + one.isSuccess());
        System.out.println("Generations: " + one.getGenerations());
        System.out.println("Time (ms): " + one.getTimeMs());

        // 2) mutation rate tuning
        ga.runWithDifferentMutationRates(passcode);
        //3) crossover rate tuning
        ga.runWithDifferentCrossoverRate(passcode);
        //4) population size tuning
        ga.runWithDifferentPopulationSize(passcode);
    }

}