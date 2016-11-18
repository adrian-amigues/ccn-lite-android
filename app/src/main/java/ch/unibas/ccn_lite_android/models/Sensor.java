package ch.unibas.ccn_lite_android.models;

/**
 * Created by adrian on 2016-11-16.
 */

public class Sensor {
    private String uri;

    public Sensor(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
