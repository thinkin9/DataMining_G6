import FPTree.FPNode;
import FPTree.FPGrowth;
import FPTree.Pattern;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing
        String file_name = args[0];
        double threshold = Double.parseDouble(args[1]);


        long startTime = System.currentTimeMillis();

        FPGrowth fpGrowth = new FPGrowth(file_name, threshold);
        fpGrowth.buildTransactions();
        fpGrowth.buildItemCounts();
        fpGrowth.buildFPTree();

        //conditional pattern base check
        String item = "a";

        FPNode node = fpGrowth.headerTable.get(item);
        while(node != null){
            fpGrowth.patternGrowth(node);
        }


        long endTime = System.currentTimeMillis();
        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);

    }

}

