package code;

import java.util.Arrays;
import java.util.Objects;

public class CoastGuardState {
    Cell[][] grid;
    int remainingCapacity;
    int[] cgLocation;

    public CoastGuardState(Cell[][] grid, int capacity, int[] cgLocation) {
        this.grid = grid;
        this.remainingCapacity = capacity;
        this.cgLocation = cgLocation;
    }

    public CoastGuardState(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoastGuardState that = (CoastGuardState) o;
        return remainingCapacity == that.remainingCapacity && Arrays.deepEquals(grid, that.grid) && Arrays.equals(cgLocation, that.cgLocation);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(remainingCapacity);
        result = 31 * result + Arrays.deepHashCode(grid);
        result = 31 * result + Arrays.hashCode(cgLocation);
        return result;
    }
}
