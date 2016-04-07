package com.rmr.ngusarov.criminalintent;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateAndTimeDialog extends DialogFragment{

    public static final String EXTRA_DATE = "com.rmr.ngusarov.criminalintent.date";
    public static final int EXTRA_REQUEST_DATE = 1;
    public static final int EXTRA_REQUEST_TIME = 2;


    private Date mDate;
    private PagerAdapter adapter;
    private Button mButtonOk;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container);

        getDialog().setTitle(R.string.date_picker_title);

        mButtonOk = (Button) view.findViewById(R.id.dialog_button_ok);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        adapter = new PagerAdapter(getChildFragmentManager(), getFragments());
        viewPager.setAdapter(adapter);

        //TODO need tests
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                DateFragment df = (DateFragment) getFragments().get(position);
//                if (df != null) {
//                }
//            }
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK);
                //todo need debug
                getDialog().dismiss();
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(CrimeListFragment.TAG, "result code DTD= " + resultCode);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == EXTRA_REQUEST_DATE) {
            year = data.getIntExtra(DateFragment.DATE + "y", 2007) - 1900;
            month = data.getIntExtra(DateFragment.DATE + "m", 7);
            day = data.getIntExtra(DateFragment.DATE + "d", 7);
            mDate.setYear(year);
            mDate.setMonth(month);
            mDate.setDate(day);
            Log.d(CrimeListFragment.TAG, " on result mDate after date= " + mDate);
        }
        //todo tests
        if (requestCode == EXTRA_REQUEST_TIME) {
            hour = data.getIntExtra(TimeFragment.TIME + "h", 7);
            minute = data.getIntExtra(TimeFragment.TIME + "m", 7);
            Log.d(CrimeListFragment.TAG, " on result hour = " + hour);
            Log.d(CrimeListFragment.TAG, " on result minute= " + minute);
            mDate.setHours(hour);
            mDate.setMinutes(minute);
            Log.d(CrimeListFragment.TAG, "in dialog date = " + mDate);
        }
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    private List getFragments() {
        List list = new ArrayList();
        DateFragment df = DateFragment.newInstance(year, month, day);
        df.setTargetFragment(DateAndTimeDialog.this, EXTRA_REQUEST_DATE);
        TimeFragment rf = TimeFragment.newInstance(mDate.getHours(), mDate.getMinutes());
        rf.setTargetFragment(DateAndTimeDialog.this, EXTRA_REQUEST_TIME);
        list.add(df);
        list.add(rf);
        return list;
    }

    public static DateAndTimeDialog newInstance(Date date) {
        DateAndTimeDialog dialog = new DateAndTimeDialog();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
        dialog.setArguments(args);

        return dialog;
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fm, List<Fragment> fragmnets) {
            super(fm);
            this.fragments = fragmnets;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
