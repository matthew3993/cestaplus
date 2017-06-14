package sk.cestaplus.cestaplusapp.network;

import java.text.ParseException;
import java.util.Date;

import sk.cestaplus.cestaplusapp.utilities.DateFormats;
import sk.cestaplus.cestaplusapp.utilities.MyApplication;

import static sk.cestaplus.cestaplusapp.extras.Constants.ART_NUM;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Endpoints {

    //constants
    public static final String URL_CESTA_PLUS_ANDROID = "http://www.cestaplus.sk/_android/";

    public static final String GET_ARTICLES = "getAndroidData.php";
    public static final String GET_NEW_ARTICLES = "getAndroidNewArticle.php";
    public static final String GET_CONCRETE_ARTICLE = "getAndroidDataArticle.php";
    public static final String GET_ANDROID_BATERKA = "getAndroidBaterka.php";
    public static final String LOGIN = "getAndroidLogin.php";

    //static methods
    public static String getListOfArticlesRequestUrl(String sectionId, int limit, int page){
        return URL_CESTA_PLUS_ANDROID + GET_ARTICLES  + "?section="+sectionId + "&limit="+limit + "&page="+page;
    }

    /**
     * Builds up URL address of update request
     */
    public static String getUpdateRequestUrl(){
        Date date = MyApplication.getWritableDatabase().getFirstArticleDate();
        String lastUpdateTime = DateFormats.dateFormatAPI.format(date);

        return URL_CESTA_PLUS_ANDROID + GET_NEW_ARTICLES + "?date=" + lastUpdateTime + "&limit="+ART_NUM + "&page="+1;
    }

    public static String getConcreteArticleRequestUrl(String id, boolean withPictures) {
        int wp = 1;
        if (!withPictures){
            wp = 0;
        }

        return URL_CESTA_PLUS_ANDROID + GET_CONCRETE_ARTICLE + "?id="+id + "&withPictures="+wp;
    } //end getConcreteArticleRequestUrl

    public static String getBaterkaUrl(Date pubDate) {

        String baterkaDateString = DateFormats.dateFormatBaterkaURL.format(pubDate);

        return URL_CESTA_PLUS_ANDROID + GET_ANDROID_BATERKA + "?date=" + baterkaDateString;
    }

    public static String getLoginUrl(){
        return URL_CESTA_PLUS_ANDROID + LOGIN;
    }

} //end class Endpoints
