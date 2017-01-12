package ch.unibas.ccn_lite_android.helpers;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by adrian on 2016-11-23.
 */

public class Helper {
    private static String TAG = "unoise";
    /**
     * Cleans and returns the passed string by stripping it of all '\n' at its end
     * @param str the string to clean
     * @return the cleaned string
     */
    static public String cleanResultString(String str) {
        if (str != null) {
            while (str.length() > 0 && str.charAt(str.length()-1)=='\n') {
                str = str.substring(0, str.length()-1);
            }
        }
        return str;
    }

    static public Calendar stringToCalendar(String timeSeconds) {
//        try {
//            String[] parts = dateString.split("-");
//            int year = Integer.parseInt(parts[0]);
//            int month = Integer.parseInt(parts[1]) - 1;
//            int day = Integer.parseInt(parts[2]);
//            int hour = Integer.parseInt(parts[3]);
//            int minute = Integer.parseInt(parts[4]);
//            int second = Integer.parseInt(parts[5]);
//            Calendar cal = Calendar.getInstance();
//            cal.set(year, month, day, hour, minute, second);
//            return cal;
//        }
//        catch(Exception e) {
//            Log.e(TAG, "Error reading date string: "+e);
//            return null;
//        }
        try {
            Calendar cal = Calendar.getInstance();
            long timeMillis = Long.parseLong(timeSeconds) * 1000;
            cal.setTimeInMillis(timeMillis);
            return cal;
        }
        catch(Exception e) {
            Log.e(TAG, "Error reading date string: "+e);
            return null;
        }
    }

    static public int getSeqno(long initialTime, long time, int looptime, int initialSeqno) {
//        long nowTime = Calendar.getInstance().getTimeInMillis() / 1000;
        long nowTime = time;
        long diffTime = nowTime - initialTime;
        long diffSeqno =  diffTime / looptime;
        return (int) (diffSeqno - 3);
    }

    static public Calendar getCalendarFromSeqno(long initialTime, int looptime, int initialSeqno, int currentSeqno) {
        Calendar cal = Calendar.getInstance();
        int diffSeqno = currentSeqno - initialSeqno + 1;
        long diffTime = diffSeqno * looptime;
        cal.setTimeInMillis(initialTime + diffTime);
        return cal;
    }
}
