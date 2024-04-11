import FPTree.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing
        String file_name = args[0];
        double threshold = Double.parseDouble(args[1]);

        // Track running time of the FP algorithm
        long startTime = System.currentTimeMillis();
        FPGrowth fpGrowth = new FPGrowth(file_name, threshold);
        fpGrowth.buildTransactions();
        fpGrowth.buildItemCounts();
        fpGrowth.buildFPTree();
        double min_sup = fpGrowth.min_sup;

        // Initialize the input of pattern growth-- initial prefix : null & initial tree - transaction Tree
        List<String > empty = new ArrayList<>();
        Pattern patternItem = new Pattern(empty,0);
        fpGrowth.performFPGrowthRecursive(fpGrowth.fp_root, patternItem, fpGrowth.headerTable, min_sup);

        //Sort the output (list of Frequent patterns) in increasing order -- optional step
        Collections.sort(fpGrowth.Final, Comparator.comparingInt(Pattern::getSupport));
        for (Pattern pattern : fpGrowth.Final) {
            System.out.println(pattern.getItems()+": "+(double)( pattern.getSupport())/fpGrowth.transactions.size());
        }


        long endTime = System.currentTimeMillis();
        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);

    }

}