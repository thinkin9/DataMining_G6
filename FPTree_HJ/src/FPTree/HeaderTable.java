// HeaderTable.java
package FPTree;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class HeaderTable {
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


    public void printHeaderTable() {
        System.out.println("Header Table Contents:");
        for (Map.Entry<String, FPNode> entry : headerTable.entrySet()) {
            String itemName = entry.getKey();
            FPNode node = entry.getValue();
            System.out.println("Item: " + itemName + ", Support: " + node.getName());
        }
    }

    public FPNode getRoot(String name) {
        FPNode node = headerTable.get(name);
        if (node != null) {
            while (node.parent != null) {
                node = node.parent;
            }
        }
        return node;
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



}
