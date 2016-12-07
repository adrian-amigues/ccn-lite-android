package ch.unibas.ccn_lite_android.models;

import android.util.Log;

import ch.unibas.ccn_lite_android.helpers.Helper;

/**
 * Created by adrian on 2016-11-02.
 */

public class SensorReading {
    private Sensor sensor;
    private String seqNo;
    private String interval;
    private String light;
    private String temperature;
    private String humidity;
    private final String TAG = "SensorReading";

    public SensorReading(String content, Sensor sensor) throws Exception {
        content = Helper.cleanResultString(content);
        this.sensor = sensor;
        try {
            String[] parts = content.split("-");
            this.seqNo = parts[0];
            this.interval = parts[1];
            this.light = parts[2];
            this.temperature = parts[3];
            this.humidity = parts[4];
        } catch(Exception e) {
            Log.e(TAG, "Error creating a SensorReading");
            throw new Exception("Error creating a SensorReading: " + e);
        }
    }

    public static boolean isSensorReading(String content) {
        return (content.split("-").length == 5);
    }

//    @Override
//    public boolean equals(Object object)
//    {
//        boolean same = false;
//        if (object != null && object instanceof SensorReading)
//        {
//            same = this.id.equals(((SensorReading) object).getId());
//        }
//        return same;
//        return false;
//    }

    public void updateSensorValues() {
        sensor.updateValues(this);
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
