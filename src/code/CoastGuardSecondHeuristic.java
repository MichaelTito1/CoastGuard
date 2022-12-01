package code;

public class CoastGuardSecondHeuristic implements Heuristic{
    @Override
    public int[] heuristicCost(TreeNode n) {
        CoastGuardTreeNode node=(CoastGuardTreeNode) n;
        Cell[][] grid=node.getState().grid;
        int[] cgLocation=node.getState().cgLocation;
        int closestDistance=Integer.MAX_VALUE;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if(grid[i][j].isShip()){
                    int blockDistance=getBlockDistance(i,j,cgLocation);
                    if(closestDistance>blockDistance){
                        closestDistance=blockDistance;
                    }
                }
            }
        }
        int estimatedDeaths=0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if(grid[i][j].isShip()){
                    Ship curShip=(Ship)grid[i][j];
                    estimatedDeaths+=Math.min(closestDistance,curShip.passengersAlive);
                }
            }
        }
        return new int[]{estimatedDeaths,0};
    }

    private int getBlockDistance(int i, int j, int[] cgLocation) {
        return Math.abs(i-cgLocation[0])+Math.abs(j-cgLocation[1]);
    }
}
