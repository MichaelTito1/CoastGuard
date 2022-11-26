package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    @Override
     public TreeNode makeNode(SearchProblem problem){
        return new CoastGuardTreeNode(((CoastGuard)problem).getInitialState(), null,null,0,new int[2]);
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
        //System.out.println("capacity = " + node.getState().capacity);
        //System.out.println("maxCapacity = " + maxCapacity);
        if(node.getState().capacity != maxCapacity)
            return false;
        
        //printState(node.getState());
        Cell[][] curGrid = deserializeGrid(node.getState().grid);
        for (int i = 0; i < curGrid.length; i++) {
            for (int j = 0; j < curGrid[0].length; j++) {
                Cell cell = curGrid[i][j];
                if(cell.isShip()){ // check for passengers and box on ship
                    Ship ship = (Ship) cell; 
                    //System.out.println(ship.passengersAlive);
                    if(ship.passengersAlive > 0 || (ship.boxHealth > 0 && !ship.boxRetrieved) )
                        return false;
                }
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
    // private Object[] deserializeCell(Cell cell){
    //     String[] cellInfo = cell.split(",");
    //     Object[] cellArr = new Object[cellInfo.length];
    //     String cellType = cellInfo[0];
    //     cellArr[0] = cellType;
    //     if(cellType.equals("S")){
    //         cellArr[1] = Integer.parseInt(cellInfo[1]); // num of passengers on board
    //         cellArr[2] = Integer.parseInt(cellInfo[2]); // box health on board
    //     }
    //     return cellArr;
    // }

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
        // String coastGuardPosition = node.getState().cgLocation;
        // String [] coastGuardPositionSplitted = coastGuardPosition.split(",");
        // int coastGuardX = Integer.parseInt(coastGuardPositionSplitted[0]);
        // int coastGuardY = Integer.parseInt(coastGuardPositionSplitted[1]);

        int[] cost = new int[2];
        if(node.parent == null) //root node
            return cost;
        // Cell[][] parentGrid = deserializeGrid(node.getParent().getState().grid); //parent Grid State
        Cell[][] nodeGrid = deserializeGrid(node.getState().grid); //node Grid State
        for(int i=0 ; i<nodeGrid.length ; i++){
            for(int j=0 ; j<nodeGrid[0].length ; j++){
                if(nodeGrid[i][j].isShip()){
                        Ship shipInfo = (Ship) nodeGrid[i][j];
                        // if(shipInfo.passengersAlive>0){
                        //        cost[0] += 1; //m x n x 2
                        // }
                        // else if(shipInfo.boxHealth==1 && !shipInfo.boxRetrieved){ // TODO: Is there a better way to check for boxes that are about to be destroyed????
                        //     cost[1] += 1;
                        // }
                        cost[0] += shipInfo.deadPassengers;
                        if(!shipInfo.boxRetrieved && shipInfo.boxHealth <= 0) 
                            cost[1] += 1;
                }
            }
        }
        // TODO: is there a better way to check for correctness of cost?
        // if(nodeGrid[coastGuardX][coastGuardY].isShip()){
        //     Ship currentCoastGuardCell = (Ship) nodeGrid[coastGuardX][coastGuardY];
        //     Ship parentCoastGuardCell = (Ship) parentGrid[coastGuardX][coastGuardY]; //the previous state of the cell where the coast guard is in now
        //     if(node.operator == Operators.PICKUP && currentCoastGuardCell.passengersAlive==0 && parentCoastGuardCell.passengersAlive>0){
        //         cost[0] -= 1; //subtracts the one death calculated for the cell of coastguard
        //     }
        //     else if(node.operator == Operators.RETRIEVE && parentCoastGuardCell.boxHealth > 0){
        //         cost[1] -= 1;
        //     }

        // }
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
        Cell[][] grid = deserializeGrid(node.getState().grid);
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

    // TODO: Revise box retrieval mechanism. Check in pathcost that boxRetrieved and boxHealth
    private CoastGuardTreeNode expandRetrieve(Cell[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        Cell[][] newStateGrid=getNextMovementGridState(grid);
        Ship cell=( (Ship) grid[cgLocation[0]][cgLocation[1]]).clone();
        cell.boxRetrieved = true;
        newStateGrid[cgLocation[0]][cgLocation[1]]= cell;
        //TODO: fix this to make location in state int[] mara wa7da
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,cgLocation[0]+","+cgLocation[1]),node,Operators.RETRIEVE, node.depth+1);
    }

    private boolean canRetrieve(Cell[][] grid, int[] cgLocation) {
        Cell cell= grid[cgLocation[0]][cgLocation[1]];
        if(!cell.isShip()|| ((Ship)cell).boxHealth <= 0 || ((Ship)cell).boxRetrieved)
            return false;
        return true;
    }

    private CoastGuardTreeNode expandDrop(Cell[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        Cell[][] newStateGrid=getNextMovementGridState(grid);
        capacity=maxCapacity;
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,cgLocation[0]+","+cgLocation[1]),node,Operators.DROP, node.depth+1);
    }

    private boolean canDrop(Cell[][] grid, int[] cgLocation) {
        Cell cell=  grid[cgLocation[0]][cgLocation[1]];
        return cell.isStation();
    }

    private CoastGuardTreeNode expandPickup(Cell[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        Cell[][] newStateGrid=getNextMovementGridState(grid);
        Ship cell= ((Ship) grid[cgLocation[0]][cgLocation[1]]).clone();
        int passengers2 = cell.passengersAlive;
        cell.passengersAlive=Math.max(0, passengers2 - capacity);
        capacity=Math.max(0,capacity-passengers2);
        newStateGrid[cgLocation[0]][cgLocation[1]]= cell;
        //System.out.println(newStateGrid[cgLocation[0]][cgLocation[1]].toString());
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,cgLocation[0]+","+cgLocation[1]),node,Operators.PICKUP, node.depth+1);
    }

    private boolean canPickUp(Cell[][] grid, int capacity, int[] cgLocation) {
        if(capacity==0)
            return false;
        Cell cell= grid[cgLocation[0]][cgLocation[1]];
        if(!cell.isShip() || ((Ship)cell).passengersAlive ==0)
            return false;
        return true;
    }

    private ArrayList<TreeNode> expandMovement(CoastGuardTreeNode parent,Cell[][] grid,int capacity, int[] cgLocation) {
        ArrayList<TreeNode> res=new ArrayList<>();

        Cell[][] newStateGrid= getNextMovementGridState(grid);

        res.add(expandMovementUp(newStateGrid,capacity,cgLocation,parent));
        res.add(expandMovementDown(newStateGrid,capacity,cgLocation,parent));
        res.add(expandMovementLeft(newStateGrid,capacity,cgLocation,parent));
        res.add(expandMovementRight(newStateGrid,capacity,cgLocation,parent));
        return res;
    }

    private CoastGuardTreeNode expandMovementUp(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] upLocation;
        if(cgLocation[0]==0)
            upLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            upLocation= new int[]{cgLocation[0]-1,cgLocation[1]};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,upLocation[0]+","+upLocation[1]),parent,Operators.UP,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementDown(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] downLocation;
        if(cgLocation[0]==newStateGrid.length-1)
            downLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            downLocation= new int[]{cgLocation[0]+1,cgLocation[1]};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,downLocation[0]+","+downLocation[1]),parent,Operators.DOWN,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementLeft(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] leftLocation;
        if(cgLocation[1]==0)
            leftLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            leftLocation= new int[]{cgLocation[0],cgLocation[1]-1};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,leftLocation[0]+","+leftLocation[1]),parent,Operators.LEFT,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementRight(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] rightLocation;
        if(cgLocation[1]==newStateGrid[0].length-1)
            rightLocation = new int[]{cgLocation[0],cgLocation[1]};
        else
            rightLocation= new int[]{cgLocation[0],cgLocation[1]+1};
        return new CoastGuardTreeNode(new CoastGuardState(serializeGrid(newStateGrid),capacity,rightLocation[0]+","+rightLocation[1]),parent,Operators.RIGHT,parent.depth+1);

    }

    private Cell[][] getNextMovementGridState(Cell[][] grid) {
        Cell[][] newGridState=new Cell[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Cell cell= grid[i][j];
                newGridState[i][j]= cell;
                if(cell.isShip()){
                    Ship ship = ((Ship) cell).clone();
                    if(ship.passengersAlive==0){
                        if(ship.boxHealth>0 && !ship.boxRetrieved)
                            ship.boxHealth--;
                    }else{
                        ship.killPassenger();
                    }
                    newGridState[i][j]= ship;
                }
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
      Cell[][] parsedGrid = new Cell[dimensions[1]][dimensions[0]];
      int maxCapacity = Integer.parseInt(splitted[1]);
      getInitialState().cgLocation = splitted[2];
      getInitialState().capacity = maxCapacity;
      putStationsInGrid(splitted[3],parsedGrid);
      putShipsInGridInitial(splitted[4],parsedGrid);
      removeNullsFromGrid(parsedGrid);
      getInitialState().grid=serializeGrid(parsedGrid);
      //System.out.println("serializeGrid = " + getInitialState().grid);
    }



    private static void putStationsInGrid(String stations, Cell[][]parsedGrid) {
        boolean comma = false;
        String x = "";
        String y = "";
        for(int i=0 ; i<stations.length() ; i++){
            if(stations.charAt(i)==','){
                if(comma){
                    int xVal = Integer.parseInt(x);
                    int yVal = Integer.parseInt(y);
                    parsedGrid[xVal][yVal] = new Station();
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
        parsedGrid[xVal][yVal] = new Station();
    }

    private static void putShipsInGrid(String ships, Cell[][] parsedGrid) {
        // string ships = {x,y,pass,boxHealth}*
        String[] shipInfo = ships.split(",");
        for (int i = 0; i < shipInfo.length-5; i+=6) {
            int x = Integer.parseInt(shipInfo[i]);
            int y = Integer.parseInt(shipInfo[i+1]);
            int passengersAlive = Integer.parseInt(shipInfo[i+2]);
            int deadPassengers = Integer.parseInt(shipInfo[i+3]);
            int boxHealth = Integer.parseInt(shipInfo[i+4]);
            boolean boxRetrieved=shipInfo[i+5].equals("t")?true:false;
            parsedGrid[x][y] = new Ship(passengersAlive,deadPassengers, boxHealth,boxRetrieved);
        }
    }

    private static void putShipsInGridInitial(String ships, Cell[][] parsedGrid) {
       // System.out.println(ships);
        String[] shipInfo = ships.split(",");
        for (int i = 0; i < shipInfo.length-2; i+=3) {
            int x = Integer.parseInt(shipInfo[i]);
            int y = Integer.parseInt(shipInfo[i+1]);
            int numPassengers = Integer.parseInt(shipInfo[i+2]);
            //int boxHealth = Integer.parseInt(shipInfo[i+3]);
            parsedGrid[x][y] = new Ship(numPassengers, 20);
        }
        // for(int i=0 ; i<parsedGrid.length ; i++){
        //     for(int j=0 ; j<parsedGrid[0].length ; j++){
        //         System.out.print(parsedGrid[i][j] + " ");
        //     }
        //     System.out.println();
        // }
        // int comma = 0;
        // String x = "";
        // String y = "";
        // String passengers = "";
        // for(int i=0 ; i<ships.length() ; i++){
        //     if(ships.charAt(i)==','){
        //         if(comma == 2){
        //            comma = 0;
        //            int xVal = Integer.parseInt(x);
        //            int yVal = Integer.parseInt(y);
        //            parsedGrid[xVal][yVal] = new Ship(Integer.parseInt(passengers));
        //            x = "";
        //            y = "";
        //            passengers = "";
        //         }
        //         else 
        //            comma++;
        //     }
        //     else if(comma == 0)
        //         x += ships.charAt(i);
        //     else if(comma == 1)
        //         y += ships.charAt(i);
        //     else
        //         passengers += ships.charAt(i);
        // }
        // int xVal = Integer.parseInt(x);
        // int yVal = Integer.parseInt(y);
        // parsedGrid[xVal][yVal] = new Ship(Integer.parseInt(passengers));
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

    /**
     * this method expects a serialized string representing the grid, and returns the cell converted to a 2D array of type Cell
     * @param serializedString the grid is serialized in the following format: "m,n;[stationX,stationY]*;[shipX,shipY,numPassengers,box]*"
     */
    private static Cell[][] deserializeGrid(String serializedString){
        String[] splitted = serializedString.split(";");
        int[] dimensions = getIntTuplesFromString(splitted[0]);
        Cell[][] stateParsedGrid = new Cell[dimensions[1]][dimensions[0]];
        putStationsInGrid(splitted[1],stateParsedGrid);
        putShipsInGrid(splitted[2],stateParsedGrid);
        removeNullsFromGrid(stateParsedGrid);
        return stateParsedGrid;
    }

    /**
     * This method converts the 2D grid to a serialized format in a string. 
     * All cell information (including box health) are serialized. 
     * @param grid 2D string array containing information for each cell
     * @return string serialization of grid in the following format: "m,n;[stationX,stationY]*;[shipX,shipY,numPassengers,box]*"
     */
    private static String serializeGrid(Cell[][] grid){
        int n=grid.length;
        int m=grid[0].length;
        // 2. get ships and stations info from the 2D grid
        String stationString = "";
        String shipString = "";
        String gridString = m+","+n+';';
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!grid[i][j].isEmpty()) {
                    // String[] cell = grid[i][j].split(",");
                    Cell cell = grid[i][j];
                    if (cell.isStation()) 
                        stationString += i + "," + j + ",";
                    else if (cell.isShip()) {
                        shipString += i + "," + j + ","
                                    + ((Ship)cell).passengersAlive + ","
                                    +((Ship)cell).deadPassengers + ","
                                    + ((Ship)cell).boxHealth + ","
                                    + (((Ship)cell).boxRetrieved?"t":"f") + ",";
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
        Cell[][] arr = deserializeGrid(parsedGrid);
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.print(arr[i][j].toString() + ' ');
            }
            System.out.println();
        }
        
        String location= state.cgLocation;
        int capacity= state.capacity;

        System.out.println(parsedGrid);
        System.out.println("Coast guard : " + location + " and " + capacity);
    }

    private static void removeNullsFromGrid(Cell[][] grid){
        for(int i=0 ; i<grid.length ; i++){
            for(int j=0 ; j<grid[0].length ; j++){
                if(grid[i][j]==null)
                   grid[i][j] = new Empty();
            }
        }
    }

    public static String solve(String grid, String strategy , boolean visualize){
          CoastGuard cg = new CoastGuard(grid);
          QingFun qf = parseStrategy(strategy);
          CoastGuardTreeNode cgt = (CoastGuardTreeNode) genericSearchProcedure(cg, qf);
          CoastGuardTreeNode[] nodePath = getAllParentNodes(cgt);
          Operators[] operators = getAllParentsOperations(nodePath);
          String nodeState = cgt.getState().grid;
          int boxesRetrieved = getRetrievedBoxes(nodeState);
          int deathsCases = cgt.pathCost[0];
          long expandedNodes=qf.expandedNodes;
          if(visualize){
              visualize(nodePath);
          }
          return constructSolveOutput(operators,deathsCases,boxesRetrieved,expandedNodes);
    }

    private static String constructSolveOutput(Operators[] operators, int deathsCases, int boxesRetrieved,
                long expandedNodes) {
                String output = "";
                for(int i=0 ; i<operators.length-1 ; i++ ){
                    output+= operators[i] + ",";
                }
                output += operators[operators.length -1];
                output += ";" + deathsCases + ";" + boxesRetrieved + ";" + expandedNodes;
                return output;
    }

    private static int getRetrievedBoxes(String nodeState) {
        int boxes = 0;
        Cell[][] nodeGrid = deserializeGrid(nodeState);
        for(int i=0 ; i<nodeGrid.length ; i++){
            for(int j=0 ; j<nodeGrid[0].length ; j++){
                if(nodeGrid[i][j].isShip()){
                    Ship ship = (Ship) nodeGrid[i][j];
                    if(ship.boxRetrieved)
                      boxes++;
                }
            }
        }
        return boxes;
    }

    private static CoastGuardTreeNode[] getAllParentNodes(CoastGuardTreeNode cgt) {
          ArrayList<CoastGuardTreeNode> nodes = new ArrayList<>();
          while(cgt != null){
            nodes.add(cgt);
            cgt = cgt.getParent();
          }
          CoastGuardTreeNode[] cgtn = new CoastGuardTreeNode[nodes.size()];
          for(int i=0 ; i<nodes.size() ; i++){
            cgtn[i] = nodes.get(nodes.size()-1-i);
          }
          return cgtn;
          //Collections.reverse(nodes);
          //return (CoastGuardTreeNode[]) nodes.toArray();
    }

    private static Operators[] getAllParentsOperations(CoastGuardTreeNode[] cgt) {
        Operators[] operators = new Operators[cgt.length-1];
        for(int i=1 ; i<cgt.length ; i++){
           operators[i-1] = (Operators) cgt[i].operator;
        }
        return operators;
    }

    public static QingFun parseStrategy(String strategy){
        switch (strategy) {
            case "BF":
                return new BFS();
            case "DF":
                return new DFS();
            case "ID":
                return new ID();
            default:
                throw new NullPointerException();
        }
    }

    /**
     * This method visualizes the given grid in the console.
     * @param grid a 2D array of type Cell, representing the array.
     * @param cgLocation
     */
    public static void visualize(Cell[][] grid, String cgLocation){
        int n = grid.length;
        int m = grid[0].length;
        String form = "", formCgPos = "";
        int[] cgCoordinates = getIntTuplesFromString(cgLocation);
        int x = cgCoordinates[0], y = cgCoordinates[1];
        for (int i = 0; i < m; i++) {
            form += "%15s";
            if(i == y)
                formCgPos += "[%13s]";
            else
                formCgPos += "%15s";
        }
        form += "%n";
        for (int i = 0; i < n; i++) {
            Cell [] cells = grid[i];
            if(i == x){
                System.out.format(formCgPos, cells);
                System.out.println();
            }else
                System.out.format(form, cells);
        }
    }
    
    /**
     * This method visualizes the given grid in the console.
     * @param gridStr a string representing the array. It has the following format: "m,n;[stationX,stationY]*;[shipX,shipY,numPassengers,box]*"
     * @param cgLocation
     */
    public static void visualize(String gridStr, String cgLocation){
        Cell[][] grid = deserializeGrid(gridStr);
        visualize(grid, cgLocation);
    }
    
    /**
     * This method visualizes the given node in the console.
     * @param node
     */
    public static void visualize(CoastGuardTreeNode node){
        CoastGuardState state = node.getState();
        System.out.println("Coast Guard Position: "+node.getState().cgLocation);
        if(node.parent!=null){
            System.out.println("Operator: "+node.operator + ", " + "Path cost: "+Arrays.toString(node.pathCost)
                    + ", " +"depth: "+ node.depth);
        }
        visualize(state.grid, state.cgLocation);
        System.out.println("=================================");
    }

    private static void visualize(CoastGuardTreeNode[] nodePath) {
        for (CoastGuardTreeNode cur: nodePath
             ) {
            visualize(cur);
        }
    }


    // Driver code
    public static void main(String[] args) {
        //String s = genGrid();
        //System.out.println(s);
        // BFS bfs = new BFS();
        //CoastGuard cg = new CoastGuard(s);
        // cg.printState(cg.getInitialState());
        // CoastGuardTreeNode node = (CoastGuardTreeNode) CoastGuard.genericSearchProcedure(cg,bfs);
        //System.out.println(CoastGuard.solve("3,4;97;1,2;0,1;3,2,65;", "BF", true));
        System.out.println(CoastGuard.solve("3,4;97;1,2;0,1;3,2,65;", "ID", true));
        //System.out.println(CoastGuard.solve("3,4;97;1,2;0,1;3,2,65;", "DF", false));

        // System.out.println(CoastGuard.solve(genGrid(), "BF", false));

        // testing visualize
        // String g = "3,4;0,1;3,2,65,20";
        //CoastGuard.visualize(cg.getInitialState().grid);
    }
}
