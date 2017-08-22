package sk.cestaplus.cestaplusapp.utilities.navDrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;

/**
 * Created by matth on 17.02.2017.
 */
public class ActivityStarter
    implements IAction{

    private Context context;
    private Class classToStart;
    private String parentActivity;  // String key of parent activity
    private int delay;              // delay when starting the new activity
    //private List<AbstractMap.SimpleEntry<String, Serializable>> toExtras;
    private Bundle toExtras;

    public ActivityStarter(Context context, Class classToStart, String parentActivity, int delay) {
        this.context = context;
        this.classToStart = classToStart;
        this.parentActivity = parentActivity;
        this.delay = delay;
        //toExtras = Collections.emptyList();
        toExtras = new Bundle();
    }

    public ActivityStarter(Context context, Class classToStart, String parentActivity, int delay,
                           Bundle toExtras) {
        this(context, classToStart, parentActivity, delay);
        this.toExtras = toExtras;
    }

    public void execute(){
        final Intent intent = new Intent(context, classToStart);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //SOURCES: http://stackoverflow.com/a/12664620  http://stackoverflow.com/a/12319970

        intent.putExtra(KEY_PARENT_ACTIVITY, parentActivity);
        intent.putExtras(toExtras); // this method is the KEY !!
        //intent.getExtras().putAll(toExtras); // not working !!!

        //add Extras
        //intent.putExtra(KEY_PARENT_ACTIVITY, parentActivity);
        //Bundle intentExtras = intent.getExtras();
        //intentExtras.putAll(toExtras);

        //intentExtras.putString(KEY_PARENT_ACTIVITY, parentActivity);
        //intent.getExtras().putAll(toExtras);
        //intent.getExtras().putString(KEY_PARENT_ACTIVITY, parentActivity);

        // delay the start because of onClick animation
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        }, delay);
    }

    /*
    OLD implementation of adding extras
    private void addExtras(Intent intent) {
        for (AbstractMap.SimpleEntry entry: toExtras) {
            intent.putExtra((String) entry.getKey(), (Serializable) entry.getValue());
        }
    }
    */
}
