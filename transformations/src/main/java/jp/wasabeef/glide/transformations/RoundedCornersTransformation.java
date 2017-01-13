package jp.wasabeef.glide.transformations;

/**
 * Copyright (C) 2015 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * 圆角
 */
public class RoundedCornersTransformation implements Transformation<Bitmap> {

    private static int DEFAULT_WIDTH = 0;
    private static int DEFAULT_COLOR = Color.BLACK;
    private static float DEFAULT_ALPHA = 1f;

    private final static int ALPHA_MAX = 255;

    public enum CornerType {
        ALL,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT,
        OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT,
        DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT
    }

    private BitmapPool mBitmapPool;
    private int mRadius;
    private int mDiameter;
    private CornerType mCornerType;

    //边框宽度
    private float mBorderWidth;
    //边框颜色
    private int mBorderColor;
    //边框透明度
    private float mBorderAlpha;

    public RoundedCornersTransformation(Context context, int radius) {
        this(Glide.get(context).getBitmapPool(), radius, DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA,
                CornerType.ALL);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius) {
        this(pool, radius, DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA, CornerType.ALL);
    }

    public RoundedCornersTransformation(Context context, int radius, float borderWidth) {
        this(Glide.get(context).getBitmapPool(), radius, borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA,
                CornerType.ALL);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, float borderWidth) {
        this(pool, radius, borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA, CornerType.ALL);
    }

    public RoundedCornersTransformation(Context context, int radius, float borderWidth, int borderColor) {
        this(Glide.get(context).getBitmapPool(), radius, borderWidth, borderColor, DEFAULT_ALPHA,
                CornerType.ALL);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, float borderWidth, int borderColor) {
        this(pool, radius, borderWidth, borderColor, DEFAULT_ALPHA, CornerType.ALL);
    }

    public RoundedCornersTransformation(Context context, int radius, float borderWidth, int borderColor,
                                        float borderAlpha) {
        this(Glide.get(context).getBitmapPool(), radius, borderWidth, borderColor, borderAlpha,
                CornerType.ALL);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, float borderWidth, int borderColor,
                                        float borderAlpha) {
        this(pool, radius, borderWidth, borderColor, borderAlpha, CornerType.ALL);
    }

    public RoundedCornersTransformation(Context context, int radius, CornerType cornerType) {
        this(Glide.get(context).getBitmapPool(), radius, DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA,
                cornerType);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, CornerType cornerType) {
        this(pool, radius, DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA, cornerType);
    }

    public RoundedCornersTransformation(Context context, int radius, float borderWidth,
                                        CornerType cornerType) {
        this(Glide.get(context).getBitmapPool(), radius, borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA,
                cornerType);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, float borderWidth,
                                        CornerType cornerType) {
        this(pool, radius, borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA, cornerType);
    }

    public RoundedCornersTransformation(Context context, int radius, float borderWidth, int borderColor,
                                        CornerType cornerType) {
        this(Glide.get(context).getBitmapPool(), radius, borderWidth, borderColor, DEFAULT_ALPHA,
                cornerType);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, float borderWidth, int borderColor,
                                        CornerType cornerType) {
        this(pool, radius, borderWidth, borderColor, DEFAULT_ALPHA, cornerType);
    }

    public RoundedCornersTransformation(Context context, int radius, float borderWidth, int borderColor,
                                        float borderAlpha, CornerType cornerType) {
        this(Glide.get(context).getBitmapPool(), radius, borderWidth, borderColor, borderAlpha, cornerType);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, float borderWidth, int borderColor,
                                        float borderAlpha, CornerType cornerType) {
        mBitmapPool = pool;
        mRadius = radius;
        mDiameter = mRadius * 2;
        mBorderWidth = borderWidth;
        mBorderColor = borderColor;
        mBorderAlpha = borderAlpha;
        mCornerType = cornerType;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = mBitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(mBorderColor);
        borderPaint.setAlpha(Float.valueOf(mBorderAlpha * ALPHA_MAX).intValue());

        drawRoundRect(canvas, paint, borderPaint, width, height);
        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    private void drawRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float width, float height) {
        float right = width - mBorderWidth;
        float bottom = height - mBorderWidth;

        switch (mCornerType) {
            case ALL:
                drawAllRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case TOP_LEFT:
                drawTopLeftRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case TOP_RIGHT:
                drawTopRightRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case BOTTOM_LEFT:
                drawBottomLeftRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case BOTTOM_RIGHT:
                drawBottomRightRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case TOP:
                drawTopRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case BOTTOM:
                drawBottomRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case LEFT:
                drawLeftRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case RIGHT:
                drawRightRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case OTHER_TOP_LEFT:
                drawOtherTopLeftRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case OTHER_TOP_RIGHT:
                drawOtherTopRightRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case OTHER_BOTTOM_LEFT:
                drawOtherBottomLeftRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case OTHER_BOTTOM_RIGHT:
                drawOtherBottomRightRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case DIAGONAL_FROM_TOP_LEFT:
                drawDiagonalFromTopLeftRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            case DIAGONAL_FROM_TOP_RIGHT:
                drawDiagonalFromTopRightRoundRect(canvas, paint, borderPaint, right, bottom, width, height);
                break;
            default:
                canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, right, bottom), mRadius,
                        mRadius, paint);
                break;
        }
    }

    private void drawAllRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom, float width,
                                  float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, right, bottom), mRadius,
                    mRadius, paint);
        }
    }

    //左上角圆角
    private void drawTopLeftRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                      float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, mRadius + mBorderWidth, width, height), borderPaint);
            canvas.drawRect(new RectF(mRadius + mBorderWidth, 0, width, height), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, mBorderWidth + mDiameter,
                    mBorderWidth + mDiameter), mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth + mRadius, mBorderWidth + mRadius,
                    bottom), paint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, mBorderWidth, right, bottom), paint);
        }
    }

    //右上角圆角
    private void drawTopRightRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                       float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, 0, right - mRadius, height), borderPaint);
            canvas.drawRect(new RectF(0, mBorderWidth + mRadius, width, height), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(right - mDiameter, mBorderWidth, right,
                    mBorderWidth + mDiameter), mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth, right - mRadius, bottom), paint);
            canvas.drawRect(new RectF(right - mRadius, mBorderWidth + mRadius, right, bottom), paint);
        }
    }

    //左下角
    private void drawBottomLeftRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                         float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, 0, width, bottom - mRadius), borderPaint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, 0, width, height), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, bottom - mDiameter, mBorderWidth + mDiameter,
                    bottom), mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth, mBorderWidth + mDiameter,
                    bottom - mRadius), paint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, mBorderWidth, right, bottom), paint);
        }
    }

    //右下角
    private void drawBottomRightRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                          float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, 0, right - mRadius, height), borderPaint);
            canvas.drawRect(new RectF(right - mRadius, 0, width, bottom - mRadius), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth, right - mRadius, bottom), paint);
            canvas.drawRect(new RectF(right - mRadius, mBorderWidth, right, bottom - mRadius), paint);
        }
    }

    //上部
    private void drawTopRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom, float width,
                                  float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, mBorderWidth + mRadius, width, height), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, right, mBorderWidth + mDiameter),
                    mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth + mRadius, right, bottom), paint);
        }
    }

    //底部
    private void drawBottomRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                     float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, 0, width, bottom - mRadius), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, bottom - mDiameter, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth, right, bottom - mRadius), paint);
        }
    }

    //左部
    private void drawLeftRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                   float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, 0, width, height), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, mBorderWidth + mDiameter, bottom),
                    mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, mBorderWidth, right, bottom), paint);
        }
    }

    //右部
    private void drawRightRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                    float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, 0, right - mRadius, height), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(right - mDiameter, mBorderWidth, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth, right - mRadius, bottom), paint);
        }
    }

    //除左上角
    private void drawOtherTopLeftRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                           float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, 0, mRadius + mBorderWidth, mRadius + mBorderWidth), borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, bottom - mDiameter, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRoundRect(new RectF(right - mDiameter, mBorderWidth, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth, right - mRadius, bottom - mRadius),
                    paint);
        }
    }

    //除右上角
    private void drawOtherTopRightRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                            float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(right - mBorderWidth, 0, width, mBorderWidth + mRadius),
                    borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, mBorderWidth + mDiameter,
                    bottom), mRadius, mRadius, paint);
            canvas.drawRoundRect(new RectF(mBorderWidth, bottom - mDiameter, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, mBorderWidth, right, bottom - mRadius),
                    paint);
        }
    }

    //除左下角
    private void drawOtherBottomLeftRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right, float bottom,
                                              float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, bottom - mBorderWidth, mBorderWidth + mRadius, height),
                    borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, right, mBorderWidth + mDiameter),
                    mRadius, mRadius, paint);
            canvas.drawRoundRect(new RectF(right - mDiameter, mBorderWidth, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth + mRadius, right - mRadius, bottom),
                    paint);
        }
    }

    //除右下角
    private void drawOtherBottomRightRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right,
                                               float bottom, float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(right - mBorderWidth, bottom - mBorderWidth, width, height),
                    borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, right, mBorderWidth + mDiameter),
                    mRadius, mRadius, paint);
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, mBorderWidth + mDiameter, bottom),
                    mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, mBorderWidth + mRadius, right, bottom),
                    paint);
        }
    }

    //左上右下
    private void drawDiagonalFromTopLeftRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right,
                                                  float bottom, float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, bottom - mBorderWidth, mBorderWidth + mRadius, height),
                    borderPaint);
            canvas.drawRect(new RectF(right - mBorderWidth, 0, width, mBorderWidth + mRadius),
                    borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, mBorderWidth + mDiameter, mBorderWidth + mDiameter),
                    mRadius, mRadius, paint);
            canvas.drawRoundRect(new RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius,
                    mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth + mRadius, right - mBorderWidth, bottom), paint);
            canvas.drawRect(new RectF(mBorderWidth + mBorderWidth, mBorderWidth, right, bottom - mRadius), paint);
        }
    }

    //左下右上
    private void drawDiagonalFromTopRightRoundRect(Canvas canvas, Paint paint, Paint borderPaint, float right,
                                                   float bottom, float width, float height) {
        if (mBorderWidth > 0) {
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius + mBorderWidth,
                    mRadius + mBorderWidth, borderPaint);
            canvas.drawRect(new RectF(0, 0, mBorderWidth + mRadius, mBorderWidth + mRadius),
                    borderPaint);
            canvas.drawRect(new RectF(right - mBorderWidth, bottom - mRadius, width, height),
                    borderPaint);
        }
        if (right > mBorderWidth && bottom > mBorderWidth) {
            canvas.drawRoundRect(new RectF(right - mDiameter, mBorderWidth, right, mBorderWidth + mDiameter), mRadius,
                    mRadius, paint);
            canvas.drawRoundRect(new RectF(mBorderWidth, bottom - mDiameter, mBorderWidth + mDiameter, bottom),
                    mRadius, mRadius, paint);
            canvas.drawRect(new RectF(mBorderWidth, mBorderWidth, right - mRadius, bottom - mRadius), paint);
            canvas.drawRect(new RectF(mBorderWidth + mRadius, mBorderWidth + mRadius, right, bottom), paint);
        }
    }

    @Override
    public String getId() {
        return "RoundedTransformation(radius=" + mRadius + ", diameter=" + mDiameter +
                ",borderWidth=" + mBorderWidth + ", borderColor=" + mBorderColor +
                ", borderAlpha=" + mBorderAlpha + ", cornerType=" + mCornerType.name() + ")";
    }
}
