package it.smileapp.smilemovies.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;
import org.json.JSONObject;

import it.smileapp.smilemovies.MovieDetailActivity;
import it.smileapp.smilemovies.R;
import it.smileapp.smilemovies.db.MoviesContract;

/**
 * Created by carde on 25/02/17.
 */

public class MoviesReminderJobService extends JobService {

    private AsyncTask<Void, Void, Cursor> mGetRandomMovieTask;

    @Override
    public boolean onStartJob(JobParameters job) {

        final Context context = getApplicationContext();

        mGetRandomMovieTask = new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... params) {
                return context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                int nOfFavouriteMovies = cursor.getCount();

                //if nOfFavouriteMovies is greater than 3 we have enough movies to pick a random movie
                if (nOfFavouriteMovies > 3) {
                    int filmIndex = (int) Math.floor(Math.random() * nOfFavouriteMovies);
                    cursor.moveToPosition(filmIndex);

                    int movieId = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ORIGINAL_ID));
                    String movieTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE));
                    try {
                        showNotification(context, movieTitle, movieId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        mGetRandomMovieTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mGetRandomMovieTask != null) {
            mGetRandomMovieTask.cancel(true);
        }
        return true;
    }


    /**
     * Shows the notification to the user that will suggest to the user which movie he could see again based from his favourite list.
     *
     * @param context
     * @param movieTitle - The title of the suggested movie
     * @param movieId    - The id of the suggested movie
     * @throws JSONException
     */
    private static void showNotification(Context context, String movieTitle, int movieId) throws JSONException {

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_suggest_title))
                .setContentText(context.getString(R.string.notification_suggest_text, movieTitle))
                .setAutoCancel(true);


        Intent notificationIntent = new Intent(context, MovieDetailActivity.class);

        JSONObject movieObj = new JSONObject();
        movieObj.put("id", movieId);
        movieObj.put("isFromNotification", true);

        notificationIntent.putExtra("MOVIE", movieObj.toString());

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(notificationIntent);
        PendingIntent resultPendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification.build());
    }
}
