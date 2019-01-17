package Data;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Objects;


public class Point extends Circle {
    private final int BLACK=0;
    private final int ORANGE=1;
    private int id;
    private int status=0;

    public Arrow arrow;
    public Point(){
        super(8);
        setFill(Color.BLACK);
    }

    public Point(int x, int y,int id){
        super(x,y,4);
        this.id=id;
        setFill(Color.BLACK);
    }

    public void setArr(double x,double y){
        arrow=new Arrow(getCenterX(),getCenterY(),x,y,8.0);
    }
    public void sendData(){
        status=ORANGE;
    }
    public void setOff(){
        status=BLACK;
    }

}
