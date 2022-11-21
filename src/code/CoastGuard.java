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
        int cgX = rand.nextInt(n), cgY = rand.nextInt(m);

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
                        stationString += i + "," + j + ",";
                    else if(cell[0].equals("ship")){
                        shipString += i + "," + j + "," + cell[1]+",";
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

    public static void main(String[] args) {
        System.out.println(genGrid());
    }
    @Override
    public boolean goalTest() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int pathCost() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    
}
