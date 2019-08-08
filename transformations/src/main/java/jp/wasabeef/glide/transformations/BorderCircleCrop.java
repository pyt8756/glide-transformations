package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/**
 * 创建者：pyt
 * 创建时间：2019-08-07 15:20
 * 描述：裁剪带边框的圆形
 */
public class BorderCircleCrop extends BaseBorderTransformation {

    private static final String ID = "BorderCircleCrop.";

    private static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;
    private static final int CIRCLE_CROP_PAINT_FLAGS = PAINT_FLAGS | Paint.ANTI_ALIAS_FLAG;
    private static final Paint CIRCLE_CROP_SHAPE_PAINT = new Paint(CIRCLE_CROP_PAINT_FLAGS);
    private static final Paint CIRCLE_CROP_BITMAP_PAINT;

    static {
        CIRCLE_CROP_BITMAP_PAINT = new Paint(CIRCLE_CROP_PAINT_FLAGS);
        CIRCLE_CROP_BITMAP_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    /**
     * 构造方法 无边框
     */
    public BorderCircleCrop() {
        super(0, 0, 0, 0);
    }

    /**
     * 构造方法 只有一个边框
     *
     * @param borderWidth 边框宽度 dp
     * @param borderColor 边框颜色
     */
    public BorderCircleCrop(int borderWidth, int borderColor) {
        super(borderWidth, borderColor, 0, 0);
    }

    /**
     * 构造方法 两个边框
     *
     * @param border1Width 边框1宽度 dp
     * @param border1Color 边框1颜色
     * @param border2Width 边框2宽度 dp
     * @param border2Color 边框2颜色
     */
    public BorderCircleCrop(int border1Width, int border1Color, int border2Width, int border2Color) {
        super(border1Width, border1Color, border2Width, border2Color);
    }

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int min = Math.min(outWidth, outHeight);

        int destMinEdge = (min - mAllBorder * 2);
        float radius = min / 2f;

        int srcWidth = toTransform.getWidth();
        int srcHeight = toTransform.getHeight();

        float scaleX = destMinEdge / (float) srcWidth;
        float scaleY = destMinEdge / (float) srcHeight;
        float maxScale = Math.max(scaleX, scaleY);

        float scaledWidth = maxScale * srcWidth;
        float scaledHeight = maxScale * srcHeight;
        float left = (destMinEdge - scaledWidth) / 2f + mAllBorder;
        float top = (destMinEdge - scaledHeight) / 2f + mAllBorder;

        RectF destRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // 此转换需要Alpha
        Bitmap bitmap = getAlphaSafeBitmap(pool, toTransform);

        Bitmap result = pool.get(min, min, getAlphaSafeConfig(toTransform));
        result.setHasAlpha(true);

        Canvas canvas = new Canvas(result);
        // 画图片的圆
        canvas.drawCircle(radius, radius, radius - mAllBorder, CIRCLE_CROP_SHAPE_PAINT);
        // 在圆里画图片
        canvas.drawBitmap(bitmap, null, destRect, CIRCLE_CROP_BITMAP_PAINT);

        //画边框
        if (mBorder1Breadth > 0) {
            canvas.drawCircle(radius, radius, radius - mBorder1Breadth / 2f, mBorder1Paint);

            if (mBorder2Breadth > 0) {
                canvas.drawCircle(radius, radius, radius - mBorder1Breadth - mBorder2Breadth / 2f, mBorder2Paint);
            }
        }

        clear(canvas);

        if (!bitmap.equals(toTransform)) {
            pool.put(bitmap);
        }

        return result;
    }

    @Override
    protected String getID() {
        return ID + mBorder1Color + mBorder1Breadth + mBorder2Color + mBorder2Breadth;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BorderCircleCrop) {
            BorderCircleCrop other = (BorderCircleCrop) o;
            return mBorder1Color == other.mBorder1Color &&
                    mBorder1Breadth == other.mBorder1Breadth &&
                    mBorder2Color == other.mBorder2Color &&
                    mBorder2Breadth == other.mBorder2Breadth;
        }
        return false;
    }

}
