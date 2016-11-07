package ch.unibas.ccn_lite_android;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by adrian on 2016-11-04.
 */

public class SensorReadingManager {
    private List<SensorReading> sensorReadings;

    public SensorReadingManager() {
        sensorReadings = new ArrayList<>();
    }

    public void addSensorReading(SensorReading sr) {
        if (sensorReadings.contains(sr)) {
            sensorReadings.remove(sr); // remove the old reading with the same id
        }
        sensorReadings.add(sr);
    }
}
