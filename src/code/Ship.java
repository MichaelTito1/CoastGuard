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

    public Ship(int passengersAlive, int boxHealth) {
        cellType=CellType.SHIP;
        this.passengersAlive = passengersAlive;
        deadPassengers=0;
        this.boxHealth=boxHealth;
        boxRetrieved=false;
    }

    public Ship(int passengersAlive, int dead, int boxHealth, boolean retrieved) {
        cellType=CellType.SHIP;
        this.passengersAlive = passengersAlive;
        deadPassengers=dead;
        this.boxHealth=boxHealth;
        boxRetrieved=retrieved;
    }

    public void killPassenger(){
        if(passengersAlive<=0)
            return;
        deadPassengers++;
        passengersAlive--;
    }

    public void retrieveBox(){
        boxRetrieved=true;
    }

    public void decrementBoxHealth(){
        boxHealth--;
    }

    @Override
    public String toString() {
        return "S,"+passengersAlive+","+deadPassengers+","+boxHealth+","+(boxRetrieved?"t":"f");
    }

    protected Ship clone(){
        return new Ship(passengersAlive, deadPassengers, boxHealth, boxRetrieved);
    }
}
