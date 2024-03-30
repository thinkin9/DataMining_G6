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

        //conditional pattern base check
        String item = "m";
//        List<FPNode> conditionalPatternBase = fpGrowth.findConditionalPatternBase(item);
//        System.out.println("Conditional Pattern Base for item \"" + item + "\":");
//        for (FPNode patternNode : conditionalPatternBase) {
//            System.out.print("[" + patternNode.getName() + "] - Count: " + patternNode.getCount() + "\n");
//            for (FPNode child : patternNode.getChildren()) {
//                System.out.print("\t[" + child.getName() + "] - Count: " + child.getCount() + "\n");
//                // Add more levels if necessary
//            }
//        }
        List<Pattern> conditionalPatternBase = fpGrowth.findConditionalPatternBase(item);

        System.out.println("Conditional Pattern Base for item \"" + item + "\":");
        for (Pattern pattern : conditionalPatternBase) {
            System.out.println(pattern.getItems() + " - Count: " + pattern.getSupport());
        }

//        List<Map<String, Integer>> convertedConditionalPatternBase = fpGrowth.convertConditionalPatternBase(conditionalPatternBase);
//
//        System.out.println("Converted Conditional Pattern Base:");
//        for (Map<String, Integer> patternMap : convertedConditionalPatternBase) {
//            for (Map.Entry<String, Integer> entry : patternMap.entrySet()) {
//                System.out.println(entry.getKey() + "=" + entry.getValue() + " ");
//            }
//            System.out.println();
//        }

        List<Pattern> conditionalFPTree = fpGrowth.buildConditionalFPTree(conditionalPatternBase, min_sup);

        System.out.println("Conditional FP Tree:");
        for (Pattern pattern : conditionalFPTree) {
            System.out.println(pattern.getItems() + " - Count: " + pattern.getSupport());
        }

        List<Pattern> subpatterns = FPGrowth.generateSubpatterns(conditionalFPTree);

        System.out.println("Subpatterns:");
        for (Pattern subpattern : subpatterns) {
            System.out.println(subpattern.getItems() + " - Count: " + subpattern.getSupport());
        }


        long endTime = System.currentTimeMillis();
        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);

    }

}

