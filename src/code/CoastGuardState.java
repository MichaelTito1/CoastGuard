package code;

import java.util.Objects;

public class CoastGuardState {
    String grid;
    int capacity;
    int[] cgLocation;

    public CoastGuardState(String grid, int capacity, int[] cgLocation) {
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
        return capacity == that.capacity && grid.equals(that.grid) && cgLocation.equals(that.cgLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grid, capacity, cgLocation);
    }
}
