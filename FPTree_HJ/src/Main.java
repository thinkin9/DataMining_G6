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

        // System.out.println(file_name);
        // System.out.println(threshold);

        long startTime = System.currentTimeMillis();
        FPGrowth fpGrowth = new FPGrowth(file_name, threshold);
        fpGrowth.buildTransactions();
        fpGrowth.buildItemCounts();
        fpGrowth.buildFPTree();
        double min_sup = fpGrowth.min_sup;

        System.out.println(fpGrowth.freqItems);

        //conditional pattern base check
        String item = "m";
        //List<Map<String,Integer>> conditionalPatternBase = fpGrowth.findConditionalPatternBase(item);
        List<Pattern> patternBase = fpGrowth.findConditionalPatternBase(item, fpGrowth.headerTable);
        fpGrowth.buildFPTreeFromPatterns(patternBase);

//
//        List<Map<String,Integer>> conditionalFPTree = fpGrowth.buildConditionalFPTree(conditionalPatternBase, min_sup);


//        List<Pattern> subpatterns = FPGrowth.generatePatterns(conditionalFPTree, item);
//
//        System.out.println("Subpatterns:");
//        for (Pattern subpattern : subpatterns) {
//            System.out.println(subpattern.getItems() + " - Count: " + subpattern.getSupport());
//        }


        long endTime = System.currentTimeMillis();
        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);

    }

}