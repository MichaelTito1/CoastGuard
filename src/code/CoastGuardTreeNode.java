package code;

public class CoastGuardTreeNode extends TreeNode{
    public CoastGuardTreeNode(CoastGuardState state, CoastGuardTreeNode parent, CoastGuard.Operators operator, int depth) {
        super(state, parent, operator, depth, null);
    }

    public CoastGuardTreeNode(CoastGuardState state, CoastGuardTreeNode parent, CoastGuard.Operators operator, int depth, int[] pathCost) {
        super(state, parent, operator, depth, pathCost);
    }

    public CoastGuardState getState(){
        return (CoastGuardState) state;
    }

    public CoastGuardTreeNode getParent(){
        return (CoastGuardTreeNode) parent;
    }
}
