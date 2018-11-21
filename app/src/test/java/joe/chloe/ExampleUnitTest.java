package joe.chloe;

import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void accula(){

        float a = accuracy(5999,1000,3,RoundingMode.DOWN);
        System.out.println(a);
    }

    public static float accuracy(double int1, double int2, int decimalDigits,RoundingMode mode) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(decimalDigits);
        //模式 例如四舍五入
//        df.setRoundingMode(mode);
        return Float.parseFloat(df.format(int1 / int2));
    }

}