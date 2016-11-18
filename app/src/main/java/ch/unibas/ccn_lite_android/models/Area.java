package ch.unibas.ccn_lite_android.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.unibas.ccn_lite_android.R;

/**
 * Represents an Area
 *
 * Created by adrian on 2016-10-18.
 */

public class Area {
    private String name;
    private List<Sensor> sensors;
    private String currentValue;

    private String description;
    private int photoId;
    private String uriBase;
    private int valueCounter;

    private final int numberOfValues = 1;

    public Area(String name, String description, int photoId, String uriBase) {
        this.name = name;
        this.sensors = new ArrayList<>();

        this.description = description;
        this.currentValue = description;
        this.photoId = photoId;
        this.uriBase = uriBase;
        this.valueCounter = 1;
    }
    public Area(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
        this.currentValue = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getUri() {
//        return uriBase + valueCounter;
        return uriBase;
    }

    public int getValueCounter() {
        return valueCounter;
    }

    public void increaseValueCounter() {
        valueCounter = (valueCounter % numberOfValues) + 1;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public Sensor getSensor(int i) {
        return sensors.get(i);
    }

    public void emptySensors() {
        sensors = new ArrayList<>();
    }

    public void addSensor(Sensor s) {
        sensors.add(s);
    }
}



