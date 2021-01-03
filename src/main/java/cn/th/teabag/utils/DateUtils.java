package cn.th.teabag.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String convert(String source) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
            Date parse = simpleDateFormat.parse(source);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parse);
            cal.add(Calendar.HOUR,8);
            parse=cal.getTime();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df1.format(parse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
