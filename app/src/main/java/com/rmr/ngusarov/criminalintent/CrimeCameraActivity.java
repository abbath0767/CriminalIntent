package com.rmr.ngusarov.criminalintent;

import android.app.Fragment;

public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
