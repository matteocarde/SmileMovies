package it.smileapp.smilemovies.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by carde on 22/02/17.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "it.smileapp.smilemovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "favourites";


    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String TABLE_NAME = "favourites";

        public static final String COLUMN_MOVIE_ORIGINAL_ID = "movie_original_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";

    }

}
