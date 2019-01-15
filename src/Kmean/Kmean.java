package Kmean;

import Data.Arrow;
import Data.Point;
import Data.Sensor;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Kmean {

    int c;
    List<Clouster> clousters;

    public Kmean() {
        this(4);
    }

    public Kmean(int k){
        c=k;
        Random rand=new Random();
        clousters=new ArrayList<Clouster>();
        for(int i=0;i<c;i++){
            double random=rand.nextDouble();
            clousters.add(new Clouster(100+(random*(500-100)),100+(random*(600-100))));
        }
    }


    public void createClousters(List<Sensor> points){
        int changed=0;
        do{
            changed=0;
            addPointsToclouster(points);
            for (Clouster clouster:clousters
                    ) {
                changed=changed+clouster.coutCentroid();
            }
        }while(changed>0);
    }



    public void addPointsToclouster(List<Sensor> points){
        clousters.forEach(clouster -> {clouster.getPoints().clear();});
        for (Sensor point: points) {
            double distance=800;
            int j=0;
            for(int i=0;i<clousters.size();i++){
                Clouster clouster=clousters.get(i);
                double secondDistance=Math.sqrt(Math.pow(point.getX()-clouster.centroid.getX(),2)+Math.pow(point.getY()-clouster.centroid.getY(),2));
                if(distance>secondDistance){
                    distance=secondDistance;
                    j=i;
                }
            }
            clousters.get(j).addPoint(point);
        }

    }

    public List<Clouster> getClousters() {
        return clousters;
    }

    public void setClousters(List<Clouster> clousters) {
        this.clousters = clousters;
    }
}
