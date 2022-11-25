package code;

public abstract class QingFun {
    Object queue;
    long expandedNodes;

    abstract void makeQ(TreeNode root);

    abstract boolean qIsEmpty();

    abstract TreeNode getNextNode();

    abstract void enqueue(TreeNode[] nodes);

    public void incrementExpndedNodes(){
        expandedNodes++;
    }
}
