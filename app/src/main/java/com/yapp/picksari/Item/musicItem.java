package com.yapp.picksari.Item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by myeong on 2018. 4. 1..
 */

public class musicItem implements Parcelable{
    public String mName;
    public String mSinger;
    public String mOctave;
    public String mGenre;

    public musicItem(String mName, String mSinger, String mOctave, String mGenre){
        this.mName = mName;
        this.mSinger = mSinger;
        this.mOctave = mOctave;
        this.mGenre = mGenre;
    }
    public musicItem(Parcel in){
        mName = in.readString();
        mSinger = in.readString();
        mOctave = in.readString();
        mGenre = in.readString();
    }

    public static final Creator<musicItem> CREATOR = new Creator<musicItem>() {
        @Override
        public musicItem createFromParcel(Parcel source) {
            return new musicItem(source);
        }

        @Override
        public musicItem[] newArray(int size) {
            return new musicItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mSinger);
        dest.writeString(mOctave);
        dest.writeString(mGenre);
    }
}
