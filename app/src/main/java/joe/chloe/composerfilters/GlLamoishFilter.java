package joe.chloe.composerfilters;

import com.daasuu.mp4compose.filter.GlFilter;

public class GlLamoishFilter extends GlFilter {

    private final static String mVertexShader = "uniform mat4 uMVPMatrix;\n"
            + "uniform mat4 uSTMatrix;\n"
            + "attribute vec4 aPosition;\n"
            + "attribute vec4 aTextureCoord;\n"
            + "varying vec2 vTextureCoord;\n"
            + "void main() {\n"
            + "  gl_Position = uMVPMatrix * aPosition;\n"
            + "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n"
            + "}\n";
    public GlLamoishFilter(){
        super(mVertexShader,DEFAULT_FRAGMENT_SHADER);
    }

}
