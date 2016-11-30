package me.leofontes.movies.Databases;

import android.provider.BaseColumns;

/**
 * Created by leo on 28/11/16.
 */

public class ContractDB {
    public static final class MovieContract implements BaseColumns {
        public static final String TABLE_NAME = "movies";

        //Columns
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String[] COLUMNS = {_ID, COLUMN_NAME, COLUMN_IMAGE, COLUMN_SYNOPSIS, COLUMN_RATING, COLUMN_RELEASE_DATE};

        //Create String
        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_IMAGE + " TEXT NOT NULL, " +
                COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                COLUMN_RATING + " REAL NOT NULL, " +
                COLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                " );";
    }

    public static final class ReviewContract implements BaseColumns {
        public static final String TABLE_NAME = "reviews";

        //Columns
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        //Foreign key
        public static final String COLUMN_MOVIE = "movie_id";

        public static final String[] COLUMNS = {_ID, COLUMN_AUTHOR, COLUMN_CONTENT, COLUMN_MOVIE};

        //Create String
        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT NOT NULL, " +
                COLUMN_MOVIE + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + COLUMN_MOVIE + ") REFERENCES " + MovieContract.TABLE_NAME + "(" + MovieContract._ID + ")" +
                " );";
    }

    public static final class VideoContract implements BaseColumns {
        public static final String TABLE_NAME = "videos";

        //Columns
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        //Foreign key
        public static final String COLUMN_MOVIE = "movie_id";

        public static final String[] COLUMNS = {_ID, COLUMN_KEY, COLUMN_NAME, COLUMN_MOVIE};

        //Create String
        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_KEY + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_MOVIE + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + COLUMN_MOVIE + ") REFERENCES " + MovieContract.TABLE_NAME + "(" + MovieContract._ID + ")" +
                " );";
    }
}
