package com.rmr.ngusarov.criminalintent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

public class TimeFragment extends Fragment{

    private TimePicker mTimePicker;

    private int hour;
    private int minute;

    public static TimeFragment newInstance(int hour, int minute) {
        TimeFragment fragment = new TimeFragment();
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hour = getArguments().getInt("hour");
        minute = getArguments().getInt("minute");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.time_fragment, container, false);

        mTimePicker = (TimePicker) v.findViewById(R.id.time_picker);
        mTimePicker.is24HourView();



        return v;
    }
}
