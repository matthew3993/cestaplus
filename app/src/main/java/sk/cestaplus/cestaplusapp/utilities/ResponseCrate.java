package sk.cestaplus.cestaplusapp.utilities;

import java.util.ArrayList;

import sk.cestaplus.cestaplusapp.objects.ArticleObj;

/**
 * Created by matth on 01.03.2017.
 */

public class ResponseCrate {

    private final String headerArticleId;
    private final ArrayList<ArticleObj> articles;

    public ResponseCrate(String headerArticleId, ArrayList<ArticleObj> articles) {
        this.headerArticleId = headerArticleId;
        this.articles = articles;
    }

    public String getHeaderArticleId() {
        return headerArticleId;
    }

    public ArrayList<ArticleObj> getArticles() {
        return articles;
    }
}
