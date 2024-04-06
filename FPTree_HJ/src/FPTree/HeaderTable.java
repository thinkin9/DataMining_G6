// HeaderTable.java
package FPTree;

import java.util.HashMap;
import java.util.Map;

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


}
