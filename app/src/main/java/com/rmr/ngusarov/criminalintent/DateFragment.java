package com.rmr.ngusarov.criminalintent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateFragment extends Fragment {

    public static final String DATE = "date";
    private Date mDate;
    private int year;
    private int month;
    private int day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = (Date) getArguments().getSerializable(DateAndTimeDialog.EXTRA_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.date_fragment, container, false);

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.date_picker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                getArguments().putSerializable(DateAndTimeDialog.EXTRA_DATE, mDate);
            }
        });

        return v;
    }

    public static DateFragment newInstance(Date date) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putSerializable(DateAndTimeDialog.EXTRA_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    public Date getDate() {
        return mDate;
    }

}
