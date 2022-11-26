package code;

import java.util.HashSet;
import java.util.PriorityQueue;

public class AS extends QingFun{

    Heuristic heuristic;

    public AS(Heuristic heuristic){
        this.heuristic=heuristic;
        queue=new PriorityQueue<TreeNode>((a, b)->{
            int aCost=heuristic.heuristicCost(a)+a.pathCost[0]*400+a.pathCost[1];
            int bCost=heuristic.heuristicCost(b)+b.pathCost[0]*400+b.pathCost[1];
            return aCost-bCost;
        });
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
