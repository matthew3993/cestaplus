package bc.cestaplus.network;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.MyApplication;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Endpoints {

//atributes
    private static DateFormat dateFormatBaterka = new SimpleDateFormat("dd-MM-yyyy");

//constants
    public static final String URL_CESTA_PLUS_ANDROID = "http://www.cestaplus.sk/_android/";

    public static final String GET_ARTICLES = "getAndroidData.php";
    public static final String GET_NEW_ARTICLES = "getAndroidNewArticle.php";
    public static final String GET_CONCRETE_ARTICLE = "getAndroidDataArticle.php";
    public static final String GET_ANDROID_BATERKA = "getAndroidBaterka.php";
    public static final String LOGIN = "getAndroidLogin.php";

//dates formats
    public static DateFormat dateFormatAPP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat dateFormatAPI = new SimpleDateFormat("yyyy-MM-dd%HH:mm:ss");

//static methods
    public static String getListOfArticlesRequestUrl(String section, int limit, int page){
        return URL_CESTA_PLUS_ANDROID + GET_ARTICLES  + "?section="+section + "&limit="+limit + "&page="+page;
    }

    /**
     * Metóda vyskladá URL adresu update requestu.
     * @return
     */
    public static String getUpdateRequestUrl(){

        long defaultVal = 0;
        try {
            defaultVal = dateFormatAPP.parse("2010-01-01 00:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String lastUpdateTime = dateFormatAPI.format(MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(), "lastUpdate", defaultVal));

        return URL_CESTA_PLUS_ANDROID + GET_NEW_ARTICLES + "?date=" + lastUpdateTime + "&limit="+20 + "&page="+1;
    }

    public static String getConcreteArticleRequestUrl(String id, boolean withPictures, String API_key) {
        int wp = 1;
        if (!withPictures){
            wp = 0;
        }

        return URL_CESTA_PLUS_ANDROID + GET_CONCRETE_ARTICLE + "?id="+id + "&withPictures="+wp
                + ( API_key != null ? ("&apikey="+API_key) : "" );
    } //end getConcreteArticleRequestUrl

    public static String getBaterkaUrl(Date pubDate) {

        String baterkaDateString = dateFormatBaterka.format(pubDate);

        return URL_CESTA_PLUS_ANDROID + GET_ANDROID_BATERKA + "?date=" + baterkaDateString;
    }

    public static String getLoginUrl(){
        return URL_CESTA_PLUS_ANDROID + LOGIN;
    }

} //end class Endpoints
