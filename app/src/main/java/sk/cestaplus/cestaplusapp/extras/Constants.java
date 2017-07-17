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

    //Volley cache expiration constants
    int CACHE_ENTRY_SOFT_TTL_MIN = 24 * 60; // 24 hours in minutes
    int CACHE_ENTRY_TTL_MIN = 24 * 60; // 24 hours in minutes

    //List styles
    int LIST_STYLE_ALL = 0;
    int LIST_STYLE_PICTURES_AND_TITLES = 1;

    // urls
    String URL_CESTA_PLUS = "http://www.cestaplus.sk/";
    String IMAGES = "images/";
    String SMALL = "_small/";

    String DIMEN_A = "img_75x50/";  // not used
    String DIMEN_B = "img_128x85/";
    String DIMEN_C = "img_300x191/";
    String DIMEN_D = "img_700x467/"; //not used for now - loading default pictures instead

    String URL_SUBSCRIPTION_INFO = "http://www.cestaplus.sk/predplatne/info";
    String URL_SUBSCRIPTION_PROLONG = "http://www.cestaplus.sk/predplatne?temp=new";

    //other
    int RED_REMAINING_DAYS_LIMIT = 5; // <=

    //Log tags - max 20 characters long
    String VOLLEY_DEBUG = "VolleyDebug";
    String NEW_ART_NOTIFICATIONS_DEBUG = "newArticlesNotifDebug";
    String IMAGE_DEBUG = "imageDebug";

}
