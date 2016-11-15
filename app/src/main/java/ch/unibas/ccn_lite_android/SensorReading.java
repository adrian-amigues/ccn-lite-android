package ch.unibas.ccn_lite_android;

import android.util.Log;

/**
 * Created by adrian on 2016-11-02.
 */

public class SensorReading {
    private String id;
    private String seqNo;
    private String light;
    private String temperature;
    private final String TAG = "SensorReading";

    public SensorReading(String content) throws Exception {
        try {
            String[] parts = content.split("-");
            this.id = parts[0];
            this.seqNo = parts[1];
            this.light = Integer.toString(Math.round(Float.parseFloat(parts[2])));
            this.temperature = Integer.toString(Math.round(Float.parseFloat(parts[3])));
        } catch(Exception e) {
            Log.e(TAG, "Error creating a SensorReading");
            throw new Exception("Error creating a SensorReading: " + e);
        }
    }

    public static boolean isSensorReading(String content) {
        return (content.split("-").length == 4);
    }

    @Override
    public boolean equals(Object object)
    {
        boolean same = false;
        if (object != null && object instanceof SensorReading)
        {
            same = this.id.equals(((SensorReading) object).getId());
        }
        return same;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
