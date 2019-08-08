package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import jp.wasabeef.glide.transformations.internal.Utils;

public class BorderMask extends BaseBorderTransformation {

    private static final String ID = "MaskTransformation.";

    private static Paint mSrcInPaint = new Paint();
    private static Paint mDstOutPaint = new Paint();
    private int maskId;

    static {
        mSrcInPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mDstOutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    /**
     * 构造方法 无边框
     */
    public BorderMask(int maskId) {
        this(maskId, 0, 0, 0, 0);
    }

    /**
     * 构造方法 只有一个边框
     *
     * @param borderWidth 边框宽度 dp
     * @param borderColor 边框颜色
     */
    public BorderMask(int maskId, int borderWidth, int borderColor) {
        this(maskId, borderWidth, borderColor, 0, 0);
    }

    /**
     * 构造方法 两个边框
     *
     * @param border1Width 边框1宽度 dp
     * @param border1Color 边框1颜色
     * @param border2Width 边框2宽度 dp
     * @param border2Color 边框2颜色
     */
    public BorderMask(int maskId, int border1Width, int border1Color, int border2Width, int border2Color) {
        super(border1Width, border1Color, border2Width, border2Color);
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
        mask.setBounds(0, 0, width, height);

        Canvas resultCanvas = new Canvas(result);

        if (mBorder1Breadth > 0) {
            //有边框1
            Paint resultPaint = new Paint();

            int workColor = Color.BLACK;

            //创建边框1相关对象
            Bitmap border1Bitmap = pool.get(width, height, getAlphaSafeConfig(toTransform));
            border1Bitmap.setHasAlpha(true);
            Canvas border1Canvas = new Canvas(border1Bitmap);
            //绘制边框形状
            mask.draw(border1Canvas);

            //根据形状填充颜色，此时形状颜色为实心
            border1Canvas.drawColor(workColor, PorterDuff.Mode.SRC_IN);

            //创建边框2相关对象
            Bitmap border2Bitmap = null;
            Canvas border2Canvas = null;

            //创建图片相关对象
            Bitmap imgBitmap = scaleBitmap(border1Bitmap, width - mAllBorder * 2, height - mAllBorder * 2);
            imgBitmap.setHasAlpha(true);
            Canvas imgCanvas = new Canvas(imgBitmap);
            //绘制图片内容
            imgCanvas.drawBitmap(bitmap, 0, 0, mSrcInPaint);

            if (mBorder2Breadth > 0) {
                border2Bitmap = scaleBitmap(border1Bitmap, width - mBorder1Breadth * 2, height - mBorder1Breadth * 2);
                border2Bitmap.setHasAlpha(true);
                border2Canvas = new Canvas(border2Bitmap);

                //根据形状填充颜色，此时形状颜色为实心
                border2Canvas.drawColor(workColor, PorterDuff.Mode.SRC_IN);

                //把边框1绘制为空心
                border1Canvas.drawBitmap(border2Bitmap, mBorder1Breadth, mBorder1Breadth, mDstOutPaint);
                //把边框2绘制为空心
                border2Canvas.drawBitmap(imgBitmap, mBorder2Breadth, mBorder2Breadth, mDstOutPaint);
            } else {
                //把边框1绘制为空心
                border1Canvas.drawBitmap(imgBitmap, mBorder1Breadth, mBorder1Breadth, mDstOutPaint);
            }

            drawColor(border1Canvas, mBorder1Color);

            if (border2Canvas != null) {
                drawColor(border2Canvas, mBorder2Color);
            }

            //在结果上绘制边框1
            resultCanvas.drawBitmap(border1Bitmap, 0, 0, resultPaint);
            //在结果上绘制边框2
            if (border2Bitmap != null) {
                resultCanvas.drawBitmap(border2Bitmap, mBorder1Breadth, mBorder1Breadth, resultPaint);
            }
            //在结果上绘制图片
            resultCanvas.drawBitmap(imgBitmap, mAllBorder, mAllBorder, resultPaint);

        } else {
            //无边框
            mask.draw(resultCanvas);
            resultCanvas.drawBitmap(bitmap, 0, 0, mSrcInPaint);
        }

        clear(resultCanvas);

        return result;
    }

    private void drawColor(Canvas canvas, int color) {
        if (color == Color.TRANSPARENT) {
            canvas.drawColor(color, PorterDuff.Mode.CLEAR);
        } else {
            canvas.drawColor(color, PorterDuff.Mode.SRC_IN);
        }
    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        float widhtRatio = newWidth * 1f / width;
        float heightRatio = newHeight * 1f / height;

        matrix.preScale(widhtRatio, heightRatio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        return newBM;
    }

    @Override
    protected String getID() {
        return ID + maskId + mBorder1Breadth + mBorder1Color + mBorder2Breadth + mBorder2Color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorderMask that = (BorderMask) o;
        return maskId == that.maskId &&
                mBorder1Breadth == that.mBorder1Breadth &&
                mBorder1Color == that.mBorder1Color &&
                mBorder2Breadth == that.mBorder2Breadth &&
                mBorder2Color == that.mBorder2Color;
    }

}
