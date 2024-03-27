package FPTree;

import java.util.Vector;

public class FPNode {

    // Root
    boolean     isRoot;

    // information for a single node
    int         idx;
    int         count;
    String      name;

    // Relationship between nodes
    Vector<FPNode> children;
    FPNode      parent;

    // Constructor
    public FPNode(String str){
        this.isRoot = false;
        this.idx = -1;
        this.count = 0;
        this.name = str;
        this.children = new Vector<FPNode>();
        this.parent = null;
    }
}
