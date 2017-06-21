package sk.cestaplus.cestaplusapp.utilities;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import sk.cestaplus.cestaplusapp.services.UpdateService;

import static sk.cestaplus.cestaplusapp.extras.Constants.EXECUTION_WINDOW_WIDTH_SEC;
import static sk.cestaplus.cestaplusapp.extras.Constants.NEW_ART_NOTIFICATIONS_DEBUG;
import static sk.cestaplus.cestaplusapp.extras.Constants.UPDATE_JOB_TAG;
import static sk.cestaplus.cestaplusapp.extras.Constants.UPDATE_PERIOD_SEC;

/**
 * Created by matth on 06.03.2017.
 *
 * Problem with context SOURCE:
 * http://stackoverflow.com/questions/36817412/singleton-with-context-as-a-variable-memory-leaks
 *
 * Firebase JobDispatcher SOURCE:
 * https://github.com/firebase/firebase-jobdispatcher-android#user-content-firebase-jobdispatcher-
 */
public class CustomJobManager {

    private static CustomJobManager instance;

    private SessionManager session;
    private Context context;

    private CustomJobManager(Context context) {
        this.context = context;
        session = new SessionManager(context);
    }

    /**
     *  About Context - check class comment.
     */
    public static CustomJobManager getInstance(Context context){
        if (instance == null){
            instance = new CustomJobManager(context);
        }
        return instance;
    }

    public void constructAndScheduleUpdateJob(){

        // check post notifications - for sure
        if (session.getPostNotificationStatus()) { //if notifications are on

            //  1 - Create a new dispatcher using the Google Play driver.
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

            // 2 - set job properties
            Job updateJob = dispatcher.newJobBuilder()
                    .setService(UpdateService.class) // the JobService that will be called
                    .setTag(UPDATE_JOB_TAG)          // uniquely identifies the job
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(UPDATE_PERIOD_SEC - EXECUTION_WINDOW_WIDTH_SEC, UPDATE_PERIOD_SEC)) // in seconds
                    .setLifetime(Lifetime.FOREVER)   // job WILL persist past a device reboot
                    .setConstraints(
                            Constraint.ON_ANY_NETWORK // run only if there is network connection (of any type)
                    )
                    .setReplaceCurrent(true) // overwrite an existing job with the same tag
                .build();

            // 3 - schedule Job
            dispatcher.mustSchedule(updateJob);
            Log.d(NEW_ART_NOTIFICATIONS_DEBUG, "Job constructed");

            // 4 - (optional) post info notification
            //CustomNotificationManager.issueNotification("Job scheduled", 3); // scheduling id = 3

        } else {
            // notifications are off
            cancelUpdateJob();
        }
    }

    public void cancelUpdateJob(){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.cancel(UPDATE_JOB_TAG);
    }

    /*
    OLD IMPLEMENTANTION
    USING: //compile 'me.tatarka.support:jobscheduler:0.1.1'
    DON'T forget changes in AndroidManifest

    private void constructJob(){

        // check is needed also here, due to delay of Handler in onCreate()
        if (session.getPostNotificationStatus()) { //if notifications are on
            //0 - inicialize JobScheduler
            mJobScheduler = JobScheduler.getInstance(CustomApplication.getCustomAppContext());

            //1 - vytvorenie Builder-a
            JobInfo.Builder builder = new JobInfo.Builder(Constants.UPDATE_JOB_ID, new ComponentName(this, UpdateService.class)); // Component name = meno služby, ktorú chceme spustiť

            //2 - nastavenie vlastností Jobu
            builder.setPeriodic(UPDATE_PERIOD_MIN * 60 * 1000); // in miliseconds
            //builder.setPeriodic(60*1000); //1 min
            builder.setPersisted(true);
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

            //3 - naplánovanie Jobu
            mJobScheduler.schedule(builder.build());

            //4 - (optional) info notification
            //Util.issueNotification("Job naplánovaný", 3); // naplánovanie id = 3
        }
    }
     */
}
