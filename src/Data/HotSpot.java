package Data;

import javafx.scene.paint.Color;

public class HotSpot {
    Sensor sensor;
    private int id;
    private  boolean isMain;

    public HotSpot(Sensor sensor, int id){
        this.sensor=sensor;
        this.id=id;
        this.isMain=false;
    }

    public HotSpot(){
        sensor=new Sensor();
        sensor.setFill(Color.PINK);
        sensor.setX(10);
        sensor.setY(10);
        this.isMain=false;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void send(){
        sensor.getArrow().setFill(Color.GOLD);
    }

    public void sendOff(){
        sensor.getArrow().setFill(Color.BLACK);
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }
}
