package code;

import java.util.Random;

public class CoastGuard extends SearchProblem{

    enum Operators {
        LEFT,RIGHT,UP,DOWN,PICKUP,DROP,RETRIEVE
    }

    public CoastGuard(){
        this(genGrid());
    }

    public CoastGuard(String generatedGrid){
        operators=new Operators[]{
                Operators.LEFT, Operators.RIGHT,
                Operators.UP,Operators.DOWN,
                Operators.PICKUP,Operators.DROP,
                Operators.RETRIEVE};
        //state: SerializedGrid,capacity,location
        initialState=new Object[3];
        getInitialState(generatedGrid);
    }

    /**
     * This method checks if the given node's state is a goal state
     * @param node the current node
     * @return true if it passes the goal test, false otherwise
     */
    @Override
    public boolean goalTest(TreeNode node) {
        // state = {grid, capacity, location}
        // if coast guard still has passengers on board, return false
        if(Integer.parseInt((String) node.state[1]) > 0)
            return false;
        
        int passengers = 0;
        int boxes = 0;
        String[][] curGrid = (String[][]) node.state[0];
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
        String[] cellInfo = cell.split(";");
        Object[] cellArr = new Object[cellInfo.length];
        String cellType = cellInfo[0];
        cellArr[0] = cellType;
        if(cellType.equals("S")){
            cellArr[1] = Integer.parseInt(cellInfo[1]); // num of passengers on board
            cellArr[2] = Integer.parseInt(cellInfo[2]); // box health on board
        }
        return cellArr;
    }

    /**
     * This method converts the string representation of a grid cell 
     * 
     * @param cell
     * @return
     */
    private Object[] deserializeCell(String cell){
        String[] cellInfo = cell.split(";");
        Object[] cellArr = new Object[cellInfo.length];
        String cellType = cellInfo[0];
        cellArr[0] = cellType;
        if(cellType.equals("S")){
            cellArr[1] = Integer.parseInt(cellInfo[1]); // num of passengers on board
            cellArr[2] = Integer.parseInt(cellInfo[2]); // box health on board
        }
        return cellArr;
    }

    @Override
    public int[] pathCost(TreeNode node) {
        //getting position of coast guard
        String coastGuardPosition = (String)node.state[2];
        String [] coastGuardPositionSplitted = coastGuardPosition.split(",");
        int coastGuardX = Integer.parseInt(coastGuardPositionSplitted[0]);
        int coastGuardY = Integer.parseInt(coastGuardPositionSplitted[1]);

        int[] cost = new int[2];
        if(node.parent == null) //root node
            return cost;
        String[][] parentGrid = deserializeGrid((String)node.parent.state[0]); //parent Grid State
        String[][] nodeGrid = deserializeGrid((String)node.state[0]); //node Grid State
        for(int i=0 ; i<nodeGrid.length ; i++){
            for(int j=0 ; j<nodeGrid[0].length ; j++){
                if(!(nodeGrid[i][j] == null || (nodeGrid[i][j]).equals("I"))){
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
        if(!(nodeGrid[coastGuardY][coastGuardX]==null || (nodeGrid[coastGuardY][coastGuardX]).equals("I"))){
            String[] currentCoastGuardCell = nodeGrid[coastGuardY][coastGuardX].split(",");
            String[] parentCoastGuardCell = nodeGrid[coastGuardY][coastGuardX].split(","); //the previous state of the cell where the coast guard is in now
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
    public TreeNode[] expand(TreeNode node) {
        // TODO Auto-generated method stub
        return new TreeNode[0];
    }



    // Driver code
    public static void main(String[] args) {
        System.out.println(genGrid());
        String s = "5,7;200;3,4;1,2,4,5;4,6,100,2,2,50,1,6,30";
        CoastGuard cg=new CoastGuard(genGrid());
        cg.printState(cg.initialState);
    }
    
    private static int[] getGridDimensions(String firstPortion){
        String[] firstSplitted = firstPortion.split(",");
        int m = Integer.parseInt(firstSplitted[0]);
        int n = Integer.parseInt(firstSplitted[1]);
        return(new int[]{m,n});
    }

    public void getInitialState(String grid){
      String[] splitted = grid.split(";");
      int[] dimensions = getGridDimensions(splitted[0]);
      String[][] parsedGrid = new String[dimensions[1]][dimensions[0]];
      int maxCapacity = Integer.parseInt(splitted[1]);
      initialState[2] = splitted[2];
      initialState[1] = maxCapacity;
      putStationsInGrid(splitted[3],parsedGrid);
      putShipsInGridInitial(splitted[4],parsedGrid);
      initialState[0]=serializeGrid(parsedGrid);
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
                    parsedGrid[yVal][xVal] = "I";
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
        parsedGrid[yVal][xVal] = "I";
    }

    private static void putShipsInGrid(String ships, String[][] parsedGrid) {
        int comma = 0;
        String x = "";
        String y = "";
        String passengers = "";
        String boxHealth = "";
        for(int i=0 ; i<ships.length() ; i++){
            if(ships.charAt(i)==','){
                if(comma == 3){
                   comma = 0;
                   int xVal = Integer.parseInt(x);
                   int yVal = Integer.parseInt(y);
                   parsedGrid[yVal][xVal] = "S"+","+passengers+","+boxHealth;
                   x = "";
                   y = "";
                   passengers = "";
                   boxHealth = "";
                }
                else 
                   comma++;
            }
            else if(comma == 0)
                x += ships.charAt(i);
            else if(comma == 1)
                y += ships.charAt(i);
            else if(comma == 2)
                passengers += ships.charAt(i);
            else 
                boxHealth += ships.charAt(i);
                
        }
        int xVal = Integer.parseInt(x);
        int yVal = Integer.parseInt(y);
        parsedGrid[yVal][xVal] = "S"+","+passengers+","+boxHealth;
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
                   parsedGrid[yVal][xVal] = "S"+","+passengers+",20";
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
        parsedGrid[yVal][xVal] = "S"+","+passengers+",20";
    }

    public static String genGrid(){
        Random rand = new Random();

        int m = rand.nextInt(11)+5;
        int n = rand.nextInt(11)+5;
        // Cell string format: "type{;numOfPassengers;BoxHealth}", where:
        // {;numOfPassengers;BoxHealth} is added if type is ship
        String [][] grid = new String[n][m];

        // generating coast guard's data
        int maxCapacity = rand.nextInt(71)+30;
        int cgX = rand.nextInt(m), cgY = rand.nextInt(n);

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
                        stationString += j + "," + i + ",";
                    else if (cell[0].equals("S")) {
                        shipString += j + "," + i + "," + cell[1] + ",";
                    }
                }
            }
        }
        // 3. adjust format of strings
        stationString = stationString.substring(0, stationString.length() - 1) + ";";
        shipString = shipString.substring(0, shipString.length() - 1) + ";";

        // 4. add info to grid string and return
        gridString += stationString + shipString;
        return gridString;
    }

    private static String[][] deserializeGrid(String serializedString){
        String[] splitted = serializedString.split(";");
        int[] dimensions = getGridDimensions(splitted[0]);
        String[][] stateParsedGrid = new String[dimensions[1]][dimensions[0]];
        putStationsInGrid(splitted[1],stateParsedGrid);
        putShipsInGrid(splitted[2],stateParsedGrid);
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
                if (grid[i][j] != null) {
                    String[] cell = grid[i][j].split(";");

                    if (cell[0].equals("I"))
                        stationString += j + "," + i + ",";
                    else if (cell[0].equals("S")) {
                        shipString += j + "," + i + "," + cell[1] + ","+cell[2];
                    }
                }
            }
        }
        // 3. adjust format of strings
        stationString = stationString.substring(0, stationString.length() - 1) + ";";
        shipString = shipString.substring(0, shipString.length() - 1) + ";";

        // 4. add info to grid string and return
        gridString += stationString + shipString;
        return gridString;
    }

    private void printState(Object[] state) {
        String[][] parsedGrid=(String[][]) state[0];
        String location=(String)state[2];
        int capacity=(int)state[1];
        for(int i = 0; i< parsedGrid.length; i++){
            for(int j = 0; j< parsedGrid[0].length; j++){
                System.out.print(parsedGrid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Coast guard : " + location + " and " + capacity);
    }
}
