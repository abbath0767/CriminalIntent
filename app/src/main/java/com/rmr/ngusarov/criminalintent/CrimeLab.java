package com.rmr.ngusarov.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private CriminalIntentJSONSerializer mSerializer;

    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context appContext) {
        mAppContext = appContext;

        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(CrimeListFragment.TAG, "Error loading crimes: ", e);
        }
    }

    public static CrimeLab get(Context c) {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(c.getApplicationContext());

        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime: mCrimes) {
            if (crime.getId().equals(id))
                return crime;
        }
        return null;
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(CrimeListFragment.TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(CrimeListFragment.TAG, "Error saving crimes: ", e);
            return false;
        }
    }

}
