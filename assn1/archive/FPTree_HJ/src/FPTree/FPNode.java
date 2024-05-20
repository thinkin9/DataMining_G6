package FPTree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;

public class FPNode {

    // Root
    boolean     isRoot;

    // information for a single node
    int         idx;
    int         count;
    String      name;

    // Relationship between nodes
    ArrayList<FPNode> children;
    FPNode      parent;
    FPNode      next;  // next FPNode @HeaderTable

    // Constructor
    public FPNode(String str){
        this.isRoot = false;
        this.idx = -1;
        this.count = 1;
        this.name = str;
        this.children = new ArrayList<FPNode>();
        this.parent = null;
        this.next = null;
    }

    public void setRoot(){
        this.isRoot = true;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int num) {this.count = num;}

    public ArrayList<FPNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public void setParent(FPNode parent) {
        this.parent = parent;
    }

    public void addChild(FPNode child) {
        children.add(child);
    }

    public void addCount(int num) {count += num;}

    public boolean hasSingleChild() {
        return this.getChildren().size() == 1;
    }

    public void removeChild(FPNode childToRemove) {
        children.remove(childToRemove);
    }

}
