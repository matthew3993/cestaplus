package sk.cestaplus.cestaplusapp.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by matth on 26.11.2016.
 */
public class DateFormats {
    public static DateFormat dateFormatArticleDatePrint = new SimpleDateFormat("dd. MM. yyyy");
    public static DateFormat dateFormatArticleTimePrint = new SimpleDateFormat("HH:mm");

    public static DateFormat dateFormatJSON = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static DateFormat dateFormatBaterkaURL = new SimpleDateFormat("dd-MM-yyyy");
    public static DateFormat dateFormatBaterkaShortPrint = new SimpleDateFormat("dd. MM.");
    public static DateFormat dateFormatBaterkaShort = new SimpleDateFormat("dd.MM.");

    public static DateFormat dateFormatDay = new SimpleDateFormat("d.");
    public static DateFormat dateFormatYear = new SimpleDateFormat("yyyy");

    public static DateFormat dateFormatAPI = new SimpleDateFormat("yyyy-MM-dd%HH:mm:ss");
}
