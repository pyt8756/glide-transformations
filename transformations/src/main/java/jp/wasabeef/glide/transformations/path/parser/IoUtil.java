package jp.wasabeef.glide.transformations.path.parser;

import java.io.InputStream;

/**
 * 引用android-shape-imageview
 */
@SuppressWarnings("FinalStaticMethod")
public class IoUtil {
    public static final void closeQuitely(InputStream is) {
        if(is != null) {
            try {
                is.close();
            } catch (Throwable ignored) {
                //ignored
            }
        }
    }
}
