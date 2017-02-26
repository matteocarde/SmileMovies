package it.smileapp.smilemovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.smileapp.smilemovies.db.MoviesContract.MoviesEntry;

public class MoviesDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";


    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String dbCreationSql = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesEntry.COLUMN_MOVIE_ORIGINAL_ID + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL " +
                ")";

        db.execSQL(dbCreationSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On upgrade add columns or tables that changed;
    }
}
