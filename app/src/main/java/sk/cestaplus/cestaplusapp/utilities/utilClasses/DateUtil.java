package sk.cestaplus.cestaplusapp.utilities.utilClasses;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.utilities.DateFormats;

/**
 * Created by matth on 21.02.2017.
 */

public class DateUtil {

    /**
     * SOURCE: http://stackoverflow.com/a/7784110
     */
    public static Date getZeroTimeDate(Date inputDate) {
        Date res;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime( inputDate );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    /**
     * SOURCES:
     *      http://stackoverflow.com/questions/5574673/what-is-the-easiest-way-to-get-the-current-day-of-the-week-in-android
     *      http://stackoverflow.com/questions/18600257/how-to-get-the-weekday-of-a-date
     *      http://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
     */
    public static String getDayNameFromDate(Context context, Date pubDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pubDate);                      // http://stackoverflow.com/questions/18600257/how-to-get-the-weekday-of-a-date
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String dayOfTheWeekString;
        switch (day) {
            case Calendar.MONDAY:{
                dayOfTheWeekString = context.getString(R.string.monday);
                break;
            }
            case Calendar.TUESDAY:{
                dayOfTheWeekString = context.getString(R.string.tuesday);
                break;
            }
            case Calendar.WEDNESDAY:{
                dayOfTheWeekString = context.getString(R.string.wednesday);
                break;
            }
            case Calendar.THURSDAY:{
                dayOfTheWeekString = context.getString(R.string.thursday);
                break;
            }
            case Calendar.FRIDAY:{
                dayOfTheWeekString = context.getString(R.string.friday);
                break;
            }
            case Calendar.SATURDAY:{
                dayOfTheWeekString = context.getString(R.string.saturday);
                break;
            }
            case Calendar.SUNDAY:{
                dayOfTheWeekString = context.getString(R.string.sunday);
                break;
            }
            default: dayOfTheWeekString = context.getString(R.string.sunday);
        }

        return dayOfTheWeekString;
    }

    /**
     * !!! WARNING !!! : Week in Calendar class starts with SUNDAY!
     * So method returns 1 for Sunday, 2 for Monday, etc. !!!
     *
     * SOURCES:
     *      http://stackoverflow.com/questions/5574673/what-is-the-easiest-way-to-get-the-current-day-of-the-week-in-android
     *      http://stackoverflow.com/questions/18600257/how-to-get-the-weekday-of-a-date
     *      http://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
     */
    public static int getDayOfWeekInt(Date pubDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pubDate);                      // http://stackoverflow.com/questions/18600257/how-to-get-the-weekday-of-a-date
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek; // Week in Calendar class starts with SUNDAY !
    }

    /**
     * @param date Date object to be formated
     * @return String of date in format: 5. februára 2017
     */
    public static String getDateString(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        sb.append(DateFormats.dateFormatDay.format(date)).append(" ");

        sb.append(getMonthStringFromDate(context, date)).append(" ");;

        sb.append(DateFormats.dateFormatYear.format(date)).append(" ");

        return sb.toString();
    }

    /**
     * SOURCE: http://stackoverflow.com/questions/7182996/java-get-month-integer-from-date
     */
    private static String getMonthStringFromDate(Context context, Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int monthNum = cal.get(Calendar.MONTH);

        switch (monthNum){
            case Calendar.JANUARY:{
                return context.getString(R.string.january);
            }
            case Calendar.FEBRUARY:{
                return context.getString(R.string.february);
            }
            case Calendar.MARCH:{
                return context.getString(R.string.march);
            }
            case Calendar.APRIL:{
                return context.getString(R.string.april);
            }
            case Calendar.MAY:{
                return context.getString(R.string.may);
            }
            case Calendar.JUNE:{
                return context.getString(R.string.june) ;
            }
            case Calendar.JULY:{
                return context.getString(R.string.july);
            }
            case Calendar.AUGUST:{
                return context.getString(R.string.august);
            }
            case Calendar.SEPTEMBER:{
                return context.getString(R.string.september);
            }
            case Calendar.OCTOBER:{
                return context.getString(R.string.october);
            }
            case Calendar.NOVEMBER:{
                return context.getString(R.string.november);
            }
            case Calendar.DECEMBER:{
                return context.getString(R.string.december);
            }
            default: {
                return String.format("%02d", monthNum); //SOURCE: http://stackoverflow.com/a/10651147
            }
        }
    }

    /**
     * Can return negative number!
     * SOURCE: https://stackoverflow.com/a/37659716
     */
    public static long daysBetween(Date inputDate){
        //Calendar calendar = Calendar.getInstance();
        Date inputWithoutTime = removeTime(inputDate);
        //calendar.setTime(inputWithoutTime);
        //long inputWithoutTimeMs = calendar.getTimeInMillis();

        long inputWithoutTimeMs = inputWithoutTime.getTime();

        Date today = Calendar.getInstance().getTime();
        Date todayWithoutTime = removeTime(today);
        long todayWithoutTimeMs = todayWithoutTime.getTime();

        //long todayWithoutTimeMs = removeTime(Calendar.getInstance().getTime()).getTime();

        //long msDiff = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        long msDiff = inputWithoutTimeMs - todayWithoutTimeMs;
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

        return daysDiff;
    }

    /**
     * SOURCES:
     *      https://stackoverflow.com/questions/9816459/removing-time-from-a-date-object
     *      https://stackoverflow.com/a/10331747
     * Check: https://stackoverflow.com/a/16225287
     *
     * Removing time part from Date object is impossible.
     * It only possible to set all time attributes to 0.
     * @param date date you want to "remove" time.
     * @return date with all time attributes set to 0
     */
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
}
