package code;

public class Station extends Cell{
    public Station(){
        cellType=CellType.STATION;
    }

    @Override
    public String toString() {
        return "I";
    }
}
