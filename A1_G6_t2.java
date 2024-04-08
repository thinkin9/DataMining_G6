import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


// Class FPNode
class FPNode {

    // Root
    boolean     isRoot;

    // information for a single node
    int         idx;
    int         count;
    String      name;

    // Relationship between nodes
    ArrayList<FPNode> children;
    FPNode      parent;
    FPNode      next;  // next FPNode @HeaderTable

    // Constructor
    public FPNode(String str){
        this.isRoot = false;
        this.idx = -1;
        this.count = 1;
        this.name = str;
        this.children = new ArrayList<FPNode>();
        this.parent = null;
        this.next = null;
    }

    public void setRoot(){
        this.isRoot = true;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int num) {this.count = num;}

    public ArrayList<FPNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public void setParent(FPNode parent) {
        this.parent = parent;
    }

    public void addChild(FPNode child) {
        children.add(child);
    }

    public void incrementCount() {
        count++;
    }

    public void addCount(int num) {count += num;}

    public boolean hasSingleChild() {
        return this.getChildren().size() == 1;
    }

    public void removeChild(FPNode childToRemove) {
        children.remove(childToRemove);
    }

}


// Class ItemCount
class ItemCount {
    public String name;
    public int count;

    public ItemCount(String name, int count){
        this.name = name;
        this.count = count;
    }
    public String getItems() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String toString() {
        return "[" + name + "] - Count: " + count;
    }
}


// Class Pattern
class Pattern {
    private List<String> items;
    private int support;

    public Pattern(List<String> items, int support) {
        //this.items = items;
        this.items = new ArrayList<>(items);
        this.support = support;
    }

    public List<String> getItems() {
        return items;
    }

    public int getSupport() {
        return support;
    }

    public void incrementSupport() {
        this.support++;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public void addItem(String item) {
        this.items.add(item);
    }
    public void addItems(List<String> newItems) {
        items.addAll(newItems);
    }

    public void addItemToFront(String item) {
        items.add(0, item);
    }
}

// Class FPTreeConstructionResult
class FPTreeConstructionResult {
    private FPNode root;
    private HeaderTable headerTable;

    private double threshold;

    public FPTreeConstructionResult(FPNode root, HeaderTable headerTable, double threshold) {
        this.root = root;
        this.headerTable = headerTable;
        this.threshold = threshold;
    }

    public FPNode getRoot() {
        return root;
    }

    public HeaderTable getHeaderTable() {
        return headerTable;
    }
}


// Class HeaderTable
class HeaderTable {
    public HashMap<String, FPNode> headerTable;

    public HeaderTable() {
        this.headerTable = new HashMap<>();
    }

    public void put(String name, FPNode node) {
        headerTable.put(name, node);
    }

    public FPNode get(String name) {
        return headerTable.get(name);
    }

    public void removeItem(String item) {
        headerTable.remove(item);
    }

    public void printHeaderTable() {
        System.out.println("Header Table Contents:");
        for (Map.Entry<String, FPNode> entry : headerTable.entrySet()) {
            String itemName = entry.getKey();
            FPNode node = entry.getValue();
            System.out.println("Item: " + itemName + ", Support: " + node.getName());
        }
    }

    public FPNode getRoot() {
        FPNode rootNode = null;
        for (FPNode node : headerTable.values()) {
            if (node.isRoot) {
                rootNode = node;
                break;
            }
        }
        if (rootNode != null) {
            while (rootNode.parent != null) {
                rootNode = rootNode.parent;
            }
        }
        return rootNode;
    }

    public List<String> getAllItems() {
        List<String> allItems = new ArrayList<>();
        for (Map.Entry<String, FPNode> entry : headerTable.entrySet()) {
            String key = entry.getKey();
            if (!allItems.contains(key)) {
                allItems.add(key);
            }
        }
        return allItems;
    }

    public int getTotalCount(String item) {
        int totalCount = 0;
        if (headerTable.containsKey(item)) {
            FPNode node = headerTable.get(item);
            while (node != null) {
                totalCount += node.getCount();
                node = node.next;
            }
        }
        return totalCount;
    }

    public void removeAll(String item) {
        FPNode node = headerTable.get(item);

        while (node != null) {
            FPNode parentNode = node.parent;

            if (parentNode != null) {
                if (!node.getChildren().isEmpty()) {
                    for (FPNode childNode : node.getChildren()) {
                        parentNode.addChild(childNode);
                        childNode.setParent(parentNode);
                    }
                }
                parentNode.removeChild(node);
            }
            node = node.next;
        }
        headerTable.remove(item);
    }
}


// Class FPGrowth
class FPGrowth {
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


    public FPTreeConstructionResult buildFPTreeFromPatterns(List<Pattern> patterns, double threshold) {
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

        // Remove nodes with support less than the threshold
        removeNodesWithLowSupport(threshold, newHeaderTable);

        //checkFPTree(root);

        return new FPTreeConstructionResult(root, newHeaderTable, threshold);
    }

    public void removeNodesWithLowSupport(double threshold, HeaderTable headerTable) {

        // Iterate over all items in the header table
        for (String item : headerTable.getAllItems()) {
            int totalCount = headerTable.getTotalCount(item);
            if (totalCount < threshold) {
                headerTable.removeAll(item);
                ////System.out.println("Remove " + item + ": " + totalCount);
            }
        }

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
            ////System.out.println("Base :"+path+":"+head.count);
            head = head.next;
        }

        return conditionalPatternBase;
    }

    public boolean isSinglePath(FPNode root) {
        if (root == null) {
            return false;
        }
        FPNode currentNode = root;
        while (!currentNode.getChildren().isEmpty() && currentNode.hasSingleChild()) {
            currentNode = currentNode.getChildren().get(0);
        }
        return currentNode.getChildren().isEmpty();
    }

    public List<Pattern> generateSinglePathPatterns(FPNode root) {
        List<Map<String, Integer>> singlePathPatterns = findAllSinglePathPatterns(root);
        List<Pattern> patterns = new ArrayList<>();

        for (Map<String, Integer> pattern : singlePathPatterns) {
//            if (pattern.containsKey("null")) {
//                continue;
//            }
            List<String> items = new ArrayList<>(pattern.keySet());
            int minSupport = Collections.min(pattern.values());
            Pattern singlePathPattern = new Pattern(items, minSupport);
            patterns.add(singlePathPattern);
            ////System.out.println(items+": "+minSupport);
        }

        return patterns;
    }

    private List<Map<String, Integer>> findAllSinglePathPatterns(FPNode root) {
        List<Map<String, Integer>> singlePathPatterns = new ArrayList<>();
        List<FPNode> singlePath = new ArrayList<>();

        // Check if the FP tree is empty
        if (root.getChildren().isEmpty()) {
            return singlePathPatterns; // Return an empty list if the tree is empty
        }

        // Traverse FP tree to find single path
        FPNode currentNode = root.getChildren().get(0);
        while (!currentNode.getChildren().isEmpty() && currentNode.hasSingleChild()) {
            singlePath.add(currentNode);
            currentNode = currentNode.getChildren().get(0);
        }
        singlePath.add(currentNode);

        // Generate all possible subsets of single path
        generateSubsets(singlePath, 0, new LinkedHashMap<>(), singlePathPatterns);

        return singlePathPatterns;
    }

    private void generateSubsets(List<FPNode> singlePath, int index, Map<String, Integer> subset, List<Map<String, Integer>> subsets) {
        if (index == singlePath.size()) {
            if (!subset.isEmpty()) {
                subsets.add(new LinkedHashMap<>(subset));
            }
            return;
        }

        // Include current node in the subset
        FPNode currentNode = singlePath.get(index);
        subset.put(currentNode.getName(), currentNode.getCount());
        generateSubsets(singlePath, index + 1, subset, subsets);

        // Exclude current node from the subset
        subset.remove(currentNode.getName());
        generateSubsets(singlePath, index + 1, subset, subsets);
    }



    public List<Pattern> Final = new ArrayList<>();

    public void performFPGrowthRecursive(FPNode currentNode, Pattern patternList, HeaderTable thistable, double thr) {
        if (thistable == null) {
            return;
        }

        if (isSinglePath(currentNode)) {
            List<Pattern> singlePathPatterns = generateSinglePathPatterns(currentNode);
            for (Pattern pattern : singlePathPatterns) {
                //System.out.println(pattern+": "+pattern.getSupport());
                Pattern newPattern = new Pattern(pattern.getItems(), pattern.getSupport());
                newPattern.addItems(patternList.getItems());
                Final.add(newPattern);
//                System.out.println("before: "+pattern.getItems());
//                pattern.addItems(patternList.getItems());
//                System.out.println("after: "+pattern.getItems());
//                System.out.println("Pattern list: "+patternList.getItems());
////                for (String element : patternList) {
////                    System.out.println("before: "+pattern.getItems());
////                    pattern.addItem(element);
////                    System.out.println("after: "+pattern.getItems());
////                }
//                Final.add(pattern);
            }
            //Final.add(patternList);

        } else {
            ////thistable.printHeaderTable();
            for (String item : thistable.getAllItems()) {
                ////System.out.println("Item= "+item);
                Pattern newPattern = new Pattern(patternList.getItems(), patternList.getSupport());
                ////System.out.println("new Pattern list before: "+newPattern.getItems()+": "+newPattern.getSupport());
//                List<String> newPatternItems = new ArrayList<>();
//                if (patternList != null) {
//                    newPatternItems.addAll(patternList);
//                    //System.out.println("pattern: "+newPatternItems);
//                }
                //newPatternItems.add(0, item);
                newPattern.addItemToFront(item);

                List<Pattern> conditionalPatternBase = findConditionalPatternBase(item, thistable);
                FPTreeConstructionResult conditionalTreeRoot = buildFPTreeFromPatterns(conditionalPatternBase, thr);
                FPNode newroot = conditionalTreeRoot.getRoot();

                int count = thistable.getTotalCount(item);
                ////System.out.println("item count for!!!!!"+item+" : "+count);
                newPattern.setSupport(count);
                ////System.out.println("new Pattern list after: "+newPattern.getItems()+": "+newPattern.getSupport());
                performFPGrowthRecursive(newroot,newPattern, conditionalTreeRoot.getHeaderTable(), thr);
                Final.add(newPattern);
            }
        }
    }


}


public class A1_G6_t2 {
    public static void main(String[] args) throws FileNotFoundException {

        String file_name = args[0];
        double threshold = Double.parseDouble(args[1]);

        long startTime = System.currentTimeMillis();

        FPGrowth fpGrowth = new FPGrowth(file_name, threshold);

        fpGrowth.buildTransactions();
        fpGrowth.buildItemCounts();
        fpGrowth.buildFPTree();
        
        double min_sup = fpGrowth.min_sup;

        List<String > empty = new ArrayList<>();
        Pattern patternItem3 = new Pattern(empty,0);
        fpGrowth.performFPGrowthRecursive(fpGrowth.fp_root, patternItem3, fpGrowth.headerTable, min_sup);

        Collections.sort(fpGrowth.Final, Comparator.comparingInt(Pattern::getSupport));
        for (Pattern pattern : fpGrowth.Final) {
            System.out.println(pattern.getItems()+" "+(double)( pattern.getSupport())/fpGrowth.transactions.size());
        }

        long endTime = System.currentTimeMillis();

        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);

    }

}