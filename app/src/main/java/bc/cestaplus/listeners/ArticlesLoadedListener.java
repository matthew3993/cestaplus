package bc.cestaplus.listeners;

import java.util.ArrayList;

import bc.cestaplus.objects.ArticleObj;

/**
 * Created by Matej on 13. 4. 2015.
 */
public interface ArticlesLoadedListener {
    public void onArticlesLoaded(ArrayList<ArticleObj> listArticles);
    public void numNewArticles(int count); //count of new articles
}