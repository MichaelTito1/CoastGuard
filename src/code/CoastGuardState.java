package code;

import java.util.Objects;

public class CoastGuardState {
    String grid;
    int capacity;
    String cgLocation;

    public CoastGuardState(String grid, int capacity, String cgLocation) {
        this.grid = grid;
        this.capacity = capacity;
        this.cgLocation = cgLocation;
    }

    public CoastGuardState(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoastGuardState that = (CoastGuardState) o;
        if(!cgLocation.equals(that.cgLocation))
            return false;
        if(capacity != that.capacity)
            return false;
        if(grid.equals(that.grid))
            return true;
        return !betterState(grid,that.grid);
        //return grid.equals(that.grid);
    }

    private boolean betterState(String grid, String grid1) {
        Cell[][] curGrid=CoastGuard.deserializeGrid(grid);
        Cell[][] otherGrid=CoastGuard.deserializeGrid(grid1);
        for (int i = 0; i < curGrid.length; i++) {
            for (int j = 0; j < curGrid[0].length; j++) {
                if(curGrid[i][j].isShip()){
                    Ship curShip=(Ship)curGrid[i][j];
                    Ship otherShip=(Ship)otherGrid[i][j];

                    if(curShip.passengersAlive+curShip.deadPassengers!=otherShip.passengersAlive+otherShip.deadPassengers)
                        return true;
                    if (curShip.passengersAlive>otherShip.passengersAlive)
                        return true;
                    if(curShip.boxRetrieved!=otherShip.boxRetrieved)
                        return true;
                    if(curShip.boxHealth>otherShip.boxHealth)
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(grid, capacity, cgLocation);
    }
}
