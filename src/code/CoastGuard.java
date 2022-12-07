package code;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.*;

public class CoastGuard extends SearchProblem{

    static int maxCapacity;

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
        if(node.getState().capacity != maxCapacity)
            return false;
        
            
        Cell[][] curGrid = node.getState().grid;
        for (int i = 0; i < curGrid.length; i++) {
            for (int j = 0; j < curGrid[0].length; j++) {
                Cell cell = curGrid[i][j];
                if(cell.isShip()){ // check for passengers and box on ship
                    Ship ship = (Ship) cell; 
                    
                    if(ship.passengersAlive > 0 || (ship.boxHealth > 0 && !ship.boxRetrieved) )
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    public int[] pathCost(TreeNode n) {
        CoastGuardTreeNode node=(CoastGuardTreeNode) n;
        int[] cost = new int[2];
        if(node.parent == null) //root node
            return cost;
        Cell[][] nodeGrid = node.getState().grid; //node Grid State
        for(int i=0 ; i<nodeGrid.length ; i++){
            for(int j=0 ; j<nodeGrid[0].length ; j++){
                if(nodeGrid[i][j].isShip()){
                        Ship shipInfo = (Ship) nodeGrid[i][j];
                        cost[0] += shipInfo.deadPassengers;
                        if(!shipInfo.boxRetrieved && shipInfo.boxHealth <= 0) 
                            cost[1] += 1;
                }
            }
        }
        node.pathCost = cost;
        return cost;
    }

    @Override
    public TreeNode[] expand(TreeNode n) {
        CoastGuardTreeNode node=(CoastGuardTreeNode) n;
        //get current state values
        Cell[][] grid = node.getState().grid;
        int capacity=node.getState().capacity;
        final int[] cgLocation=node.getState().cgLocation;

        ArrayList<TreeNode> expandedNodes=new ArrayList<>();

        //get nodes resulted from movement expansion
        if(canRetrieve(grid,cgLocation))
            expandedNodes.add(expandRetrieve(grid,capacity,cgLocation,node));

        if(canPickUp(grid,capacity,cgLocation))
            expandedNodes.add(expandPickup(grid,capacity,cgLocation,node));


        if(canDrop(grid,cgLocation,capacity))
            expandedNodes.add(expandDrop(grid,capacity,cgLocation,node));


        expandedNodes.addAll(expandMovement(node,grid,capacity,cgLocation));
        for (TreeNode cur:
             expandedNodes) {
            cur.pathCost=pathCost(cur);
        }
        return expandedNodes.toArray(new TreeNode[0]);
    }

    
    private CoastGuardTreeNode expandRetrieve(Cell[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        Cell[][] newStateGrid=getNextMovementGridState(grid);
        Ship cell=( (Ship) newStateGrid[cgLocation[0]][cgLocation[1]]);
        cell.boxRetrieved = true;
        return new CoastGuardTreeNode(new CoastGuardState(newStateGrid,capacity,cgLocation.clone()),node,Operators.RETRIEVE, node.depth+1);
    }

    private boolean canRetrieve(Cell[][] grid, int[] cgLocation) {
        Cell cell= grid[cgLocation[0]][cgLocation[1]];
        if(!cell.isShip()|| ((Ship)cell).passengersAlive > 0 || ((Ship)cell).boxHealth <= 1 || ((Ship)cell).boxRetrieved)
            return false;
        return true;
    }

    private CoastGuardTreeNode expandDrop(Cell[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        Cell[][] newStateGrid=getNextMovementGridState(grid);
        capacity=maxCapacity;
        return new CoastGuardTreeNode(new CoastGuardState(newStateGrid,capacity,cgLocation.clone()),node,Operators.DROP, node.depth+1);
    }
    
    private boolean canDrop(Cell[][] grid, int[] cgLocation,int capacity) {
        Cell cell=  grid[cgLocation[0]][cgLocation[1]];
        return cell.isStation()&&capacity<maxCapacity;
    }

    private CoastGuardTreeNode expandPickup(Cell[][] grid, int capacity, int[] cgLocation, CoastGuardTreeNode node) {
        Cell[][] newStateGrid=getNextMovementGridState(grid);
        Ship cell= ((Ship) grid[cgLocation[0]][cgLocation[1]]).clone();
        int passengers2 = cell.passengersAlive;
        cell.passengersAlive=Math.max(0, passengers2 - capacity);
        capacity=Math.max(0,capacity-passengers2);
        newStateGrid[cgLocation[0]][cgLocation[1]]= cell;
        cell.killPassenger();
        //System.out.println(newStateGrid[cgLocation[0]][cgLocation[1]].toString());
        return new CoastGuardTreeNode(new CoastGuardState(newStateGrid,capacity,cgLocation.clone()),node,Operators.PICKUP, node.depth+1);
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
        if(cgLocation[0]!=0)
            res.add(expandMovementUp(newStateGrid,capacity,cgLocation,parent));
        if(cgLocation[0]!=newStateGrid.length-1)
            res.add(expandMovementDown(newStateGrid,capacity,cgLocation,parent));
        if(cgLocation[1]!=0)
            res.add(expandMovementLeft(newStateGrid,capacity,cgLocation,parent));
        if(cgLocation[1]!=newStateGrid[0].length-1)
            res.add(expandMovementRight(newStateGrid,capacity,cgLocation,parent));
        return res;
    }

    private CoastGuardTreeNode expandMovementUp(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] upLocation= new int[]{cgLocation[0]-1,cgLocation[1]};
        return new CoastGuardTreeNode(new CoastGuardState(newStateGrid,capacity,upLocation),parent,Operators.UP,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementDown(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] downLocation= new int[]{cgLocation[0]+1,cgLocation[1]};
        return new CoastGuardTreeNode(new CoastGuardState(newStateGrid,capacity,downLocation),parent,Operators.DOWN,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementLeft(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] leftLocation= new int[]{cgLocation[0],cgLocation[1]-1};
        return new CoastGuardTreeNode(new CoastGuardState(newStateGrid,capacity,leftLocation),parent,Operators.LEFT,parent.depth+1);
    }

    private CoastGuardTreeNode expandMovementRight(Cell[][] newStateGrid,int capacity, int[] cgLocation,CoastGuardTreeNode parent) {
        int[] rightLocation= new int[]{cgLocation[0],cgLocation[1]+1};
        return new CoastGuardTreeNode(new CoastGuardState(newStateGrid,capacity,rightLocation),parent,Operators.RIGHT,parent.depth+1);

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
    
    public static int[] getIntTuplesFromString(String firstPortion){
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
      getInitialState().cgLocation = getIntTuplesFromString(splitted[2]);
      getInitialState().capacity = maxCapacity;
      putStationsInGrid(splitted[3],parsedGrid);
      putShipsInGridInitial(splitted[4],parsedGrid);
      removeNullsFromGrid(parsedGrid);
      getInitialState().grid=parsedGrid;
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
    }

    public static String genGrid(){
        Random rand = new Random();

        int m = rand.nextInt(11)+5;
        int n = rand.nextInt(11)+5;
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
    public static Cell[][] deserializeGrid(String serializedString){
        String[] splitted = serializedString.split(";");
        int[] dimensions = getIntTuplesFromString(splitted[0]);
        Cell[][] stateParsedGrid = new Cell[dimensions[1]][dimensions[0]];
        putStationsInGrid(splitted[1],stateParsedGrid);
        putShipsInGrid(splitted[2],stateParsedGrid);
        removeNullsFromGrid(stateParsedGrid);
        return stateParsedGrid;
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
          Cell[][] nodeState = cgt.getState().grid;
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
                    output+= operators[i].toString().toLowerCase() + ",";
                }
                output += operators[operators.length -1].toString().toLowerCase();
                output += ";" + deathsCases + ";" + boxesRetrieved + ";" + expandedNodes;
                return output;
    }

    private static int getRetrievedBoxes(Cell[][] nodeState) {
        int boxes = 0;
        Cell[][] nodeGrid = nodeState;
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
            case "UC":
                return new UC();
            case "GR1":
                return new Greedy(new CoastGuardFirstHeuristic());
            case "AS1":
                return new AS(new CoastGuardFirstHeuristic());
            case "GR2":
                return new Greedy(new CoastGuardSecondHeuristic());
            case "AS2":
                return new AS(new CoastGuardSecondHeuristic());
            default:
                throw new NullPointerException();
        }
    }

    /**
     * This method visualizes the given grid in the console.
     * @param grid a 2D array of type Cell, representing the array.
     * @param cgLocation
     */
    public static void visualize(Cell[][] grid, int[] cgLocation){
        int n = grid.length;
        int m = grid[0].length;
        String form = "", formCgPos = "";
        int x = cgLocation[0], y = cgLocation[1];
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
     * @param cgLocation coast guard location
     */
    public static void visualize(String gridStr, int[] cgLocation){
        Cell[][] grid = deserializeGrid(gridStr);
        visualize(grid, cgLocation);
    }
    
    /**
     * This method visualizes the given node in the console.
     * @param node
     */
    public static void visualize(CoastGuardTreeNode node){
        CoastGuardState state = node.getState();
        System.out.println("Coast Guard Position: "+node.getState().cgLocation[0]+","+node.getState().cgLocation[1] + ", Remaining Capacity = " + node.getState().capacity + ", Passengers aboard = " + (maxCapacity- node.getState().capacity));
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

    static long getUsedRam(){
        return Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
    }

    // Driver code
    public static void main(String[] args) {
        long before = getUsedRam();

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        float processTimeBefore = osBean.getProcessCpuTime()/(float)1e9;

        String grid9 = "7,5;100;3,4;2,6,3,5;0,0,4,0,1,8,1,4,77,1,5,1,3,2,94,4,3,46;";
        System.out.println(CoastGuard.solve(grid9, "BF", false));
        
        float processTimeAfter = osBean.getProcessCpuTime()/(float)1e9;
        float processTimeDiff = processTimeAfter - processTimeBefore;

        long after = getUsedRam();
        float ramUsage = (after - before)/(float)1e6;
        System.out.println("RAM usage = " + ramUsage + " MBs");
        System.out.println("CPU Time = " + processTimeDiff + " seconds");
    }
}
