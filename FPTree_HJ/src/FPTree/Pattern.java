package FPTree;

import java.util.List;

public class Pattern {
    private List<String> items;
    private int support;

    public Pattern(List<String> items, int support) {
        this.items = items;
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
}
