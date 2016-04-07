package com.rmr.ngusarov.criminalintent;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CrimeListFragment extends ListFragment {
    public static final String TAG = "myTag";

    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimes_title);
        setHasOptionsMenu(true);

        getActivity().setTitle(R.string.title_activity_crime);

        mCrimes = CrimeLab.get(getActivity()).getCrimes();

        CrimeAdapter cAdapter = new CrimeAdapter(mCrimes);

        setListAdapter(cAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                Log.d(TAG, "new Crime created, UUID = " + crime.getId());
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
                startActivityForResult(i, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            if (contentView == null)
                contentView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);

            Crime c = getItem(position);

            TextView tittleTextView = (TextView) contentView.findViewById(R.id.crime_list_item_tittleTextView);
            tittleTextView.setText(c.getTitle());

            TextView dateTextView = (TextView) contentView.findViewById(R.id.crime_list_item_dateTextView);
            Date crimeDate = c.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm");
            String formatDate = sdf.format(crimeDate);
            dateTextView.setText(formatDate);

            CheckBox solvedChexkBox =(CheckBox) contentView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedChexkBox.setChecked(c.isSolved());

            return contentView;
        }
    }
}
