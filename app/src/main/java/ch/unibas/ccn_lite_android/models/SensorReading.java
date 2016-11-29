package ch.unibas.ccn_lite_android.models;

import android.util.Log;

import ch.unibas.ccn_lite_android.helpers.Helper;

/**
 * Created by adrian on 2016-11-02.
 */

public class SensorReading {
    private Sensor sensor;
    private String seqNo;
    private String light;
    private String temperature;
    private final String TAG = "SensorReading";

    public SensorReading(String content, Sensor sensor) throws Exception {
        content = Helper.cleanResultString(content);
        this.sensor = sensor;
        try {
            String[] parts = content.split("-");
            this.seqNo = parts[0];
//            this.temperature = Integer.toString(Math.round(Float.parseFloat(parts[1])));
//            this.light = Integer.toString(Math.round(Float.parseFloat(parts[2])));
            this.light = parts[1];
            this.temperature = parts[2];
        } catch(Exception e) {
            Log.e(TAG, "Error creating a SensorReading");
            throw new Exception("Error creating a SensorReading: " + e);
        }
    }

    public static boolean isSensorReading(String content) {
        return (content.split("-").length == 3);
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
}
