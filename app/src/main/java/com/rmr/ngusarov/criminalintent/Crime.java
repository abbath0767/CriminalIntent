package com.rmr.ngusarov.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mPhone;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID uuid) {
        mId = uuid;
        mDate = new Date();
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
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

    public String getSuspect() {return mSuspect;}

    public void setSuspect(String suspect) {this.mSuspect = suspect;}

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {mPhone = phone;}

    @Override
    public String toString() {
        return getTitle();
    }
}
