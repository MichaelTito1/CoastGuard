package code;

import java.util.ArrayList;
import java.util.Random;

public class CoastGuard extends SearchProblem{

    int maxCapacity;

    enum Operators {
        LEFT,RIGHT,UP,DOWN,PICKUP,DROP,RETRIEVE
    }

    public CoastGuard(){
        this(genGrid());
    }

    public CoastGuardState getInitialState(){
        return (CoastGuardState) initialState;
    }

    public CoastGuard(String generatedGrid){
        operators=new Operators[]{
                Operators.LEFT, Operators.RIGHT,
                Operators.UP,Operators.DOWN,
                Operators.PICKUP,Operators.DROP,
                Operators.RETRIEVE};
        //state: SerializedGrid,capacity,location
        initialState=new CoastGuardState();
        getInitialStateOfProblem(generatedGrid);
        maxCapacity= getInitialState().capacity;
    }

    /**
     * This method checks if the given node's state is a goal state
     * @param n the current node
     * @return true if it passes the goal test, false otherwise
     */
    @Override
    public boolean goalTest(TreeNode n) {
        CoastGuardTreeNode node=(CoastGuardTreeNode) n;
        // state = {grid, capacity, location}
        // if coast guard still has passengers on board, return false
        if(node.getState().capacity > 0)
            return false;
        
        int passengers = 0;
        int boxes = 0;
        String[][] curGrid = deserializeGrid(node.getState().grid);
        for (int i = 0; i < curGrid.length; i++) {
            for (int j = 0; j < curGrid[0].length; j++) {
                Object[] cell = deserializeCell(curGrid[i][j]);
                if(cell[0].equals("S")){ // check for passengers and box on ship
                    passengers = Integer.parseInt((String) cell[1]);
                    boxes = Integer.parseInt((String) cell[1]) > 0 ? 1 : 0;
                }
                if(passengers > 0 || boxes > 0) // if there are alive passengers or unretrieved boxes, return false
                    return false;
            }
        }
        return true;
    }

    /**
     * This method converts the string representation of a grid cell 
     * 
     * @param cell
     * @return
     */
    private Object[] deserializeCell(String cell){
        String[] cellInfo = cell.split(",");
        Object[] cellArr = new Object[cellInfo.length];
        String cellType = cellInfo[0];
        cellArr[0] = cellType;
        if(cellType.equals("S")){
            cellArr[1] = Integer.parseInt(cellInfo[1]); // num of passengers on board
            cellArr[2] = Integer.parseInt(cellInfo[2]); // box health on board
        }
        return cellArr;
    }

    private String serializeCell(Object[] cell){
        String[] strCell=new String[cell.length];
        for (int i = 0; i < cell.length; i++) {
            strCell[i]=cell[i].toString();
        }
        return String.join(",",strCell);
    }

    @Override
    public int[] pathCost(TreeNode n) {
        CoastGuardTreeNode node=(CoastGuardTreeNode) n;
        //getting position of coast guard
        String coastGuardPosition = node.getState().cgLocation;
        String [] coastGuardPositionSplitted = coastGuardPosition.split(",");
        int coastGuardX = Integer.parseInt(coastGuardPositionSplitted[0]);
        int coastGuardY = Integer.parseInt(coastGuardPositionSplitted[1]);

        int[] cost = new int[2];
        if(node.parent == null) //root node
            return cost;
        String[][] parentGrid = deserializeGrid(node.getParent().getState().grid); //parent Grid State
        String[][] nodeGrid = deserializeGrid(node.getState().grid); //node Grid State
        for(int i=0 ; i<nodeGrid.length ; i++){
            for(int j=0 ; j<nodeGrid[0].length ; j++){
                if(!(nodeGrid[i][j].equals("E") || (nodeGrid[i][j]).equals("I"))){
                        String[] shipInfo = parentGrid[i][j].split(",");
                        if(Integer.parseInt(shipInfo[1])>0){
                               cost[0] += 1; //m x n x 2
                        }
                        else if(Integer.parseInt(shipInfo[2])==1){
                            cost[1] += 1;
                        }
                }
            }
        }
        if(!(nodeGrid[coastGuardX][coastGuardY].equals("E") || (nodeGrid[coastGuardX][coastGuardY]).equals("I"))){
            String[] currentCoastGuardCell = nodeGrid[coastGuardX][coastGuardY].split(",");
            String[] parentCoastGuardCell = nodeGrid[coastGuardX][coastGuardY].split(","); //the previous state of the cell where the coast guard is in now
            if(node.operator == Operators.PICKUP && Integer.parseInt(currentCoastGuardCell[1])==0 && Integer.parseInt(parentCoastGuardCell[1])>0){
                cost[0] -= 1; //subtracts the one death calculated for the cell of coastguard
            }
            else if(node.operator == Operators.RETRIEVE && Integer.parseInt(parentCoastGuardCell[2])>0){
                cost[1] -= 1;
            }

        }
        node.pathCost = cost;
        return cost;
        // if(!node.state.equals(this.initialState[0])){
        //     return node.pathCost + pathCost(node.parent);
        // }
        //        // TODO Auto-generated method stub
        // return node.pathCost;
    }

    @Override
    public TreeNode[] expand(TreeNode n) {
        CoastGuardTreeNode node=(CoastGuardTreeNode) n;
        //get current state values
        String[][] grid = deserializeGrid(node.getState().grid);
        int capacity=node.getState().capacity;
        int[] cgLocation=getIntTuplesFromString(node.getState().cgLocation);

        ArrayList<TreeNode> expandedNodes=new ArrayList<>();

        //get nodes resulted from movement expansion
        expandedNodes.addAll(expandMovement(node,grid,capacity,cgLocation));

        if(canPickUp(grid,capacity,cgLocation))
            expandedNodes.add(expandPickup(grid,capacity,cgLocation,node));

        if(canDrop(grid,cgLocation))
            expandedNodes.add(expandDrop(grid,capacity,cgLocation,node));

        if(canRetrieve(grid,cgLocation))
            expandedNodes.add(expandRetrieve(grid,capacity,cgLocation,node));

        for (TreeNode cur:
             expandedNodes) {
            cur.pathCost=pathCost(cur);
        }
        return expandedNodes.toArray(new TreeNode[0]);
    }

    private CoastGuardTreeNode expandRetrieve(String[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        String[][] newStateGrid=getNextMovementGridState(grid);
        Object[] cell=deserializeCell(grid[cgLocation[0]][cgLocation[1]]);
        cell[2]=-1;
        newStateGrid[cgLocation[0]][cgLocation[1]]=serializeCell(cell);
        //TODO fix this to make location in state int[] mara wa7da
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,cgLocation[0]+","+cgLocation[1]),node,Operators.RETRIEVE, node.depth+1);
    }

    private boolean canRetrieve(String[][] grid, int[] cgLocation) {
        Object[] cell=deserializeCell(grid[cgLocation[0]][cgLocation[1]]);
        if(!cell[0].equals("S")||(int)cell[2]<=0)
            return false;
        return true;
    }

    private CoastGuardTreeNode expandDrop(String[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        String[][] newStateGrid=getNextMovementGridState(grid);
        capacity=maxCapacity;
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,cgLocation[0]+","+cgLocation[1]),node,Operators.DROP, node.depth+1);
    }

    private boolean canDrop(String[][] grid, int[] cgLocation) {
        Object[] cell=deserializeCell(grid[cgLocation[0]][cgLocation[1]]);
        return cell[0].equals("I");
    }

    private CoastGuardTreeNode expandPickup(String[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        String[][] newStateGrid=getNextMovementGridState(grid);
        Object[] cell=deserializeCell(grid[cgLocation[0]][cgLocation[1]]);
        cell[1]=Math.max(0,(int)cell[1]-capacity);
        capacity=Math.max(0,capacity-(int)cell[1]);
        newStateGrid[cgLocation[0]][cgLocation[1]]=serializeCell(cell);
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,cgLocation[0]+","+cgLocation[1]),node,Operators.PICKUP, node.depth+1);
    }

    private boolean canPickUp(String[][] grid, int capacity, int[] cgLocation) {
        if(capacity==0)
            return false;
        Object[] cell=deserializeCell(grid[cgLocation[0]][cgLocation[1]]);
        if(!cell[0].equals("S")||(int)cell[1]==0)
            return false;
        return true;
    }

    private ArrayList<TreeNode> expandMovement(CoastGuardTreeNode parent,String[][] grid,int capacity, int[] cgLocation) {
        ArrayList<TreeNode> res=new ArrayList<>();

        String[][] newStateGrid= getNextMovementGridState(grid);

        res.add(expandMovementUp(newStateGrid,capacity,cgLocation,parent));
        res.add(expandMovementDown(newStateGrid,capacity,cgLocation,parent));
        res.add(expandMovementLeft(newStateGrid,capacity,cgLocation,parent));
        res.add(expandMovementRight(newStateGrid,capacity,cgLocation,parent));
        return res;
    }

    private CoastGuardTreeNode expandMovementUp(String[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] upLocation;
        if(cgLocation[1]==0)
            upLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            upLocation= new int[]{cgLocation[0],cgLocation[1]-1};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,upLocation[0]+","+upLocation[1]),parent,Operators.UP,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementDown(String[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] downLocation;
        if(cgLocation[1]==newStateGrid[0].length-1)
            downLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            downLocation= new int[]{cgLocation[0],cgLocation[1]+1};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,downLocation[0]+","+downLocation[1]),parent,Operators.DOWN,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementLeft(String[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] leftLocation;
        if(cgLocation[0]==0)
            leftLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            leftLocation= new int[]{cgLocation[0]-1,cgLocation[1]};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,leftLocation[0]+","+leftLocation[1]),parent,Operators.LEFT,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementRight(String[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] rightLocation;
        if(cgLocation[0]==newStateGrid.length-1)
            rightLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            rightLocation= new int[]{cgLocation[0]+1,cgLocation[1]};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,rightLocation[0]+","+rightLocation[1]),parent,Operators.RIGHT,parent.depth+1);

    }

    private String[][] getNextMovementGridState(String[][] grid) {
        String[][] newGridState=new String[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Object[] cell=deserializeCell(grid[i][j]);
                if(cell[0].equals("S")){
                    if((int)cell[1]==0){
                        if((int)cell[2]>0)
                            cell[2]=(int)cell[2]-1;
                    }else{
                        cell[1]=(int)cell[1]-1;
                    }
                }
                newGridState[i][j]=serializeCell(cell);
            }
        }
        return newGridState;
    }
    
    private static int[] getIntTuplesFromString(String firstPortion){
        String[] firstSplitted = firstPortion.split(",");
        int m = Integer.parseInt(firstSplitted[0]);
        int n = Integer.parseInt(firstSplitted[1]);
        return(new int[]{m,n});
    }

    public void getInitialStateOfProblem(String grid){
      String[] splitted = grid.split(";");
      int[] dimensions = getIntTuplesFromString(splitted[0]);
      String[][] parsedGrid = new String[dimensions[1]][dimensions[0]];
      int maxCapacity = Integer.parseInt(splitted[1]);
      getInitialState().cgLocation = splitted[2];
      getInitialState().capacity = maxCapacity;
      putStationsInGrid(splitted[3],parsedGrid);
      putShipsInGridInitial(splitted[4],parsedGrid);
      removeNullsFromGrid(parsedGrid);
      getInitialState().grid=serializeGrid(parsedGrid);
      System.out.println("serializeGrid = " + getInitialState().grid);
    }



    private static void putStationsInGrid(String stations, String[][]parsedGrid) {
        boolean comma = false;
        String x = "";
        String y = "";
        for(int i=0 ; i<stations.length() ; i++){
            if(stations.charAt(i)==','){
                if(comma){
                    int xVal = Integer.parseInt(x);
                    int yVal = Integer.parseInt(y);
                    parsedGrid[xVal][yVal] = "I";
                    x = "";
                    y = "";
                }
                comma = !comma;
            }
            else if(!comma)
                x += stations.charAt(i);
            else
                y += stations.charAt(i);
        }
        int xVal = Integer.parseInt(x);
        int yVal = Integer.parseInt(y);
        parsedGrid[xVal][yVal] = "I";
    }

    private static void putShipsInGrid(String ships, String[][] parsedGrid) {
        // string ships = {x,y,pass,boxHealth}*
        String[] shipInfo = ships.split(",");
        for (int i = 0; i < shipInfo.length-4; i+=4) {
            int x = Integer.parseInt(shipInfo[i]);
            int y = Integer.parseInt(shipInfo[i+1]);
            int numPassengers = Integer.parseInt(shipInfo[i+2]);
            int boxHealth = Integer.parseInt(shipInfo[i+3]);
            parsedGrid[x][y] = "S," + numPassengers + "," + boxHealth;
        }
    }

    private static void putShipsInGridInitial(String ships, String[][] parsedGrid) {
        int comma = 0;
        String x = "";
        String y = "";
        String passengers = "";
        for(int i=0 ; i<ships.length() ; i++){
            if(ships.charAt(i)==','){
                if(comma == 2){
                   comma = 0;
                   int xVal = Integer.parseInt(x);
                   int yVal = Integer.parseInt(y);
                   parsedGrid[xVal][yVal] = "S"+","+passengers+",20";
                   x = "";
                   y = "";
                   passengers = "";
                }
                else 
                   comma++;
            }
            else if(comma == 0)
                x += ships.charAt(i);
            else if(comma == 1)
                y += ships.charAt(i);
            else
                passengers += ships.charAt(i);
        }
        int xVal = Integer.parseInt(x);
        int yVal = Integer.parseInt(y);
        parsedGrid[xVal][yVal] = "S"+","+passengers+",20";
    }

    public static String genGrid(){
        Random rand = new Random();

        int m = rand.nextInt(11)+5;
        // int m = rand.nextInt(5)+1;
        int n = rand.nextInt(11)+5;
        // int n = rand.nextInt(5)+1;
        // Cell string format: "type{;numOfPassengers;BoxHealth}", where:
        // {;numOfPassengers;BoxHealth} is added if type is ship
        String [][] grid = new String[n][m];

        // generating coast guard's data
        int maxCapacity = rand.nextInt(71)+30;
        int cgX = rand.nextInt(n), cgY = rand.nextInt(m);

        // generating ships randomly
        int numShips = rand.nextInt(n*m-1) + 1;
        for (int i = 0; i < numShips; i++) {
            do{
                int x = rand.nextInt(n), y = rand.nextInt(m);
                if(grid[x][y] == null){
                    int numPassengers = rand.nextInt(100)+1;
                    grid[x][y] = "S;"+numPassengers+";20";
                    break;
                }
            }while(true);
        }

        //generating stations randomly
        // NOTE: there is no upper limit for the number of ships or stations generated as long as no 2 objects occupy the same cell
        int numStations = rand.nextInt(n*m-numShips)+1;
        for (int i = 0; i < numStations; i++) {
            do{
                int x = rand.nextInt(n), y = rand.nextInt(m);
                if(grid[x][y] == null){
                    grid[x][y] = "I";
                    break;
                }
            }while(true);
        }

        // converting generated grid to string
        return convertGridToString(grid, maxCapacity, cgX, cgY);
    }

    /**
     * Converts the given grid to a specific string format specified in the project description.
     *
     * @param grid a 2D string array representing the grid itself
     * @param maxCapacity maximum capacity of the coast guard
     * @param cgX initial x coordinate of the coast guard on the grid
     * @param cgY initial y coordinate of the coast guard on the grid
     * @return the grid in its string format
     */
    private static String convertGridToString(String[][] grid, int maxCapacity, int cgX, int cgY) {
        // 1. general info
        int n=grid.length;
        int m=grid[0].length;
        String gridString = m + "," + n + ";" + maxCapacity + ";" + cgX + "," + cgY + ";";

        // 2. get ships and stations info from the 2D grid
        String stationString = "";
        String shipString = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] != null) {
                    String[] cell = grid[i][j].split(";");

                    if (cell[0].equals("I"))
                        stationString += i + "," + j + ",";
                    else if (cell[0].equals("S")) {
                        shipString += i + "," + j + "," + cell[1] + ",";
                    }
                }
            }
        }
        // 3. adjust format of strings
        stationString = stationString.substring(0, stationString.length() - 1) + ";";
        shipString = shipString.substring(0, shipString.length() - 1);

        // 4. add info to grid string and return
        gridString += stationString + shipString;
        return gridString;
    }

    private static String[][] deserializeGrid(String serializedString){
        String[] splitted = serializedString.split(";");
        int[] dimensions = getIntTuplesFromString(splitted[0]);
        String[][] stateParsedGrid = new String[dimensions[1]][dimensions[0]];
        putStationsInGrid(splitted[1],stateParsedGrid);
        putShipsInGrid(splitted[2],stateParsedGrid);
        removeNullsFromGrid(stateParsedGrid);
        return stateParsedGrid;
    }

    /**
     * This method converts the 2D grid to a serialized format in a string. 
     * All cell information (including box health) are serialized. 
     * @param grid 2D string array containing information for each cell
     * @return string serialization of grid
     */
    private static String serializeGrid(String[][] grid){
        int n=grid.length;
        int m=grid[0].length;
        // 2. get ships and stations info from the 2D grid
        String stationString = "";
        String shipString = "";
        String gridString = m+","+n+';';
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!(grid[i][j].equals("E"))) {
                    String[] cell = grid[i][j].split(",");

                    if (cell[0].equals("I"))
                        stationString += i + "," + j + ",";
                    else if (cell[0].equals("S")) {
                        shipString += i + "," + j + "," + cell[1] + ","+cell[2] + ",";
                    }
                }
            }
        }
        // 3. adjust format of strings
        stationString = stationString.substring(0, stationString.length() - 1) + ";";
        shipString = shipString.substring(0, shipString.length() - 1);

        // 4. add info to grid string and return
        gridString += stationString + shipString;
        return gridString;
    }

    private void printState(CoastGuardState state) {
        String parsedGrid= state.grid;
        String[][] arr = deserializeGrid(parsedGrid);
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.print(arr[i][j] + ' ');
            }
            System.out.println();
        }
        
        String location= state.cgLocation;
        int capacity= state.capacity;

        System.out.println(parsedGrid);
        System.out.println("Coast guard : " + location + " and " + capacity);
    }

    private static void removeNullsFromGrid(String[][] grid){
        for(int i=0 ; i<grid.length ; i++){
            for(int j=0 ; j<grid[0].length ; j++){
                if(grid[i][j]==null)
                   grid[i][j] = "E";
            }
        }
    }

    // Driver code
    public static void main(String[] args) {
        String s = genGrid();
        System.out.println(s);
        CoastGuard cg = new CoastGuard(s);
        cg.printState(cg.getInitialState());

        // String[][] toPrintGrid = deserializeGrid(s);
        // for (int i = 0; i < toPrintGrid.length; i++) {
        //     for (int j = 0; j < toPrintGrid[0].length; j++) {
        //         System.out.print(toPrintGrid[i][j] + " ");
        //     }
        //     System.out.println();
        // }
    }
}
