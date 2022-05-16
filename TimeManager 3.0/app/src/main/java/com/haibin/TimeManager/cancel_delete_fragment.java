package com.haibin.TimeManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.haibin.TimeManager.menu.search_future;
import com.haibin.TimeManager.menu.search_history;

public class cancel_delete_fragment extends Fragment {
    private LocalBroadcastManager localBroadcastManager;
    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
         View root=inflater.inflate(R.layout.fragment_cancel_delete,container,false);
         TextView textView=root.findViewById(R.id.cancel_delete_text);
         Button button=root.findViewById(R.id.cancel_delete_button);
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getContext());
         return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = (Button) getActivity().findViewById(R.id.cancel_delete_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Context context = v.getContext();
                if(context instanceof showActivity){
                    intent = new Intent("MyAction1");
                }else if(context instanceof showDailyTodoActivity){
                    intent = new Intent("MyAction2");
                }else if(context instanceof search_future){
                    intent = new Intent("MyAction3");
                }else if(context instanceof search_history){
                    intent = new Intent("MyAction4");
                }else if(context instanceof showDailyTodoActivity){
                    intent = new Intent("MyAction5");
                }
                else{
                    intent = new Intent("MyAction6");
                }

                boolean cancel=true;
                intent.putExtra("cancel",cancel);
                localBroadcastManager.sendBroadcast(intent);
                //Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_LONG).show();
            }
        });
    }

}
