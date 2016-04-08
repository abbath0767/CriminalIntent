package com.rmr.ngusarov.criminalintent;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }
    public Date getDate() {return mDate;}

    public void setDate(Date date) {mDate = date;}

    public boolean isSolved() {return mSolved;}

    public void setSolved(boolean solved) {mSolved = solved;}

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId);
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.getTime());
        return json;
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        //if tittle = null
        try {
            mTitle = json.getString(JSON_TITLE);
        } catch (Exception e) {
            mTitle = "Crime id=" + this.mId.toString().substring(0, 10);
            Log.d(CrimeListFragment.TAG, "mTittle empty, set default = " + mTitle);
        }
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
    }
}
