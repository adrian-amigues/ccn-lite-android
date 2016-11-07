package ch.unibas.ccn_lite_android;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


public class TabFragment_Chart extends Fragment {


    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static TabFragment_Chart newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabFragment_Chart fragment = new TabFragment_Chart();
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
        View view = inflater.inflate(R.layout.fragment_tab_fragment__chart, container, false);
        BarChart chartTest = (BarChart) view.findViewById(R.id.DayChart);
        ViewPortHandler handler = chartTest.getViewPortHandler();
        BarData data = new BarData(getDataSet2());
        data.setBarWidth(0.9f);
        chartTest.setData(data);

        String description = "Noise/Day Chart";
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
        final String[] quarters = new String[] { "9.00", "10.00", "11.00", "12.00", "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00", "21.00", "22.00", "23.00", "00.00", "00.01" };
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

        chartTest.moveViewToX(xAxis.getAxisMaximum());
        chartTest.animateXY(2000, 2000);
        chartTest.invalidate();
        return view;
    }
    private List<IBarDataSet> getDataSet2() {
        List<IBarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(1.00f, 300);
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(2.00f, 350);
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(3.00f, 498);
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(4.00f, 1200);
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(5.00f, 1100);
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(6.00f, 900);
        valueSet1.add(v1e6);
        BarEntry v1e7 = new BarEntry(7.00f, 307);
        valueSet1.add(v1e7);
        BarEntry v1e8 = new BarEntry(8.00f, 350);
        valueSet1.add(v1e8);
        BarEntry v1e9 = new BarEntry(9.00f, 790);
        valueSet1.add(v1e9);
        BarEntry v1e10 = new BarEntry(10.00f, 325);
        valueSet1.add(v1e10);
        BarEntry v1e11 = new BarEntry(11.00f, 730);
        valueSet1.add(v1e11);
        BarEntry v1e12 = new BarEntry(12.00f, 298);
        valueSet1.add(v1e12);
        BarEntry v1e13 = new BarEntry(13.00f, 210);
        valueSet1.add(v1e13);
        BarEntry v1e14 = new BarEntry(14.00f, 289);
        valueSet1.add(v1e14);
        BarEntry v1e15 = new BarEntry(15.00f, 304);
        valueSet1.add(v1e15);
        BarEntry v1e16 = new BarEntry(16.00f, 317);
        valueSet1.add(v1e16);



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




    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TabFragment_Chart() {
        // Required empty public constructor
    }

    *//**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment_Chart.
     *//*
    // TODO: Rename and change types and number of parameters
    public static TabFragment_Chart newInstance(String param1, String param2) {
        TabFragment_Chart fragment = new TabFragment_Chart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_fragment__chart, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
