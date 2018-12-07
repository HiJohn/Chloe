package joe.chloe.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utils {

    /**
     * 数字除法保留小数位
     *
     * @param int1          除数
     * @param int2          被除数
     * @param decimalDigits 保留小数位
     * @return
     */
    public static float accuracy(double int1, double int2, int decimalDigits) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(decimalDigits);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        return Float.parseFloat(df.format(int1 / int2));
    }

    public static float accuracy(double int1, double int2, int decimalDigits,RoundingMode mode) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(decimalDigits);
        //模式 例如四舍五入
        df.setRoundingMode(mode);
        return Float.parseFloat(df.format(int1 / int2));
    }

}
