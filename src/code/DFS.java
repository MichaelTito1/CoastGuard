package code;

import java.util.HashSet;
import java.util.Stack;

public class DFS extends QingFun{

    public DFS(){
        queue=new Stack<TreeNode>();
        statesEnqueued=new HashSet<>();
    }
    @Override
    void makeQ(TreeNode root) {
        getQueue().push(root);
        statesEnqueued.add(root.state);
    }

    @Override
    boolean qIsEmpty() {
        return getQueue().isEmpty();
    }

    @Override
    TreeNode getNextNode() {
        return getQueue().pop();
    }

    @Override
    void enqueue(TreeNode[] nodes) {
        for (TreeNode node:
                nodes) {
            if(statesEnqueued.add(node.state)){
                getQueue().push(node);
            }
        }
    }
    private Stack<TreeNode> getQueue(){
        return (Stack<TreeNode>)queue;
    }
}
