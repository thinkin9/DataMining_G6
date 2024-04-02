import FPTree.FPNode;
import FPTree.FPGrowth;
import FPTree.ItemCount;
import FPTree.Pattern;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;



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

        //System.out.println(fpGrowth.freqItems);



        Map<List<String>, Integer> subpatternMap = new HashMap<>();
        for (ItemCount item : fpGrowth.freqItems) {
            List<Pattern> conditionalPatternBase = fpGrowth.findConditionalPatternBase(item.getItems());
            List<Pattern> conditionalFPTree = fpGrowth.buildConditionalFPTree(conditionalPatternBase, min_sup);
            List<Pattern> subpatterns = FPGrowth.generateSubpatterns(conditionalFPTree, item.getItems());
            for (Pattern subpattern : subpatterns) {
                List<String> items = subpattern.getItems();
                int count = subpattern.getSupport();
                subpatternMap.put(items, subpatternMap.getOrDefault(items, 0) + count);
            }
            List<String> myList = new ArrayList<>();
            myList.add(item.getItems());
            subpatternMap.put(myList,  item.getCount());
        }


        // Convert map to list of patterns
        List<Pattern> subpatterns = new ArrayList<>();
        for (Map.Entry<List<String>, Integer> entry : subpatternMap.entrySet()) {
            subpatterns.add(new Pattern(entry.getKey(), entry.getValue()));
        }

        // Sort subpatterns by support count (descending order)
        subpatterns.sort(Comparator.comparingInt(Pattern::getSupport));

        // Print subpatterns
        System.out.println("Subpatterns:");
        for (Pattern subpattern : subpatterns) {
            System.out.println(subpattern.getItems() + " - Count: " + ((double) subpattern.getSupport())/ fpGrowth.transactions.size());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);

    }

}

