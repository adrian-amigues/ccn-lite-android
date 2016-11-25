package ch.unibas.ccn_lite_android.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.adapters.AreasAdapter;
import ch.unibas.ccn_lite_android.helpers.Helper;
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
                String areaName = areaObject.getString("location");
                Area area = new Area(areaName);
                String uri = areaObject.getString("prefix");
                String sensorMac = areaObject.getString("sensor_mac");
                Calendar sensorInitialDate = Helper.stringToDate(areaObject.getString("time"));
                int initialSeqno = Integer.parseInt(areaObject.getString("sqn"));
                int looptime = Integer.parseInt(areaObject.getString("looptime"));
                Sensor sensor = new Sensor(sensorMac, uri, sensorInitialDate, initialSeqno, looptime);
                area.addSensor(sensor);
                area.setPhotoId(R.drawable.foobar);
                areas.add(area);
            }
        } catch(org.json.JSONException e) {
            Log.e(TAG, "Unvalid Json: "+e);
        } catch (Exception e) {
            Log.e(TAG, "Error when handling Json: "+e);
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

    public int getTotalUris() {
        int count = 0;
        for (int i = 0; i < areas.size(); i++) {
            Area a = areas.get(i);
            count += a.getSensors().size();
        }
        return count;
    }
}
