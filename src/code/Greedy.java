package code;

import java.util.HashSet;
import java.util.PriorityQueue;

public class Greedy extends QingFun{
    Heuristic heuristic;
    public Greedy(Heuristic heuristic){
        this.heuristic=heuristic;
        queue=new PriorityQueue<TreeNode>((a,b)->{return heuristic.heuristicCost(a)- heuristic.heuristicCost(b);});
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
            if(!statesEnqueued.contains(node.state)){
                getQueue().add(node);
                statesEnqueued.add(node.state);
            }
        }
    }
    private PriorityQueue<TreeNode> getQueue(){
        return (PriorityQueue<TreeNode>) queue;
    }
}
