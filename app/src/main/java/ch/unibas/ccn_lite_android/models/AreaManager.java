package ch.unibas.ccn_lite_android.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.unibas.ccn_lite_android.adapters.AreasAdapter;
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

    public int getNumberAreas() {
        return areas.size();
    }

    public void sortAreas() {
        Collections.sort(areas, new AreaComparator());
    }

    private class AreaComparator implements Comparator<Area> {
        @Override
        public int compare(Area a1, Area a2) {
            try {
                int value1 = Integer.parseInt(a1.getCurrentValue());
                int value2 = Integer.parseInt(a2.getCurrentValue());
                return value1 - value2;
            } catch (Exception e) {
                return a1.getCurrentValue().compareTo(a2.getCurrentValue());
            }
        }
    }
}
