package code;

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
}
