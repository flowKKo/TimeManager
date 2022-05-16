package com.haibin.TimeManager.AddTodoDialog;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.haibin.TimeManager.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YearFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Calendar calendar= Calendar.getInstance(Locale.CHINA);
    Button btn_select;//日期选择按钮
    private DatePickerDialog datePickerDialog;//日期选择对话框

    public YearFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YearFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YearFragment newInstance(String param1, String param2) {
        YearFragment fragment = new YearFragment();
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
        initDialog();//初始化对话框
    }

    private void initDialog() {
        datePickerDialog=new DatePickerDialog(getContext(), 0, null,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_year, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置默认今天
        btn_select=((Button)getView().findViewById(R.id.button_year));
        btn_select.setText(String.format("%02d",calendar.get(Calendar.MONTH)+1)+"月"+
                String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH))+"日");
        btn_select.setOnClickListener(this::onClick);//设置监听

    }

    private void onClick(View view) {
        switch (view.getId()){
            case R.id.button_year://显示日期选择对话框
                datePickerDialog.show();
                break;
            default: break;
        }
    }

    public DatePickerDialog GetDatePickerDialog(){//获取日期选择对话框
        return datePickerDialog;
    }
}