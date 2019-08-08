package jp.wasabeef.glide.transformations.path.parser;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 引用android-shape-imageview
 */
class CopyInputStream {
    private static final String TAG = SvgToPath.TAG;

    private final InputStream _is;
    private ByteArrayOutputStream _copy;

    CopyInputStream(InputStream is) {
        _is = is;

        try {
            copy();
        }
        catch(IOException ex) {
            Log.w(TAG, "IOException in CopyInputStream " + ex.toString());
        }
    }

    private void copy() throws IOException {
        _copy = new ByteArrayOutputStream();
        int chunk;
        byte[] data = new byte[256];

        while(-1 != (chunk = _is.read(data))) {
            _copy.write(data, 0, chunk);
        }
        _copy.flush();
    }

    public ByteArrayInputStream getCopy() {
        return new ByteArrayInputStream(_copy.toByteArray());
    }
}
