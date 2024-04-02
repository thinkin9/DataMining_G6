package FPTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FPGrowth {
    public String   file_name;
    public File     file;
    public double   threshold; //# of input/whole transactions
    public double   min_sup;

    public FPNode   fp_root;
    public List<ArrayList<String>> transactions;
    public Map<String, Integer> itemCounts;
    public List<ItemCount> freqItems;
    public HashMap<String, FPNode> headerTable;


    // Constructor
    public FPGrowth(String file_name, double threshold){
        this.file_name = file_name;
        this.file = new File(file_name);
        this.threshold = threshold;
        this.min_sup = 0;
        this.transactions = new ArrayList<ArrayList<String>>();
        this.itemCounts = new HashMap<String, Integer>();
        this.freqItems = new ArrayList<ItemCount>();
        this.headerTable = new HashMap<String, FPNode>();
    }


    // buildTransactions
    public void buildTransactions() throws FileNotFoundException {
        try {
            Scanner scanner = new Scanner(this.file);
            // System.out.println(scanner);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                List<String> list_items = Arrays.asList(line.split(","));
                ArrayList<String> items = new ArrayList<>(list_items);

                this.transactions.add(items);
            }
            min_sup = threshold * transactions.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    // buildItemCounts
    public void buildItemCounts(){
        // System.out.println(transactions.get(0).getClass());
        for (ArrayList<String> transaction : transactions) {

            for (String item : transaction) {
                if (itemCounts.containsKey(item)) {
                    itemCounts.put(item, itemCounts.get(item) + 1);
                } else {
                    itemCounts.put(item, 1);
                }
            }
        }

        List<String> keySet_itemCounts = new ArrayList<>(itemCounts.keySet());
        // https://velog.io/@dev-easy/Java-Map%EC%9D%84-Key-Value%EB%A1%9C-%EC%A0%95%EB%A0%AC%ED%95%98%EA%B8%B0
        keySet_itemCounts.sort((e1, e2) -> itemCounts.get(e2).compareTo(itemCounts.get(e1)));

        // Ordered items (Slide #51)
        for (String key : keySet_itemCounts) {
            int cnt = itemCounts.get(key);
            if(cnt >= min_sup){
                freqItems.add(new ItemCount(key, cnt));
            }
        }

        // Set headerTable (Slide #52)
        for (ItemCount i : freqItems){
            headerTable.put(i.name, null);
        }

//        for (ItemCount i : freqItems){
//            System.out.print("Key : " + i.name);
//            System.out.println(" | Val : " + i.count);
//        }

        //        groceries.csv
        //        Key : whole milk          | Val : 2510
        //        Key : other vegetables    | Val : 1901
        //        Key : rolls/buns          | Val : 1807
        //        Key : soda                | Val : 1715
        //
        //        lecture_example.csv
        //        Key : c | Val : 4
        //        Key : f | Val : 4
        //        Key : a | Val : 3
        //        Key : b | Val : 3
        //        Key : m | Val : 3
        //        Key : p | Val : 3

    }


    // buildFPTree
    public void buildFPTree(){
        fp_root = new FPNode("null");
        fp_root.setRoot();

        for (ArrayList<String> transaction : transactions) {
            ArrayList<String> freqSortedTransaction = new ArrayList<String>();

            for (ItemCount i: freqItems){
                if (transaction.contains(i.name)){
                    // System.out.print(i.name);
                    freqSortedTransaction.add(i.name);
                }
            }
            //System.out.println(freqSortedTransaction);
            processTransactionFPTree(fp_root, freqSortedTransaction);
        }
        //checkFPTree();
    }


    // processTransactionFPTree
    public void processTransactionFPTree(FPNode fp_node, ArrayList<String> freqSortedTransaction){
        for (String name : freqSortedTransaction) {
            // System.out.println(name);
            boolean item_exist = false;

            for (FPNode child : fp_node.children) {
                if (child.name.equals(name)) {
                    child.count++;
                    item_exist = true;
                    fp_node = child;
                    break;
                }
            }

            if (!item_exist) {
                FPNode child = new FPNode(name); // child.count is initially set to 1
                child.parent = fp_node;
                fp_node.children.add(child);

                FPNode head = headerTable.get(name);
                if (head == null) {
                    headerTable.put(name, child);
                } else {
                    // find the last head of (name)
                    while (head.next != null) {
                        head = head.next;
                    }
                    // last head points new child node
                    head.next = child;
                }
                fp_node = child;
            }
        }
    }

    public void checkFPTree(){
        // Traverse FP Tree for checking
        FPNode root = this.fp_root;

        Stack<FPNode> stack = new Stack<>();

        stack.push(root);
        while(!stack.empty()){
            FPNode ptr = stack.pop();
            System.out.println(ptr.name);
            for(FPNode child: ptr.children){
                stack.push(child);
            }
        }
    }

    // conditional pattern
//    public List<FPNode> findConditionalPatternBase(String item) {
//        List<FPNode> conditionalPatternBase = new ArrayList<>();
//        FPNode head = headerTable.get(item);
//
//        if (head == null) {
//            return conditionalPatternBase;
//        }
//
//        while (head != null) {
//            List<FPNode> path = new ArrayList<>();
//            FPNode node = head.parent;
//
//            // Traverse the path to root and add nodes to the pattern base
//            while (node != null && !node.isRoot) {
//                path.add(new FPNode(node.name)); // Create a new FPNode for each node in the path
//                node = node.parent; // Move to the parent node
//            }
//
//            // Reverse the path to maintain the original item order
//            Collections.reverse(path);
//
//            // Create the pattern base by linking the nodes
//            FPNode patternNode = null;
//            for (FPNode pathNode : path) {
//                if (patternNode == null) {
//                    patternNode = pathNode;
//                    conditionalPatternBase.add(patternNode);
//                } else {
//                    patternNode.parent = pathNode;
//                    pathNode.children.add(patternNode);
//                    patternNode = pathNode;
//                }
//            }
//
//            // Add the pattern node to the conditional pattern base
//            for (int i = 0; i < head.count; i++) {
//                conditionalPatternBase.add(patternNode);
//            }
//
//            head = head.next;
//        }
//        return conditionalPatternBase;
//    }

    public List<Pattern> findConditionalPatternBase(String item) {
        List<Pattern> conditionalPatternBase = new ArrayList<>();
        FPNode head = headerTable.get(item);

        while(head != null){
            List<String> path = new ArrayList<>();
            FPNode parent = head.parent;

            while(parent != null && !parent.isRoot) {
                path.add(0, parent.name);
                parent = parent.parent;
            }

            // add cond. pattern base (Slide #57) with head.count
            conditionalPatternBase.add(new Pattern(path, head.count));

            head = head.next;
        }

        // if (head == null) {
        //     return conditionalPatternBase;
        // }

        // while (head != null) {
        //     List<String> path = new ArrayList<>();
        //     FPNode node = head.parent;

        //     while (node != null && !node.isRoot) {
        //         path.add(0, node.name);
        //         node = node.parent;
        //     }

        //     for (int i = 0; i < head.count; i++) {
        //         addPatternToConditionalPatternBase(conditionalPatternBase, path);
        //     }

        //     head = head.next;
        // }

        return conditionalPatternBase;
    }

    // private void addPatternToConditionalPatternBase(List<Pattern> conditionalPatternBase, List<String> items) {
    //     for (Pattern pattern : conditionalPatternBase) {
    //         if (pattern.getItems().equals(items)) {
    //             pattern.incrementSupport();
    //             return;
    //         }
    //     }
    //     conditionalPatternBase.add(new Pattern(items, 1));
    // }

    //get support of each pattern base Map<String, Integer>
    //To avoid Alphabet order sorting in HashMap, used linked hash map.

//    public List<Map<String, Integer>> convertConditionalPatternBase(List<Pattern> conditionalPatternBase) {
//        List<Map<String, Integer>> convertedConditionalPatternBase = new ArrayList<>();
//
//        for (Pattern pattern : conditionalPatternBase) {
//            Map<String, Integer> patternMap = new LinkedHashMap<>(); // LinkedHashMap
//            for (String item : pattern.getItems()) {
//                patternMap.put(item, pattern.getSupport());
//            }
//            convertedConditionalPatternBase.add(patternMap);
//        }
//
//        return convertedConditionalPatternBase;
//    }

    // conditional FP Tree

    public List<Pattern> buildConditionalFPTree(List<Pattern> conditionalPatternBase, double min_sup) {
        List<Pattern> conditionalFPTree = new ArrayList<>();

        // add count for each item (pattern base)
        Map<String, Integer> itemCounts = new LinkedHashMap<>();
        for (Pattern pattern : conditionalPatternBase) {
            for (String item : pattern.getItems()) {
                itemCounts.put(item, itemCounts.getOrDefault(item, 0) + pattern.getSupport());
            }
        }

        // save item (>threshold) in the list
        List<String> frequentItems = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            if (entry.getValue() >= min_sup) {
                frequentItems.add(entry.getKey());
            }
        }

        // make new pattern list
        for (Pattern pattern : conditionalPatternBase) {
            Map<String, Integer> newItemCounts = new LinkedHashMap<>();
            for (String item : frequentItems) {
                if (pattern.getItems().contains(item)) {
                    newItemCounts.put(item, itemCounts.get(item));
                }
            }

            // merge same pattern set
            Pattern newPattern = new Pattern(new ArrayList<>(newItemCounts.keySet()), pattern.getSupport());
            boolean exists = false;
            for (Pattern existingPattern : conditionalFPTree) {
                if (existingPattern.getItems().equals(newPattern.getItems())) {
                    existingPattern.setSupport(existingPattern.getSupport() + newPattern.getSupport());
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                conditionalFPTree.add(newPattern);
            }
        }

        // remove item < threshold
        conditionalFPTree.removeIf(pattern -> pattern.getSupport() < min_sup);

        return conditionalFPTree;
    }


    public static List<Pattern> generateSubpatterns(Pattern pattern) {
        List<Pattern> subpatterns = new ArrayList<>();
        List<String> items = pattern.getItems();
        int n = items.size();

        // Subsets of the pattern
        for (int i = 1; i < (1 << n); i++) {
            List<String> subpatternItems = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    subpatternItems.add(items.get(j));
                }
            }
            subpatterns.add(new Pattern(subpatternItems, pattern.getSupport()));
        }

        return subpatterns;
    }

    public static List<Pattern> generateSubpatterns(List<Pattern> patterns, String targetItem) {
        Map<List<String>, Integer> combinedSubpatternsMap = new HashMap<>();

        // Generate subpatterns for each pattern and combine them
        for (Pattern pattern : patterns) {
            List<Pattern> subpatterns = generateSubpatterns(pattern);
            for (Pattern subpattern : subpatterns) {
                List<String> items = subpattern.getItems();
                items.add(targetItem);
                int count = subpattern.getSupport();
                combinedSubpatternsMap.put(items, combinedSubpatternsMap.getOrDefault(items, 0) + count);
            }
        }

        // Convert map back to list of patterns
        List<Pattern> combinedSubpatterns = new ArrayList<>();
        for (Map.Entry<List<String>, Integer> entry : combinedSubpatternsMap.entrySet()) {
            combinedSubpatterns.add(new Pattern(entry.getKey(), entry.getValue()));
        }

        return combinedSubpatterns;
    }



}
