package ch.unibas.ccn_lite_android.fragments;

import android.graphics.Color;
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

import ch.unibas.ccn_lite_android.AxisFormatter;
import ch.unibas.ccn_lite_android.R;


public class TabFragment_Chart_Week extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private int mPage;

    // TODO: Rename and change types of parameters

    public static final String ARG_PAGE = "ARG_PAGE";

    public TabFragment_Chart_Week() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TabFragment_Chart_Week newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabFragment_Chart_Week fragment = new TabFragment_Chart_Week();
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
        View view = inflater.inflate(R.layout.fragment_tab_fragment__chart__week, container, false);
        BarChart chartTest = (BarChart) view.findViewById(R.id.WeekChart);
        ViewPortHandler handler = chartTest.getViewPortHandler();
        BarData data = new BarData(getDataSet2());
        data.setBarWidth(0.9f);
        chartTest.setData(data);

        String description = "Noise/Week Chart";
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
        final String[] quarters = new String[] { "", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
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

        chartTest.setHighlightPerTapEnabled(false);
        chartTest.animateXY(2000, 2000);
        chartTest.invalidate();
        return view;
    }

    private List<IBarDataSet> getDataSet2() {
        List<IBarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(1.00f, 1000);
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(2.00f, 900);
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(3.00f, 1200);
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(4.00f, 850);
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(5.00f, 890);
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(6.00f, 200);
        valueSet1.add(v1e6);
        BarEntry v1e7 = new BarEntry(7.00f, 350);
        valueSet1.add(v1e7);



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
