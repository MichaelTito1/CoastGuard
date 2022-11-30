package code;

import java.util.HashSet;
import java.util.PriorityQueue;

public class UC extends QingFun {
    public UC(){
        queue=new PriorityQueue<TreeNode>((a, b)->{
            int[] aCost=a.pathCost;
            int[] bCost=b.pathCost;
            if(aCost[0]==bCost[0])
                return aCost[1]-bCost[1];
            return aCost[0]-bCost[0];
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
