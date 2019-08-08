package jp.wasabeef.glide.transformations;

import android.content.res.Resources;
import android.graphics.Paint;

/**
 * 创建者：pyt
 * 创建时间：2019-08-08 11:27
 * 描述：边框转换父类
 */
abstract class BaseBorderTransformation extends BaseTransformation {

    /**
     * 总边框宽度
     */
    protected int mAllBorder;
    /**
     * 第一边框
     */
    protected Paint mBorder1Paint;
    protected int mBorder1Breadth;
    protected int mBorder1Color;
    /**
     * 第二边框
     */
    protected Paint mBorder2Paint;
    protected int mBorder2Breadth;
    protected int mBorder2Color;


    public BaseBorderTransformation(int border1Width, int border1Color, int border2Width, int border2Color) {
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
        mAllBorder = mBorder1Breadth + mBorder2Breadth;
    }

    /**
     * 初始化边框1
     */
    private void initBorder1(int borderWidth, int borderColor) {
        mBorder1Breadth = (int) (Resources.getSystem().getDisplayMetrics().density * borderWidth);
        mBorder1Color = borderColor;
        mBorder1Paint = new Paint();
        setPaintParams(mBorder1Paint, borderColor, mBorder1Breadth);
    }

    /**
     * 初始化边框2
     */
    private void initBorder2(int borderWidth, int borderColor) {
        mBorder2Breadth = (int) (Resources.getSystem().getDisplayMetrics().density * borderWidth);
        mBorder2Color = borderColor;
        mBorder2Paint = new Paint();
        setPaintParams(mBorder2Paint, borderColor, mBorder2Breadth);
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

}
