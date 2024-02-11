package com.example.todoido.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todoido.R;
import com.example.todoido.WeekPage.Week1Fragment;
import com.example.todoido.WeekPage.Week2Fragment;
import com.example.todoido.WeekPage.Week3Fragment;
import com.example.todoido.WeekPage.Week4Fragment;

public class WeekFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ViewPager viewPager = view.findViewById(R.id.week_viewPager);
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(0);

        return view;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Week1Fragment();
                case 1:
                    return new Week2Fragment();
                case 2:
                    return new Week3Fragment();
                case 3:
                    return new Week4Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
