package com.haibin.TimeManager.Statistics;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.haibin.TimeManager.Pomodoro.Clock_Database;
import com.haibin.TimeManager.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.haibin.TimeManager.Todo.Todo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TodoChartFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LineChart lineChart;
    private LineDataSet lineDataSet;
    private LineData lineData;
    private String[] DateStrs = new String[7];
    private int[] Datas = new int[7];


    public TodoChartFragment() {
        // Required empty public constructor
    }


    public static TodoChartFragment newInstance(String param1, String param2) {
        TodoChartFragment fragment = new TodoChartFragment();
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
        return inflater.inflate(R.layout.fragment_todo_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart =  view.findViewById(R.id.todoChart);
        java.util.Calendar cld= java.util.Calendar.getInstance(Locale.CHINA);//创建一个日历
        List<Todo> mToDoList;
        for(int i = 6 ; i >= 0 ; i--){
            String curDate = Integer.toString(cld.get(java.util.Calendar.YEAR))+'/'+
                    String.format("%02d",cld.get(java.util.Calendar.MONTH)+1)+'/'+
                    String.format("%02d",cld.get(java.util.Calendar.DAY_OF_MONTH));

            String showDate = String.format("%02d",cld.get(java.util.Calendar.MONTH)+1)+'.'+
                    String.format("%02d",cld.get(java.util.Calendar.DAY_OF_MONTH));


            mToDoList= LitePal.where("is_delete = ? and date=? and is_done = ?", "0",curDate, "1").find(Todo.class);
            DateStrs[i] = showDate;
            Datas[i] = mToDoList.size();
            cld.add(Calendar.DAY_OF_MONTH,-1);
        }

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
        float x = dm.widthPixels / (float)2.2;
        float y = dm.heightPixels / 30;
        lineChart.getDescription().setText("七日工作量趋势");//设置文本
        lineChart.getDescription().setTextSize(18f); //设置文本大小
        lineChart.getDescription().setTextColor(Color.BLACK);//设置文本颜色
        lineChart.getDescription().setPosition(x,y);
    }

    private void setXAxis(){
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setGranularity(3);
        xAxis.setLabelCount(7, true);
        xAxis.setValueFormatter(new ValueFormatter(){
            @Override
            public String getFormattedValue(float v) {
                if (v < 0 || !isIntegerForDouble(v))
                    return "";
                return DateStrs[(int) (v - 1)];
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
        List<Entry> entries = new ArrayList<Entry>();
        for(int i = 0 ; i < Datas.length; i++){
            entries.add(new Entry(i+1, Datas[i]));
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