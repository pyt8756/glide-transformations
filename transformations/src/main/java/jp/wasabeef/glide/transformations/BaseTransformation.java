package jp.wasabeef.glide.transformations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * 创建者：pyt
 * 创建时间：2019-08-07 15:42
 * 描述：转换父类
 */
public abstract class BaseTransformation extends BitmapTransformation {

    private static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;
    protected static final Paint DEFAULT_PAINT = new Paint(PAINT_FLAGS);
    private static final int CIRCLE_CROP_PAINT_FLAGS = PAINT_FLAGS | Paint.ANTI_ALIAS_FLAG;
    static final Paint CIRCLE_CROP_SHAPE_PAINT = new Paint(CIRCLE_CROP_PAINT_FLAGS);
    static final Paint CIRCLE_CROP_BITMAP_PAINT;

    private byte[] ID_BYTES;

    static {
        CIRCLE_CROP_BITMAP_PAINT = new Paint(CIRCLE_CROP_PAINT_FLAGS);
        CIRCLE_CROP_BITMAP_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    Bitmap getAlphaSafeBitmap(@NonNull BitmapPool pool, @NonNull Bitmap maybeAlphaSafe) {
        Bitmap.Config safeConfig = getAlphaSafeConfig(maybeAlphaSafe);
        if (safeConfig.equals(maybeAlphaSafe.getConfig())) {
            return maybeAlphaSafe;
        }

        Bitmap argbBitmap = pool.get(maybeAlphaSafe.getWidth(), maybeAlphaSafe.getHeight(), safeConfig);
        new Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0, 0, null);
        return argbBitmap;
    }

    Bitmap.Config getAlphaSafeConfig(@NonNull Bitmap inBitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Bitmap.Config.RGBA_F16.equals(inBitmap.getConfig())) {
                return Bitmap.Config.RGBA_F16;
            }
        }
        return Bitmap.Config.ARGB_8888;
    }

    void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }

    @Override
    public int hashCode() {
        return getID().hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        if (ID_BYTES == null)
            ID_BYTES = getID().getBytes(CHARSET);
        messageDigest.update(ID_BYTES);
    }

    protected abstract String getID();

}
