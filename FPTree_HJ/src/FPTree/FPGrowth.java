package FPTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FPGrowth {
    public String   file_name;  // file name
    public File     file;       // file object
    public double   threshold;  // threshold value
    public double   min_sup;    // threshold * #transactions

    public FPNode   fp_root;    // root node
    public List<ArrayList<String>> transactions;    // Database of Transactions
    public Map<String, Integer> itemCounts;         // Database of Item-Count Pairs
    public List<ItemCount> freqItems;               // List of Frequent items
    public HashMap<String, FPNode> headerTable;     // Table of header


    // FPGrowth Constructor
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


    // Build Transaction database, List<ArrayList<String>> transactions from the given dataset
    public void buildTransactions() throws FileNotFoundException {
        try {
            Scanner scanner = new Scanner(this.file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                List<String> list_items = Arrays.asList(line.split(","));
                ArrayList<String> items = new ArrayList<>(list_items);

                this.transactions.add(items);
            }

            // min_sup value is determined by threshold * # transactions
            min_sup = threshold * transactions.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    // Build Item-Count database
    public void buildItemCounts(){
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

        // Sort keySet_itemCounts based on the frequency of each items
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

        // Check headerTable
        // for (ItemCount i : freqItems){
        //     System.out.print("Key : " + i.name);
        //     System.out.println(" | Val : " + i.count);
        // }

        // groceries.csv
        // Key : whole milk          | Val : 2510
        // Key : other vegetables    | Val : 1901
        // Key : rolls/buns          | Val : 1807
        // Key : soda                | Val : 1715
        //
        // lecture_example.csv
        // Key : c | Val : 4
        // Key : f | Val : 4
        // Key : a | Val : 3
        // Key : b | Val : 3
        // Key : m | Val : 3
        // Key : p | Val : 3
    }


    // Build FPTree
    public void buildFPTree(){
        // Initialize a root node
        fp_root = new FPNode("null");
        fp_root.setRoot();

        for (ArrayList<String> transaction : transactions) {
            ArrayList<String> freqSortedTransaction = new ArrayList<String>();

            for (ItemCount i: freqItems){
                if (transaction.contains(i.name)){
                    freqSortedTransaction.add(i.name);
                }
            }

            processTransactionFPTree(fp_root, freqSortedTransaction);
        }
        checkFPTree();
    }


    // Process a single transaction to build FPTree
    public void processTransactionFPTree(FPNode fp_node, ArrayList<String> freqSortedTransaction){
        for (String name : freqSortedTransaction) {
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
                // initialize a child node
                FPNode child = new FPNode(name); // child.count is initially set to 1
                child.parent = fp_node;
                fp_node.children.add(child);

                FPNode head = headerTable.get(name);

                // Traverse headerTable to set the linked-list like connection between nodes having the same item name
                if (head == null) {
                    headerTable.put(name, child);
                } else {
                    // while find the last node having item name as name
                    while (head.next != null) {
                        head = head.next;
                    }
                    // last node points new child node
                    head.next = child;
                }
                fp_node = child;
            }
        }
    }

    // Traverse FP Tree for checking
    public void checkFPTree(){
        FPNode root = this.fp_root;

        Stack<FPNode> stack = new Stack<>();

        stack.push(root);
        System.out.println("START");
        while(!stack.empty()){
            FPNode ptr = stack.pop();
            System.out.println(ptr.name);
            System.out.println(ptr.count);
            for(FPNode child: ptr.children){
                stack.push(child);
            }
        }
        System.out.println("END");
    }

    public boolean checkSinglePath(FPNode fpnode){
        boolean tf = true;
        if(fpnode.children.size() > 1){
            tf = false;
            return tf;
        }
        else{
            for (FPNode child: fpnode.children){
                if (tf) tf = checkSinglePath(child);
                else break;
            }
        }
        return tf;
    }

    public void patternGrowth(FPNode fpnode){
        if(checkSinglePath(fpnode)){
            List<ItemCount> sp_items = new ArrayList<>();
            FPNode child = fpnode;
            while(child != null){
                sp_items.add(new ItemCount(child.name, child.count));
                if (child.children.isEmpty()) break;
                else child = child.children.get(0);
            }

            for(ItemCount item: sp_items){
                System.out.print(item.name);
                System.out.println(item.count);
            }

            List<ArrayList<ItemCount>> subpatterns = generateSubpattern(sp_items);
            for(ArrayList<ItemCount> subpattern: subpatterns){
                for(ItemCount item: subpattern){
                    System.out.print(item.name);
                    System.out.println(item.count);
                }
            }
        }
    }

    public List<ArrayList<ItemCount>> generateSubpattern(List<ItemCount> sp_items){
        List<ArrayList<ItemCount>> subpatterns = new ArrayList<ArrayList<ItemCount>>();

        if(sp_items.size() > 0){
            ItemCount first = sp_items.get(0);
            if(sp_items.size() > 1){
                sp_items.remove(0);
                List<ArrayList<ItemCount>> recursive_subpatterns = generateSubpattern(sp_items);

                if (first.count >= min_sup) {
                    List<ArrayList<ItemCount>> prefix_subpatterns = new ArrayList<ArrayList<ItemCount>>();
                    for (ArrayList<ItemCount> recursive_subpattern : recursive_subpatterns) {
                        recursive_subpattern.add(0, first);
                        prefix_subpatterns.add(recursive_subpattern);
                    }
                    recursive_subpatterns.addAll(prefix_subpatterns);
                }
                subpatterns.addAll(recursive_subpatterns);
            }
            // sp_items.size() == 1
            else{
                if (first.count >= min_sup){
                    ArrayList<ItemCount> base = new ArrayList<>();
                    base.add(first);
                    subpatterns.add(base);
                }
            }
        }
        return subpatterns;
    }

    // public List<Pattern> generateSubpattern(List<String> sp_items){
    //     List<Pattern> subpatterns = new ArrayList<>();
         
	// 	if(sp_items.size() > 0){
	// 		String first = sp_items.get(0);
	// 		if(sp_items.size() > 1){
	// 			sp_items.remove(0);
	// 			List<Pattern> recursive_subpatterns = generateSubpattern(sp_items);

    //             for(Pattern subpattern: recursive_subpatterns){
    //                 for()
    //             }

	// 			subpatterns.addAll(recursive_subpatterns);



	// 			for(Pattern k_1_subpattern: k_1_subpatterns)
	// 				for(int i = 0; i < k_1_subpattern.size(); i++){
	// 					ArrayList<String> combination = new ArrayList<String>();
	// 					int count = Integer.MAX_VALUE;
	// 					for(int j = 0; j <= i; j++){
	// 						for(HeaderNode h: newHeaderTable){
	// 							if(h.itemID.equals(a.get(j)) && count < h.supportCount){
	// 								count = h.supportCount;
	// 							}
	// 						}
	// 						combination.add(a.get(j));
	// 					}
	// 					for(HeaderNode h: newHeaderTable){
	// 						if(h.itemID.equals(s) && count < h.supportCount){
	// 							count = h.supportCount;
	// 						}
	// 					}
	// 					if(count >= minSupport*nTrans){
	// 						combination.add(s);
	// 						combinations.add(combination);
	// 					}
	// 				}
	// 			}
	// 		}
	// 		ArrayList<String> combination = new ArrayList<String>();
	// 		combination.add(s);
	// 		combinations.add(combination);
	// 	}
	// 	return combinations;
	// }
    // }
    // public void checkSinglePath(FPNode fpnode){
    //     boolean check = true;
    //     while(fh)
    // }


    // public List<Pattern> findConditionalPatternBase(String item) {
    //     List<Pattern> conditionalPatternBase = new ArrayList<>();
    //     FPNode head = headerTable.get(item);

    //     while(head != null){
    //         List<String> path = new ArrayList<>();
    //         FPNode parent = head.parent;

    //         while(parent != null) {
    //             path.add(0, parent.name);
    //             parent = parent.parent;
    //         }

    //         // add cond. pattern base (Slide #57) with head.count 
    //         conditionalPatternBase.add(new Pattern(path, head.count));
    //         // System.out.println(head.count);
    //         head = head.next;
    //     }

    //     return conditionalPatternBase;
    // }


    // public List<Pattern> _buildConditionalFPTree(List<Pattern> conditionalPatternBase, double min_sup) {
    //     List<Pattern> conditionalFPTree = new ArrayList<>();

    //     // add count for each item (pattern base)
    //     Map<String, Integer> itemCounts = new LinkedHashMap<>();
    //     for (Pattern pattern : conditionalPatternBase) {
    //         for (String item : pattern.getItems()) {
    //             itemCounts.put(item, itemCounts.getOrDefault(item, 0) + pattern.getSupport());
    //         }
    //     }

    //     System.out.println("_buildConditionalFPTree");
    //     for (ItemCount i : freqItems){
    //         System.out.print(i.name);
    //         System.out.print(i.count);
    //     }

    //     System.out.println(headerTable.keySet());
    //     return conditionalFPTree;
    // }



    // public List<Pattern> buildConditionalFPTree(List<Pattern> conditionalPatternBase, double min_sup) {
    //     List<Pattern> conditionalFPTree = new ArrayList<>();

    //     // add count for each item (pattern base)
    //     Map<String, Integer> itemCounts = new LinkedHashMap<>();
    //     for (Pattern pattern : conditionalPatternBase) {
    //         for (String item : pattern.getItems()) {
    //             itemCounts.put(item, itemCounts.getOrDefault(item, 0) + pattern.getSupport());
    //         }
    //     }

    //     // save item (>threshold) in the list
    //     List<String> frequentItems = new ArrayList<>();
    //     for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
    //         if (entry.getValue() >= min_sup) {
    //             frequentItems.add(entry.getKey());
    //         }
    //     }

    //     // make new pattern list
    //     for (Pattern pattern : conditionalPatternBase) {
    //         Map<String, Integer> newItemCounts = new LinkedHashMap<>();
    //         for (String item : frequentItems) {
    //             if (pattern.getItems().contains(item)) {
    //                 newItemCounts.put(item, itemCounts.get(item));
    //             }
    //         }   

    //         // merge same pattern set
    //         Pattern newPattern = new Pattern(new ArrayList<>(newItemCounts.keySet()), pattern.getSupport());
    //         boolean exists = false;
    //         for (Pattern existingPattern : conditionalFPTree) {
    //             if (existingPattern.getItems().equals(newPattern.getItems())) {
    //                 existingPattern.setSupport(existingPattern.getSupport() + newPattern.getSupport());
    //                 exists = true;
    //                 break;
    //             }
    //         }
    //         if (!exists) {
    //             conditionalFPTree.add(newPattern);
    //         }
    //     }

    //     // remove item < threshold
    //     conditionalFPTree.removeIf(pattern -> pattern.getSupport() < min_sup);

    //     return conditionalFPTree;
    // }


    // public static List<Pattern> generateSubpatterns(Pattern pattern) {
    //     List<Pattern> subpatterns = new ArrayList<>();
    //     List<String> items = pattern.getItems();
    //     int n = items.size();

    //     // Subsets of the pattern
    //     for (int i = 1; i < (1 << n); i++) {
    //         List<String> subpatternItems = new ArrayList<>();
    //         for (int j = 0; j < n; j++) {
    //             if ((i & (1 << j)) > 0) {
    //                 subpatternItems.add(items.get(j));
    //             }
    //         }
    //         subpatterns.add(new Pattern(subpatternItems, pattern.getSupport()));
    //     }

    //     return subpatterns;
    // }

    // public static List<Pattern> generateSubpatterns(List<Pattern> patterns) {
    //     Map<List<String>, Integer> combinedSubpatternsMap = new HashMap<>();

    //     // Generate subpatterns for each pattern and combine them
    //     for (Pattern pattern : patterns) {
    //         List<Pattern> subpatterns = generateSubpatterns(pattern);
    //         for (Pattern subpattern : subpatterns) {
    //             List<String> items = subpattern.getItems();
    //             int count = subpattern.getSupport();
    //             combinedSubpatternsMap.put(items, combinedSubpatternsMap.getOrDefault(items, 0) + count);
    //         }
    //     }

    //     // Convert map back to list of patterns
    //     List<Pattern> combinedSubpatterns = new ArrayList<>();
    //     for (Map.Entry<List<String>, Integer> entry : combinedSubpatternsMap.entrySet()) {
    //         combinedSubpatterns.add(new Pattern(entry.getKey(), entry.getValue()));
    //     }

    //     return combinedSubpatterns;
    // }





}
