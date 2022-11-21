package code;

import java.util.Random;

public class CoastGuard extends SearchProblem{

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
                    grid[x][y] = "ship;"+numPassengers+";20";
                    break;
                }
            }while(true);
        }

        //generating stations randomly
        // NOTE: there is no upper limit for the number of ships or stations generated as long as no 2 objects occupy the same cell
        int numStations = rand.nextInt(n*m-numShips); 
        for (int i = 0; i < numStations; i++) {
            do{
                int x = rand.nextInt(n), y = rand.nextInt(m);
                if(grid[x][y] == null){
                    grid[x][y] = "station";
                    break;
                }
            }while(true);
        }

        // converting generated grid to string
        return convertGridToString(grid, n, m, maxCapacity, cgX, cgY);
    }

    /**
     * Converts the given grid to a specific string format specified in the project description.
     * 
     * @param grid a 2D string array representing the grid itself
     * @param n number of rows in the grid
     * @param m number of columns
     * @param maxCapacity maximum capacity of the coast guard 
     * @param cgX initial x coordinate of the coast guard on the grid
     * @param cgY initial y coordinate of the coast guard on the grid
     * @return the grid in its string format
     */
    private static String convertGridToString(String[][] grid, int n, int m, int maxCapacity, int cgX, int cgY){
        // 1. general info
        String gridString = m+","+n+";"+maxCapacity+";"+cgX+","+cgY+";";
        
        // 2. get ships and stations info from the 2D grid
        String stationString = "";
        String shipString = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if(grid[i][j] != null){
                    String[] cell = grid[i][j].split(";");
                    
                    if(cell[0].equals("station"))
                        stationString += j + "," + i + ",";
                    else if(cell[0].equals("ship")){
                        shipString += j + "," + i + "," + cell[1]+",";
                    }
                }
            }
        }
        // 3. adjust format of strings
        stationString = stationString.substring(0, stationString.length()-1) + ";";
        shipString = shipString.substring(0, shipString.length()-1) + ";";
        
        // 4. add info to grid string and return
        gridString += stationString + shipString;
        return gridString;
    }

    // Driver code

    public static void main(String[] args) {
        System.out.println(genGrid());
        String s = "5,7;200;3,4;1,2,4,5;4,6,100,2,2,50,1,6,30";
        gridParse(s);
    }

    @Override
    public boolean goalTest(TreeNode node) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int pathCost(TreeNode node) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public TreeNode[] expand(TreeNode node) {
        // TODO Auto-generated method stub
        return new TreeNode[0];
    }


    
    public static void gridParse(String grid){
      String[] splitted = grid.split(";");
      String[] firstSplitted = splitted[0].split(",");
      int m = Integer.parseInt(firstSplitted[0]);
      int n = Integer.parseInt(firstSplitted[1]);
      String[][] parsedGrid = new String[n][m];
      int maxCapacity = Integer.parseInt(splitted[1]);
      Object[] coastGuard = new Object[2];
      coastGuard[0] = splitted[2];
      coastGuard[1] = maxCapacity;
      putStationsinGrid(splitted[3],parsedGrid);
      putShipsinGrid(splitted[4],parsedGrid);
      for(int i=0 ; i<n ; i++){
        for(int j=0 ; j<m ; j++){
            System.out.print(parsedGrid[i][j] + " ");
        }
        System.out.println();
      }
      System.out.println("Coast guard : " + coastGuard[0] + "and " + coastGuard[1]);
        
    }

    private static void putShipsinGrid(String ships, String[][] parsedGrid) {
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
                   parsedGrid[yVal][xVal] = "S"+","+passengers;
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
        parsedGrid[yVal][xVal] = "S"+","+passengers;
    }

    private static void putStationsinGrid(String stations, String[][]parsedGrid) {
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
    
}
