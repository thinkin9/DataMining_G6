import FPTree.FPNode;
import FPTree.FPGrowth;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing
        String file_name = args[0];
        double threshold = Double.parseDouble(args[1]);

        // System.out.println(file_name);
        // System.out.println(threshold);

        long startTime = System.currentTimeMillis();
        FPGrowth fpGrowth = new FPGrowth(file_name, threshold);
        fpGrowth.parseTransactions();
        fpGrowth.findFrequentItems();

        long endTime = System.currentTimeMillis();
        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);


    }
}

