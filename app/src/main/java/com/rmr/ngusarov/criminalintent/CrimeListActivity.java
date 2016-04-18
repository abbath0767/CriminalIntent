package com.rmr.ngusarov.criminalintent;

import android.app.Fragment;
import android.content.Intent;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeRecyclerFragment.Callbacks, CrimeFragment.Callbacks {


    //todo test
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected Fragment createFragment() {
         return new CrimeRecyclerFragment();
//        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getFragmentManager().beginTransaction()
                    .replace(R.id.detailFragmentContainer, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeRecyclerFragment fragment = (CrimeRecyclerFragment)
                getFragmentManager().findFragmentById(R.id.fragmentContainer);
        fragment.updateUi();

    }
}
