package src.code;

public abstract class SearchProblem {
    Object[] operators;
    Object[] initialState;
    Object[] stateSpace;


   abstract public boolean goalTest();
   abstract public int pathCost();
}
