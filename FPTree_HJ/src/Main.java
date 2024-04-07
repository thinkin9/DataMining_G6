import FPTree.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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

        List<String > empty = new ArrayList<>();
        Pattern patternItem3 = new Pattern(empty,0);
        fpGrowth.performFPGrowthRecursive(fpGrowth.fp_root, patternItem3, fpGrowth.headerTable);

        //conditional pattern base check
//        String item = "m";
//        List<Pattern> patternBase = fpGrowth.findConditionalPatternBase(item, fpGrowth.headerTable);
//        fpGrowth.buildFPTreeFromPatterns(patternBase);
//
//        String item2 = "b";
//        List<Pattern> patternBase2 = fpGrowth.findConditionalPatternBase(item2, fpGrowth.headerTable);
//        fpGrowth.buildFPTreeFromPatterns(patternBase2);

//        String item3 = "m";
//        //System.out.println(fpGrowth.headerTable.getRoot().getChildren());
//        List<Pattern> patternBase3 = fpGrowth.findConditionalPatternBase(item3, fpGrowth.headerTable);
//        FPTreeConstructionResult newhe = fpGrowth.buildFPTreeFromPatterns(patternBase3);
//        List<String > empty = new ArrayList<>();
//        Pattern patternItem3 = new Pattern(empty,0);
//        patternItem3.addItem(item3);
//        patternItem3.setSupport(0);
//        fpGrowth.performFPGrowthRecursive(newhe.getRoot(), patternItem3, newhe.getHeaderTable());
        System.out.println("############");
        for (Pattern pattern : fpGrowth.Final) {
            System.out.println(pattern.getItems()+": "+pattern.getSupport());
        }

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