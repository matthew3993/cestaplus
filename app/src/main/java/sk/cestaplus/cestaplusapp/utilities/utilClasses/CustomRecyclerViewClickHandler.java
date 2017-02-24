package sk.cestaplus.cestaplusapp.utilities.utilClasses;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import sk.cestaplus.cestaplusapp.activities.ArticleActivity;
import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.listeners.RecyclerTouchListener;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;

import static sk.cestaplus.cestaplusapp.extras.Constants.DELAY_TO_START_ACTIVITY_MILLIS;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_EXTRA_ARTICLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_EXTRA_BATERKA;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;

/**
 * Created by matth on 22.02.2017.
 */

public class CustomRecyclerViewClickHandler
    implements RecyclerTouchListener.ClickListener {

    private Fragment fragment;
    private int pagesNum;  // number of loaded pages

    private CustomRecyclerViewClickHandlerDataProvider dataProvider;

    // utils
    private VolleySingleton volleySingleton; //networking
    private String parentActivity;

    public CustomRecyclerViewClickHandler(
            Fragment fragment,
            CustomRecyclerViewClickHandlerDataProvider dataProvider,
            String parentActivity) {
        this.fragment = fragment;
        this.pagesNum = 1;
        this.dataProvider = dataProvider;

        this.parentActivity = parentActivity;

        volleySingleton = VolleySingleton.getInstance(fragment.getActivity().getApplicationContext());
    }

    @Override
    public void onClick(View view, int position) {
        handleClick(view, position);
    }

    @Override
    public void onLongClick(View view, int position) {
        //onLongClick code
    }

    private void handleClick(View view, int position) {
        ArrayList<ArticleObj> articlesAll = dataProvider.getArticles();
        ArticleRecyclerViewAdapter arvaAll = dataProvider.getAdapter();

        if (position == articlesAll.size()){ // if loadmore button or progress bar was clicked

            // load more articles only if we are not already loading more articles
            // - case when user clicks on progress bar (or double clicks the load more button)
            if (!arvaAll.isLoading()) {
                loadMoreArticles();
            }

        } else {
            startArticleOrBaterkaActivity(articlesAll.get(position));
        }
    }//end handleClick()

    private void loadMoreArticles() {
        final ArrayList<ArticleObj> articlesAll = dataProvider.getArticles();
        final ArticleRecyclerViewAdapter arvaAll = dataProvider.getAdapter();

        arvaAll.startAnim();

        pagesNum++;  // !!! zvysenie poctu nacitanych stran !!!
        //nacitanie dalsej stranky
        Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //page-ovanie
                if (pagesNum == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                    articlesAll.clear();
                    articlesAll.addAll(Parser.parseJsonArrayResponse(response));

                    if (articlesAll.size() < MainActivity.ART_NUM) {
                        arvaAll.setNoMoreArticles();
                    }

                } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                    ArrayList<ArticleObj> moreArticles = Parser.parseJsonArrayResponse(response);
                    if (moreArticles.size() < MainActivity.ART_NUM) {
                        arvaAll.setNoMoreArticles();
                    }
                    articlesAll.addAll(Parser.parseJsonArrayResponse(response));
                    Toast.makeText(fragment.getActivity().getApplicationContext(), "Načítaná stránka číslo: " + pagesNum, Toast.LENGTH_SHORT).show();
                }
                arvaAll.setArticlesList(articlesAll);
            }

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                arvaAll.setError();
                pagesNum--;  // !!!  reducing of loaded pages number - because page was not loaded !!!
                Toast.makeText(fragment.getActivity().getApplicationContext(), "Chyba pri načítavaní ďalších článkov", Toast.LENGTH_SHORT).show();
            } //end of onErrorResponse
        };

        volleySingleton.createGetClankyArrayRequestGET("all", 20, pagesNum, responseLis, errorLis);
    }

    private void startArticleOrBaterkaActivity(ArticleObj articleObj) {
        final Intent intent;

        if (articleObj.getSection().equalsIgnoreCase("baterka")) { //if baterka was clicked
            intent = new Intent(fragment.getActivity(), BaterkaActivity.class);
            intent.putExtra(KEY_INTENT_EXTRA_BATERKA, articleObj);

        } else { // if other sections was clicked
            intent = new Intent(fragment.getActivity(), ArticleActivity.class);
            intent.putExtra(KEY_INTENT_EXTRA_ARTICLE, articleObj);
        }
        intent.putExtra(KEY_PARENT_ACTIVITY, parentActivity);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //SOURCES: http://stackoverflow.com/a/12664620  http://stackoverflow.com/a/12319970

        //region  OLD IMPLEMENTATION with header in recyler view
        /*if (position == 0) {
            //header view was clicked
            intent = new Intent(getApplicationContext(), ArticleActivity.class);
            intent.putExtra(KEY_INTENT_EXTRA_ARTICLE, headerArticle);
            intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);

        } else {
            // row view was clicked (but not footer)
            if (articlesAll.get(position-1).getSection().equalsIgnoreCase("baterka")) { //if baterka was clicked
                intent = new Intent(getApplicationContext(), BaterkaActivity.class);
                intent.putExtra(KEY_INTENT_EXTRA_BATERKA, articlesAll.get(position-1));
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);

            } else { // if other sections was clicked
                intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra(KEY_INTENT_EXTRA_ARTICLE, articlesAll.get(position-1));
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
            }
        }*/
        // endregion

        // delay the start of ArticleActivity because of onClick animation
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment.startActivity(intent);
            }
        }, DELAY_TO_START_ACTIVITY_MILLIS);
    }

    public int getPagesNum() {
        return pagesNum;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
    }

    public interface CustomRecyclerViewClickHandlerDataProvider {

        ArrayList<ArticleObj> getArticles();

        ArticleRecyclerViewAdapter getAdapter();
    }
}
