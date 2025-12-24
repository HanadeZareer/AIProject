import java.util.Random;

public class Passcode {

    static Random random = new Random();

    static int[] randomPasscode() {
        int[] p = new int[Chromosome.CHROM_LEN];
        for (int i = 0; i < Chromosome.CHROM_LEN; i++) p[i] = random.nextBoolean() ? 1 : 0;
        return p;
    }

    static String bitsToString(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int b : bits) sb.append(b);
        return sb.toString();
    }
}
