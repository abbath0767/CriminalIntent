package com.rmr.ngusarov.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rmr.ngusarov.criminalintent.database.CrimeCursorWrapper;
import com.rmr.ngusarov.criminalintent.database.CrimeDbHelper;
import com.rmr.ngusarov.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private SQLiteDatabase mDataBase;


    private CrimeLab(Context appContext) {
        mAppContext = appContext.getApplicationContext();
        mDataBase = new CrimeDbHelper(mAppContext).getWritableDatabase();
    }

    public static CrimeLab get(Context c) {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(c.getApplicationContext());

        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        ContentValues content = getContentVlues(c);

        mDataBase.insert(CrimeTable.NAME, null, content);
    }

    public void deleteCrime(Crime c) {
        mDataBase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{c.getId().toString()});

    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[]{id.toString()});

        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDataBase.query(CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);

        return new CrimeCursorWrapper(cursor);
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues content = getContentVlues(crime);

        mDataBase.update(CrimeTable.NAME, content,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public ArrayList<Crime> getCrimes() {
        ArrayList<Crime> mCrimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                mCrimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return mCrimes;
    }

    private static ContentValues  getContentVlues(Crime crime) {
        ContentValues content = new ContentValues();
        content.put(CrimeTable.Cols.UUID, crime.getId().toString());
        content.put(CrimeTable.Cols.TITLE, crime.getTitle());
        content.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        content.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        content.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return content;
    }

}
