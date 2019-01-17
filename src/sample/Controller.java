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

import java.util.*;

public class Controller {
    public SplitPane splitPane;
    public AnchorPane rightPane;
    public Pane centerPane;
    public TextField sensorsTextField, pointsTextField;
    public CheckBox showRangeCheckBox, showPointsCheckBox, showArrowCheckBox;
    public Label timeLabel, lifeNetwork;
    private List<Sensor> sensors = new ArrayList<>();
    private List<Sensor> sensorsOff = new ArrayList<>();
    private List<Point> points = new ArrayList<>();
    private List<HotSpot> mainHotSpot = new ArrayList<>();
    private AnimationTimer animationTimer;
    private List<HotSpot> hotSpots = new ArrayList<>();
    private int time = 0, min = 0, i = 0, gold = 0;
    boolean hotSpotSed = false, changeSensorSend = false, pom=false, mainFlag=false; // flagi odpowiadające za ustawienie stanu senorów false-sensor nie przesyłą danych true-sensor wysyła dane
    long timeon, timeoff, time3, time4, timeChange;
    private Stack<Point> stack = new Stack<>();
    private Kmean kmean = new Kmean();

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

    //wyświetlanie lub usuwanie lini przesyłania danych sensorów
    public void arrowCheckBox(ActionEvent event) {
        if (showArrowCheckBox.isSelected()) {
            kmean.getClousters().forEach(clouster -> {
                clouster.getPoints().forEach(sensor -> {
                    if (sensor.getStatus() == 1) {
                        centerPane.getChildren().add(sensor.getArrow());
                    }
                });
            });
        } else {
            kmean.getClousters().forEach(clouster -> {
                clouster.getPoints().forEach(sensor -> {

                    centerPane.getChildren().remove(sensor.getArrow());

                });
            });
        }
    }

    private void setLifeNetwork() {
        Set<Point> pointsInSensorsRange = new HashSet<>();
        sensors.forEach(sensor -> {
            pointsInSensorsRange.addAll(sensor.getPoints());
        });
        if (sensors.size() != 0) {

            lifeNetwork.setText(String.valueOf((int) (pointsInSensorsRange.size() / (double) points.size() * 100)));
        }
    }

    //dodanie sensoró do planszy
    private void addSensors() {
        Random rand = new Random();
        int sens = Integer.parseInt(sensorsTextField.getText()); // pobranie ilości sensorów z pola tekstowego
        for (int i = 1; i <= sens; i++) {
            Sensor sensor = new Sensor(rand.nextInt(530) + 30, rand.nextInt(420) + 30, i);
            sensors.add(sensor); // dodanie sensora do listy
            centerPane.getChildren().addAll(sensor, sensor.getLabel()); // dodanie sensora do sceny tzn do okna widoczengo
        }
    }

    // dodanie punktów do planszy, współrzędne punktów są losowe
    private void addPoints() {
        Random rand = new Random();
        int poi = Integer.parseInt(pointsTextField.getText()); // pobranie ilości punktów z pola tekstowego
        for (int i = 1; i <= poi; i++) {
            Point point = new Point(rand.nextInt(530) + 30, rand.nextInt(420) + 30, i);
            points.add(point);
            centerPane.getChildren().addAll(point); // dodanie punktów które są nasłuchiwane do listy
            sensors.forEach(sensor -> {
                sensor.addPointToListen(point);
            }); // dodanie do sensorów punktów które są nasłuchiwane
        }
    }

    private double distance(Sensor sensor1, Sensor sensor2) {
        return Math.sqrt(Math.pow(sensor1.getX() - sensor2.getX(), 2) + Math.pow(sensor1.getY() - sensor2.getY(), 2));
    }

    // tworzeni hotspotów w klastrach
    private void addHotSpots() {
        hotSpots.add(new HotSpot());
        hotSpots.get(0).setId(0);
        centerPane.getChildren().addAll(hotSpots.get(0).getSensor()); //ustawnienie głównego hotspoda BS
        int k = 1;
        kmean.getClousters().forEach(clouster -> {
            clouster.setHotSpot();
            hotSpots.add(clouster.getHotSpot()); // dodanie hotspoda do listy hotspotów
            clouster.getHotSpot().setId(k);
        });
        Random random = new Random();

        for (int i = 0; i < (hotSpots.size() - 1) / 2; i++) {
            int j = random.nextInt(hotSpots.size() - 1) + 1;
            hotSpots.get(j).setMain(true);
            mainHotSpot.add(hotSpots.get(j));
        }
        HotSpot SIM = hotSpots.get(0);
        mainHotSpot.forEach(hotSpot -> {
            hotSpot.getSensor().setHotSpot(SIM);  // ustawnienie hotspoda do którego są presyłane dane z klastrów
            hotSpots.remove(hotSpot);
            sensors.remove(hotSpot.getSensor());
            hotSpot.getSensor().setFill(Color.PINK);
        });

        for (int i=0;i<hotSpots.size();i++) {
            double dist = Integer.MAX_VALUE;
            for (HotSpot hotSpot1 : mainHotSpot) {
                if (distance(hotSpot1.getSensor(), hotSpots.get(i).getSensor()) <= dist) {
                    hotSpots.get(i).getSensor().setHotSpot(hotSpot1);
                }
            }
        }

        hotSpots.forEach(hotSpot -> {
            sensors.remove(hotSpot.getSensor());
            hotSpot.getSensor().setFill(Color.PINK);
        });
    }

    //metoda odpowiada za nasłuchiwane przycisku start i zainicjowanie symulacji
    public void start(ActionEvent event) {
        time = 0;
        min = 0;
        centerPane.getChildren().clear();
        sensors.clear();
        points.clear();
        hotSpots.clear();
        mainHotSpot.clear();
        showRangeCheckBox.setSelected(false);
        showArrowCheckBox.setSelected(false);
        showPointsCheckBox.setSelected(true);


        addSensors();
        addPoints();
        kmean.createClousters(sensors);
        addHotSpots();

        setLifeNetwork();
        long starttime = System.currentTimeMillis();
        timeon = starttime;
        timeoff = starttime;
        timeChange=starttime;
        // tutaj jest zapętlenie tzn metoda onUpdate wykonywana jest cały czas po wywołaniy animationtimer start w lini 154
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

        // wyświetlanie czasu
        StringBuilder sB = new StringBuilder();
        sB.append(time / 60000);
        sB.append(".");
        sB.append(time / 1000);
        sB.append(".");
        sB.append(time % 1000);
        timeLabel.setText(sB.toString());

        // wywołanie zdarzenia na punktach tzn punkty odczytały dane
        if ((System.currentTimeMillis() - timeoff) / 1000 >= 2) {
            Random random = new Random();
            int j = random.nextInt(points.size());
            for (int k = 0; k < j; k++) {
                int b = random.nextInt(points.size());
                points.get(b).setFill(Color.ORANGE);
                stack.push(points.get(b));
            }
            sensors.forEach(sensor -> {
                sensor.reductionbatteryLevel(time);
            });
            timeoff = System.currentTimeMillis();
            pom=true;
        }

        //zakonczenie wysyłania danych z sensorów
        if ((System.currentTimeMillis() - timeoff) >= 500 && pom) {
            while (!stack.empty()) {
                Point point = stack.pop();
                point.setFill(Color.BLACK);
                sensors.forEach(sensor -> {
                    sensor.addPointRead(point);
                });
            }
            timeon = System.currentTimeMillis();
            sensors.forEach(sensor -> {
                if (sensor.getStatus() == 1 && sensor.isGetNewData()) {
                    sensor.getArrow().setFill(Color.GOLD);
                    changeSensorSend = true;
                }

            });
            pom=false;
        }

        //zakonczenie wysłania danych z sensorów oraz rozpoczęcie wysyłania danych z hotspotów w klastrach
        if (System.currentTimeMillis() - timeon >= 500 && changeSensorSend) {

            sensors.forEach(sensor -> {
                if (sensor.isGetNewData() && sensor.getStatus()==1) {
                    sensor.getArrow().setFill(Color.BLACK);
                    sensor.reductionbatteryLevel(time);
                    sensor.setGetNewData(false);
                    sensor.getHotSpot().getSensor().setGetNewData(true);
                }
            });
            hotSpots.forEach(hotSpot -> {
                if (hotSpot.getSensor().getStatus() == 1 && hotSpot.getSensor().isGetNewData()) {
                    hotSpot.send();
                    hotSpot.getSensor().setGetNewData(true);
                }
            });
            changeSensorSend=false;
            hotSpotSed = true;


            time3=System.currentTimeMillis();

        }

        //zakonczenie wysyłania danych z hotspotów w klastrach
        if (System.currentTimeMillis() - time3 >= 500 && hotSpotSed) {
            hotSpots.forEach(hotSpot -> {
                if (hotSpot.getSensor().isGetNewData() && hotSpot.getSensor().getStatus()==1) {
                    hotSpot.sendOff();
                    hotSpot.getSensor().batteryLevelAfterSend(time);
                    hotSpot.getSensor().setGetNewData(false);
                    hotSpot.getSensor().reductionbatteryLevel(time);
                    hotSpot.getSensor().getHotSpot().getSensor().setGetNewData(true);
                }
            });
            hotSpotSed = false;
            mainHotSpot.forEach(hotSpot -> {
                if (hotSpot.getSensor().isGetNewData() && hotSpot.getSensor().getStatus()==1)
                hotSpot.send();
            });
            time4=System.currentTimeMillis();
            mainFlag=true;
        }

        if (System.currentTimeMillis() - time4 >= 500 && mainFlag) {
            mainHotSpot.forEach(hotSpot -> {
                if  (hotSpot.getSensor().isGetNewData() && hotSpot.getSensor().getStatus()==1) {

                    hotSpot.sendOff();
                    hotSpot.getSensor().batteryLevelAfterSend(time);
                    hotSpot.getSensor().setGetNewData(false);
                    hotSpot.getSensor().batteryLevelAfterSend(time);
                }
            });
            mainFlag=false;
        }


        if(System.currentTimeMillis()-timeChange>=10000){
            Random random = new Random();

            hotSpots.addAll(mainHotSpot);
            mainHotSpot.clear();
            if (showArrowCheckBox.isSelected()) {
                kmean.getClousters().forEach(clouster -> {
                    clouster.getPoints().forEach(sensor -> {

                        centerPane.getChildren().remove(sensor.getArrow());
                    });
                });
            }

            for (int i = 0; i < (hotSpots.size() - 1) / 2; i++) {
                int j = random.nextInt(hotSpots.size() - 1) + 1;
                hotSpots.get(j).setMain(true);
                mainHotSpot.add(hotSpots.get(j));
            }
            HotSpot SIM = hotSpots.get(0);
            mainHotSpot.forEach(hotSpot -> {
                hotSpot.getSensor().setHotSpot(SIM);  // ustawnienie hotspoda do którego są presyłane dane z klastrów
                hotSpots.remove(hotSpot);
                sensors.remove(hotSpot.getSensor());
            });

            for (int i=0;i<hotSpots.size();i++) {
                double dist = Integer.MAX_VALUE;
                for (HotSpot hotSpot1 : mainHotSpot) {
                    if (distance(hotSpot1.getSensor(), hotSpots.get(i).getSensor()) <= dist) {
                        hotSpots.get(i).getSensor().setHotSpot(hotSpot1);
                    }
                }
            }

            if (showArrowCheckBox.isSelected()) {
                kmean.getClousters().forEach(clouster -> {
                    clouster.getPoints().forEach(sensor -> {
                        if (sensor.getStatus() == 1) {
                            centerPane.getChildren().add(sensor.getArrow());
                        }
                    });
                });
            }

            timeChange=System.currentTimeMillis();
        }

    }
}
