package FPTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FPGrowth {
    public String   file_name;
    public File     file;
    public double   threshold;
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

        for (ItemCount i : freqItems){
            System.out.print("Key : " + i.name);
            System.out.println(" | Val : " + i.count);
        }

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
            // System.out.println();
            processTransactionFPTree(fp_root, freqSortedTransaction);
        }
        checkFPTree();
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
                    while (head.next != null) {
                        head = head.next;
                    }
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
    // Print (Notion에 트리 그려놓았으니 참고해주세요)
    // Slide에서는 F C A B M P 순서인데 제꺼에서는 C F A B M P 순서로 F-list 만들어지고
    // 그거 기반으로 HeaderTable, FPTree build까지 구현 완료했습니다.

    // null - f - b
    //      - c - b - p
    //          - f - a - b - m
    //                  - m - p

}
