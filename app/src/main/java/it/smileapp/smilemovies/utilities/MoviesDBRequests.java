package it.smileapp.smilemovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by carde on 06/02/17.
 */

public class MoviesDBRequests {

    /**
     * The key that allows access to the 'The Movie Database' database of movies. Very literal name :)
     */
    private final static String TMD_KEY = "<YOUR_API_KEY>";

    private final static String TMD_HOST = "https://api.themoviedb.org/3/";
    private final static String TMD_POSTERS_HOST = "http://image.tmdb.org/t/p/";

    private final static String PREFERRED_POSTER_SIZE = "w185";

    private final static String YT_THUMBNAIL_HOST = "http://img.youtube.com/vi/";
    private final static String YOUTUBE_HOST = "https://www.youtube.com/";


    public static URL getMovieById(int id) throws MalformedURLException {
        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(id))
                .appendQueryParameter("api_key", TMD_KEY)
                .build();


        URL url = new URL(uri.toString());

        return url;
    }


    public static URL getMostPopularFilmsURL() throws MalformedURLException {
        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("movie")
                .appendPath("popular")
                .appendQueryParameter("api_key", TMD_KEY)
                .build();


        URL url = new URL(uri.toString());

        return url;
    }

    public static URL getMostRatedFilmsURL() throws MalformedURLException {
        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("movie")
                .appendPath("top_rated")
                .appendQueryParameter("api_key", TMD_KEY)
                .build();

        URL url = new URL(uri.toString());

        return url;
    }

    public static String getPhotoUrl(String posterPath) {

        Uri uri = Uri.parse(TMD_POSTERS_HOST)
                .buildUpon()
                .appendPath(PREFERRED_POSTER_SIZE)
                .appendEncodedPath(posterPath)
                .build();

        return uri.toString();

    }

    public static URL getMostPopularItalianFilmsURL() throws MalformedURLException {

        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", "popularity.desc")
                .appendQueryParameter("with_original_language", "it") //The awesome Aldo Baglio
                .appendQueryParameter("api_key", TMD_KEY)
                .build();

        URL url = new URL(uri.toString());

        return url;
    }

    public static URL getMostRatedItalianFilmsURL() throws MalformedURLException {

        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", "vote_average.desc")
                .appendQueryParameter("with_original_language", "it") //The awesome Aldo Baglio
                .appendQueryParameter("api_key", TMD_KEY)
                .build();

        URL url = new URL(uri.toString());

        return url;
    }

    public static URL getMatteosFavouritesFilmsURL() throws MalformedURLException {

        //https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&with_cast=120019&api_key=d0ce6070355b63b8323ef4348445f8d2
        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", "vote_average.desc")
                .appendQueryParameter("with_cast", "120019") //The awesome Cataldo Baglio
                .appendQueryParameter("api_key", TMD_KEY)
                .build();

        URL url = new URL(uri.toString());

        return url;
    }

    public static URL getMovieTrailersURL(String movieId) throws MalformedURLException {
        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("trailers")
                .appendQueryParameter("api_key", TMD_KEY)
                .build();

        URL url = new URL(uri.toString());

        return url;
    }


    public static URL getMovieActorsURL(String movieId) throws MalformedURLException {

        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("credits")
                .appendQueryParameter("api_key", TMD_KEY)
                .build();

        URL url = new URL(uri.toString());

        return url;
    }

    public static URL getMovieReviewsURL(String movieId) throws MalformedURLException {

        Uri uri = Uri.parse(TMD_HOST)
                .buildUpon()
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter("api_key", TMD_KEY)
                .build();

        URL url = new URL(uri.toString());

        return url;
    }

    public static String getTrailerThumbnailFromYT(String YTVideoKey) {

        Uri uri = Uri.parse(YT_THUMBNAIL_HOST)
                .buildUpon()
                .appendPath(YTVideoKey)
                .appendPath("maxresdefault.jpg")
                .build();

        return uri.toString();


    }

    public static Uri getYoutubeUri(String YTVideoKey) throws MalformedURLException {

        Uri uri = Uri.parse(YOUTUBE_HOST)
                .buildUpon()
                .appendPath("watch")
                .appendQueryParameter("v", YTVideoKey)
                .build();

        return uri;
    }
}
