package joe.chloe.composerfilters;

import android.opengl.GLES20;

import com.daasuu.mp4compose.Resolution;
import com.daasuu.mp4compose.filter.GlFilter;
import com.daasuu.mp4compose.filter.IResolutionFilter;

public class GlSketchFilter extends GlFilter implements IResolutionFilter {

    private int mSingleStepOffsetLocation;
    //0.0 - 1.0
    private int mStrength;

    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 vTextureCoord;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    vTextureCoord = inputTextureCoordinate.xy;\n" +
            "}";
    
    private static final String FRAGMENT_SHADER = "varying highp vec2 vTextureCoord;\n" +
            "precision mediump float;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform vec2 singleStepOffset; \n" +
            "uniform float strength;\n" +
            "const highp vec3 W = vec3(0.299,0.587,0.114);\n" +
            "void main()\n" +
            "{ \n" +
            "float threshold = 0.0;\n" +

            "vec4 oralColor = texture2D(inputImageTexture, vTextureCoord);\n" +

            "vec3 maxValue = vec3(0.,0.,0.);\n" +
            "for(int i = -2; i<=2; i++)\n" +
            "{\n" +
            "for(int j = -2; j<=2; j++)\n" +
            "{\n" +
            "vec4 tempColor = texture2D(inputImageTexture, vTextureCoord+singleStepOffset*vec2(i,j));\n" +
            "maxValue.r = max(maxValue.r,tempColor.r);\n" +
            "maxValue.g = max(maxValue.g,tempColor.g);\n" +
            "maxValue.b = max(maxValue.b,tempColor.b);\n" +
            "threshold += dot(tempColor.rgb, W);\n" +
            "}\n" +
            "}\n" +

            "float gray1 = dot(oralColor.rgb, W);\n" +

            "float gray2 = dot(maxValue, W);\n" +

            "float contour = gray1 / gray2;\n" +
            "threshold = threshold / 25.;\n" +
            "float alpha = max(1.0,gray1>threshold?1.0:(gray1/threshold));\n" +
            "float result = contour * alpha + (1.0-alpha)*gray1;\n" +
            "gl_FragColor = vec4(vec3(result,result,result), oralColor.w);\n" +
            "}";

    public GlSketchFilter() {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
    }


    @Override
    public void setUpSurface() {
        super.setUpSurface();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrength = GLES20.glGetUniformLocation(getProgram(), "strength");
        setFloat(mStrength, 0.5f);
    }
    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
    }
    @Override
    public void setResolution(Resolution resolution) {
        setTexelSize(resolution.width(), resolution.height());
    }
}
