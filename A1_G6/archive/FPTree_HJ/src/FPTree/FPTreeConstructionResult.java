package FPTree;

public class FPTreeConstructionResult {
    private FPNode root;
    private HeaderTable headerTable;

    private double threshold;

    public FPTreeConstructionResult(FPNode root, HeaderTable headerTable, double threshold) {
        this.root = root;
        this.headerTable = headerTable;
        this.threshold = threshold;
    }

    public FPNode getRoot() {
        return root;
    }

    public HeaderTable getHeaderTable() {
        return headerTable;
    }
}
