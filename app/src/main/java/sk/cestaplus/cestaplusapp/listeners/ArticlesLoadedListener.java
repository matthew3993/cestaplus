package sk.cestaplus.cestaplusapp.listeners;

import sk.cestaplus.cestaplusapp.utilities.ResponseCrate;

/**
 * Created by Matej on 13. 4. 2015.
 */
public interface ArticlesLoadedListener {
    void onArticlesLoaded(ResponseCrate responseCrate);
    void numNewArticles(int count); //count of new articles
    void onLoadingError(); // this method is called if there is error during loading (most likely problem with network)
}
