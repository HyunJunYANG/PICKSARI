package com.yapp.picksari.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by dain on 2017-11-04.
 */

public class DBhelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "picksari_db";
    public final static String TABLE_NAME = "picksari_table";

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +  "( _id integer primary key autoincrement, mName TEXT, mSinger TEXT, mGenre TEXT, mOctave TEXT, mPick integer)";
        db.execSQL(query);
//        ContentValues first = new ContentValues();
//        first.put("mName", "Jenga (Feat. Gaeko)");
//        first.put("mSinger", "heize");
//        first.put("mGenre", "알앤비");
//        first.put("mOctave", "3'미");
//        first.put("mPick", 0);
//        db.insert(DBhelper.TABLE_NAME, null, first);
//
//        ContentValues two = new ContentValues();
//        two.put("mName", "그 노래");
//        two.put("mSinger", "존박");
//        two.put("mGenre", "발라드");
//        two.put("mOctave", "1'미");
//        two.put("mPick", 0);
//        db.insert(DBhelper.TABLE_NAME, null, two);
//
//        ContentValues three = new ContentValues();
//        three.put("mName", "gee");
//        three.put("mSinger", "소녀시대");
//        three.put("mGenre", "댄스");
//        three.put("mOctave", "3'미");
//        three.put("mPick", 0);
//        db.insert(DBhelper.TABLE_NAME, null, three);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
