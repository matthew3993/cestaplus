package sk.cestaplus.cestaplusapp.extras;

/**
 * Created by matth on 22.02.2017.
 */
public interface Constants {
    // job constants
    String UPDATE_JOB_TAG = "update_job";
    int UPDATE_PERIOD_SEC = 4 * 60 * 60; // 4 hours in seconds, time between automatic updates of articles list
    int EXECUTION_WINDOW_WIDTH_SEC = 1 * 60 *60; // 1 hour

    //other constants
    int ART_NUM = 20; //number of articles per page
    int DELAY_TO_START_ACTIVITY_MILLIS = 200;

    //List styles
    int LIST_STYLE_ALL = 0;
    int LIST_STYLE_PICTURES_AND_TITLES = 1;

    String URL_SUBSCRIPTION_INFO = "http://www.cestaplus.sk/predplatne/info";
    String URL_SUBSCRIPTION_PROLONG = "http://www.cestaplus.sk/predplatne?temp=new";
}
