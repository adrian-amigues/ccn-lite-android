package ch.unibas.ccn_lite_android.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        Button dateButton = (Button) findViewById(R.id.select_date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialogCustom.Builder(context)
                    //.bottomSheet()
                    //.curved()
                    .title("Simple")
                    .listener(new SingleDateAndTimePickerDialogCustom.Listener() {
                        @Override
                        public void onDateSelected(Date date) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            requestHistoricalData(cal);
                        }
                    }).display();
            }
        });
    }

    private void requestHistoricalData(Calendar cal) {
        long time = cal.getTimeInMillis() / 1000;
        String uri = sensor.getUri() + "/" + Helper.getSeqno(sensor.getInitialTime(), time,
                sensor.getLooptime(), sensor.getInitialSeqno());
//        String uri = sensor.getUri() + "/" + 1;
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
            TextView light = (TextView)readingsList.findViewById(R.id.card_item_sensor_light);
            TextView temperature = (TextView)readingsList.findViewById(R.id.card_item_sensor_temperature);
            TextView humidity = (TextView)readingsList.findViewById(R.id.card_item_sensor_humidity);
            TextView historyLink = (TextView)readingsList.findViewById(R.id.history_link);
            sensorName.setText(sensor.getUriWithSeqno());
            light.setText(sensor.printLight());
            temperature.setText(sensor.printTemperature());
            humidity.setText(sensor.printHumidity());
            historyLink.setEnabled(false);

            lv.addView(readingsList);
        }
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


























