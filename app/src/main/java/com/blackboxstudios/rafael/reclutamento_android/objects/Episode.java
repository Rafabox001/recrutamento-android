package com.blackboxstudios.rafael.reclutamento_android.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rafael on 30/09/2015.
 *
 * Implemented using parcelable because even if its not needed by this app in particular, implementing parcelable
 * in object data let us share this object between activities.
 *
 * Also makes easier to restore values on configuration changes.
 */
public class Episode implements Parcelable{
    private String season;
    private String number;
    private String title;

    public Episode(String season, String number, String title) {
        super();
        this.season = season;
        this.number = number;
        this.title = title;
    }

    public Episode(){}

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(season);
        dest.writeString(number);
        dest.writeString(title);
    }

    public static final Creator<Episode> CREATOR
            = new Creator<Episode>() {
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    public Episode(Parcel in) {

        ReadFromParcel(in);
    }

    private void ReadFromParcel(Parcel in) {
        season = in.readString();
        number = in.readString();
        title = in.readString();
    }
}
