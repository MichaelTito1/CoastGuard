package code;

public class CoastGuardFirstHeuristic implements Heuristic{
    @Override
    public int[] heuristicCost(TreeNode n) {
        CoastGuardTreeNode node=(CoastGuardTreeNode) n;
        Cell[][] grid=CoastGuard.deserializeGrid(node.getState().grid);
        int[] cgLocation=CoastGuard.getIntTuplesFromString(node.getState().cgLocation);
        Ship closestShip=null;
        int closestDistance=Integer.MAX_VALUE;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if(grid[i][j].isShip()){
                    int blockDistance=getBlockDistance(i,j,cgLocation);
                    if(closestDistance>blockDistance){
                        closestDistance=blockDistance;
                        closestShip=(Ship)grid[i][j];
                    }
                }
            }
        }
        return new int[]{Math.min(closestDistance,closestShip.passengersAlive),0};
    }

    private int getBlockDistance(int i, int j, int[] cgLocation) {
        return Math.abs(i-cgLocation[0])+Math.abs(j-cgLocation[1]);
    }
}
