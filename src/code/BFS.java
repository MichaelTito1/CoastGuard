package code;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BFS extends QingFun{

    public BFS(){
        queue=new LinkedList<TreeNode>();
    }

    @Override
    void makeQ(TreeNode root) {
        getQueue().add(root);
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
        getQueue().addAll(Arrays.asList(nodes));
    }

    private Queue<TreeNode> getQueue(){
        return (Queue<TreeNode>)queue;
    }
}
