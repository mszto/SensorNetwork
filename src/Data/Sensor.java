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
    private int id; //numer id sensora
    private int status=1;
    private int baterry; //poziom bateri
    private Circle circle; // zasięg działania sensora
    private Label label; //wyświetlany jest numer id sensora
    private HotSpot hotSpot; //hotspot do którego przesyłane są dane
    private Arrow arrow; //kierunek przesyłania danych
    private List<Point> points; //nasłuchiwane punkty
    private Map<Point,Integer> numberOfReads; // mapa przechowuje liczbę odczytanych danych z każdego punktu
    private String timeWork; //czas działąnia sensora
    private  boolean isGetNewData; // flaga odpowiada za informację czy zostały odczytane czujnik

    // konstruktory
    public Sensor (){
        this(0,0);
    }
    public Sensor (int x, int y){
        this(x,y,0);
    }



    public Sensor (int x, int y, int id){
        super(x,y,20,20); // utworznie prostokąta odpowiadającego za wizualizację sensora
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
        this.isGetNewData=false;
    }

    // ustawienie hot spoda oraz utworzenie linie któa wskazuje kierunek przysłania danych
    public void setHotSpot(HotSpot hotSpot){
        this.hotSpot=hotSpot;
        arrow=new Arrow(getX(),getY(),hotSpot.getSensor().getX()+10,hotSpot.getSensor().getY()+10,8.0);
    }

    // redukcja beteri po przesłaniu oraz wyłączenie sensora i zapisanie czasu jego pracy, kolor jest zmieniany na czerwono oraz status ustawiny jest na 0 czyli wyłączony
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

    //oblicza odległość eklidesową sensora do punktu
    private double distance(Point point){
        return Math.sqrt(Math.pow(circle.getCenterX()-point.getCenterX(),2)+Math.pow(circle.getCenterY()- point.getCenterY(),2));
    }

    // ustalenie czy punkt znajduje się w zasigu sensora
    public void addPointToListen(Point point){
        if(distance(point)<=circle.getRadius()) {
            points.add(point);
            numberOfReads.put(point,0);
        }
    }

    //dodanie liczby odczutu punktu
    public void addPointRead(Point point){
        if(numberOfReads.get(point)!=null) {
            isGetNewData=true;
            numberOfReads.put(point, numberOfReads.get(point) + 1);
        }
    }

    //redukcja bateri po pewnym czasie
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

    //getery oraz setery

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

    public boolean isGetNewData() {
        return isGetNewData;
    }

    public void setGetNewData(boolean getNewData) {
        isGetNewData = getNewData;
    }
}
