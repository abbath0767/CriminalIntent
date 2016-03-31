package com.rmr.ngusarov.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

public class TimeFragment extends Fragment{

    public static final String TIME = "time";

    private TimePicker mTimePicker;

    private int mHour;
    private int mMinute;

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
        mHour = getArguments().getInt("hour");
        mMinute = getArguments().getInt("minute");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.time_fragment, container, false);

        mTimePicker = (TimePicker) v.findViewById(R.id.time_picker);
        mTimePicker.is24HourView();
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                sendResult(Activity.RESULT_OK);
            }
        });

        return v;
    }

    public void sendResult(int resultCode) {
        if (getTargetFragment() == null) return;
        Intent i = new Intent();
        i.putExtra(TIME + "h", mHour);
        i.putExtra(TIME + "m", mMinute);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
