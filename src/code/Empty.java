package code;

public class Empty extends Cell{
    public Empty(){
        cellType=CellType.EMPTY;
    }

    @Override
    public String toString() {
        return "E";
    }
}
