package com.haibin.TimeManager.AddTodoDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;

import com.haibin.TimeManager.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private NumberPicker numberPicker=null;//数字选择器选取某月的哪一天

    public MonthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthFragment newInstance(String param1, String param2) {
        MonthFragment fragment = new MonthFragment();
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
        //连接按钮响应事件
       /* Button btn_dayofmonth=(Button) getView().findViewById(R.id.button_DayOfMonth);
        btn_dayofmonth.setOnClickListener(this::onClick);*/
    }

  /*  public void onClick(View view) {
        switch (getView().getId()){
            case R.id.button_DayOfMonth://选择月中的某一天
                numberPicker=getView().findViewById(R.id.numberPicker);


                break;
            default: break;
        }

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_month, container, false);
    }

    @Override
    public  void onViewCreated(final View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //初始化numberPicker
        numberPicker=(NumberPicker) getView().findViewById(R.id.numberPicker);
        String[] days=new String[31];
        for(int i=1;i<=31;i++){
            days[i-1]="第"+Integer.toString(i)+"天";
        }
        numberPicker.setDisplayedValues(days);
        numberPicker.setMaxValue(30);
        numberPicker.setMinValue(0);
        numberPicker.setValue(0);
    }

    public NumberPicker GetNumberPicker(){
        return numberPicker;
    }
}