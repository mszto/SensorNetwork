package Kmean;

import Data.HotSpot;
import Data.Point;
import Data.Sensor;


import java.util.*;

public class Clouster {

    Vector<Sensor> points;
    HotSpot hotSpot;
    Centroid centroid;
    Centroid newCetroid;

    public Clouster(){
        this(3.0,4.0);
    }

    public Clouster(double x, double y){
        centroid=new Centroid();
        newCetroid=new Centroid();
        this.centroid.setY(y);
        this.centroid.setX(x);
        hotSpot=new HotSpot();
        points=new Vector<>();
    }

    public void addPoint(Sensor p){
        points.add(p);
    }

    public int coutCentroid() {
        double x = 0;
        double y = 0;
        int value=0;

        for (Sensor point : points) {
            x += point.getX();
            y += point.getY();
        }

        if (points.size() != 0) {
            newCetroid.setX(x / points.size());
            newCetroid.setY(y / points.size());
        }
        if(!newCetroid.equals(centroid)){
            centroid.setX(newCetroid.getX());
            centroid.setY(newCetroid.getY());
            value=1;
        }
        return value;
    }


    public List<Sensor> getPoints() {
        return points;
    }

    public void setPoints(Vector<Sensor> points) {
        this.points = points;
    }

    public Centroid getCentroid() {
        return centroid;
    }

    public void setCentroid(Centroid centroid) {
        this.centroid = centroid;
    }

    public void setHotSpot(){

        int j=0;
        hotSpot.setSensor(points.get(0));
        for(int i=1;i<points.size();i++){
            points.get(i).setHotSpot(hotSpot);
        }
    }

    public void setHotSpot(Vector<Sensor> sensors){
        hotSpot=new HotSpot();
        int j=0;
        hotSpot.setSensor(points.get(0));
        for(int i=1;i<points.size();i++){
           Sensor s= sensors.get(sensors.indexOf(points.get(i)));
           s.setHotSpot(hotSpot);
        }
    }


    public void setHotSpot(HotSpot hotSpot){
        this.hotSpot=hotSpot;
    }

    public HotSpot getHotSpot() {
        return hotSpot;
    }

    protected class Centroid {
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Centroid)) return false;
            Centroid centroid = (Centroid) o;
            return Double.compare(centroid.getX(), getX()) == 0 &&
                    Double.compare(centroid.getY(), getY()) == 0;
        }

        @Override
        public int hashCode() {

            return Objects.hash(getX(), getY());
        }
    }

}
