package me.leofontes.movies.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import me.leofontes.movies.Models.Movie;
import me.leofontes.movies.Models.Review;
import me.leofontes.movies.Models.Video;

/**
 * Created by leo on 28/11/16.
 */

public class MovieDBAdapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movies.db";


    public MovieDBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create the tables, SQL is specified on the contract classes
        db.execSQL(ContractDB.MovieContract.SQL_CREATE);
        db.execSQL(ContractDB.ReviewContract.SQL_CREATE);
        db.execSQL(ContractDB.VideoContract.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the tables, in reverse order because of foreign keys
        db.execSQL("DROP TABLE IF EXISTS " + ContractDB.VideoContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ContractDB.ReviewContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ContractDB.MovieContract.TABLE_NAME);

        onCreate(db);
    }
}
