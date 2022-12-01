package code;

import java.util.Objects;

public class Cell {
    public enum CellType{
        SHIP,STATION,EMPTY
    }

    CellType cellType;

    public boolean isShip(){
        return cellType==CellType.SHIP;
    }

    public boolean isStation(){
        return cellType==CellType.STATION;
    }

    public boolean isEmpty(){
        return cellType==CellType.EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return cellType == cell.cellType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType);
    }
}
