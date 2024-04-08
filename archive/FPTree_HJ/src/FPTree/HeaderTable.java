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
