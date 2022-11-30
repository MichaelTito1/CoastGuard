package code;

public abstract class SearchProblem {
    Object[] operators;
    Object initialState;//TODO class state a7san?
    Object[] stateSpace;


   abstract public boolean goalTest(TreeNode node);
   abstract public int[] pathCost(TreeNode node);
   abstract public TreeNode[] expand(TreeNode node);

   public static TreeNode genericSearchProcedure(SearchProblem problem,QingFun qf){
        qf.makeQ(problem.makeNode(problem));
        while(!qf.qIsEmpty()){
            TreeNode currentNode=qf.getNextNode();
            boolean test = problem.goalTest(currentNode);
            if(test){
                return currentNode;
            }

            qf.enqueue(problem.expand(currentNode));
            qf.incrementExpandedNodes();
        }
        return null;
   }
   abstract public TreeNode makeNode(SearchProblem problem);
}
