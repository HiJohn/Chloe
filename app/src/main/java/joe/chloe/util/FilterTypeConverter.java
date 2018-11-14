package joe.chloe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.blankj.utilcode.util.LogUtils;
import com.daasuu.epf.filter.GlFilterGroup;
import com.daasuu.mp4compose.filter.GlSphereRefractionFilter;
import com.daasuu.mp4compose.filter.GlBilateralFilter;
import com.daasuu.mp4compose.filter.GlBoxBlurFilter;
import com.daasuu.mp4compose.filter.GlBulgeDistortionFilter;
import com.daasuu.mp4compose.filter.GlCGAColorspaceFilter;
import com.daasuu.mp4compose.filter.GlFilter;
import com.daasuu.mp4compose.filter.GlGaussianBlurFilter;
import com.daasuu.mp4compose.filter.GlGrayScaleFilter;
import com.daasuu.mp4compose.filter.GlHazeFilter;
import com.daasuu.mp4compose.filter.GlInvertFilter;
import com.daasuu.mp4compose.filter.GlLutFilter;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlSepiaFilter;
import com.daasuu.mp4compose.filter.GlSharpenFilter;
import com.daasuu.mp4compose.filter.GlToneCurveFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;

import java.io.IOException;
import java.io.InputStream;

import joe.chloe.R;
import joe.chloe.exoplayerfilter.FilterType;

public class FilterTypeConverter {


    public static GlFilter convertToComposerFilter(Context context, FilterType filterType){
        switch (filterType) {
            case DEFAULT:
                return new GlFilter();
            case SEPIA:
                return new GlSepiaFilter();
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case INVERT:
                return new GlInvertFilter();
            case HAZE:
                return new GlHazeFilter();
            case MONOCHROME:
                return new GlMonochromeFilter();
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case LOOK_UP_TABLE_SAMPLE:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.foggy_night);

                return new GlLutFilter(bitmap);
            case TONE_CURVE_SAMPLE:
                try {
//                String filePath = "acv/filter_co_blue.acv";
//                String filePath = "acv/filter_bloom_summer.acv";
//                String filePath = "acv/filter_sun_light.acv";
//                String filePath = "acv/filter_pure_white.acv";
//                    String filePath = "acv/filter_pure.acv";
                String filePath = "acv/filter_fresh.acv";
//                String filePath = "acv/filter_late_autumn.acv";
//                String filePath = "acv/filter_pre_autumn.acv";
//                String filePath = "acv/filter_violet.acv";


                    InputStream is = context.getAssets().open(filePath);
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    LogUtils.e("FilterType", "Error");
                }
                return new GlFilter();

            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case VIGNETTE:
                return new GlVignetteFilter();
//            case FILTER_GROUP_SAMPLE:
//                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAUSSIAN_FILTER:
                return new GlGaussianBlurFilter();
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLORSPACE:
                return new GlCGAColorspaceFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(4f);
                return glSharpenFilter;
//            case BITMAP_OVERLAY_SAMPLE:
//                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
            default:
                return new GlFilter();
        }
    }


}
