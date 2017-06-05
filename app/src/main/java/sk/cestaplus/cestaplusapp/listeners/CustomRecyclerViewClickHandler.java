package sk.cestaplus.cestaplusapp.listeners;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.ArticleActivity;
import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.adapters.ArticlesRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.extras.Constants;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.ResponseCrate;

import static sk.cestaplus.cestaplusapp.extras.Constants.DELAY_TO_START_ACTIVITY_MILLIS;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ALL_SECTION;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_BATERKA_SECTION;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_EXTRA_ARTICLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_EXTRA_BATERKA;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;

/**
 * Created by matth on 22.02.2017.
 */

public class CustomRecyclerViewClickHandler
    implements RecyclerTouchListener.ClickListener {

    private Fragment fragment;
    private ICustomRecyclerViewClickHandlerDataProvider dataProvider;
    private String sectionID;

    // utils
    private VolleySingleton volleySingleton; //networking
    private String parentActivity;

    /**
     * sectionID is always set to "all"
     */
    public CustomRecyclerViewClickHandler(
            Fragment fragment,
            ICustomRecyclerViewClickHandlerDataProvider dataProvider,
            String parentActivity) {

        this.fragment = fragment;
        this.dataProvider = dataProvider;
        this.parentActivity = parentActivity;

        this.sectionID = KEY_ALL_SECTION; //!!
        volleySingleton = VolleySingleton.getInstance(this.fragment.getActivity().getApplicationContext());
    }

    /**
     * Sets sectionID according to parameter.
     * @param sectionID - ID of section to load more articles
     */
    public CustomRecyclerViewClickHandler(
            Fragment fragment,
            ICustomRecyclerViewClickHandlerDataProvider dataProvider,
            String sectionID,
            String parentActivity) {

        this.fragment = fragment;
        this.dataProvider = dataProvider;
        this.sectionID = sectionID;
        this.parentActivity = parentActivity;

        volleySingleton = VolleySingleton.getInstance(this.fragment.getActivity().getApplicationContext());
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
        ArrayList<ArticleObj> articles = dataProvider.getArticles();
        ArticlesRecyclerViewAdapter articlesRecyclerViewAdapter = dataProvider.getAdapter();

        if (position == articles.size()){ // if loadmore button or progress bar was clicked

            // load more articles only if we are not already loading more articles
            // - case when user clicks on progress bar (or double clicks the load more button)
            if (!articlesRecyclerViewAdapter.isLoading()) {
                loadMoreArticles();
            }

        } else {
            // some article was clicked
            startArticleOrBaterkaActivity(articles.get(position));
        }
    }//end handleClick()

    private void loadMoreArticles() {
        final Context context = fragment.getActivity();
        final ArrayList<ArticleObj> articles = dataProvider.getArticles();
        final ArticlesRecyclerViewAdapter articlesRecyclerViewAdapter = dataProvider.getAdapter();

        int pagesNumTmp = dataProvider.getPagesNum();
        pagesNumTmp++;  // !!! increase number of loaded pages !!!
        dataProvider.setPagesNum(pagesNumTmp);
        final int pagesNum = pagesNumTmp;

        articlesRecyclerViewAdapter.startAnim();

        //load next page
        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //paging
                if (pagesNum == 1) {  // if this is first page, article list is overwritten
                    articles.clear();
                    ResponseCrate responseCrate = Parser.parseJsonObjectResponse(response);
                    articles.addAll(responseCrate.getArticles());

                    if (articles.size() < Constants.ART_NUM) {
                        articlesRecyclerViewAdapter.setNoMoreArticles();
                    }

                } else {
                    // pagesNum > 1 => loaded articles are added to existing list of articles

                    ResponseCrate responseCrate = Parser.parseJsonObjectResponse(response);
                    ArrayList<ArticleObj> moreArticles = responseCrate.getArticles();
                    if (moreArticles.size() < Constants.ART_NUM) {
                        articlesRecyclerViewAdapter.setNoMoreArticles();
                    }
                    articles.addAll(moreArticles);
                    Toast.makeText(fragment.getActivity().getApplicationContext(), context.getString(R.string.toast_loaded_page_num) + " " + pagesNum, Toast.LENGTH_SHORT).show();
                }
                articlesRecyclerViewAdapter.setArticlesList(articles);
            }

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                articlesRecyclerViewAdapter.setError();
                dataProvider.setPagesNum(pagesNum - 1); // !!!  reducing of loaded pages number - because page was not loaded !!!
                Toast.makeText(fragment.getActivity().getApplicationContext(), context.getString(R.string.load_more_error), Toast.LENGTH_SHORT).show();
            } //end of onErrorResponse
        };

        volleySingleton.createGetArticlesObjectRequestGET(sectionID, Constants.ART_NUM, pagesNum, responseLis, errorLis);
    }

    private void startArticleOrBaterkaActivity(ArticleObj articleObj) {
        final Intent intent;

        if (articleObj.getSection().equalsIgnoreCase(KEY_BATERKA_SECTION)) { //if baterka section was clicked
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

        // delay the start of activity because of onClick animation
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment.startActivity(intent);
            }
        }, DELAY_TO_START_ACTIVITY_MILLIS);
    }

    public interface ICustomRecyclerViewClickHandlerDataProvider {

        int getPagesNum();

        void setPagesNum(int pagesNum);

        ArrayList<ArticleObj> getArticles();

        ArticlesRecyclerViewAdapter getAdapter();
    }
}
