package com.rmr.ngusarov.criminalintent;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

    private Date mDate;
    private PagerAdapter adapter;
    private Button mButtonOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container);

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
//                    Date saved = df.getDate();
//                    mDate.setYear(saved.getYear());
//                    mDate.setMonth(saved.getMonth());
//                    mDate.setDate(saved.getDay());
//                    mDate.setHours(saved.getHours());
//                    mDate.setMinutes(saved.getMinutes());
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
            }
        });

        return view;
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
        DateFragment df = DateFragment.newInstance(mDate);
        TimeFragment rf = TimeFragment.newInstance(mDate.getHours(), mDate.getMinutes());
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
