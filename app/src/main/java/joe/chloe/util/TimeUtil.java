package joe.chloe.util;

import com.blankj.utilcode.util.LogUtils;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 */
public class TimeUtil {

    /**
     * 根据当前秒数返回剩余时间格式： 1天02小时03分05秒
     *
     * @param ms
     * @return
     */
    public static String formatTime_1(Long ms) {
        Integer ss = 1;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }

        if (hour > 0) {
            if (hour < 10) {
                sb.append("0");
            }
            sb.append(hour + ":");
        } else {
            sb.append("00:");
        }

        if (minute > 0) {
            if (minute < 10) {
                sb.append("0");
            }
            sb.append(minute + ":");
        } else {
            sb.append("00:");
        }

        if (second > 0) {
            if (second < 10) {
                sb.append("0");
            }
            sb.append(second);
            //  sb.append("秒");
        } else {
            sb.append("00");
        }

        return sb.toString();
    }

    /**
     * 整数(秒数)转换为时分秒格式(xx:xx:xx)
     *
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


    /**
     * A-Z
     * a-z
     * 0-9
     *
     * @return
     */
    public static String getARandomRange() {
        String allChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int allLen = allChar.length();
        for (int i = 0; i < 8; i++) {
            sb.append(allChar.charAt(random.nextInt(allLen)));
        }
        return sb.toString();
    }


    /**
     * 短视频上传文件名
     *
     * @param userId
     * @return
     */
    public static String formatSvideoTimeStamp(String userId) {

        int uid = Integer.valueOf(userId);
        uid = uid%10;

        String timeStamp = String.valueOf(System.currentTimeMillis());
        StringBuilder result = new StringBuilder("svideo/");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        int monthInt = calendar.get(Calendar.MONTH) + 1;

        String month = formatWithZero(monthInt);
        String day =formatWithZero(calendar.get(Calendar.DAY_OF_MONTH));
        String hour = formatWithZero(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = formatWithZero(calendar.get(Calendar.MINUTE));

        String random = getARandomRange();

        result.append(year).append("/").append(month).append(day)
                .append("/").append(hour).append(minute).append("/")
                .append(timeStamp).append("_").append(String.valueOf(uid)).append(random).append(".mp4");

//        return EncodeUtils.urlEncode(result.toString());
        return result.toString();

    }

    public static String formatWithZero(int time){
        return  time < 10 ? "0".concat(String.valueOf(time)):String.valueOf(time);
    }

    /**
     * format : 1990-01-22
     *
     * @param birthString
     * @return
     */
    public static String birthToAge(String birthString) {
        String[] splitsBirth = birthString.split("-");
        if (splitsBirth.length != 3) {
            return "0";
        }
        Calendar now = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        try {
            birth.set(Integer.parseInt(splitsBirth[0]), Integer.parseInt(splitsBirth[1]), Integer.parseInt(splitsBirth[2]));
        } catch (Exception e) {
            LogUtils.e(" birth to age parse int error:" + e.getMessage());
            return "0";
        }
        if (birth.after(now)) {
//            LogUtils.e("Can't be born in the future");
            return "0";
        }
        int yearNow = now.get(Calendar.YEAR);
        int monthNow = now.get(Calendar.MONTH) + 1;
        int dayNow = now.get(Calendar.DAY_OF_MONTH);

        int yearBirth = birth.get(Calendar.YEAR);
        int monthBirth = birth.get(Calendar.MONTH);
        int dayBirth = birth.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthBirth > monthNow) {
            age--;
        } else if (monthNow == monthBirth) {
            // in birth month
            if (dayBirth > dayNow) {
                age--;
            }
        }

        return String.valueOf(age);
    }

    /**
     * 根据当前秒数返回剩余时间格式： 99:05:20
     *
     * @param ms
     * @return
     */
    public static String formatTime_2(Long ms) {
        Integer ss = 1;
        Integer mi = ss * 60;
        Integer hh = mi * 60;

        Long hour = ms / hh;
        Long minute = (ms - hour * hh) / mi;
        Long second = (ms - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();

        if (hour < 10) {
            sb.append("0");
        }
        sb.append(hour + ":");

        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute + ":");


        if (second < 10) {
            sb.append("0");
        }
        sb.append(second);

        return sb.toString();
    }

}
