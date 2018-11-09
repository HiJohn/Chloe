package joe.chloe.composerfilters;

import com.daasuu.mp4compose.filter.GlFilter;

public class GlSketchFilter extends GlFilter {
    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +

                    "varying highp vec2 vTextureCoord;" +
                    "precision highp float;" +
                    "uniform sampler2D sTexture;" +
                    "uniform sampler2D curve;" +
                    "void main()" +
                    "{ " +
                    "lowp vec4 textureColor;\n" +
                    "lowp vec4 textureColorOri;\n" +
                    "float xCoordinate = vTextureCoord.x;\n" +
                    "float yCoordinate = vTextureCoord.y;\n" +
                    "highp float redCurveValue;\n" +
                    "highp float greenCurveValue;\n" +
                    "highp float blueCurveValue;\n" +
                    "textureColor = texture2D( sTexture, vec2(xCoordinate, yCoordinate));\n" +
                    "textureColorOri = textureColor;\n" +
                    "redCurveValue = texture2D(curve, vec2(textureColor.r, 0.0)).r;\n" +
                    "greenCurveValue = texture2D(curve, vec2(textureColor.g, 0.0)).g;\n" +
                    "blueCurveValue = texture2D(curve, vec2(textureColor.b, 0.0)).b;\n" +
                    "redCurveValue = texture2D(curve, vec2(redCurveValue, 0.0)).a;\n" +
                    "greenCurveValue = texture2D(curve, vec2(greenCurveValue, 0.0)).a;\n" +
                    "blueCurveValue = texture2D(curve, vec2(blueCurveValue, 0.0)).a;\n" +
                    "redCurveValue = redCurveValue * 1.25 - 0.12549;\n" +
                    "greenCurveValue = greenCurveValue * 1.25 - 0.12549; \n" +
                    "blueCurveValue = blueCurveValue * 1.25 - 0.12549;\n" +
                    "textureColor = vec4(redCurveValue, greenCurveValue, blueCurveValue, 1.0);\n" +
                    "textureColor = (textureColorOri - textureColor) * 0.549 + textureColor;\n" +
                    "gl_FragColor = vec4(textureColor.r, textureColor.g, textureColor.b, 1.0);\n" +
                    "}";


    public GlSketchFilter(){
        super(DEFAULT_VERTEX_SHADER,FRAGMENT_SHADER);
    }

}
