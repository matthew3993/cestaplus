package bc.cestaplus.extras;

import java.util.ArrayList;

import bc.cestaplus.objects.ArticleObj;

/**
 * Created by Matej on 13. 4. 2015.
 */
public interface ArticlesLoadedListener {
    public void onArticlesLoaded(ArrayList<ArticleObj> listArticles);
}
