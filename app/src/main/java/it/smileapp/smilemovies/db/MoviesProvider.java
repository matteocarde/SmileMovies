package it.smileapp.smilemovies.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by carde on 22/02/17.
 */

public class MoviesProvider extends ContentProvider {

    MoviesDBHelper mDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int CODE_FAVOURITES_DIR = 100;
    private static final int CODE_FAVOURITE_ROW = 101;

    private static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH, CODE_FAVOURITES_DIR);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH + "/#", CODE_FAVOURITE_ROW);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int match = sUriMatcher.match(uri);

        SQLiteDatabase mDb = mDbHelper.getReadableDatabase();

        Cursor returnCursor;


        switch (match) {
            case CODE_FAVOURITES_DIR:
                returnCursor = mDb.query(MoviesContract.MoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FAVOURITE_ROW:
                String originalId = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
                returnCursor = mDb.query(MoviesContract.MoviesEntry.TABLE_NAME, projection, MoviesContract.MoviesEntry.COLUMN_MOVIE_ORIGINAL_ID + "=?", new String[]{originalId}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation");
        }

        return returnCursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int match = sUriMatcher.match(uri);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (match) {
            case CODE_FAVOURITES_DIR:

                long insertedId = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    Uri returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, insertedId);
                    getContext().getContentResolver().notifyChange(returnUri, null);
                    db.close();
                    return returnUri;
                } else {
                    throw new SQLException("Unable to insert new row");
                }
            default:
                throw new UnsupportedOperationException("Unsupported operation");
        }


    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        SQLiteDatabase mDb = mDbHelper.getWritableDatabase();

        switch (match) {
            case CODE_FAVOURITE_ROW:

                String originalId = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
                //I use the original movie id because I want to remove also eventual duplicates in the list
                int deletedElements = mDb.delete(MoviesContract.MoviesEntry.TABLE_NAME, MoviesContract.MoviesEntry.COLUMN_MOVIE_ORIGINAL_ID + "= ?", new String[]{originalId});

                if (deletedElements > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    mDb.close();
                }
                return deletedElements;


            default:
                throw new UnsupportedOperationException("Unsupported operation");
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
