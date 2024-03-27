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
    public Map<String, Integer> freqItems;

    // Constructor
    public FPGrowth(String file_name, double threshold){
        this.file_name = file_name;
        this.file = new File(file_name);
        this.threshold = threshold;
        this.min_sup = 0;
        this.transactions = new ArrayList<ArrayList<String>>();
        this.itemCounts = new HashMap<String, Integer>();
        this.freqItems = new HashMap<String, Integer>();
    }


    // ParseTransactions
    public void parseTransactions() throws FileNotFoundException {
        try {
            Scanner scanner = new Scanner(this.file);
            System.out.println(scanner);
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


    // findFrequentItems
    public void findFrequentItems(){
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
        for (String key : keySet_itemCounts) {
            int cnt = itemCounts.get(key);
            if(cnt >= min_sup){
                freqItems.put(key, cnt);
            }
        }

        List<String> keySet_frequentItems = new ArrayList<>(freqItems.keySet());
        // https://velog.io/@dev-easy/Java-Map%EC%9D%84-Key-Value%EB%A1%9C-%EC%A0%95%EB%A0%AC%ED%95%98%EA%B8%B0
        keySet_frequentItems.sort((e1, e2) -> freqItems.get(e2).compareTo(freqItems.get(e1)));
//        for (String key : keySet_frequentItems){
//            System.out.print("Key : " + key);
//            System.out.println(" | Val : " + frequentItems.get(key));
//        }
//        Key : whole milk          | Val : 2510
//        Key : other vegetables    | Val : 1901
//        Key : rolls/buns          | Val : 1807
//        Key : soda                | Val : 1715

    }


    // buildFPTree
    public void buildFPTree(){
        fp_root = new FPNode("null");
        fp_root.setRoot();

        for (ArrayList<String> transaction : transactions) {
            ArrayList<String> freqSortedTransaction = new ArrayList<String>();
            List<String> keySet_freqItems = new ArrayList<>(freqItems.keySet());
            for (String item: keySet_freqItems){
                if (transaction.contains(item)){
                    freqSortedTransaction.add(item);
                }
            }
            processTransactionFPTree(fp_root, freqSortedTransaction);
        }
    }

    // processTransactionFPTree
    public void processTransactionFPTree(FPNode fp_node, ArrayList<String> freqSortedTransaction){
        for (String item : freqSortedTransaction){
            boolean checkChildren = false;

            for (FPNode child: fp_node.children){
                if (child.name.equals(item)){
                    child.count++;
                    checkChildren = true;
                   break;
                }
            }

            if(!checkChildren){
                FPNode child = new FPNode(item); // child.count is initially set to 1
                child.parent = fp_node;
                fp_node.children.add(child);

                // TODO
            }
        }
    }


}
