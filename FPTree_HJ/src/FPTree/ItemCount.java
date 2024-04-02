package FPTree;

import java.util.List;

public class ItemCount {
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
