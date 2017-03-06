package sk.cestaplus.cestaplusapp.utilities.utilClasses;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import sk.cestaplus.cestaplusapp.services.UpdateService;
import sk.cestaplus.cestaplusapp.utilities.CustomNotificationManager;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static sk.cestaplus.cestaplusapp.extras.Constants.UPDATE_JOB_TAG;
import static sk.cestaplus.cestaplusapp.extras.Constants.UPDATE_PERIOD_MIN;
import static sk.cestaplus.cestaplusapp.extras.Constants.UPDATE_PERIOD_SEC;

/**
 * Created by matth on 06.03.2017.
 * Problem with context SOURCE:
 * http://stackoverflow.com/questions/36817412/singleton-with-context-as-a-variable-memory-leaks
 */
public class CustomJobManager {

    private static CustomJobManager instance;

    private SessionManager session;
    private Context context;

    private CustomJobManager(Context context) {
        this.context = context;
        session = new SessionManager(context);
    }

    public static CustomJobManager getInstance(Context context){
        if (instance == null){
            instance = new CustomJobManager(context);
        }
        return instance;
    }

    public void constructUpdateJob(){

        // check is needed also here, due to delay of Handler in onCreate()
        if (session.getPostNotificationStatus()) { //if notifications are on

            //  1 - Create a new dispatcher using the Google Play driver.
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

            Job updateJob = dispatcher.newJobBuilder()
                    .setService(UpdateService.class) // the JobService that will be called
                    .setTag(UPDATE_JOB_TAG)        // uniquely identifies the job
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(Math.round((float) UPDATE_PERIOD_MIN / 2), UPDATE_PERIOD_MIN)) // in seconds
                    //.setPeriodic(UPDATE_PERIOD_MIN * 60 * 1000); // in miliseconds
                    .setLifetime(Lifetime.FOREVER)
                    .setConstraints(
                            Constraint.ON_ANY_NETWORK
                    )
                    .setReplaceCurrent(true) // overwrite an existing job with the same tag
                    .build();

            //2 - nastavenie vlastností Jobu
            //builder.setPeriodic(UPDATE_PERIOD_MIN * 60 * 1000); // in miliseconds
            //builder.setPeriodic(60*1000); //1 min
            //builder.setPersisted(true);
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

            //3 - schedule Job
            dispatcher.mustSchedule(updateJob);

            //4 - (optional) info notification
            CustomNotificationManager.issueNotification("Job scheduled", 3); // naplánovanie id = 3
        }
    }

    public void cancelUpdateJob(){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.cancel(UPDATE_JOB_TAG);
    }

    /*
    OLD CODE
    USING: //compile 'me.tatarka.support:jobscheduler:0.1.1'

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
