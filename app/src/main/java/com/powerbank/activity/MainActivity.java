package com.powerbank.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.powerbank.R;
import com.powerbank.adapter.TransactionAdapter;
import com.powerbank.fragment.OutflowElectricityFragment;
import com.powerbank.fragment.RechargeElectricityFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Fragment> fragments;
    private TransactionAdapter adapter;
    private ViewPager viewPager;
    private ImageView firstDot;
    private ImageView secondDot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        firstDot=(ImageView)findViewById(R.id.first_dot);
        secondDot=(ImageView)findViewById(R.id.second_dot);
        initView();
    }
    private void initView(){
        initAdapter();
        initListener();
    }
    /**
     * 初始化adapter
     */
    private void initAdapter() {
        fragments = new ArrayList<>();
        fragments.add(new OutflowElectricityFragment());
        fragments.add(new RechargeElectricityFragment());

        adapter = new TransactionAdapter(this.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size() - 1);
    }
    /**
     * 初始化监听
     */
    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                seletedAction(position);
            }

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });
    }
    /**
     * 选择页面
     */
    private void seletedAction(int position) {
        switch (position) {
            case 0:
                firstDot.setBackgroundResource(R.mipmap.default_sel);
                secondDot.setBackgroundResource(R.mipmap.default_nor);
                viewPager.setCurrentItem(0);
                break;
            case 1:
                firstDot.setBackgroundResource(R.mipmap.default_nor);
                secondDot.setBackgroundResource(R.mipmap.default_sel);
                viewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
}
