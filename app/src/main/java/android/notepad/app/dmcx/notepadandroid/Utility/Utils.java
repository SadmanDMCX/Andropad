package android.notepad.app.dmcx.notepadandroid.Utility;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class Utils {

    public static String getFormatedDateTimeFromLong(String dateTime, String pattern) {
        long  dt = Long.valueOf(dateTime);
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(dt);
    }

}
