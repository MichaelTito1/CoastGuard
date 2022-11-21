package code;

public class TreeNode {
    Object[] state;
    TreeNode parent;
    Object operator;
    int depth;
    int pathCost;

    public TreeNode(Object[] state, TreeNode parent, Object operator, int depth, int pathCost) {
        this.state = state;
        this.parent = parent;
        this.operator = operator;
        this.depth = depth;
        this.pathCost = pathCost;
    }
}
