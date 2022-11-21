package code;

import java.rmi.server.ObjID;

public class CoastGuard extends SearchProblem{

    public static void main(String[] args) {
        String s = "5,7;200;3,4;1,2,4,5;4,6,100,2,2,50,1,6,30";
        gridParse(s);
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
