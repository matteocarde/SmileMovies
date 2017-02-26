package it.smileapp.smilemovies.jobs;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by carde on 25/02/17.
 */

public class JobsProvider {

    private static final int INTERVAL_HOURS = 24;
    private static final int INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(INTERVAL_HOURS);
    private static final int FLEXTIME_SECONDS = INTERVAL_SECONDS / INTERVAL_HOURS;


    private static final String JOB_TAG = "movie-suggestion-job";

    /**
     * Called to initialize the job that will suggest to the user every 24h and only when the phone is charging
     * (and so probably only when the user is at home) which movie he could see again based from his favourite list.
     *
     * @param context
     */
    public static void initializeNotificationJob(Context context) {

        GooglePlayDriver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job job = dispatcher.newJobBuilder()
                .setService(MoviesReminderJobService.class)
                .setTag(JOB_TAG)
                //Home is where your charger is... So display it only when phone is recharging
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        INTERVAL_SECONDS,
                        INTERVAL_SECONDS + FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();


        dispatcher.schedule(job);
    }
}
