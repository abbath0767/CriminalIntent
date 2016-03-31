package com.rmr.ngusarov.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

public class DateFragment extends Fragment {

    public static final String DATE = "date";
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear = getArguments().getInt("year");
        mMonth = getArguments().getInt("month");
        mDay = getArguments().getInt("day");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.date_fragment, container, false);

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.date_picker);
        datePicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                getArguments().putSerializable(DateAndTimeDialog.EXTRA_DATE, mDate);
                //TODO meed debug
                mYear = year;
                mDay = dayOfMonth;
                mMonth = monthOfYear;
                sendResult(Activity.RESULT_OK);
            }
        });

        return v;
    }

    public static DateFragment newInstance(int year, int month, int day) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        fragment.setArguments(args);
        return fragment;
    }

    public void sendResult(int resultCode) {
        if (getTargetFragment() == null) return;
        Intent i = new Intent();
        i.putExtra(DATE + "y", mYear);
        i.putExtra(DATE + "m", mMonth);
        i.putExtra(DATE + "d", mDay);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
