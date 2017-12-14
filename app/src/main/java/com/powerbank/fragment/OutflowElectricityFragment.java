package com.powerbank.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.toolview.View.view.MyDialView;
import com.powerbank.R;

/**
 * Created by Administrator on 2017/12/14.
 */

public class OutflowElectricityFragment extends Fragment{
    private String TAG="OutflowElectricityFragment";
    private MyDialView temperatureDialView;
    private MyDialView DialView2;
    private MyDialView DialView3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_outflow_electricity, container, false);
        temperatureDialView=(MyDialView)view.findViewById(R.id.temperatureDialView);
        DialView2=(MyDialView)view.findViewById(R.id.temperatureDialView2);
        DialView3=(MyDialView)view.findViewById(R.id.temperatureDialView3);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //获取屏幕宽高 和 屏幕密度dpi
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        int screenWidth = displayMetrics.widthPixels;
      int  screenHeight = displayMetrics.heightPixels;
        Log.e(TAG,"screenWidth="+screenWidth+"  screenHeight="+screenHeight);
        init();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void init(){
        temperatureDialView.setStart(true);
        temperatureDialView.setSpeed(30);
        DialView2.setStart(true);
        DialView2.setSpeed(50);
        DialView3.setStart(true);
        DialView3.setSpeed(100);
    }
}
