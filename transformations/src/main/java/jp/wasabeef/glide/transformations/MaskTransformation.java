package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import jp.wasabeef.glide.transformations.internal.Utils;

public class MaskTransformation extends BaseTransformation {

    private static final String ID = "MaskTransformation.";

    private static Paint paint = new Paint();
    private int maskId;

    static {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    public MaskTransformation(int maskId) {
        this.maskId = maskId;
    }

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        Bitmap bitmap = getAlphaSafeBitmap(pool, toTransform);

        Bitmap result = pool.get(width, height, getAlphaSafeConfig(toTransform));
        result.setHasAlpha(true);

        Drawable mask = Utils.getMaskDrawable(context.getApplicationContext(), maskId);

        Canvas canvas = new Canvas(result);
        mask.setBounds(0, 0, width, height);
        mask.draw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        clear(canvas);

        return result;
    }

    @Override
    protected String getID() {
        return ID + maskId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MaskTransformation &&
                ((MaskTransformation) o).maskId == maskId;
    }

}
