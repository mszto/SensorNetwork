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
    private AnimationTimer animationTimer;
    private List<HotSpot> hotSpots = new ArrayList<>();
    private int time = 0, min = 0, i = 0, gold = 0;

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
            sensors.forEach(sensor -> {
                if (sensor.getStatus() == 1) {
                    centerPane.getChildren().add(sensor.getArrow());
                }
            });
            for (int i = 1; i < hotSpots.size(); i++) {
                centerPane.getChildren().add(hotSpots.get(i).getSensor().getArrow());
            }

        } else {
            sensors.forEach(sensor -> {
                centerPane.getChildren().remove(sensor.getArrow());
            });
            for (int i = 1; i < hotSpots.size(); i++) {
                centerPane.getChildren().remove(hotSpots.get(i).getSensor().getArrow());
            }
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

    private void setLifeNetwork() {
        Set<Point> pointsInSensorsRange = new HashSet<>();
        List<Sensor> sensorsOn = new ArrayList<>();

        sensorsOn.addAll(sensors);
        sensorsOn.removeAll(sensorsOff);

        sensorsOn.forEach(sensor -> {
            pointsInSensorsRange.addAll(sensor.getPoints());
        });


        if (points.size() != 0) {

            lifeNetwork.setText(String.valueOf((int) (pointsInSensorsRange.size() / (double) points.size() * 100)));
        }
    }

    private double distance(Sensor sensor1, Sensor sensor2) {
        return Math.sqrt(Math.pow(sensor1.getX() - sensor2.getX(), 2) + Math.pow(sensor1.getY() - sensor2.getY(), 2));
    }


    //metoda odpowiada za nasłuchiwane przycisku start i zainicjowanie symulacji
    public void start(ActionEvent event) {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        time = 0;
        min = 0;
        centerPane.getChildren().clear();
        sensors.clear();
        points.clear();
        hotSpots.clear();

        showRangeCheckBox.setSelected(false);
        showArrowCheckBox.setSelected(false);
        showPointsCheckBox.setSelected(true);

        addSensors();
        addPoints();
        //addHotSpots();

        setLifeNetwork();

        // tutaj jest zapętlenie tzn metoda onUpdate wykonywana jest cały czas po wywołaniy animationtimer start w lini 154
        if (false) {
            ClousteringHierarhy clousteringHierarhy = new ClousteringHierarhy();
            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    clousteringHierarhy.onUpdate();
                }
            };
        } else {
            hotSpots.add(new HotSpot());
            PathFinds pathFind = new PathFinds(sensors, hotSpots.get(0).getSensor());
            pathFind.setPath();
            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    pathFind.onUpdate();
                }
            };
        }
        animationTimer.start();
    }

    private class PathFinds {
        //private List<Sensor> sensors;
        private Sensor goal;
        private long startTime;
        private Map<Sensor, Double> times;
        List<Sensor> sensorsOn;
        private boolean hotSpotSed = false, changeSensorSend = false, pom = false, mainFlag = false;
        private long timeon, timeoff, time3, time4, timeChange, timeCH;
        public PathFinds() {

        }

        public PathFinds(List<Sensor> sensors, Sensor goal) {
            this.times=new HashMap<>();
            this.sensorsOn=new ArrayList<>();
            sensorsOn.addAll(sensors);
            for (Sensor sensor:sensors) {
                times.put(sensor,0.0);
            }
            this.startTime = System.currentTimeMillis();
            this.goal = goal;
            this.timeon = startTime;
            this.timeoff = startTime;
            this.timeChange = startTime;
            this.timeCH = startTime;
        }

        private double distance(Sensor sensor1, Sensor sensor2) {
            return Math.sqrt(Math.pow(sensor1.getX() - sensor2.getX(), 2) + Math.pow(sensor1.getY() - sensor2.getY(), 2));
        }


        public void setPath() {
            List<Sensor> sen1 = new ArrayList<>();
            sen1.add(hotSpots.get(0).getSensor());
            List<Sensor> close = new ArrayList<>();
            List<Sensor> sensorOn=new ArrayList<>();
            sensorOn.addAll(sensors);
            sensorOn.remove(sensorsOff);
            sen1.addAll(sensorsOn);
            Sensor neigh = hotSpots.get(0).getSensor();
            double dist;
            boolean chen;

            sensors.forEach(sensor -> {
                centerPane.getChildren().remove(sensor.getArrow());
            });
            for (Sensor sensor : sensorsOn) {
                chen = true;
                close.clear();
                dist = Double.MAX_VALUE;
                sen1.clear();
                sen1.add(hotSpots.get(0).getSensor());
                sen1.addAll(sensors);
                while (chen) {
                    for (Sensor sensor1 : sen1) {

                        if (!sensor1.equals(sensor)) {
                            double dist2 = distance(sensor1, sensor);
                            if (dist2 < dist) {
                                dist = dist2;
                                neigh = sensor1;
                            }
                        }
                    }
                    if (!neigh.equals(goal)) {
                        if (distance(sensor, goal) > distance(neigh, goal)) {
                            sensor.setHotSpot(new HotSpot(neigh, 1));
                            chen = false;
                        } else {
                            sen1.remove(neigh);
                            dist = Double.MAX_VALUE;

                        }
                    } else {
                        sensor.setHotSpot(new HotSpot(neigh, 1));
                        chen = false;
                    }
                }
            }

            if (showArrowCheckBox.isSelected()) {
                sensorsOn.forEach(sensor -> {
                    centerPane.getChildren().addAll(sensor.getArrow());
                });
            }

        }

        public void onUpdate() {
            time = (int) ((int) System.currentTimeMillis() - startTime);

            if (Integer.parseInt(lifeNetwork.getText()) <= 90) {

            }
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
                int j = random.nextInt(points.size() / 2);
                for (int k = 0; k < j; k++) {
                    int b = random.nextInt(points.size());
                    points.get(b).setFill(Color.ORANGE);
                    stack.push(points.get(b));
                }
                sensorsOn.forEach(sensor -> {
                    if (sensor.reductionbatteryLevel(time) == 0) {
                        setLifeNetwork();
                        sensorsOff.add(sensor);
                    }
                });

                timeoff = System.currentTimeMillis();
                pom = true;
            }

            //zakonczenie wysyłania danych z sensorów
            if ((System.currentTimeMillis() - timeoff) >= 500 && pom) {
                while (!stack.empty()) {
                    Point point = stack.pop();
                    point.setFill(Color.BLACK);
                    sensorsOn.forEach(sensor -> {
                        sensor.addPointRead(point);
                    });
                }
                timeon = System.currentTimeMillis();
                sensorsOn.forEach(sensor -> {
                    if (sensor.getStatus() == 1 && sensor.isGetNewData()) {
                        sensor.getArrow().setFill(Color.GOLD);
                        changeSensorSend = true;
                        times.put(sensor, Double.valueOf(System.currentTimeMillis()));
                    }

                });
                pom = false;
            }

            for(Sensor sensor:sensors){

                if( System.currentTimeMillis()-times.get(sensor).doubleValue()>=500 && sensor.isGetNewData()){
                    sensor.getArrow().setFill(Color.BLACK);
                    sensor.setGetNewData(false);
                    sensor.getHotSpot().getSensor().setGetNewData(true);
                    sensor.getHotSpot().getSensor().getArrow().setFill(Color.GOLD);
                    if(sensor.reductionbatteryLevel(time)==0){
                        //sensorsOn.remove(sensor);
                        sensorsOff.add(sensor);
                        setPath();
                        setLifeNetwork();
                    }
                    times.put(sensor.getHotSpot().getSensor(), Double.valueOf(System.currentTimeMillis()));
                }
            }

        }
    }

    private class ClousteringHierarhy {

        private boolean hotSpotSed = false, changeSensorSend = false, pom = false, mainFlag = false; // flagi odpowiadające za ustawienie stanu senorów false-sensor nie przesyłą danych true-sensor wysyła dane
        private long timeon, timeoff, time3, time4, timeChange, timeCH;
        private List<HotSpot> mainHotSpot = new ArrayList<>();
        private long startTime;
        private List<HotSpot> secondHotSpots = new ArrayList<>();

        ClousteringHierarhy() {

            kmean.createClousters(sensors);
            addHotSpots();
            sensors.clear();
            kmean.getClousters().forEach(clouster -> {
                sensors.addAll(clouster.getPoints());
            });
            hotSpots.forEach(hotSpot -> {
                sensors.remove(hotSpot.getSensor());
            });
            this.startTime = System.currentTimeMillis();
            this.timeon = startTime;
            this.timeoff = startTime;
            this.timeChange = startTime;
            this.timeCH = startTime;

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

            secondHotSpots.addAll(hotSpots);
            secondHotSpots.removeAll(mainHotSpot);
            secondHotSpots.remove(0);
            for (int i = 0; i < secondHotSpots.size(); i++) {
                double dist = Integer.MAX_VALUE;
                for (HotSpot hotSpot1 : mainHotSpot) {
                    if (distance(hotSpot1.getSensor(), secondHotSpots.get(i).getSensor()) <= dist) {
                        secondHotSpots.get(i).getSensor().setHotSpot(hotSpot1);
                        dist = distance(hotSpot1.getSensor(), secondHotSpots.get(i).getSensor());
                    }
                }
            }

            HotSpot SIM = hotSpots.get(0);
            mainHotSpot.forEach(hotSpot -> {
                hotSpot.getSensor().setHotSpot(SIM);  // ustawnienie hotspoda do którego są presyłane dane z klastrów
                // hotSpots.remove(hotSpot);
                hotSpot.getSensor().setFill(Color.PINK);
            });

            hotSpots.forEach(hotSpot -> {
                hotSpot.getSensor().setFill(Color.PINK);
            });
        }

        private void onUpdate() {
            time = (int) ((int) System.currentTimeMillis() - startTime);
            if (Integer.parseInt(lifeNetwork.getText()) <= 90) {

            }
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
                int j = random.nextInt(points.size() / 2);
                for (int k = 0; k < j; k++) {
                    int b = random.nextInt(points.size());
                    points.get(b).setFill(Color.ORANGE);
                    stack.push(points.get(b));
                }
                sensors.forEach(sensor -> {
                    if (sensor.reductionbatteryLevel(time) == 0) {
                        setLifeNetwork();
                        sensorsOff.add(sensor);
                    }
                });

                timeoff = System.currentTimeMillis();
                pom = true;
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
                pom = false;
            }

            //zakonczenie wysłania danych z sensorów oraz rozpoczęcie wysyłania danych z hotspotów w klastrach
            if (System.currentTimeMillis() - timeon >= 500 && changeSensorSend) {

                sensors.forEach(sensor -> {
                    if (sensor.isGetNewData() && sensor.getStatus() == 1) {
                        sensor.getArrow().setFill(Color.BLACK);
                        if (sensor.reductionbatteryLevel(time) == 0) {
                            sensorsOff.add(sensor);
                            setLifeNetwork();
                        }
                        sensor.setGetNewData(false);
                        sensor.getHotSpot().getSensor().setGetNewData(true);
                    }
                });
                changeSensorSend = false;
            }

            if (System.currentTimeMillis() - timeCH >= 5000) {
                secondHotSpots.forEach(hotSpot -> {
                    if (hotSpot.getSensor().getStatus() == 1 && hotSpot.getSensor().isGetNewData()) {
                        hotSpot.send();
                        hotSpot.getSensor().setGetNewData(true);
                    }
                });
                hotSpotSed = true;
                timeCH = System.currentTimeMillis();
                time3 = System.currentTimeMillis();
            }

            //zakonczenie wysyłania danych z hotspotów w klastrach
            if (System.currentTimeMillis() - time3 >= 500 && hotSpotSed) {
                secondHotSpots.forEach(hotSpot -> {
                    if (hotSpot.getSensor().isGetNewData() && hotSpot.getSensor().getStatus() == 1) {
                        hotSpot.sendOff();
                        hotSpot.getSensor().reductionbatteryLevel(time);
                        hotSpot.getSensor().setGetNewData(false);
                        hotSpot.getSensor().getHotSpot().getSensor().setGetNewData(true);
                    }
                });
                hotSpotSed = false;
                mainHotSpot.forEach(hotSpot -> {
                    if (hotSpot.getSensor().isGetNewData() && hotSpot.getSensor().getStatus() == 1)
                        hotSpot.send();
                });
                time4 = System.currentTimeMillis();
                mainFlag = true;
            }

            if (System.currentTimeMillis() - time4 >= 500 && mainFlag) {
                mainHotSpot.forEach(hotSpot -> {
                    if (hotSpot.getSensor().isGetNewData() && hotSpot.getSensor().getStatus() == 1) {
                        hotSpot.sendOff();
                        hotSpot.getSensor().batteryLevelAfterSend(time);
                        hotSpot.getSensor().setGetNewData(false);
                    }
                });
                mainFlag = false;
            }


            if (System.currentTimeMillis() - timeChange >= 15000) {
                Random random = new Random();


                mainHotSpot.clear();
                secondHotSpots.clear();
                if (showArrowCheckBox.isSelected()) {
                    sensors.forEach(sensor -> {
                        if (sensor.getStatus() == 1) {
                            // centerPane.getChildren().remove(sensor.getArrow());
                        }
                    });
                    for (int i = 1; i < hotSpots.size(); i++) {
                        centerPane.getChildren().remove(hotSpots.get(i).getSensor().getArrow());
                    }
                }


                for (int i = 1; i < (hotSpots.size() - 1) / 2; i++) {
                    int j = random.nextInt(hotSpots.size() - 1) + 1;
                    // hotSpots.get(j).setMain(true);
                    mainHotSpot.add(hotSpots.get(j));
                }
                secondHotSpots.addAll(hotSpots);
                secondHotSpots.removeAll(mainHotSpot);
                secondHotSpots.remove(0);

                HotSpot SIM = hotSpots.get(0);
                mainHotSpot.forEach(hotSpot -> {
                    hotSpot.getSensor().setHotSpot(SIM);  // ustawnienie hotspoda do którego są presyłane dane z klastrów
                    // hotSpots.remove(hotSpot);
                    //sensors.remove(hotSpot.getSensor());
                });

                for (int i = 0; i < secondHotSpots.size(); i++) {
                    double dist = Integer.MAX_VALUE;
                    for (HotSpot hotSpot1 : mainHotSpot) {
                        if (distance(hotSpot1.getSensor(), secondHotSpots.get(i).getSensor()) <= dist) {
                            secondHotSpots.get(i).getSensor().setHotSpot(hotSpot1);
                            dist = distance(hotSpot1.getSensor(), secondHotSpots.get(i).getSensor());
                        }
                    }
                }

                if (showArrowCheckBox.isSelected()) {
                    sensors.forEach(sensor -> {
                        if (sensor.getStatus() == 1) {
                            //     centerPane.getChildren().add(sensor.getArrow());
                        }
                    });
                    for (int i = 1; i < hotSpots.size(); i++) {
                        centerPane.getChildren().add(hotSpots.get(i).getSensor().getArrow());
                    }
                }

                timeChange = System.currentTimeMillis();
            }

        }


    }
}

