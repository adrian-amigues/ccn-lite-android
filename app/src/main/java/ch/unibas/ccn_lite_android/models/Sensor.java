package ch.unibas.ccn_lite_android.models;

import java.util.Calendar;

/**
 * Created by adrian on 2016-11-16.
 */

public class Sensor {
    private String id;
    private String uri;
    private Calendar initialDate;
    private int initialSeqno;
    private int looptime;
    private String light;
    private String temperature;

    public Sensor(String id, String uri, Calendar initialDate, int seqno, int looptime) {
        this.id = id;
        this.uri = uri;
        this.initialDate = initialDate;
        this.initialSeqno = seqno;
        this.looptime = looptime;
        this.light = "0";
        this.temperature = "0";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseUri() {
        return uri;
    }

    public String getUri() {
        return uri + "/" + getCurrentSeqno();
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public void updateValues(SensorReading sr) {
        this.light = sr.getLight();
        this.temperature = sr.getTemperature();
    }

    public int getCurrentSeqno() {
        Calendar dateNow = Calendar.getInstance();
        long nowTimeMillis = dateNow.getTimeInMillis();
        long initialTimeMillis = initialDate.getTimeInMillis();
        long diffTimeMillis = nowTimeMillis - initialTimeMillis;
        Double diffSeqno = Math.floor(diffTimeMillis / (looptime * 1000));
        return initialSeqno + diffSeqno.intValue();
    }
}
