package sk.cestaplus.cestaplusapp.extras;

/**
 * Created by Matej on 4.3.2015.
 */
public interface IKeys {

    // Activity keys
    String KEY_PARENT_ACTIVITY = "ParentActivity";
    String KEY_MAIN_ACTIVITY = "MainActivity";
    String KEY_ARTICLE_ACTIVITY = "ArticleActivity";
    String KEY_BATERKA_ACTIVITY = "BaterkaActivity";
    String KEY_O_PORTALI_ACTIVITY = "OPortaliActivity";
    String KEY_SETTINGS_ACTIVITY = "SettingsActivity_old";
    String KEY_LOGGED_ACTIVITY = "LoggedActivity";
    String KEY_NOT_LOGGED_ACTIVITY = "NotLoggedActivity";

    //SAVED STATE
    String KEY_SAVED_STATE_HEADER_ARTICLE = "headerArticle_savedState";
    String KEY_SAVED_STATE_ARTICLES_ALL = "articlesAll_savedState";
    String KEY_SAVED_STATE_PAGES_NUM = "pagesNum_savedState";
    String KEY_SAVED_STATE_BATERKA_TEXT = "baterkaText_savedState";
    String KEY_SAVED_STATE_ARTICLE_OBJ = "articleObj_savedState";
    String KEY_SAVED_STATE_ARTICLE_TEXT = "articleText_savedState";
    String KEY_SAVED_STATE_USER_INFO = "userInfo_savedState";
    String KEY_SAVED_STATE_WAS_NETWORK_ERROR = "wasError_savedState";
    String KEY_SAVED_STATE_ARTICLE_ERROR_CODE = "articleErrorCode_savedState";
    String KEY_SAVED_STATE_ROLE = "role_savedState";
    String KEY_SAVED_SCROLL_PERC = "scrollPercentage_savedState";
    String KEY_SAVED_SECTION = "ulozene_rubrika";

    // INTENTS
    String KEY_INTENT_EXTRA_ARTICLE = "article_intent";
    String KEY_INTENT_EXTRA_BATERKA = "baterka_intent";
    String KEY_INTENT_LOAD_BATERKA_ON_TODAY = "baterka_intent";
    String KEY_INTENT_FROM_NOTIFICATION = "fromNotification";
    String KEY_INTENT_ARTICLE_URL = "article_url";

    //fragment parameters
    String KEY_ROLE = "role";
    String KEY_SECTION_ID = "section_id";
    String KEY_SECTION_NAME = "section_NAME";

    //login
    String KEY_ERROR_CODE = "error_code";
    String KEY_API_KEY = "API_key";

    //articleObj
    String KEY_HEADER_ARTICLE_ID = "idTitleArticle";
    String KEY_ARRAY_ARTICLE = "arrayArticle";
    String KEY_TITLE = "title";
    String KEY_SHORT_TEXT = "short_text";
    String KEY_AUTOR_OBJ = "author";
    String KEY_IMAGE_URL = "img";
    String KEY_PUB_DATE = "dateOfPublsihed";
    String KEY_SECTION = "section";
    String KEY_LOCKED = "locked";
    String KEY_ID = "ID";

    //articleText
    String KEY_AUTOR_TEXT = "author";
    String KEY_TEXT = "text";
    String KEY_URL = "url";
    String DEFAULT_URL = "http://www.cestaplus.sk/cestaplus/clanok/2017-03-06-ako-si-upevnit-vztah-so-straznym-anjelom";

    //baterka
    String KEY_COORDINATES = "coordinates";
    String KEY_SCRIPTURE = "scripture";
    String KEY_QUOTE = "quote";
    String KEY_QUOTE_AUTHOR = "quote_author";
    String KEY_DEPTH_1 = "depth1";
    String KEY_DEPTH_2 = "depth2";
    String KEY_DEPTH_3 = "depth3";
    String KEY_HINT = "hint";

    //user info - for API & session manager
    String KEY_NAME = "name";
    String KEY_SURNAME = "surname";
    String KEY_SUBSCRIPTION_START = "subscription_start";
    String KEY_SUBSCRIPTION_END = "subscription_end";
    String KEY_SUBSCRIPTION_NAME = "subscription_name";

    //
    String KEY_LAST_UPDATE = "lastUpdate";
    String KEY_LAST_TRY_TIME = "lastTryTime";

    //fragment tags
    String TAG_ALL_FRAGMENT = "AllFragment";
    String TAG_SECTION_FRAGMENT = "SectionFragment";
    String TAG_O_PORTALI_FRAGMENT = "AllFragment";

    //sections keys
    String KEY_ALL_SECTION = "all";
    String KEY_BATERKA_SECTION = "baterka";

    //HTTP request params - outgoing
    String KEY_PARAMS_API_KEY = "apikey";
    String KEY_PARAMS_EMAIL = "email";
    String KEY_PARAMS_PASSWORD = "password";
}
