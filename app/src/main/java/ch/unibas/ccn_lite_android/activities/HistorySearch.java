package ch.unibas.ccn_lite_android.activities;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.fragments.SingleDateAndTimePickerDialogCustom;
import ch.unibas.ccn_lite_android.helpers.Helper;
import ch.unibas.ccn_lite_android.models.Sensor;
import ch.unibas.ccn_lite_android.models.SensorReading;

/**
 *
 * Created by adrian on 2016-12-12.
 */

public class HistorySearch extends AppCompatActivity {
    private String TAG = "unoise";
    private Context context = this;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_search);

        sensor = (Sensor) getIntent().getSerializableExtra("SENSOR");



        final EditText dateEditText = (EditText) findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialogCustom.Builder(context)
                        .title("Pick a Date")
                        .listener(new SingleDateAndTimePickerDialogCustom.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                dateEditText.setText(date.toString());

                                LinearLayout reading_list_layout = (LinearLayout) findViewById(R.id.reading_list);
                                reading_list_layout.setVisibility(View.VISIBLE);
                                TextView v = new TextView(context);
                                v.setText("Result for " + date.toString() + ":");
                                v.setTextColor(Color.BLACK);
                                reading_list_layout.addView(v);
                                requestHistoricalData(cal);
                            }
                        }).display();
            }
        });
    }

//    private void displayFakeData() {
//        Sensor s = new Sensor("fakeHistory", 11111111, 24, 10);
//        try {
//            SensorReading sr = new SensorReading("32-10-240.3-23.2-22", s);
//            updateShownReading(sr);
//        } catch(Exception e) {}
//    }

    private void requestHistoricalData(Calendar cal) {
        long time = cal.getTimeInMillis() / 1000;
        String uri = sensor.getUri() + "/" + Helper.getSeqno(sensor.getInitialTime(), time,
                sensor.getLooptime(), sensor.getInitialSeqno());
        new PeekHistoryTask().execute(uri);
    }

    private void updateShownReading(SensorReading sr) {
        LinearLayout lv = (LinearLayout) findViewById(R.id.reading_list);
        if (sr == null) {
            TextView v = new TextView(context);
            v.setText("Unable to fetch sensor data");
            lv.addView(v);
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout readingsList = (LinearLayout) inflater.inflate(R.layout.card_item_sensor, null);
            TextView sensorName = (TextView)readingsList.findViewById(R.id.card_item_sensor_name);
            TextView dateField = (TextView)readingsList.findViewById(R.id.card_item_date);
            TextView light = (TextView)readingsList.findViewById(R.id.card_item_sensor_light);
            TextView temperature = (TextView)readingsList.findViewById(R.id.card_item_sensor_temperature);
            TextView humidity = (TextView)readingsList.findViewById(R.id.card_item_sensor_humidity);

            TextView historyLink = (TextView)readingsList.findViewById(R.id.history_link);
            sensorName.setText(sensor.getUriWithSeqno());
            light.setText(sensor.printLight());
            temperature.setText(sensor.printTemperature());
            humidity.setText(sensor.printHumidity());
            historyLink.setVisibility(View.GONE);

            Calendar cal = Helper.getCalendarFromSeqno(sensor.getInitialTime(), sensor.getLooptime(),
                    sensor.getInitialSeqno(), Integer.parseInt(sr.getSeqNo()));
            dateField.setVisibility(View.VISIBLE);
            dateField.setText(String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", cal));
            lv.addView(readingsList);
        }
        View v = new View(context);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2
        ));
        v.setBackgroundColor(Color.parseColor("#B3B3B3"));
        lv.addView(v);
    }

    /**
     * Task in charge of using the jni androidPeek function.
     * calls androidPeek with passed parameters and treats the returned string
     */
    private class PeekHistoryTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String ccnSuite = getString(R.string.default_ccn_suite);
            String ipString = getString(R.string.localIp);
            int portInt = Integer.parseInt(getString(R.string.port));
            String contentString = params[0];
            Log.i(TAG, "PeekHistoryTask: "+contentString);
            return androidPeek(ccnSuite, ipString, portInt, contentString);
        }

        /**
         * Treats the returned string from androidPeek
         * @param result the returned string from androidPeek
         */
        protected void onPostExecute(String result) {
            if (SensorReading.isSensorReading(result)) {
                Log.i(TAG, "onPostExecute PeekHistoryTask sensor reading result = " + result);
                try {
                    SensorReading sr = new SensorReading(result, sensor);
                    updateShownReading(sr);
                } catch (Exception e) {
                    Log.e(TAG, "Unvalid SensorReading: " + e);
                    updateShownReading(null);
                }
            } else {
                Log.i(TAG, "onPostExecute PeekHistoryTask unknown result = " + result);
                updateShownReading(null);
            }
        }
    }

    static {
        System.loadLibrary("ccn-lite-android");
    }
    public native String androidPeek(String suiteString, String ipString,
                                     int portString, String contentString);
}


























