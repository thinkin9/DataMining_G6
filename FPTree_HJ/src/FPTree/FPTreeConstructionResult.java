package FPTree;

public class FPTreeConstructionResult {
    private FPNode root;
    private HeaderTable headerTable;

    public FPTreeConstructionResult(FPNode root, HeaderTable headerTable) {
        this.root = root;
        this.headerTable = headerTable;
    }

    public FPNode getRoot() {
        return root;
    }

    public HeaderTable getHeaderTable() {
        return headerTable;
    }
}
