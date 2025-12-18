import java.util.Random;

public class Parent {
   public static final int CHROM_LEN=32;
    int[] genes = new int[CHROM_LEN];
    int fitness;

    static final Random random = new Random();
    Parent() { }

    Parent copy() {
        Parent parentCopy = new Parent();
        for (int i = 0; i < CHROM_LEN; i++)
            parentCopy.genes[i] = this.genes[i];
        parentCopy.fitness = this.fitness;
        return parentCopy;
    }

    static Parent randomParent() {
        Parent parent = new Parent();
        for (int i = 0; i < CHROM_LEN; i++)
            parent.genes[i] = random.nextBoolean() ? 1 : 0;
        return parent;
    }




}
