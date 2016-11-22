package ch.unibas.ccn_lite_android.models;

/**
 * Created by adrian on 2016-11-16.
 */

public class Sensor {
    private String id;
    private String uri;
    private String light;
    private String temperature;

    public Sensor(String uri) {
        this.uri = uri;
        this.light = "0";
        this.temperature = "0";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
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
}
