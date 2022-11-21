package code;

import java.rmi.server.ObjID;

public class CoastGuard extends SearchProblem{

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
    
    public void GridParse(String grid){
      String[] splitted = grid.split(";");
      String[] firstSplitted = splitted[0].split(",");
      int m = Integer.parseInt(firstSplitted[0]);
      int n = Integer.parseInt(firstSplitted[1]);
      String[][] parsedGrid = new String[m][n];
      int maxCapacity = Integer.parseInt(splitted[1]);
      Object[] coastGuard = new Object[2];
      coastGuard[0] = splitted[2];
      coastGuard[1] = maxCapacity;
      putStationsinGrid(splitted[3],parsedGrid);
      putShipsinGrid(splitted[4],parsedGrid);
        
    }

    private void putShipsinGrid(String ships, String[][] parsedGrid) {
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
                   parsedGrid[xVal][yVal] = "S"+","+passengers;
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
        parsedGrid[xVal][yVal] = "S"+","+passengers;
    }

    private void putStationsinGrid(String stations, String[][]parsedGrid) {
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
    
}
