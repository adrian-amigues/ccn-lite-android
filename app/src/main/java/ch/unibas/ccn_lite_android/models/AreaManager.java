package ch.unibas.ccn_lite_android.models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.helpers.Helper;

/**
 *
 * Created by adrian on 2016-11-04.
 */

public class AreaManager {
    private List<Area> areas;
    private String TAG = "unoise";
    private Context context;

    public AreaManager(Context context) {
        areas = new ArrayList<>();
        this.context = context;
    }

    public void updateFromSds(String jsonStr) {
        areas = new ArrayList<>();
        Area area;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject areaObject = jsonArray.getJSONObject(i);
                Log.i(TAG, String.valueOf(areaObject));
                Log.i(TAG, areaObject.getString("lo"));
                String areaName = areaObject.getString("lo");

                area = getAreaByName(areaName);
                if (area == null) {
                    area = new Area(areaName);
                    areas.add(area);
                }

                //JSONArray jsonNamedFunctions = areaObject.getJSONArray("nfn");
                //area.getNamedFunctions().put("prediction", jsonNamedFunctions.getString(0));
                //area.getNamedFunctions().put("historical", jsonNamedFunctions.getString(1));
//                area.getNamedFunctions().put("historical", "/historical");

                String uri = areaObject.getString("pf");
                Double timeDouble = Double.parseDouble(areaObject.getString("bt"));
                long sensorInitialDate = timeDouble.intValue();
                int initialSeqno = 1;
                int looptime = Integer.parseInt(areaObject.getString("but"));
                Log.d(TAG, "initial date: " +   String.valueOf(sensorInitialDate));

                Sensor sensor = new Sensor(uri, sensorInitialDate, initialSeqno, looptime);

                area.addSensor(sensor);
            }
        } catch (org.json.JSONException e) {
            Log.e(TAG, "Unvalid Json: "+e);
            Log.getStackTraceString(e);
            e.printStackTrace();
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
                int value1 = Integer.parseInt(a1.getSmileyValue());
                int value2 = Integer.parseInt(a2.getSmileyValue());
                return value1 - value2;
            } catch (Exception e) {
                return a1.getSmileyValue().compareTo(a2.getSmileyValue());
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

    public void updateSmileyValues() {
        for (int i = 0; i < areas.size(); i++) {
            Area a = areas.get(i);
            a.updateSmileyValue();
        }
    }

    public boolean areaIsPresent(String areaName) {
        for (int i = 0; i < areas.size(); i++) {
            Area a = areas.get(i);
            if (a.getName().equals(areaName)) {
                return true;
            }
        }
        return false;
    }

    public Area getAreaByName(String areaName) {
        for (int i = 0; i < areas.size(); i++) {
            Area a = areas.get(i);
            if (a.getName().equals(areaName)) {
                return a;
            }
        }
        return null;
    }

    //set the images into the areas based on the paths stored in the smart phone's database
    public void setAreaImages(DatabaseTable dbTable) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.take_photo_thumbnail);

        Cursor c = dbTable.selectData();
        int count = c.getCount();
        int Column1 = c.getColumnIndex("Name");
        int Column2 = c.getColumnIndex("PictureAddress");

        for(Area a : getAreas()) {
            String s = a.getName();
            a.setBitmap(icon);

//            areas.add(new Area(s, "Mote 1", R.drawable.foobar, "/demo/mote1/", icon));

            String Name = "";
            if (c != null) {
                c.moveToFirst();
                int index = 0;
                // Loop through all Results
                while (index < count) {
                    Name = c.getString(Column1);
                    if (Name.equals(s)) {
                        String fileName = c.getString(Column2);
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(fileName)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                            a.setBitmap(bitmap);
//                            areas.set(areas.size()-1, new Area(s, "Mote 1", R.drawable.foobar, "/demo/mote1/", bitmap));
                        }
                        break;
                    }
                    c.moveToNext();
                    index++;
                }
            }
        }
    }
}
