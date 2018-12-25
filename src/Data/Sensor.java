/**
 * Klasa przedstawia pojedyńczy czujnik oraz jego parametry
 */

package Data;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;


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
    }

    public void setHotSpot(HotSpot hotSpot){
        this.hotSpot=hotSpot;
        arrow=new Arrow(getX(),getY(),hotSpot.getSensor().getX(),hotSpot.getSensor().getY(),8.0);
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
}
