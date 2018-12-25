package sample;


import Data.HotSpot;
import Data.Point;
import Data.Sensor;
import Kmean.Kmean;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.Stack;
import java.util.Vector;

public class Controller {
    public SplitPane splitPane;
    public AnchorPane rightPane;
    public Pane centerPane;
    public TextField sensorsTextField, pointsTextField;
    public CheckBox showRangeCheckBox, showPointsCheckBox, showArrowCheckBox;
    public Label timeLabel;
    private Vector<Sensor> sensors = new Vector<>();
    private Vector<Point> points = new Vector<>();
    AnimationTimer animationTimer;
    private Vector<HotSpot> hotSpots = new Vector<>();
    private int time = 0, min = 0, i = 0, gold = 0, change = 0;
    long timeon, timeoff;
    private Stack<Point> stack = new Stack<>();
    private Kmean kmean=new Kmean();

    //metoda odpowiada za wyświtleniu bądz usunięcie kólka przy czujniku "pokaż zasięg"
    public void rangeCheckBox(ActionEvent event) {
        if (showRangeCheckBox.isSelected()) {
            sensors.forEach(sensor -> {
                centerPane.getChildren().addAll(sensor.getCircle());
            });
        } else {
            sensors.forEach(sensor -> {
                centerPane.getChildren().remove(sensor.getCircle());
            });
        }
    }

    //metoda odpowieada za wyświetlenie punktów bądż usunięcie punktów które są nasłuchiwane po kliknięciu CheckBoxa "pokaż punkty"
    public void pointsCheckBox(ActionEvent event) {
        if (showPointsCheckBox.isSelected()) {
            points.forEach(point -> {
                centerPane.getChildren().addAll(point);
            });
        } else {
            points.forEach(point -> {
                centerPane.getChildren().remove(point);
            });
        }
    }

    public void arrowCheckBox(ActionEvent event) {
        if (showArrowCheckBox.isSelected()) {
            kmean.getClousters().forEach(clouster -> {clouster.getPoints().forEach(sensor -> {centerPane.getChildren().add(sensor.getArrow());});});
        } else {
            kmean.getClousters().forEach(clouster -> {clouster.getPoints().forEach(sensor -> {centerPane.getChildren().remove(sensor.getArrow());});});
        }
    }

    private void addSensors(){
        Random rand = new Random();
        int sens = Integer.parseInt(sensorsTextField.getText());
        for (int i = 1; i <= sens; i++) {
            Sensor sensor = new Sensor(rand.nextInt(530) + 30, rand.nextInt(420) + 30, i);
            sensors.add(sensor);
            centerPane.getChildren().addAll(sensor, sensor.getLabel());
        }
    }

    private void addPoints(){
        Random rand = new Random();
        int poi = Integer.parseInt(pointsTextField.getText());
        for (int i = 1; i <= poi; i++) {
            Point point = new Point(rand.nextInt(530) + 30, rand.nextInt(420) + 30);
            points.add(point);
            centerPane.getChildren().addAll(point);
        }
    }

    private void addHotSpots(){
        hotSpots.add(new HotSpot());
        hotSpots.get(0).setId(0);
        centerPane.getChildren().addAll(hotSpots.get(0).getSensor());
        int k=1;
        kmean.getClousters().forEach(clouster -> {
            clouster.setHotSpot();
            hotSpots.add(clouster.getHotSpot());
            clouster.getHotSpot().setId(k);
        });
        hotSpots.forEach(hotSpot -> {hotSpot.getSensor().setHotSpot(hotSpots.get(0));});
    }
    //metoda odpowiada za nasłuchiwane przycisku start i zainicjowanie symulacji

    public void start(ActionEvent event) {
        time = 0;
        min = 0;
        centerPane.getChildren().clear();
        sensors.clear();
        points.clear();
        showRangeCheckBox.setSelected(false);
        showArrowCheckBox.setSelected(false);
        showPointsCheckBox.setSelected(true);


        addSensors();
        kmean.createClousters(sensors);
        addHotSpots();
        addPoints();
        long starttime = System.currentTimeMillis();
        timeon = starttime;
        timeoff = starttime;
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate(starttime);
            }
        };
        animationTimer.start();
    }


    private void onUpdate(long startTime) {
        time = (int) ((int) System.currentTimeMillis() - startTime);

        StringBuilder sB = new StringBuilder();
        sB.append(time / 60000);
        sB.append(".");
        sB.append(time / 1000);
        sB.append(".");
        sB.append(time % 1000);
        timeLabel.setText(sB.toString());

        if ((System.currentTimeMillis() - timeoff) / 1000 >= 1) {
            Random random = new Random();
            int j = random.nextInt(points.size());
            for (int k = 0; k < j; k++) {
                int b = random.nextInt(points.size());
                points.get(b).setFill(Color.ORANGE);
                stack.push(points.get(b));
            }
            timeoff=System.currentTimeMillis();
        }


        if ((System.currentTimeMillis() - timeoff) / 500 >= 1) {
            while (!stack.empty()) {
                Point point = stack.pop();
                point.setFill(Color.BLACK);
            }
        }

        if (((int) (System.currentTimeMillis() - timeon)) / 1000 == 5) {
            timeon = System.currentTimeMillis();
            gold = 0;
            sensors.forEach(sensor -> {
                sensor.getArrow().setFill(Color.GOLD);
            });
            change = 1;
        }

        if (change == 1) {
            if (System.currentTimeMillis() - timeon >= 500) {
                sensors.forEach(sensor -> {
                    sensor.getArrow().setFill(Color.BLACK);
                });
                change = 0;
            }


        }

    }
}
