package ch.unibas.ccn_lite_android.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.unibas.ccn_lite_android.models.SensorReading;

/**
 *
 * Created by adrian on 2016-11-04.
 */

public class AreaManager {
    private List<Area> areas;
    private String TAG = "unoise";

    public AreaManager() {
        areas = new ArrayList<>();
    }

    public void updateFromSds(String jsonStr) {
        areas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject areaObject = jsonArray.getJSONObject(i);
                String areaName = areaObject.getString("name");
                JSONArray sensorUris = areaObject.getJSONArray("uris");
                Area area = new Area(areaName);
                for (int j = 0; j < sensorUris.length(); j++) {
                    String uri = sensorUris.getString(j);
                    Sensor sensor = new Sensor(uri);
                    area.addSensor(sensor);
                }
                areas.add(area);
            }
            String prefix = jsonArray.getJSONObject(0).getString("prefix");
            Log.d(TAG, "SDS prefix: "+prefix);
        } catch(org.json.JSONException e) {
            Log.e(TAG, "Unvalid Json: "+e);
        }
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public void addArea(Area area) {
        areas.add(area);
    }

    public void emptyAreas() {
        areas = new ArrayList<>();
    }
}
