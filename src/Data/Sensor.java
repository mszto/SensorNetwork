/**
 * Klasa przedstawia pojedyńczy czujnik oraz jego parametry
 */

package Data;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Sensor extends Rectangle {
    private final int GREEN=1;
    private  final int RED=0;
    private final int WHITE=2;
    private int id;
    private int status=1;
    private int baterry;
    private Circle circle;
    private Label label;
    private HotSpot hotSpot;
    private Arrow arrow;
    private List<Point> points;
    private Map<Point,Integer> numberOfReads;
    private String timeWork;
    public Sensor (){
        this(0,0);
    }
    public Sensor (int x, int y){
        this(x,y,0);
    }


    public Sensor (int x, int y, int id){
        super(x,y,20,20);
        setFill(Color.LIGHTGREEN);
        this.id=id;
        this.label=new Label(Integer.toString(id));
        label.setLayoutX(x);
        label.setLayoutY(y);
        circle=new Circle(getX()+10,getY()+10,50);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        hotSpot=new HotSpot(this,1);
        arrow=new Arrow(getX(),getY(),hotSpot.getSensor().getX(),hotSpot.getSensor().getY(),8.0);
        points=new ArrayList<>();
        this.baterry=100;
        this.numberOfReads=new HashMap<>();
        this.timeWork="0";
    }

    public void setHotSpot(HotSpot hotSpot){
        this.hotSpot=hotSpot;
        arrow=new Arrow(getX(),getY(),hotSpot.getSensor().getX()+10,hotSpot.getSensor().getY()+10,8.0);
    }

    public void batteryLevelAfterSend(int time){
        baterry-=2;
        if(baterry<=0){
            baterry=0;
            status=RED;
            setFill(Color.RED);
            StringBuilder sB = new StringBuilder();
            sB.append(time / 60000);
            sB.append(".");
            sB.append(time / 1000);
            sB.append(".");
            sB.append(time % 1000);
            timeWork=sB.toString();
        }
    }


    private double distance(Point point){
        return Math.sqrt(Math.pow(circle.getCenterX()-point.getCenterX(),2)+Math.pow(circle.getCenterY()- point.getCenterY(),2));
    }
    public void addPointToListen(Point point){
        if(distance(point)<=circle.getRadius()) {
            points.add(point);
            numberOfReads.put(point,0);
        }
    }

    public void addPointRead(Point point){
        if(numberOfReads.get(point)!=null) {
            System.out.println("Przed: "+numberOfReads.get(point)+" Po:");
            numberOfReads.put(point, numberOfReads.get(point) + 1);
            System.out.println(numberOfReads.get(point));
        }
    }

    public void reductionbatteryLevel(int time){
        baterry-=1;
        if(baterry<=0){
            baterry=0;
            status=RED;
            setFill(Color.RED);
            StringBuilder sB = new StringBuilder();
            sB.append(time / 60000);
            sB.append(".");
            sB.append(time / 1000);
            sB.append(".");
            sB.append(time % 1000);
            timeWork=sB.toString();
        }
    }

    public int getIdSensor() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public void setArrow(Arrow arrow) {
        this.arrow = arrow;
    }

    public Sensor getSensor(){
        return this;
    }

    public void addPoint(Point point){
        points.add(point);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBaterry() {
        return baterry;
    }

    public void setBaterry(int baterry) {
        if(baterry>=0 && baterry<=100)
        this.baterry = baterry;
    }

    public HotSpot getHotSpot() {
        return hotSpot;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
