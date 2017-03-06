package sk.cestaplus.cestaplusapp.extras;

/**
 * Created by matth on 22.02.2017.
 */
public interface Constants {
    // job constants
    String UPDATE_JOB_TAG = "update_job";
    int UPDATE_PERIOD_MIN = 4 * 60; // 60 time between automatic updates of articles list
    int UPDATE_PERIOD_SEC = 20; // time between automatic updates of articles list

    //other constants
    int ART_NUM = 20; //number of articles per page
    int DELAY_TO_START_ACTIVITY_MILLIS = 200;

    //List styles
    int LIST_STYLE_ALL = 0;
    int LIST_STYLE_PICTURES_AND_TITLES = 1;
}
