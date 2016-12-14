package ch.unibas.ccn_lite_android.models;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maria on 2016-11-24.
 */

public class Prediction {

    private List<ILineDataSet> getDataSet() {
        List<ILineDataSet> dataSets = null;

        ArrayList<Entry> valueSetPast = new ArrayList<>();
        Entry v1e1 = new Entry(1.00f, 303);
        valueSetPast.add(v1e1);
        Entry v1e2 = new Entry(2.00f, 345);
        valueSetPast.add(v1e2);
        Entry v1e3 = new Entry(3.00f, 451);
        valueSetPast.add(v1e3);
        Entry v1e4 = new Entry(4.00f, 522);
        valueSetPast.add(v1e4);
        Entry v1e5 = new Entry(5.00f, 396);
        valueSetPast.add(v1e5);
        Entry v1e6 = new Entry(6.00f, 322);
        valueSetPast.add(v1e6);
        Entry v1e7 = new Entry(7.00f, 307);
        valueSetPast.add(v1e7);
//        Entry v1e8 = new Entry(8.00f, 359);
//        valueSetPast.add(v1e8);
//        Entry v1e9 = new Entry(9.00f, 419);
//        valueSetPast.add(v1e9);
//        Entry v1e10 = new Entry(10.00f, 342);
//        valueSetPast.add(v1e10);

        ArrayList<Entry> valueSetFuture = new ArrayList<>();
        Entry v1e10Join = new Entry(7.00f, 307);
        valueSetFuture.add(v1e10Join);
        Entry v1e11 = new Entry(8.00f, 340);
        valueSetFuture.add(v1e11);
        Entry v1e12 = new Entry(9.00f, 371);
        valueSetFuture.add(v1e12);
        Entry v1e13 = new Entry(10.00f, 322);
        valueSetFuture.add(v1e13);
//        Entry v1e14 = new Entry(11.00f, 275);
//        valueSetFuture.add(v1e14);
//        Entry v1e15 = new Entry(15.00f, 304);
//        valueSetFuture.add(v1e15);
//        Entry v1e16 = new Entry(16.00f, 317);
//        valueSetFuture.add(v1e16);

        LineDataSet lineDataSetPast = new LineDataSet(valueSetPast, "Brand 1");
        LineDataSet lineDataSetFuture = new LineDataSet(valueSetFuture, "Brand 2");

        lineDataSetPast.setColor(Color.BLUE);
        lineDataSetPast.setCircleColor(Color.BLUE);
        lineDataSetFuture.setColor(Color.rgb(128, 232, 242));
        lineDataSetFuture.setCircleColor(Color.rgb(128, 232, 242));

        lineDataSetFuture.enableDashedLine(10.0f, 10.0f, 0.0f);
        lineDataSetFuture.setDrawFilled(true);

        dataSets = new ArrayList<>();
        dataSets.add(lineDataSetPast);
        dataSets.add(lineDataSetFuture);
        //dataSets.add(barDataSet2);
        return dataSets;
    }

    public void makepredictionGraph(LineChart bch){
        LineChart chartTest = bch;
        chartTest.setScaleEnabled(false);
        ViewPortHandler handler = chartTest.getViewPortHandler();
        LineData data = new LineData(getDataSet());
        //data.setBarWidth(0.9f);
        chartTest.setData(data);

        String description = "Light prediction";
        chartTest.setDescription(description);
        chartTest.setDescriptionPosition(handler.contentLeft() + 2*handler.offsetLeft() + 2*description.length(), handler.contentTop());
        chartTest.setDescriptionTextSize(14f);

        //chartTest.setFitBars(true);
        chartTest.setVisibleXRangeMaximum(7);//It makes the chart scrollable

        Legend legend = chartTest.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        int[] legendColors = {Color.BLUE, Color.rgb(128, 232, 242)};
        String[] legendLabels = {"Past", "Prediction"};
        legend.setCustom(legendColors, legendLabels);

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
//        final String[] quarters = new String[] { "9.00", "10.00", "11.00", "12.00", "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00", "21.00", "22.00", "23.00", "00.00", "00.01" };
        final String[] quarters = new String[] { "9.00", "10.00", "11.00", "12.00", "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00" };
        xAxis.setValueFormatter(new AxisFormatter(quarters));
        xAxis.setLabelRotationAngle(90.0f);

        YAxis yAxis = chartTest.getAxisLeft();
        yAxis.setTextSize(14f); // set the text size
        yAxis.setTextColor(Color.BLACK);
        yAxis.setGranularity(1f); // interval 1
        yAxis.setDrawGridLines(false);

        YAxis rightAxis = chartTest.getAxisRight();
        rightAxis.setEnabled(false);

        chartTest.setHighlightPerTapEnabled(false);
        chartTest.moveViewToX(xAxis.getAxisMaximum());
        chartTest.animateXY(2000, 2000);
        chartTest.invalidate();
    }
}
