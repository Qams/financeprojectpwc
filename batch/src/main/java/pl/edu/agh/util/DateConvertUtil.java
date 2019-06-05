package pl.edu.agh.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateConvertUtil {
    public static String getLastDayDateFormat() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    public static String getTodayDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        return dateFormat.format(new Date());
    }
}
