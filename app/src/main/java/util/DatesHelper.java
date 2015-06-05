package util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import async_request.METHOD;
import async_request.UIResponseListenerInterface;

/**
 * Created by raymundo.piedra on 25/01/15.
 */
public class DatesHelper {

    private static  String      TAG  =  "DatesHelper";

    private static 	DatesHelper   						helper;

    private DatesHelper(){
    }
    public static synchronized DatesHelper sharedInstance(){
        if (helper == null)
            helper = new DatesHelper();
        return helper;
    }

    public static String getStringDate (Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public static String getStringDatePlane (Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public static long daysFromLastUpdate(Date lastUpdate) {

        Date now                    = new Date();
        Calendar cal_now            = Calendar.getInstance();
        cal_now.setTime(now);
        Calendar call_last          = Calendar.getInstance();
        call_last.setTime(lastUpdate);

        Calendar date = (Calendar) call_last.clone();
        long daysBetween = 0;
        while (date.before(cal_now)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        Log.d("TAG","Difference: "+daysBetween);
        return daysBetween;
    }

    public static String getStringDateDays (Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String getLiteralDate(Date date){
        String str_date     = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String[] separated  = str_date.split("-");
        String year         = separated[0];
        String month        = separated[1];
        String day          = separated[2];

        return ""+day+" DE "+DatesHelper.monthByNumber(month)+" DE "+year;
    }

    public static String monthByNumber(String str_number){
        int number = Integer.parseInt(str_number);
        switch (number){
            case 1:
                return "ENERO";
            case 2:
                return "FEBRERO";
            case 3:
                return "MARZO";
            case 4:
                return "ABRIL";
            case 5:
                return "MAYO";
            case 6:
                return "JUNIO";
            case 7:
                return "JULIO";
            case 8:
                return "AGOSTO";
            case 9:
                return "SEPTIEMBRE";
            case 10:
                return "OCTUBRE";
            case 11:
                return "NOVIEMBRE";
            case 12:
                return "DICIEMBRE";
        }
        return "";
    }

    public static String dateFormattedWithYearMonthAndDay(String formattedDate){
        String[] parts  = formattedDate.split("-");
        String day      = parts[0];
        String month    = parts[1];
        if (month.length() == 1)
            month   = "0".concat(month);
        if (day.length() == 1)
            day   = "0".concat(day);

        return ((parts[2]).concat(month)).concat(day);

    }
}
