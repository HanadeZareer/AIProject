import java.util.Random;

public class Chromosome {
   public static final int CHROM_LEN=32;
    int[] genes = new int[CHROM_LEN];
    int fitness;

    static final Random random = new Random();
    Chromosome() { }

    Chromosome copy() {
        Chromosome chromosomeCopy = new Chromosome();
        for (int i = 0; i < CHROM_LEN; i++)
            chromosomeCopy.genes[i] = this.genes[i];
        chromosomeCopy.fitness = this.fitness;
        return chromosomeCopy;
    }

    static Chromosome randomChromosome() {
        Chromosome chromosome = new Chromosome();
        for (int i = 0; i < CHROM_LEN; i++)
            chromosome.genes[i] = random.nextBoolean() ? 1 : 0;
        return chromosome;
    }




}
