package me.leofontes.movies.Databases;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by leo on 28/11/16.
 */

public class ContractDB {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "me.leofontes.movies.provider";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_REVIEW = "review";

    public static final class MovieContract implements BaseColumns {
        //Provider related
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //Table name
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
        //Provider related
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        //Table name
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
        //Provider related
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //Table name
        public static final String TABLE_NAME = "videos";

        //Columns
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        //Foreign key
        public static final String COLUMN_MOVIE = "movie_id";

        public static final String[] COLUMNS = {COLUMN_KEY, COLUMN_NAME, COLUMN_MOVIE};

        //Create String
        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_KEY + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_MOVIE + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + COLUMN_MOVIE + ") REFERENCES " + MovieContract.TABLE_NAME + "(" + MovieContract._ID + ")" +
                " );";

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }
}
