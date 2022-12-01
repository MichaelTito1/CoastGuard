package code;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class BFS extends QingFun{

    public BFS(){
        queue=new LinkedList<TreeNode>();
        statesEnqueued=new HashSet<>();
    }

    @Override
    void makeQ(TreeNode root) {
        getQueue().add(root);
        statesEnqueued.add(root.state);
    }

    @Override
    boolean qIsEmpty() {
        return getQueue().isEmpty();
    }

    @Override
    TreeNode getNextNode() {
        return getQueue().remove();
    }

    @Override
    void enqueue(TreeNode[] nodes) {
        for (TreeNode node:
             nodes) {
            if(statesEnqueued.add(node.state)){
                getQueue().add(node);
            }
        }
    }

    private Queue<TreeNode> getQueue(){
        return (Queue<TreeNode>)queue;
    }
}
