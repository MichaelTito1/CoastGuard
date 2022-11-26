package code;

import com.sun.source.tree.Tree;

import java.util.HashSet;
import java.util.Stack;

public class ID extends QingFun{
    TreeNode root;
    int curDepth;
    boolean overflow;
    public ID(){
        overflow=false;
        curDepth=0;
        queue=new Stack<TreeNode>();
        statesEnqueued=new HashSet<>();
    }
    @Override
    void makeQ(TreeNode root) {
        this.root=root;
        getQueue().push(root);
        statesEnqueued.add(root.state);
    }

    @Override
    boolean qIsEmpty() {
        if(getQueue().isEmpty()&&overflow){
            statesEnqueued.clear();
            getQueue().push(root);
            statesEnqueued.add(root.state);
            curDepth++;
            overflow=false;
        }
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
            if(node.depth>curDepth){
                overflow=true;
                continue;
            }
            if(!statesEnqueued.contains(node.state)){
                getQueue().push(node);
                statesEnqueued.add(node.state);
            }
        }
    }
    private Stack<TreeNode> getQueue(){
        return (Stack<TreeNode>)queue;
    }

}
