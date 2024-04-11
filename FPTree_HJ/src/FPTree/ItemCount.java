package FPTree;


public class ItemCount {
    public String name;
    public int count;

    public ItemCount(String name, int count){
        this.name = name;
        this.count = count;
    }

    public String toString() {
        return "[" + name + "] - Count: " + count;
    }
}
