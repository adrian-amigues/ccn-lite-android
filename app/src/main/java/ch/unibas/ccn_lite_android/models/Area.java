package ch.unibas.ccn_lite_android.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Area
 *
 * Created by adrian on 2016-10-18.
 */

public class Area {
    private String name;
    private List<Sensor> sensors;
    private String smileyValue;

    private int photoId;
    private Bitmap bitmap;
    private boolean selectedPhoto;
//    private String description;
//    private String uriBase;
//    private int valueCounter;
//    private boolean isDeprecatedArea;

//    private final int numberOfValues = 1;

//    public Area(String name, String description, int photoId, String uriBase) {
//        this.name = name;
//        this.sensors = new ArrayList<>();
//        this.smileyValue = description;
//
//        this.description = description;
//        this.photoId = photoId;
//        this.uriBase = uriBase;
//        this.isDeprecatedArea = true;
//    }
    public Area(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
        this.smileyValue = "";
//        this.isDeprecatedArea = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getSmileyValue() {
        return smileyValue;
    }

    public void setSmileyValue(String smileyValue) {
        this.smileyValue = smileyValue;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    //    public String getUri() {
//        return uriBase;
//    }

//    public boolean isDeprecatedArea() {
//        return isDeprecatedArea;
//    }

//    public int getValueCounter() {
//        return valueCounter;
//    }
//
//    public void increaseValueCounter() {
//        valueCounter = (valueCounter % numberOfValues) + 1;
//    }

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

    public void updateSmileyValue() {
        float sum = 0;
        int n = 0;
        for (int i = 0; i < sensors.size(); i++) {
            Sensor s = sensors.get(i);
            if (s.isAvailable()) {
                sum += Float.parseFloat(s.getLight());
                n++;
            }
            if (n > 0) {
                this.smileyValue = Float.toString(sum / n);
            } else {
                this.smileyValue = "";
            }
        }
    }

//    public boolean updateSensors(SensorReading sr) {
//        for (int i = 0; i < sensors.size(); i++) {
//            Sensor sensor = sensors.get(i);
//            if (sensor.getId().equals(sr.getId())) {
//                sensor.updateValues(sr);
//            }
//        }
//        return false;
//    }

//    public int getAreaSize() {
//        if (isDeprecatedArea) {
//            return 1;
//        } else {
//            return sensors.size();
//        }
//    }
}



