package bc.cestaplus.extras;

/**
 * Created by Matej on 4.3.2015.
 */
public interface IKeys {

    public static final String KEY_PARENT_ACTIVITY = "ParentActivity";
    public static final String KEY_MAIN_ACTIVITY = "MainActivity";
    public static final String KEY_RUBRIKA_ACTIVITY = "RubrikaActivity";
    public static final String KEY_ARTICLE_ACTIVITY = "ArticleActivity";
    public static final String KEY_BATERKA_ACTIVITY = "BaterkaActivity";
    public static final String KEY_O_PORTALI_ACTIVITY = "OPortaliActivity";
    public static final String KEY_SETTINGS_ACTIVITY = "SettingsActivity";
    public static final String KEY_LOGGED_ACTIVITY = "LoggedActivity";
    public static final String KEY_NOT_LOGGED_ACTIVITY = "NotLoggedActivity";

    public interface IPrehlad {

    //login
        public static final String KEY_ERROR_CODE = "error_code";
        public static final String KEY_APY_KEY = "API_key";

    //articleObj
        public static final String KEY_CLANKY = "cestaplus_allArticles";
        public static final String KEY_TITLE = "title";
        public static final String KEY_SHORT_TEXT = "short_text";
        public static final String KEY_IMAGE_URL = "img";
        public static final String KEY_PUB_DATE = "dateOfPublsihed";
        public static final String KEY_SECTION = "section";
        public static final String KEY_LOCKED = "locked";
        public static final String KEY_ID = "ID";

    //articleText
        public static final String KEY_AUTOR = "author";
        public static final String KEY_TEXT = "text";

     //baterka
        public static final String KEY_COORDINATES = "coordinates";
        public static final String KEY_SCRIPTURE = "scripture";
        public static final String KEY_QUOTE = "quote";
        public static final String KEY_DEPTH_1 = "depth1";
        public static final String KEY_DEPTH_2 = "depth2";
        public static final String KEY_DEPTH_3 = "depth3";
        public static final String KEY_HINT = "hint";
    }
}
