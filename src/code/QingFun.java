package code;

import java.util.HashSet;

public abstract class QingFun {
    Object queue;
    HashSet<Object> statesEnqueued;
    long expandedNodes;

    abstract void makeQ(TreeNode root);

    abstract boolean qIsEmpty();

    abstract TreeNode getNextNode();

    abstract void enqueue(TreeNode[] nodes);

    public void incrementExpandedNodes(){
        expandedNodes++;
    }
}
