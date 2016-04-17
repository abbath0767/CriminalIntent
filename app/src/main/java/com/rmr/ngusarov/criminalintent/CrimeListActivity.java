package com.rmr.ngusarov.criminalintent;

import android.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {


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
}
