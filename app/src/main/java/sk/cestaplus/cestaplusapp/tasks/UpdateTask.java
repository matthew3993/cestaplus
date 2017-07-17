package sk.cestaplus.cestaplusapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;

//import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.Requestor;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.CustomNotificationManager;
import sk.cestaplus.cestaplusapp.utilities.DateFormats;
import sk.cestaplus.cestaplusapp.utilities.ResponseCrate;
import sk.cestaplus.cestaplusapp.utilities.MyApplication;
import sk.cestaplus.cestaplusapp.listeners.ArticlesLoadedListener;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.DateUtil;

import static java.lang.System.currentTimeMillis;
import static sk.cestaplus.cestaplusapp.extras.IKeys.*;

/**
 * Created by Matej on 13. 4. 2015.
 */
public class UpdateTask
    extends AsyncTask<Void, Void, ResponseCrate> {

    //atributes
    private ArticlesLoadedListener articlesLoadedListener;
    private boolean issueNotification;
    private boolean refreshing;

    //data
    private ArrayList<ArticleObj> newArticles;
    private boolean loadingError = false;

    //networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    //semaphore
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock w = rwl.writeLock();

    /**
     * PRIMARY constructor
     * sets {@link UpdateTask#issueNotification} to input
     * sets {@link UpdateTask#refreshing} to false
     */
    public UpdateTask(ArticlesLoadedListener myComponent, boolean issueNotification) {
        volleySingleton = VolleySingleton.getInstance(CustomApplication.getCustomAppContext());
        requestQueue = volleySingleton.getRequestQueue();

        this.articlesLoadedListener = myComponent;
        this.issueNotification = issueNotification;
        this.refreshing = false;

        newArticles = new ArrayList<>(); //initialisation of arraylist
    }

    /**
     * SECONDARY constructor - used when refreshing
     * sets {@link UpdateTask#issueNotification} to false
     * sets {@link UpdateTask#refreshing} to true
     */
    public UpdateTask(ArticlesLoadedListener myComponent) {
        this(myComponent, false);
        this.refreshing = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //what about synchronized??
    protected ResponseCrate doInBackground(Void... params) {
        SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        if(rwl.isWriteLocked()){
            CustomNotificationManager.issueNotification("Čakám, semafor zamknutý", 4); // semafory id = 4
        }
        w.lock(); //old_lock semaphore

        long tryTime = currentTimeMillis(); //save current time before sending request
        MyApplication.saveToPreferences(CustomApplication.getCustomAppContext(), KEY_LAST_TRY_TIME, tryTime); // save try time

        ArrayList<ArticleObj> retArticles;
        String retID;

        try {
            //CustomNotificationManager.issueNotification("Try to update... - " + issueNotification, 1); // test notification

            JSONObject response = Requestor.sendUpdateRequest(requestQueue); // 1 - send request - this method throws exceptions

            ResponseCrate responseCrate = Parser.parseJsonObjectResponse(response); // 2 - parse the response //response null check inside this method

            newArticles = checkNewArticles(responseCrate.getArticles()); //check if they are really new
            session.setLastHeaderArticleId(responseCrate.getHeaderArticleId());

            if (!newArticles.isEmpty()) { // if new articles are not empty
                MyApplication.getWritableDatabase().updateArticles(newArticles); //3 - update database

                if (issueNotification){
                    CustomNotificationManager.issueNotification("Počet nových článkov: " + newArticles.size(), 1); // ak sú nové články id = 1
                }
            }

        } catch (InterruptedException  | ExecutionException | TimeoutException ex) {
            Log.e("error", ex + "");
            loadingError = true;

        } finally {
            retArticles = MyApplication.getWritableDatabase().getAllArticles(); //load articles from database
            retID = session.getLastHeaderArticleId();

            if(!newArticles.isEmpty()){ // if new articles are not empty
                MyApplication.saveToPreferences(CustomApplication.getCustomAppContext(), KEY_LAST_UPDATE, retArticles.get(0).getPubDate().getTime()); // 4 - change update time
            }
        }
        w.unlock(); //unlock semaphore

        return new ResponseCrate(retID, retArticles);
    } //end doInBackground

    @Override
    protected void onPostExecute(ResponseCrate responseCrate) {
        // onArticlesLoaded is called every time !!
        articlesLoadedListener.onArticlesLoaded(responseCrate, loadingError); //callback of listener

        if (loadingError) {
            articlesLoadedListener.onLoadingError();
        } else {
            // no error
            // calling numNewArticles() only if there is no loading error

            if (refreshing){
                articlesLoadedListener.numNewArticles(newArticles.size());

            } else {
                // no refreshing - for example new start of activity
                if (!newArticles.isEmpty()){
                    // if it's not refreshing, call numNewArticles only if there ARE some new articles
                    articlesLoadedListener.numNewArticles(newArticles.size());
                }
            }
        }
    } //end onPostExecute

    private ArrayList<ArticleObj> checkNewArticles(ArrayList<ArticleObj> loadedArticles) {

        // get articles from database and date of first article in database
        ArrayList<ArticleObj> databaseArticles = MyApplication.getWritableDatabase().getAllArticles(); //load articles from database

        ArrayList<ArticleObj> filteredArticles = filterArticles(databaseArticles);

        //check IDS
        ArrayList<ArticleObj> newArticles = new ArrayList<>();

        //SOURCE: http://stackoverflow.com/questions/886955/breaking-out-of-nested-loops-in-java
        outer:
        for (ArticleObj loadedArticle : loadedArticles) {

            for (ArticleObj filteredArticle : filteredArticles) {
                if(filteredArticle.getID().equals(loadedArticle.getID())){
                    continue outer;
                }
            }

            newArticles.add(loadedArticle);
        }

        /*
        OTHER WAY:
        for (ArticleObj loadedArticle : loadedArticles) {
            //LocalDate loadedArticleDate = new LocalDate(loadedArticle.getPubDate());
            boolean exist = false;

            for (ArticleObj filteredArticle : filteredArticles) {
                //LocalDate databaseArticleDate = new LocalDate(filteredArticle.getPubDate());

                if(filteredArticle.getID().equals(loadedArticle.getID())){
                    exist = true;
                    break;
                }
            }

            if (!exist){
                newArticles.add(loadedArticle);
            }
        }*/

        return newArticles;
    }//end checkNewArticles()

    /**
     * Filter database articles - keep only articles from same day or NEWER (should not happen)
     * that first article in db == update request date
     * This method is used because getAndroidNewArticle.php script works that way - it returns
     * also articles from same day as input (request) date
     */
    private ArrayList<ArticleObj> filterArticles(ArrayList<ArticleObj> databaseArticles) {
        Date firstArticleDate = MyApplication.getWritableDatabase().getFirstArticleDate();

        ArrayList<ArticleObj> filteredArticles = new ArrayList<>();
        Date firstArticleLocalDate = DateUtil.getZeroTimeDate(firstArticleDate);

        for (ArticleObj databaseArticle : databaseArticles) {
            Date databaseArticleDate = DateUtil.getZeroTimeDate(databaseArticle.getPubDate());

            int compareResult = databaseArticleDate.compareTo(firstArticleLocalDate);
            if (compareResult >= 0){
                filteredArticles.add(databaseArticle);
            }
        }
        return filteredArticles;
    }

    /*
    @NonNull
    private ArrayList<ArticleObj> filterArticles_old(ArrayList<ArticleObj> databaseArticles) {
        Date firstArticleDate = MyApplication.getWritableDatabase().getFirstArticleDate();

        //filter database articles - keep only articles from same day or NEWER (should not happen) that first article in db
        //same day, because that way getAndroidNewArticle.php script works - it returns also articles from same day as input date
        ArrayList<ArticleObj> filteredArticles = new ArrayList<>();
        LocalDate firstArticleLocalDate = new LocalDate(firstArticleDate);

        for (ArticleObj databaseArticle : databaseArticles) {
            // SOURCE: http://stackoverflow.com/questions/20736449/converting-java-sql-date-java-util-date-to-org-joda-time-localdate
            LocalDate databaseArticleDate = new LocalDate(databaseArticle.getPubDate());

            //SOURCE: http://stackoverflow.com/questions/1439779/how-to-compare-two-dates-without-the-time-portion
            int compareResult = databaseArticleDate.compareTo(firstArticleLocalDate);
            if (compareResult >= 0){
                filteredArticles.add(databaseArticle);
            }
        }
        return filteredArticles;
    }
    */

    private ArrayList<ArticleObj> getNewArticles_old(ArrayList<ArticleObj> listArticles) {
        ArrayList<ArticleObj> newArticles = new ArrayList<>();

        long defaultVal = 0;
        try {
            defaultVal = DateFormats.dateFormatJSON.parse("2010-01-01 00:00:00").getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // date of first article in database (index 0)
        Date lastUpdate = new Date(MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(), KEY_LAST_UPDATE, defaultVal));

        //check
        for (int i = 0; i < listArticles.size(); i++){
            if ( ( listArticles.get(i).getPubDate().compareTo(lastUpdate) ) > 0 ){
                newArticles.add(listArticles.get(i));
            }
        }//end for

        return newArticles;
    }//end getNewArticles_old()

} //end UpdateTask
