package FPTree;

import java.util.ArrayList;
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
}
