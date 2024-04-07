package FPTree;

import java.util.List;
import java.util.ArrayList;

public class Pattern {
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
