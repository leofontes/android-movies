package me.leofontes.movies.Databases;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by leo on 07/12/16.
 */

public class MovieProvider extends ContentProvider {

    private MovieDBAdapter mDBAdapter;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int REVIEW = 200;
    static final int VIDEO = 300;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ContractDB.CONTENT_AUTHORITY;

        matcher.addURI(authority, ContractDB.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, ContractDB.PATH_REVIEW + "/#", REVIEW);
        matcher.addURI(authority, ContractDB.PATH_VIDEO + "/#", VIDEO);

        return matcher;
    }

    private Cursor getReviewsByMovieIdURI(Uri uri, String[] projection, String sortOrder) {
        int movieId = ContractDB.ReviewContract.getIdFromUri(uri);

        String selection = ContractDB.ReviewContract.COLUMN_MOVIE + "=?";
        String[] args = new String[] {
                String.valueOf(movieId)
        };

        return mDBAdapter.getWritableDatabase().query(
                ContractDB.ReviewContract.TABLE_NAME,
                projection,
                selection,
                args,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getVideosByMovieIdURI(Uri uri, String[] projection, String sortOrder) {
        int movieId = ContractDB.VideoContract.getIdFromUri(uri);

        String selection = ContractDB.VideoContract.COLUMN_MOVIE + "=?";
        String[] args = new String[] {
            String.valueOf(movieId)
        };

        return mDBAdapter.getWritableDatabase().query(
                ContractDB.VideoContract.TABLE_NAME,
                projection,
                selection,
                args,
                null,
                null,
                sortOrder
        );
    }

    /*
     * Override methods
     */

    @Override
    public boolean onCreate() {
        mDBAdapter = new MovieDBAdapter(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = mDBAdapter.getWritableDatabase().query(
                        ContractDB.MovieContract.TABLE_NAME,
                        ContractDB.MovieContract.COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            }
            case REVIEW: {
                retCursor = getReviewsByMovieIdURI(uri, projection, sortOrder);
                break;
            }
            case VIDEO: {
                retCursor = getVideosByMovieIdURI(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return ContractDB.MovieContract.CONTENT_TYPE;
            case REVIEW:
                return ContractDB.ReviewContract.CONTENT_TYPE;
            case VIDEO:
                return ContractDB.VideoContract.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBAdapter.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(ContractDB.MovieContract.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = ContractDB.MovieContract.buildWeatherUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long _id = db.insert(ContractDB.ReviewContract.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = ContractDB.ReviewContract.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case VIDEO: {
                long _id = db.insert(ContractDB.VideoContract.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = ContractDB.VideoContract.buildVideoUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBAdapter.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if(selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                    ContractDB.MovieContract.TABLE_NAME, selection, selectionArgs
                );
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                    ContractDB.ReviewContract.TABLE_NAME, selection, selectionArgs
                );
                break;
            case VIDEO:
                rowsDeleted = db.delete(
                    ContractDB.VideoContract.TABLE_NAME, selection, selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBAdapter.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(ContractDB.MovieContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(ContractDB.ReviewContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            case VIDEO:
                rowsUpdated = db.update(ContractDB.VideoContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDBAdapter.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case REVIEW:
                db.beginTransaction();

                try {
                    for(ContentValues value : values) {
                        long _id = db.insert(ContractDB.ReviewContract.TABLE_NAME, null, value);
                        if(_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEO:
                db.beginTransaction();

                try {
                    for(ContentValues value : values) {
                        long _id = db.insert(ContractDB.VideoContract.TABLE_NAME, null, value);
                        if(_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
