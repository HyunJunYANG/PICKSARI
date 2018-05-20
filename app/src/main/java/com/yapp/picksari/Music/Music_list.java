package com.yapp.picksari.Music;

/**
 * Created by myeong on 2018. 3. 26..
 */

public class Music_list {
    private int mId;
    private String mName;
    private String mSinger;
    private String mGenre;
    private String mOctave;

    private int mPick;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSinger() {
        return mSinger;
    }

    public void setmSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    public String getmGenre() {
        return mGenre;
    }

    public void setmGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public String getmOctave() {
        return mOctave;
    }

    public void setmOctave(String mOctave) {
        this.mOctave = mOctave;
    }

    public int getmPick() {
        return mPick;
    }

    public void setmPick(int mPick) {
        this.mPick = mPick;
    }


    public long get_id() {
        return mId;
    }
}