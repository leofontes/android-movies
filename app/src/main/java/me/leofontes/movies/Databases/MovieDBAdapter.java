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

public class MovieDBAdapter {
    private SQLiteDatabase db;
    private MovieDBHelper dbHelper;
    private final Context context;

    private static MovieDBAdapter sInstance;

    public static final String DB_NAME = "movies.db";
    public static int DB_VERSION = 1;

    public static synchronized MovieDBAdapter getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new MovieDBAdapter(context.getApplicationContext());
        }
        return sInstance;
    }

    private MovieDBAdapter(Context ctx) {
        context = ctx;
        dbHelper = new MovieDBHelper(context, DB_NAME, null, DB_VERSION);
    }

    public void open() throws SQLiteException {
        try {
            dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        db.close();
    }

    public void clear() {
        dbHelper.onUpgrade(db, DB_VERSION, DB_VERSION+1);
        DB_VERSION++;
    }

    // INSERT methods
    public long insertMovie(Movie movie) {
        // New row of values to insert
        ContentValues cvalues = new ContentValues();
        // Assign the data
        cvalues.put(ContractDB.MovieContract._ID, movie.id);
        cvalues.put(ContractDB.MovieContract.COLUMN_NAME, movie.original_title);
        cvalues.put(ContractDB.MovieContract.COLUMN_IMAGE, movie.backdrop_path);
        cvalues.put(ContractDB.MovieContract.COLUMN_RATING, movie.vote_average);
        cvalues.put(ContractDB.MovieContract.COLUMN_RELEASE_DATE, movie.release_date);
        cvalues.put(ContractDB.MovieContract.COLUMN_SYNOPSIS, movie.overview);
        // Insert to database
        return db.insert(ContractDB.MovieContract.TABLE_NAME, null, cvalues);
    }

    public long insertReview(Review review, int movieId) {
        // New row of values to insert
        ContentValues cvalues = new ContentValues();
        //Assign the data
        cvalues.put(ContractDB.ReviewContract.COLUMN_AUTHOR, review.author);
        cvalues.put(ContractDB.ReviewContract.COLUMN_CONTENT, review.content);
        cvalues.put(ContractDB.ReviewContract.COLUMN_MOVIE, movieId);
        // Insert to database
        return db.insert(ContractDB.ReviewContract.TABLE_NAME, null, cvalues);
    }

    public long insertVideo(Video video, int movieId) {
        // New row of values to insert
        ContentValues cvalues = new ContentValues();
        //Assign the data
        cvalues.put(ContractDB.VideoContract.COLUMN_KEY, video.key);
        cvalues.put(ContractDB.VideoContract.COLUMN_NAME, video.name);
        cvalues.put(ContractDB.VideoContract.COLUMN_MOVIE, movieId);
        // Insert to database
        return db.insert(ContractDB.VideoContract.TABLE_NAME, null, cvalues);
    }

    // QUERY methods
    public Cursor getAllMovies() {
        return db.query(
                ContractDB.MovieContract.TABLE_NAME,
                ContractDB.MovieContract.COLUMNS,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getReviews(int movieId) {
        String[] selectionArgs = {movieId + ""};
        return db.query(
                ContractDB.ReviewContract.TABLE_NAME,
                ContractDB.ReviewContract.COLUMNS,
                ContractDB.ReviewContract.COLUMN_MOVIE,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public Cursor getVideos(int movieId) {
        String[] selectionArgs = {movieId + ""};
        return db.query(
                ContractDB.VideoContract.TABLE_NAME,
                ContractDB.VideoContract.COLUMNS,
                ContractDB.VideoContract.COLUMN_MOVIE,
                selectionArgs,
                null,
                null,
                null
        );
    }

    // DELETE method
    public boolean removeFavorite(int movieId) {
        String[] selectionArgs = {movieId + ""};
        int reviews, videos, movies;
        reviews = db.delete(ContractDB.ReviewContract.TABLE_NAME, ContractDB.ReviewContract.COLUMN_MOVIE, selectionArgs);
        videos = db.delete(ContractDB.VideoContract.TABLE_NAME, ContractDB.VideoContract.COLUMN_MOVIE, selectionArgs);
        movies = db.delete(ContractDB.MovieContract.TABLE_NAME, ContractDB.MovieContract._ID, selectionArgs);

        return reviews > 0 && videos > 0 && movies > 0;
    }

    private class MovieDBHelper extends SQLiteOpenHelper {

        public MovieDBHelper(Context context, String name, SQLiteDatabase.CursorFactory fct, int version) {
            super(context, name, fct, version);
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
}
