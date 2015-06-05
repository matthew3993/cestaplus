package bc.cestaplus.network;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Endpoints {

//atributes
    private static DateFormat dateFormatBaterka = new SimpleDateFormat("dd-MM-yyyy");

//constants
    public static final String URL_CESTA_PLUS_ANDROID = "http://www.cestaplus.sk/_android/";

    public static final String GET_ARTICLES = "getAndroidData.php";
    public static final String GET_CONCRETE_ARTICLE = "getAndroidDataArticle.php";
    public static final String GET_ANDROID_BATERKA = "getAndroidBaterka.php";
    public static final String LOGIN = "getAndroidLogin.php";

//static methods
    public static String getArticleRequestUrl(String id, boolean withPictures, String API_key) {
        int wp = 1;
        if (!withPictures){
            wp = 0;
        }

        return URL_CESTA_PLUS_ANDROID + GET_CONCRETE_ARTICLE + "?id="+id + "&withPictures="+wp
                + ( API_key != null ? ("&apikey="+API_key) : "" );
    } //end getArticleRequestUrl

    public static String getRequestUrl(String section, int limit, int page){
        //return URL_CESTA_PLUS_ANDROID+"?apikey="+ MainActivity.API_KEY+"&limit="+limit;
        return URL_CESTA_PLUS_ANDROID + GET_ARTICLES  + "?section="+section + "&limit="+limit + "&page="+page;
    }

    public static String getLoginUrl(){
        return URL_CESTA_PLUS_ANDROID + LOGIN;
    }

    public static String getBaterkaUrl(Date pubDate) {

        String baterkaDateString = dateFormatBaterka.format(pubDate);

        return URL_CESTA_PLUS_ANDROID + GET_ANDROID_BATERKA + "?date=" + baterkaDateString;
    }
} //end class Endpoints
