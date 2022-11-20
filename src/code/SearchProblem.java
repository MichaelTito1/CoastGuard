package code;

public abstract class SearchProblem {
    Object[] operators;
    Object[] initialState;//TODO class state a7san?
    Object[] stateSpace;


   abstract public boolean goalTest(TreeNode node);
   abstract public int pathCost(TreeNode node);
   abstract public TreeNode[] expand(TreeNode node);

   public static TreeNode genericSearchProblem(SearchProblem problem,QingFun qf){
        qf.makeQ(makeNode(problem));
        while(!qf.qIsEmpty()){
            TreeNode currentNode=qf.getNextNode();
            if(problem.goalTest(currentNode)){
                return currentNode;
            }
            qf.enqueue(problem.expand(currentNode));
        }
        return null;
   }
   public static TreeNode makeNode(SearchProblem problem){
        return new TreeNode(problem.initialState, null,null,0,0);
   }
}
