package com.txu.eventfinder.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SearchCard implements Parcelable {
    private String mImageUrl;
    private String event;
    private String venue;
    private String genre;
    private String date;
    private String time;

    private String index;

    public SearchCard(String mImageUrl, String event, String venue, String genre, String date, String time, String index) {
        this.mImageUrl = mImageUrl;
        this.event = event;
        this.venue = venue;
        this.genre = genre;
        this.date = date;
        this.time = time;
        this.index = index;
    }

    protected SearchCard(Parcel in) {
        mImageUrl = in.readString();
        event = in.readString();
        venue = in.readString();
        genre = in.readString();
        date = in.readString();
        time = in.readString();
        index = in.readString();
    }

    public static final Creator<SearchCard> CREATOR = new Creator<SearchCard>() {
        @Override
        public SearchCard createFromParcel(Parcel in) {
            return new SearchCard(in);
        }

        @Override
        public SearchCard[] newArray(int size) {
            return new SearchCard[size];
        }
    };

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getEvent() {
        return event;
    }

    public String getVenue() {
        return venue;
    }

    public String getGenre() {
        return genre;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getIndex() {
        return index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(mImageUrl);
        parcel.writeString(event);
        parcel.writeString(venue);
        parcel.writeString(genre);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(index);
    }
}
