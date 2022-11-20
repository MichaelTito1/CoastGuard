package code;

public abstract class QingFun {
    Object queue;

    abstract void makeQ(TreeNode root);

    abstract boolean qIsEmpty();

    abstract TreeNode getNextNode();

    abstract void enqueue(TreeNode[] nodes);
}
