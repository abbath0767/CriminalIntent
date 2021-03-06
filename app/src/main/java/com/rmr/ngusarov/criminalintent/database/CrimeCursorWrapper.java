package com.rmr.ngusarov.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rmr.ngusarov.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

import static com.rmr.ngusarov.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeCursorWrapper extends CursorWrapper{

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String phone = getString(getColumnIndex(CrimeTable.Cols.PHONE));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved == 1);
        crime.setSuspect(suspect);
        crime.setPhone(phone);

        return crime;
    }
}
