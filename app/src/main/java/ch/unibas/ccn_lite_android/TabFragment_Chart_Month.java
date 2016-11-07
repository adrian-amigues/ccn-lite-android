package ch.unibas.ccn_lite_android;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;


public class TabFragment_Chart_Month extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;


    public TabFragment_Chart_Month() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TabFragment_Chart_Month newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabFragment_Chart_Month fragment = new TabFragment_Chart_Month();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_fragment__chart__month, container, false);
        BarChart chartTest = (BarChart) view.findViewById(R.id.MonthChart);
        ViewPortHandler handler = chartTest.getViewPortHandler();
        BarData data = new BarData(getDataSet2());
        data.setBarWidth(0.9f);
        chartTest.setData(data);

        String description = "Noise/Month Chart";
        chartTest.setDescription(description);
        chartTest.setDescriptionPosition(handler.contentLeft() + 2*handler.offsetLeft() + 2*description.length(), handler.contentTop());
        chartTest.setDescriptionTextSize(14f);

        chartTest.setFitBars(true);
        chartTest.setVisibleXRangeMaximum(7);//It makes the chart scrollable

        Legend legend = chartTest.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        int[] legendColors = {Color.rgb(67, 189, 1), Color.rgb(254, 161, 2), Color.rgb(253, 1, 1)};
        String[] legendLabels = {"Low", "Medium", "High"};
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
        final String[] quarters = new String[] { "", "4 Nov.", "3 Nov.", "2 Nov.", "1 Nov.", "31 Oct.", "30 Oct.",
                "29 Oct.", "28 Oct.", "27 Oct.", "26 Oct.", "25 Oct.", "24 Oct.", "23 Oct.", "22 Oct.", "21 Oct.",
                "20 Oct.", "19 Oct.", "18 Oct.", "17 Oct.", "16 Oct.", "15 Oct.", "14 Oct.", "13 Oct.", "12 Oct.",
                "11 Oct.", "10 Oct.", "9 Oct.", "8 Oct.", "7 Oct.", "6 Oct.", "5 Oct."};
        xAxis.setValueFormatter(new AxisFormatter(quarters));
        xAxis.setLabelRotationAngle(90.0f);


        YAxis yAxis = chartTest.getAxisLeft();
        yAxis.setTextSize(14f); // set the text size
        yAxis.setTextColor(Color.BLACK);
        yAxis.setGranularity(1f); // interval 1
        yAxis.setDrawGridLines(false);
        LimitLine line = new LimitLine(800f);
        yAxis.addLimitLine(line);

        YAxis rightAxis = chartTest.getAxisRight();
        rightAxis.setEnabled(false);


        chartTest.animateXY(2000, 2000);
        chartTest.invalidate();
        return view;
    }
    private List<IBarDataSet> getDataSet2() {
        List<IBarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(1.00f, 300);
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(2.00f, 450);
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(3.00f, 550);
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(4.00f, 900);
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(5.00f, 1200);
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(6.00f, 600);
        valueSet1.add(v1e6);
        BarEntry v1e7 = new BarEntry(7.00f, 200);
        valueSet1.add(v1e7);
        BarEntry v1e8 = new BarEntry(8.00f, 103);
        valueSet1.add(v1e8);
        BarEntry v1e9 = new BarEntry(9.00f, 320);
        valueSet1.add(v1e9);
        BarEntry v1e10 = new BarEntry(10.00f, 1000);
        valueSet1.add(v1e10);
        BarEntry v1e11 = new BarEntry(11.00f, 850);
        valueSet1.add(v1e11);
        BarEntry v1e12 = new BarEntry(12.00f, 920);
        valueSet1.add(v1e12);
        BarEntry v1e13 = new BarEntry(13.00f, 310);
        valueSet1.add(v1e13);
        BarEntry v1e14 = new BarEntry(14.00f, 340);
        valueSet1.add(v1e14);
        BarEntry v1e15 = new BarEntry(15.00f, 399);
        valueSet1.add(v1e15);
        BarEntry v1e16 = new BarEntry(16.00f, 810);
        valueSet1.add(v1e16);
        BarEntry v1e17 = new BarEntry(17.00f, 900);
        valueSet1.add(v1e17);
        BarEntry v1e18 = new BarEntry(18.00f, 245);
        valueSet1.add(v1e18);
        BarEntry v1e19 = new BarEntry(19.00f, 200);
        valueSet1.add(v1e19);
        BarEntry v1e20 = new BarEntry(20.00f, 450);
        valueSet1.add(v1e20);
        BarEntry v1e21 = new BarEntry(21.00f, 390);
        valueSet1.add(v1e21);
        BarEntry v1e22 = new BarEntry(22.00f, 202);
        valueSet1.add(v1e22);
        BarEntry v1e23 = new BarEntry(23.00f, 450);
        valueSet1.add(v1e23);
        BarEntry v1e24 = new BarEntry(24.00f, 1200);
        valueSet1.add(v1e24);
        BarEntry v1e25 = new BarEntry(25.00f, 100);
        valueSet1.add(v1e25);
        BarEntry v1e26 = new BarEntry(26.00f, 110);
        valueSet1.add(v1e26);
        BarEntry v1e27 = new BarEntry(27.00f, 260);
        valueSet1.add(v1e27);
        BarEntry v1e28 = new BarEntry(28.00f, 802);
        valueSet1.add(v1e28);
        BarEntry v1e29 = new BarEntry(29.00f, 500);
        valueSet1.add(v1e29);
        BarEntry v1e30 = new BarEntry(30.00f, 350);
        valueSet1.add(v1e30);
        BarEntry v1e31 = new BarEntry(31.00f, 320);
        valueSet1.add(v1e31);



        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        int []colors = new int[valueSet1.size()];
        for(int i=0; i<valueSet1.size(); i++){
            if(valueSet1.get(i).getY() < 400)
                colors[i] = Color.rgb(67, 189, 1);
            else if(valueSet1.get(i).getY() >= 400 && valueSet1.get(i).getY() < 800)
                colors[i] = Color.rgb(254, 161, 2);
            else
                colors[i] = Color.rgb(253, 1, 1);
        }
        barDataSet1.setColors(colors);
       /* BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Brand 2");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);*/


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        //dataSets.add(barDataSet2);
        return dataSets;
    }


}
