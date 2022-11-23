package code;

public class Ship extends Cell{
    int passengersAlive;
    int deadPassengers;
    int boxHealth;
    boolean boxRetrieved;

    public Ship(int passengersAlive) {
        cellType=CellType.SHIP;
        this.passengersAlive = passengersAlive;
        deadPassengers=0;
        boxHealth=20;
        boxRetrieved=false;
    }

    public void killPassenger(){
        deadPassengers++;
        passengersAlive--;
    }

    public void retrieveBox(){
        boxRetrieved=true;
    }

    public void decrementBoxHealth(){
        boxHealth--;
    }


}
