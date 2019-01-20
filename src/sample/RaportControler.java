package sample;

import Data.Sensor;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class RaportControler {
    private List<Sensor> sensors;
    public TableView raportList;

    public void initialize() {
    }

    public RaportControler() {

    }

    public void initData(List<Sensor> sensors) {
        this.sensors = sensors;

        TableColumn table = (TableColumn) raportList.getColumns().get(0);

        table.setCellValueFactory(new PropertyValueFactory<SensorData, Integer>("id"));
        table = (TableColumn) raportList.getColumns().get(1);
        table.setCellValueFactory(new PropertyValueFactory<SensorData, Integer>("points"));

        table = (TableColumn) raportList.getColumns().get(2);
        table.setCellValueFactory(new PropertyValueFactory<SensorData, Integer>("sending"));

        table= (TableColumn) raportList.getColumns().get(3);
        table.setCellValueFactory(new PropertyValueFactory<SensorData,String>("time"));

        table = (TableColumn) raportList.getColumns().get(4);
        table.setCellValueFactory(new PropertyValueFactory<SensorData, Integer>("battery"));

        ObservableList<SensorData> list = FXCollections.observableArrayList();
        for (Sensor sensor: sensors) {

            SensorData sensorData=new SensorData(sensor.getIdSensor(),sensor.getPoints().size(),sensor.getSum(),sensor.getTimeWork(),sensor.getBaterry());
            list.add(sensorData);
        }
        raportList.setItems(list);

    }

    public class SensorData {
        private final SimpleIntegerProperty id;
        private final SimpleIntegerProperty points;
        private final SimpleIntegerProperty sending;
        private final SimpleStringProperty battery;
        private final SimpleStringProperty time;


        public SensorData(int id, int points, int sending, String time,int battery) {
            this.id = new SimpleIntegerProperty(id);
            this.points = new SimpleIntegerProperty(points);
            this.sending = new SimpleIntegerProperty(sending);
            this.time = new SimpleStringProperty(time);
            String bat= String.valueOf(battery);
            bat+=" %";
            this.battery=new SimpleStringProperty(bat);
        }

        public int getId() {
            return id.get();
        }

        public SimpleIntegerProperty idProperty() {
            return id;
        }

        public void setId(int id) {
            this.id.set(id);
        }

        public int getPoints() {
            return points.get();
        }

        public SimpleIntegerProperty pointsProperty() {
            return points;
        }

        public void setPoints(int points) {
            this.points.set(points);
        }

        public int getSending() {
            return sending.get();
        }

        public SimpleIntegerProperty sendingProperty() {
            return sending;
        }

        public void setSending(int sending) {
            this.sending.set(sending);
        }

        public String getBattery() {
            return battery.get();
        }

        public SimpleStringProperty batteryProperty() {
            return battery;
        }

        public void setBattery(String battery) {
            this.battery.set(battery);
        }

        public String getTime() {
            return time.get();
        }

        public SimpleStringProperty timeProperty() {
            return time;
        }

        public void setTime(String time) {
            this.time.set(time);
        }
    }


}
