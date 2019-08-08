package jp.wasabeef.glide.transformations;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/**
 * 创建者：pyt
 * 创建时间：2019-08-07 15:20
 * 描述：裁剪带边框的圆形
 */
public class BorderCircleCrop extends BaseTransformation {

    private static final String ID = "BorderCircleCrop.";

    /**
     * 总边框宽度
     */
    private float mBorderWidth;
    /**
     * 第一边框
     */
    private Paint mBorder1Paint;
    private float mBorder1Width;
    private float mBorder1Color;
    /**
     * 第二边框
     */
    private Paint mBorder2Paint;
    private float mBorder2Width;
    private float mBorder2Color;


    /**
     * 构造方法 无边框
     */
    public BorderCircleCrop() {
        this(0, 0, 0, 0);
    }

    /**
     * 构造方法 只有一个边框
     *
     * @param borderWidth 边框宽度 dp
     * @param borderColor 边框颜色
     */
    public BorderCircleCrop(int borderWidth, int borderColor) {
        this(borderWidth, borderColor, 0, 0);
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
        //当只有一个边框时，保证是第一边框
        if (border1Width > 0) {
            initBorder1(border1Width, border1Color);

            if (border2Width > 0) {
                initBorder2(border2Width, border2Color);
            }

        } else if (border2Width > 0) {
            //当第一边框为0，第二边框不为0时，设置第二边框为第一边框
            initBorder1(border2Width, border2Color);
        }
        mBorderWidth = mBorder1Width + mBorder2Width;
    }

    /**
     * 初始化边框1
     */
    private void initBorder1(int borderWidth, int borderColor) {
        mBorder1Width = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorder1Color = borderColor;
        mBorder1Paint = new Paint();
        setPaintParams(mBorder1Paint, borderColor, mBorder1Width);
    }

    /**
     * 初始化边框2
     */
    private void initBorder2(int borderWidth, int borderColor) {
        mBorder2Width = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorder2Color = borderColor;
        mBorder2Paint = new Paint();
        setPaintParams(mBorder2Paint, borderColor, mBorder2Width);
    }

    /**
     * 设置画笔参数
     */
    private void setPaintParams(Paint paint, int borderColor, float borderWidth) {
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int min = Math.min(outWidth, outHeight);

        int destMinEdge = (int) (min - mBorderWidth * 2);
        float radius = min / 2f;

        int srcWidth = toTransform.getWidth();
        int srcHeight = toTransform.getHeight();

        float scaleX = destMinEdge / (float) srcWidth;
        float scaleY = destMinEdge / (float) srcHeight;
        float maxScale = Math.max(scaleX, scaleY);

        float scaledWidth = maxScale * srcWidth;
        float scaledHeight = maxScale * srcHeight;
        float left = (destMinEdge - scaledWidth) / 2f + mBorderWidth;
        float top = (destMinEdge - scaledHeight) / 2f + mBorderWidth;

        RectF destRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Alpha is required for this transformation.
        Bitmap bitmap = getAlphaSafeBitmap(pool, toTransform);

        Bitmap.Config outConfig = getAlphaSafeConfig(toTransform);
        Bitmap result = pool.get(min, min, outConfig);

        result.setHasAlpha(true);

        Canvas canvas = new Canvas(result);
        // 画图片的圆
        canvas.drawCircle(radius, radius, radius - mBorderWidth, CIRCLE_CROP_SHAPE_PAINT);
        // 在圆里画图片
        canvas.drawBitmap(bitmap, null, destRect, CIRCLE_CROP_BITMAP_PAINT);

        //画边框
        if (mBorder1Width > 0) {
            canvas.drawCircle(radius, radius, radius - mBorder1Width / 2, mBorder1Paint);

            if (mBorder2Width > 0) {
                canvas.drawCircle(radius, radius, radius - mBorder1Width - mBorder2Width / 2, mBorder2Paint);
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
        return ID + mBorder1Color + mBorder1Width + mBorder2Color + mBorder2Width;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BorderCircleCrop) {
            BorderCircleCrop other = (BorderCircleCrop) o;
            return mBorder1Color == other.mBorder1Color &&
                    mBorder1Width == other.mBorder1Width &&
                    mBorder2Color == other.mBorder2Color &&
                    mBorder2Width == other.mBorder2Width;
        }
        return false;
    }

}
