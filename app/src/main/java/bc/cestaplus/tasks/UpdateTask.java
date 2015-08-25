package bc.cestaplus.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bc.cestaplus.network.Parser;
import bc.cestaplus.network.Requestor;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.utilities.MyApplication;
import bc.cestaplus.extras.ArticlesLoadedListener;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.Util;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Matej on 13. 4. 2015.
 */
public class UpdateTask
    extends AsyncTask<Void, Void, ArrayList<ArticleObj>> {

    //atributes
    private boolean issueNotification;
    private ArticlesLoadedListener myComponent;

    //data
    private ArrayList<ArticleObj> newArticles;
    //dates format
    private DateFormat dateFormatAPP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    //semaphore
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock w = rwl.writeLock();

    /**
     * CONSTRUCTOR
     */
    public UpdateTask(ArticlesLoadedListener myComponent, boolean issueNotification) {
        //this.myService = myService;
        volleySingleton = VolleySingleton.getInstance(CustomApplication.getCustomAppContext());
        requestQueue = volleySingleton.getRequestQueue();

        this.myComponent = myComponent;
        this.issueNotification = issueNotification;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //what about synchronized??
    protected ArrayList<ArticleObj> doInBackground(Void... params) {

        if(rwl.isWriteLocked()){
            Util.issueNotification("Čakám, semafor zamknutý", 4); // semafory id = 4
        }
        w.lock(); //uzamknutie semafora

        long tryTime = currentTimeMillis(); //save current time before sending request
        MyApplication.saveToPreferences(CustomApplication.getCustomAppContext(), "lastTryTime", tryTime); // save try time

        JSONArray response = Requestor.createUpdateRequest(requestQueue); // 1 - send request
        ArrayList<ArticleObj> listArticles = Parser.parseJsonArrayResponse(response); // 2 - parse the response //response null check inside this method

        newArticles = new ArrayList<>(); //inicialisation of arraylist
        newArticles = getNewArticles(listArticles); //check if they are really new

        if (!newArticles.isEmpty()) { // if new articles are not empty
            MyApplication.getWritableDatabase().updateArticles(newArticles); //3 - update database

            //MyApplication.saveToPreferences(CustomApplication.getCustomAppContext(), "lastUpdate", tryTime); // 4 - change update time

            if (issueNotification){
                Util.issueNotification("Počet nových článkov: " + newArticles.size(), 1); // ak sú nové články id = 1
            }

        /*} else {
            if (issueNotification){
                Util.issueNotification("Žiadne nové články", 2); // ak nie sú žiadne nové články id = 2
            }
            */
        }

        ArrayList<ArticleObj> ret = MyApplication.getWritableDatabase().getAllArticles(); //nacitanie clankov z databazy

        if(!newArticles.isEmpty()){ // if new articles are not empty
            MyApplication.saveToPreferences(CustomApplication.getCustomAppContext(), "lastUpdate", ret.get(0).getPubDate().getTime()); // 4 - change update time
        }

        w.unlock(); //odomknutie semafora

        return ret;
    } //end doInBackground

    @Override
    protected void onPostExecute(ArrayList<ArticleObj> listArticles) {
        if ( myComponent != null){
            myComponent.onArticlesLoaded(listArticles);
            myComponent.numNewArticles(newArticles.size());
        }
    } //end onPostExecute

    private ArrayList<ArticleObj> getNewArticles(ArrayList<ArticleObj> listArticles) {
        ArrayList<ArticleObj> newArticles = new ArrayList<ArticleObj>();

        long defaultVal = 0;
        try {
            defaultVal = dateFormatAPP.parse("2010-01-01 00:00:00").getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date lastUpdate = new Date(MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(), "lastUpdate", defaultVal));

        //check
        for (int i = 0; i < listArticles.size(); i++){
            if ( ( listArticles.get(i).getPubDate().compareTo(lastUpdate) ) > 0 ){
                newArticles.add(listArticles.get(i));
            }
        }//end for

        return newArticles;
    }//end getNewArticles()

} //end UpdateTask
