package com.haibin.TimeManager.Statistics;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.haibin.TimeManager.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TomatoChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TomatoChartFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LineChart lineChart;
    private LineDataSet lineDataSet;
    private LineData lineData;

    public TomatoChartFragment() {
        // Required empty public constructor
    }


    public static TomatoChartFragment newInstance(String param1, String param2) {
        TomatoChartFragment fragment = new TomatoChartFragment();
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
        return inflater.inflate(R.layout.fragment_tomato_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart =  view.findViewById(R.id.tomatoChart);
        setDisplay();
        setXAxis();
        setYAxis();
        setDataset();
        lineChart.setData(lineData);
    }


    public static boolean isIntegerForDouble(double obj) {
        double eps = 1e-4;  // 精度范围
        return obj-Math.floor(obj) < eps;
    }


    private void setDisplay(){
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setExtraOffsets(10,60,10,20);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(true);
        lineChart.setBackgroundColor(ColorTemplate.rgb("#FFFFFF"));


        WindowManager wm=(WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float x = dm.widthPixels / (float)1.15;
        float y = dm.heightPixels / 30;
        lineChart.getDescription().setText("七日番茄专注时长");//设置文本
        lineChart.getDescription().setTextSize(18f); //设置文本大小
        lineChart.getDescription().setTextColor(Color.BLACK);//设置文本颜色
        lineChart.getDescription().setPosition(x,y);
    }

    private void setXAxis(){
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        final String weeks[] = { "Sun", "Mon", "Tue", "Wed", "Thu",
                "Fri", "Sat"};
        xAxis.setGranularity(3);
        xAxis.setLabelCount(7, true);
        xAxis.setValueFormatter(new ValueFormatter(){
            @Override
            public String getFormattedValue(float v) {
                if (v < 0 || !isIntegerForDouble(v))
                    return "";
                return weeks[(int) (v - 1)];
            }
        });
    }

    private void setYAxis(){
        YAxis yAxis2 = lineChart.getAxisRight();
        yAxis2.setEnabled(false);
        YAxis yAxis1 = lineChart.getAxisLeft();
        yAxis1.setXOffset(10);
        yAxis1.setDrawGridLines(false);
        yAxis1.setValueFormatter(new ValueFormatter(){
            @Override
            public String getFormattedValue(float v) {
                if(v < 0 || !isIntegerForDouble(v))
                    return "";

                String str = v + "";
                if (str.length()==0) {
                    return str;
                }
                return str.substring(0, str.indexOf("."));//设置自己的返回位数
            }
        });
    }

    private void setDataset(){
        //int datas[] = {15,20,7,13,56,34,21};
        int datas[] = {5,15,1,2,3,4,12};
        List<Entry> entries = new ArrayList<Entry>();
        for(int i = 0 ; i < datas.length; i++){
            entries.add(new Entry(i+1, datas[i]));
        }
        lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setFillColor(Color.GRAY);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setColor(ColorTemplate.rgb("#2E8B57"));
        lineDataSet.setLineWidth(2);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleColor(ColorTemplate.rgb("#2E8B57"));
        lineDataSet.setValueTextSize(10f);
        lineData = new LineData(lineDataSet);
        lineDataSet.setValueFormatter(new ValueFormatter(){
            @Override
            public String getFormattedValue(float v) {
                String str = v + "";
                if (str.length()==0) {
                    return str;
                }
                return str.substring(0, str.indexOf("."));
            }
        });
    }
}