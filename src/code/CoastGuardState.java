package code;

import java.util.Arrays;
import java.util.Objects;

public class CoastGuardState {
    Cell[][] grid;
    int capacity;
    int[] cgLocation;

    public CoastGuardState(Cell[][] grid, int capacity, int[] cgLocation) {
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
        return capacity == that.capacity && Arrays.deepEquals(grid, that.grid) && Arrays.equals(cgLocation, that.cgLocation);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(capacity);
        result = 31 * result + Arrays.deepHashCode(grid);
        result = 31 * result + Arrays.hashCode(cgLocation);
        return result;
    }
}
