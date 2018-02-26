package c.com.ecomerceuser.helper;


import android.text.format.DateUtils;
import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Ramu on 07-01-2018.
 */

public class DateTimeUtilities {


    public static String getConvertedTime(String currentDate) {
        Log.i("tag","date utils current date"+currentDate);
        String dateIs = currentDate.split(" ")[0];
        return dateIs.split("-")[0] + " " + new DateFormatSymbols().getMonths()[Integer.parseInt(dateIs.split("-")[1]) - 1] + " " + dateIs.split("-")[2];
    }


    public  static  String getCurrentOnlyDate() {
        String strDate = "";
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);//dd/MM/yyyy
            Date now = new Date();
            strDate = sdfDate.format(now);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

/*
    public static String getYesterdayDate() {
        Date date = DateUtils.addDays(new Date(), -1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
*/


    public static String convertLongToDateFormat(String timeString) {
        String chatDate = "";
        long milliseconds = 0, millis;
        try {
            millis = Long.parseLong(timeString);

            long TICKS_AT_EPOCH = 621355968000000000L;
            milliseconds = (millis - TICKS_AT_EPOCH) / (10000);

            String currentDateFormat = "dd/MM/yyyy";
            TimeZone localTimeZone = TimeZone.getDefault();
            Calendar calendarChatMessage = Calendar.getInstance(localTimeZone);
            calendarChatMessage.setTimeInMillis(milliseconds);

            chatDate = android.text.format.DateFormat.format(currentDateFormat, calendarChatMessage).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return chatDate;
    }



}
