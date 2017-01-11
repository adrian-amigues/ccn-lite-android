package ch.unibas.ccn_lite_android.models;

import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by maria on 2016-11-24.
 */

public class Prediction {
    float predictionYValue[];
        ArrayList<String> predictionTimeValue = new ArrayList<String>();
    float historicalYValue[];
    ArrayList<String> historicalTimeValue = new ArrayList<String>();
    String prediction ;
    String historic;

    public Prediction(String pred, String histo){
        this.prediction = pred;
        this.historic = histo;
        parsePredictionString(prediction);
        parseHistoricString(histo);
    }

    void  parsePredictionString(String prediction){
        if(!prediction.equals("")) {
            String[] eachLineArray = prediction.split("\n");
            // ArrayList<String> predictionData = new ArrayList<String>();
            if (eachLineArray.length >= 2) {
                predictionYValue = new float[eachLineArray.length - 2];
                for (int i = 1; i < eachLineArray.length - 1; i++) {
                    String[] columns = eachLineArray[i].split("\\s+");
                    if (columns.length == 3) {
                        String time = columns[1];
                        String[] HHMMArray = time.split(":");
                        predictionTimeValue.add(HHMMArray[0] + ":" + HHMMArray[1] + ":" + HHMMArray[2]);
                        predictionYValue[i - 1] = Float.parseFloat(columns[2]);
                    }
                }
            }
        }
    }

    void  parseHistoricString(String histo){
      /*  histo = "[{'sn': 119, 'v': '2.13 ', 'u': '%', 't': 1483524866, 'n': 'Humidity'}," +
        "{'sn': 119, 'v': '28.843', 'u': 'C', 't': 1483524866, 'n': 'Temperature'}," +
                "{'sn': 119, 'v': '253.12', 'u': 'Lux', 't': 1483524866, 'n': 'Light'}," +
                " {'sn': 119, 'v': '2.13 ', 'u': '%', 't': 1483524866, 'n': 'Humidity'}," +
                " {'sn': 119, 'v': '28.843', 'u': 'C', 't': 1483524866, 'n': 'Temperature'}," +
                "{'sn': 119, 'v': '253.12', 'u': 'Lux', 't': 1483524866, 'n': 'Light'}]";*/
        if(!histo.equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(histo);
                ArrayList historyValueArrayList = new ArrayList();

                // historicalYValue = new float[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                /*long sensorInitialDate = Long.parseLong(areaObject.getString("bt"));
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(sensorInitialDate * 1000);*/

                    String attribute = object.getString("n");
                    float humidtyValue;
                    if (attribute.equals("Humidity")) {//humidity
                        humidtyValue = Float.parseFloat(object.getString("v"));
                        //historicalYValue[i]=humidtyValue;
                        historyValueArrayList.add(humidtyValue);
                        String time = object.getString("t");
                        Float f = new Float(Float.parseFloat(time));
                        long t = f.longValue();
                        //long t = Long.parseLong(time);
                        //String s = DateUtils.formatElapsedTime(t);
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(t * 1000);
                        Date d = cal.getTime();


                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(d);   // assigns calendar to given date
                        int a = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                        int b = calendar.get(Calendar.MINUTE);        // gets hour in 12h format
                        int c = calendar.get(Calendar.SECOND);


                        historicalTimeValue.add(a + ":" + b + ":" + c);
                    }



              /*  JSONArray valuesJson = object.getJSONArray("e");
                for (int j = 0; j < valuesJson.length(); j++) {
                    JSONObject valueObject = valuesJson.getJSONObject(j);



                    String attribute = valueObject.getString("n");
                    float humidtyValue;
                    if(attribute.equals("Humidity")) {//humidity
                        humidtyValue = Float.parseFloat(valueObject.getString("v"));
                        historicalYValue[i]=humidtyValue;
                         String time = valueObject.getString("bt");
                    long t = Long.parseLong(time);
                    //String s = DateUtils.formatElapsedTime(t);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(t * 1000);
                    historicalTimeValue.add(time);
                    }
                }*/
                }
                historicalYValue = new float[historyValueArrayList.size()];
                for (int hvalue = 0; hvalue < historicalYValue.length; hvalue++) {
                    historicalYValue[hvalue] = (float) historyValueArrayList.get(hvalue);
                }
            } catch (org.json.JSONException e) {
                historicalYValue = new float[0];
            } catch (Exception e) {

            }
        }
    }


        //provide the dataset and returns that as a List
    private List<ILineDataSet> getDataSet() {
        List<ILineDataSet> dataSets = null;

        String s = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());

        //An arrayList of old data
        ArrayList<Entry> valueSetPast = new ArrayList<>();
        float counter = 1.00f;
        if(historicalYValue != null) {
            for (int i = 0; i < historicalYValue.length; i++) {
                Entry v = new Entry(counter, historicalYValue[i]);
                counter++;
                valueSetPast.add(v);
            }
            counter = historicalYValue.length;
        }
        /*Entry v1e1 = new Entry(1.00f, 3);
        valueSetPast.add(v1e1);
        Entry v1e2 = new Entry(2.00f, 3);
        valueSetPast.add(v1e2);
        Entry v1e3 = new Entry(3.00f, 4);
        valueSetPast.add(v1e3);
        Entry v1e4 = new Entry(4.00f, 9);
        valueSetPast.add(v1e4);
        Entry v1e5 = new Entry(5.00f, 8);
        valueSetPast.add(v1e5);
        Entry v1e6 = new Entry(6.00f, 9);
        valueSetPast.add(v1e6);
        Entry v1e7 = new Entry(7.00f, 4);
        valueSetPast.add(v1e7);
        Entry v1e8 = new Entry(8.00f, 5);
        valueSetPast.add(v1e8);
        Entry v1e9 = new Entry(9.00f, 4);
        valueSetPast.add(v1e9);
        Entry v1e10 = new Entry(10.00f, 3.75f);
        valueSetPast.add(v1e10);*/



        //an arrayList of predicted data
        ArrayList<Entry> valueSetFuture = new ArrayList<>();
        if(predictionYValue != null) {
            for (int i = 0; i < predictionYValue.length; i++) {
                Entry v = new Entry(counter, predictionYValue[i]);
                counter++;
                valueSetFuture.add(v);
            }
        }

        /*Entry v1e10Join = new Entry(10.00f, 3.75f);
        valueSetFuture.add(v1e10Join);
        Entry v1e11 = new Entry(11.00f, 4);
        valueSetFuture.add(v1e11);
        Entry v1e12 = new Entry(12.00f, 4.3f);
        valueSetFuture.add(v1e12);
        Entry v1e13 = new Entry(13.00f, 4);
        valueSetFuture.add(v1e13);
        Entry v1e14 = new Entry(14.00f, 6);
        valueSetFuture.add(v1e14);
        Entry v1e15 = new Entry(15.00f, 4);
        valueSetFuture.add(v1e15);
        Entry v1e16 = new Entry(16.00f, 3);
        valueSetFuture.add(v1e16);*/

        LineDataSet lineDataSetPast = new LineDataSet(valueSetPast, "Brand 1");
        LineDataSet lineDataSetFuture = new LineDataSet(valueSetFuture, "Brand 2");

        //Configures the line drawn for the old data
        lineDataSetPast.setColor(Color.BLUE);
        lineDataSetPast.setCircleColor(Color.BLUE);
        lineDataSetFuture.setColor(Color.rgb(128, 232, 242));
        lineDataSetFuture.setCircleColor(Color.rgb(128, 232, 242));

        //Configures the line drawn for the predicted data
        lineDataSetFuture.enableDashedLine(10.0f, 10.0f, 0.0f);
        lineDataSetFuture.setDrawFilled(true);

        dataSets = new ArrayList<>();
        //adds both old and predicted data to the dataset
        dataSets.add(lineDataSetPast);
        dataSets.add(lineDataSetFuture);
        //dataSets.add(barDataSet2);
        return dataSets;
    }

    //Draws a chart showing the old and predicted data
    public void makepredictionGraph(LineChart bch){
        LineChart chartTest = bch;
        chartTest.setScaleEnabled(false);
        ViewPortHandler handler = chartTest.getViewPortHandler();
        LineData data = new LineData(getDataSet());
        //data.setBarWidth(0.9f);
        chartTest.setData(data);

        //Configures the description of the chart
       /* String description = "Prediction Chart";
        chartTest.setDescription(description);
        chartTest.setDescriptionPosition(handler.contentLeft() + 2*handler.offsetLeft() + 2*description.length(), handler.contentTop());
        chartTest.setDescriptionTextSize(14f);*/

        //chartTest.setFitBars(true);
        chartTest.setVisibleXRangeMaximum(7);//It makes the chart scrollable

        //Configures the legend of the chart.
      /*  Legend legend = chartTest.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        int[] legendColors = {Color.BLUE, Color.rgb(128, 232, 242)};
        String[] legendLabels = {"Past", "Prediction"};
        legend.setCustom(legendColors, legendLabels);*/

        //Configures the X Axis of the chart
        XAxis xAxis = chartTest.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(255);
        xAxis.setGranularity(1f);// minimum axis-step (interval) is 1
        xAxis.setAxisMinValue(0);
        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[historicalTimeValue.size()+predictionTimeValue.size()+1];
        quarters[0] = "";
        for(int a=1;a<=historicalTimeValue.size();a++)
            quarters[a]=historicalTimeValue.get(a-1);
        for(int a = 0;a<predictionTimeValue.size();a++)
            quarters[a+historicalTimeValue.size()+1] = predictionTimeValue.get(a);
       // { "9.00", "10.00", "11.00", "12.00", "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00", "21.00", "22.00", "23.00", "00.00", "00.01" };
        if(quarters.length != 0)
            xAxis.setValueFormatter(new AxisFormatter(quarters));
        xAxis.setLabelRotationAngle(90.0f);

        //Configures the Y Axis of the chart
        YAxis yAxis = chartTest.getAxisLeft();
        yAxis.setTextSize(14f); // set the text size
        yAxis.setTextColor(Color.BLACK);
        yAxis.setGranularity(2f); // interval 1
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMaxValue(yAxis.getAxisMaximum()+3);

        YAxis rightAxis = chartTest.getAxisRight();
        rightAxis.setEnabled(false);

        chartTest.setHighlightPerTapEnabled(false);
        chartTest.moveViewToX(xAxis.getAxisMaximum());
        chartTest.animateXY(2000, 2000);
        chartTest.invalidate();
    }

}


