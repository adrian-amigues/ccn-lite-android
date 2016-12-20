package ch.unibas.ccn_lite_android.models;

import java.io.Serializable;
import java.util.Calendar;

import ch.unibas.ccn_lite_android.helpers.Helper;

/**
 *
 * Created by adrian on 2016-11-16.
 */

@SuppressWarnings("serial")
public class Sensor implements Serializable{
//    private String id;
    private String uri;
//    private Calendar initialTime;
    private long initialTime;
    private int initialSeqno;
    private int looptime;
    private String light;
    private String temperature;
    private String humidity;
    private final String lightUnit = " lx";
    private final String temperatureUnit = " °C";
    private final String humidityUnit = " %";
    private boolean available;

//    public Sensor(String id, String uri, Calendar initialTime, int seqno, int looptime) {
//    public Sensor(String uri, Calendar initialTime, int seqno, int looptime) {
    public Sensor(String uri, long initialTime, int seqno, int looptime) {
//        this.id = id;
        this.uri = uri;
        this.initialTime = initialTime;
        this.initialSeqno = seqno;
        this.looptime = looptime;
        this.light = "0";
        this.temperature = "0";
        this.humidity = "0";
        this.available = true;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getBaseUri() {
        return uri;
    }

    public String getUri() {
        return uri;
    }

    public String getUriWithSeqno() {
//        return uri + "/" + getCurrentSeqno();
        long nowTime = Calendar.getInstance().getTimeInMillis() / 1000;
        return uri + "/" + Helper.getSeqno(initialTime, nowTime, looptime, initialSeqno);
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

    public String printLight() {
        return light + lightUnit;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String printTemperature() {
        return temperature + temperatureUnit;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String printHumidity() {
        return humidity + humidityUnit;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long getInitialTime() {
        return initialTime;
    }

    public int getLooptime() {
        return looptime;
    }

    public int getInitialSeqno() {
        return initialSeqno;
    }

    public void updateValues(SensorReading sr) {
        this.light = sr.getLight();
        this.temperature = sr.getTemperature();
        this.humidity = sr.getHumidity();
    }

//    public int getCurrentSeqno() {
//        Calendar dateNow = Calendar.getInstance();
//        long nowTime = dateNow.getTimeInMillis() / 1000;
//        long initialTime = this.initialTime;
//        long diffTime = nowTime - initialTime;
//        Double diffSeqno = Math.floor(diffTime / looptime);
//        return initialSeqno + diffSeqno.intValue() - 1;
//    }
}
