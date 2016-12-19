package com.example.android.news;

import android.os.Parcel;
import android.os.Parcelable;

public class news implements Parcelable {

    private String mNews;
    private String mSection;
    private String mDate;
    private String mURL;

    //Class definition
    public news(String a_News, String a_Section, String a_Date, String a_URL) {
        mNews = a_News;
        mSection = a_Section;
        mDate = a_Date;
        mURL = a_URL;
    }

    //Getter methods
    public String getmNews() {
        return mNews;
    }
    public String getmSection() {
        return mSection;
    }
    public String getmDate() {
        return mDate;
    }
    public String getmURL() {
        return mURL;
    }

    //Parcelling concept to restore the result after the screen orientation is changed
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mNews);
        dest.writeString(this.mSection);
        dest.writeString(this.mDate);
        dest.writeString(this.mURL);
    }

    protected news(Parcel in) {
        this.mNews = in.readString();
        this.mSection = in.readString();
        this.mDate = in.readString();
        this.mURL = in.readString();
    }

    public static final Parcelable.Creator<news> CREATOR = new Parcelable.Creator<news>() {
        @Override
        public news createFromParcel(Parcel source) {
            return new news(source);
        }
        @Override
        public news[] newArray(int size) {
            return new news[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
