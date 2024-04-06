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
    public HeaderTable headerTable;


    // Constructor
    public FPGrowth(String file_name, double threshold){
        this.file_name = file_name;
        this.file = new File(file_name);
        this.threshold = threshold;
        this.min_sup = 0;
        this.transactions = new ArrayList<ArrayList<String>>();
        this.itemCounts = new HashMap<String, Integer>();
        this.freqItems = new ArrayList<ItemCount>();
        this.headerTable = new HeaderTable();
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

    public void checkFPTree(FPNode N){
        // Traverse FP Tree for checking
        FPNode root =N;

        Stack<FPNode> stack = new Stack<>();

        stack.push(root);
        while(!stack.empty()){
            FPNode ptr = stack.pop();
            System.out.println(ptr.name+": "+ptr.count);
            for(FPNode child: ptr.children){
                stack.push(child);
            }
        }
    }


    // buildFPTreeFromPatterns
    public FPNode buildFPTreeFromPatterns(List<Pattern> patterns) {
        FPNode root = new FPNode("null");
        root.setRoot();

        // Create a new header table
        HeaderTable newHeaderTable = new HeaderTable();

        // Construct FP Tree
        for (Pattern pattern : patterns) {
            List<String> items = pattern.getItems();
            int support = pattern.getSupport();

            processPatternFPTree(root, items, support, newHeaderTable);
        }
        checkFPTree(root);
        newHeaderTable.printHeaderTable();
        return root;
    }

    // processPatternFPTree
    private void processPatternFPTree(FPNode fpNode, List<String> items, int support, HeaderTable newHeaderTable) {
        for (String name : items) {
            boolean itemExist = false;

            // Check if the item exists as a child of the current node
            for (FPNode child : fpNode.getChildren()) {
                if (child.getName().equals(name)) {
                    child.addCount(support);
                    itemExist = true;
                    fpNode = child;
                    break;
                }
            }

            // If the item doesn't exist, create a new node
            if (!itemExist) {
                FPNode child = new FPNode(name);
                child.setCount(support);
                child.setParent(fpNode);
                fpNode.addChild(child);

                // Update Header Table
                FPNode head = newHeaderTable.get(name);
                if (head == null) {
                    newHeaderTable.put(name, child);
                } else {
                    // Find the last head of (name)
                    while (head.next != null) {
                        head = head.next;
                    }
                    // Last head points to the new child node
                    head.next = child;
                }
                fpNode = child;
            }
        }
    }



    public List<Pattern> findConditionalPatternBase(String item, HeaderTable hT) {
        List<Pattern> conditionalPatternBase = new ArrayList<>();
        FPNode head = hT.get(item);

        while(head != null){
            List<String> path = new ArrayList<>();
            FPNode parent = head.parent;

            while(parent != null && !parent.isRoot) {
                path.add(0, parent.name);
                parent = parent.parent;
            }

            // add cond. pattern base (Slide #57) with head.count
            conditionalPatternBase.add(new Pattern(path, head.count));
            System.out.println(path+":"+head.count);
            head = head.next;
        }

        return conditionalPatternBase;
    }
//
//
//    public FPNode buildConditionalFPTree(String item) {
//        // Find conditional pattern base for the given item
//        List<Pattern> conditionalPatternBase = findConditionalPatternBase(item);
//
//        // Build the conditional FP tree
//        FPNode conditionalFPRoot = new FPNode("this");
//        conditionalFPRoot.setRoot();
//
//        // Process each conditional pattern in the pattern base
//        for (Pattern pattern : conditionalPatternBase) {
//            // Extract items and count from the pattern
//            List<String> path = pattern.getItems();
//            System.out.println(path+": "+pattern.getSupport());
//            int count = pattern.getSupport();
//
//            // Build the conditional FP tree using the path and count
//            buildConditionalFPTreeHelper(conditionalFPRoot, path, count);
//            checkFPTree(conditionalFPRoot);
//        }
//
//
//        return conditionalFPRoot;
//    }
//
//    private void buildConditionalFPTreeHelper(FPNode fpNode, List<String> path, int count) {
//        // Start from the root of the conditional FP tree
//        FPNode currentNode = fpNode;
//
//        // Traverse the path and increment counts accordingly
//        for (String itemName : path) {
//            boolean itemExist = false;
//
//            // Check if the current node has a child with the same name as the item
//            for (FPNode child : currentNode.getChildren()) {
//                if (child.getName().equals(itemName)) {
//                    // Increment the count of the child node by the given count
//                    child.addCount(count);
//                    currentNode = child;
//                    itemExist = true;
//                    break;
//                }
//            }
//
//            // If the item does not exist as a child node, create a new node and add it
//            if (!itemExist) {
//                FPNode newItemNode = new FPNode(itemName);
//                newItemNode.setParent(currentNode);
//                newItemNode.addCount(count-1);
//                currentNode.addChild(newItemNode);
//                currentNode = newItemNode;
//            }
//        }
//    }
//
//
//
////    public List<Map<String, Integer>> findConditionalPatternBase(String item) {
////        List<Map<String, Integer>> conditionalPatternBase = new ArrayList<>();
////
////        FPNode head = headerTable.get(item);
////
////        while (head != null) {
////            Map<String, Integer> PatternBase = new LinkedHashMap<>();
////            FPNode parent = head.parent;
////
////            while (parent != null && !parent.isRoot) {
////                PatternBase.put(parent.name, head.count);
////                parent = parent.parent;
////            }
////
////            // Reverse the order of entries in PatternBase !!
////            Map<String, Integer> reversedPatternBase = new LinkedHashMap<>();
////            List<String> keys = new ArrayList<>(PatternBase.keySet());
////            for (int i = keys.size() - 1; i >= 0; i--) {
////                String key = keys.get(i);
////                reversedPatternBase.put(key, PatternBase.get(key));
////            }
////
////            System.out.println(reversedPatternBase);
////            conditionalPatternBase.add(reversedPatternBase);
////
////            head = head.next;
////        }
////
////        return conditionalPatternBase;
////    }
//
//
////    public List<Map<String, Integer>> buildConditionalFPTree(List<Map<String, Integer>> conditionalPatternBase, double min_sup) {
////        List<Map<String, Integer>> conditionalFPTree = new ArrayList<>();
////
////        // Add count for each item (pattern base)
////        Map<String, Integer> itemCounts = new LinkedHashMap<>();
////        for (Map<String, Integer> pattern : conditionalPatternBase) {
////            boolean count_min = false;
////            for (Map.Entry<String, Integer> entry : pattern.entrySet()) {
////                if (!count_min){
////                    int min = entry.getValue();
////                    count_min = true;
////                    itemCounts.put("min", min);
////                }
////                String item = entry.getKey();
////                int count = entry.getValue();
////                itemCounts.put(item, itemCounts.getOrDefault(item, 0) + count);
////            }
////        }
////
////        // Save item (> threshold) in the list
////        List<String> frequentItems = new ArrayList<>();
////        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
////            if (entry.getValue() >= min_sup) {
////                frequentItems.add(entry.getKey());
////            }
////            if (entry.getKey() == "min"){
////                frequentItems.add(entry.getKey());
////            }
////        }
////
////        // Make new pattern list
////        for (Map<String, Integer> pattern : conditionalPatternBase) {
////            Map<String, Integer> newItemCounts = new LinkedHashMap<>();
////            for (String item : frequentItems) {
////                if (pattern.containsKey(item)) {
////                    newItemCounts.put(item, itemCounts.get(item));
////                }
////            }
////            System.out.println(newItemCounts);
////            conditionalFPTree.add(newItemCounts);
////        }
////
////        return conditionalFPTree;
////    }
//
//
//    public static List<Pattern> generatePatterns(List<Map<String, Integer>> conditionalFPTree, String targetItem) {
//        Map<List<String>, Integer> combinedSubpatternsMap = new HashMap<>();
//
//
//        for (Map<String, Integer> map : conditionalFPTree) {
//            List<Pattern> patterns = generatePatternsHelper(map);
//            for (Pattern subpattern : patterns) {
//                List<String> items = subpattern.getItems();
//                items.add(targetItem);
//                int count = subpattern.getSupport();
//                combinedSubpatternsMap.put(items, count);
//            }
//        }
//
//        List<Pattern> combinedSubpatterns = new ArrayList<>();
//        for (Map.Entry<List<String>, Integer> entry : combinedSubpatternsMap.entrySet()) {
//            combinedSubpatterns.add(new Pattern(entry.getKey(), entry.getValue()));
//        }
//
//        return combinedSubpatterns;
//
//    }
//
//    private static List<Pattern> generatePatternsHelper(Map<String, Integer> map) {
//        List<Pattern> patterns = new ArrayList<>();
//        List<String> keys = new ArrayList<>(map.keySet());
//        int n = keys.size();
//        // Generate all possible subsets of keys
//        for (int i = 1; i < (1 << n); i++) {
//            List<String> subset = new ArrayList<>();
//            int minValue = Integer.MAX_VALUE;
//            for (int j = 0; j < n; j++) {
//                if ((i & (1 << j)) > 0) {
//                    subset.add(keys.get(j));
//                    minValue = Math.min(minValue, map.get(keys.get(j)));
//                }
//            }
//            patterns.add(new Pattern(subset, minValue));
//        }
//        return patterns;
//    }

    // 이제 single path 인지확인하고
    // 아니라면 pattern 으로 base 였던 값 넣어두고 tree 에서 각각의 header table 에 있느 ㄴ놈들을 다시 fptree 만들기.
    //이때 base 값 저장해둔 pattern?이나 리스트에 완료값 하나씩 넣기

}
