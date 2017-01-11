package ch.unibas.ccn_lite_android.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, String> namedFunctions;

    public Area(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
        this.smileyValue = "";
        this.namedFunctions = new HashMap<>();
        this.photoId = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Map<String, String> getNamedFunctions() {
        return namedFunctions;
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
}
