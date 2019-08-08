package jp.wasabeef.glide.transformations;

/**
 * Copyright (C) 2018 Wasabeef
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
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;

public class BorderRoundedCorners extends BaseBorderTransformation {

    private static final String ID = "BorderRoundedCorners.";

    public enum CornerType {
        ALL,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT,
        OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT,
        DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT
    }

    private int radius;
    private int diameter;
    private CornerType cornerType;

    public BorderRoundedCorners(int radius) {
        this(radius, CornerType.ALL, 0, 0, 0, 0);
    }

    public BorderRoundedCorners(int radius, CornerType cornerType) {
        this(radius, cornerType, 0, 0, 0, 0);
    }

    public BorderRoundedCorners(int radius, int borderWidth, int borderColor) {
        this(radius, CornerType.ALL, borderWidth, borderColor, 0, 0);
    }

    public BorderRoundedCorners(int radius, CornerType cornerType, int borderWidth, int borderColor) {
        this(radius, cornerType, borderWidth, borderColor, 0, 0);
    }

    public BorderRoundedCorners(int radius, int border1Width, int border1Color, int border2Width, int border2Color) {
        this(radius, CornerType.ALL, border1Width, border1Color, border2Width, border2Color);
    }

    public BorderRoundedCorners(int radius, CornerType cornerType, int border1Width, int border1Color, int border2Width, int border2Color) {
        super(border1Width, border1Color, border2Width, border2Color);
        this.radius = radius;
        this.diameter = this.radius * 2;
        this.cornerType = cornerType;
    }

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool,
                               @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        drawRoundRect(canvas, paint, width, height);
        return bitmap;
    }


    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
        float right = width - mAllBorder;
        float bottom = height - mAllBorder;


        switch (cornerType) {
            case ALL:
                canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, right, bottom), radius, radius, paint);
                break;
            case TOP_LEFT:
                drawTopLeftRoundRect(canvas, paint, right, bottom);
                break;
            case TOP_RIGHT:
                drawTopRightRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM_LEFT:
                drawBottomLeftRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM_RIGHT:
                drawBottomRightRoundRect(canvas, paint, right, bottom);
                break;
            case TOP:
                drawTopRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM:
                drawBottomRoundRect(canvas, paint, right, bottom);
                break;
            case LEFT:
                drawLeftRoundRect(canvas, paint, right, bottom);
                break;
            case RIGHT:
                drawRightRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_TOP_LEFT:
                drawOtherTopLeftRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_TOP_RIGHT:
                drawOtherTopRightRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_BOTTOM_LEFT:
                drawOtherBottomLeftRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_BOTTOM_RIGHT:
                drawOtherBottomRightRoundRect(canvas, paint, right, bottom);
                break;
            case DIAGONAL_FROM_TOP_LEFT:
                drawDiagonalFromTopLeftRoundRect(canvas, paint, right, bottom);
                break;
            case DIAGONAL_FROM_TOP_RIGHT:
                drawDiagonalFromTopRightRoundRect(canvas, paint, right, bottom);
                break;
            default:
                canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, right, bottom), radius, radius, paint);
                break;
        }
    }

    private void drawTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, mAllBorder + diameter, mAllBorder + diameter), radius,
                radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder + radius, mAllBorder + radius, bottom), paint);
        canvas.drawRect(new RectF(mAllBorder + radius, mAllBorder, right, bottom), paint);
    }

    private void drawTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - diameter, mAllBorder, right, mAllBorder + diameter), radius,
                radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder, right - radius, bottom), paint);
        canvas.drawRect(new RectF(right - radius, mAllBorder + radius, right, bottom), paint);
    }

    private void drawBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, bottom - diameter, mAllBorder + diameter, bottom), radius,
                radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder, mAllBorder + diameter, bottom - radius), paint);
        canvas.drawRect(new RectF(mAllBorder + radius, mAllBorder, right, bottom), paint);
    }

    private void drawBottomRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - diameter, bottom - diameter, right, bottom), radius,
                radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder, right - radius, bottom), paint);
        canvas.drawRect(new RectF(right - radius, mAllBorder, right, bottom - radius), paint);
    }

    private void drawTopRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, right, mAllBorder + diameter), radius, radius,
                paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder + radius, right, bottom), paint);
    }

    private void drawBottomRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, bottom - diameter, right, bottom), radius, radius,
                paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder, right, bottom - radius), paint);
    }

    private void drawLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, mAllBorder + diameter, bottom), radius, radius,
                paint);
        canvas.drawRect(new RectF(mAllBorder + radius, mAllBorder, right, bottom), paint);
    }

    private void drawRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - diameter, mAllBorder, right, bottom), radius, radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder, right - radius, bottom), paint);
    }

    private void drawOtherTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, bottom - diameter, right, bottom), radius, radius,
                paint);
        canvas.drawRoundRect(new RectF(right - diameter, mAllBorder, right, bottom), radius, radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder, right - radius, bottom - radius), paint);
    }

    private void drawOtherTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, mAllBorder + diameter, bottom), radius, radius,
                paint);
        canvas.drawRoundRect(new RectF(mAllBorder, bottom - diameter, right, bottom), radius, radius,
                paint);
        canvas.drawRect(new RectF(mAllBorder + radius, mAllBorder, right, bottom - radius), paint);
    }

    private void drawOtherBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, right, mAllBorder + diameter), radius, radius,
                paint);
        canvas.drawRoundRect(new RectF(right - diameter, mAllBorder, right, bottom), radius, radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder + radius, right - radius, bottom), paint);
    }

    private void drawOtherBottomRightRoundRect(Canvas canvas, Paint paint, float right,
                                               float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, right, mAllBorder + diameter), radius, radius,
                paint);
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, mAllBorder + diameter, bottom), radius, radius,
                paint);
        canvas.drawRect(new RectF(mAllBorder + radius, mAllBorder + radius, right, bottom), paint);
    }

    private void drawDiagonalFromTopLeftRoundRect(Canvas canvas, Paint paint, float right,
                                                  float bottom) {
        canvas.drawRoundRect(new RectF(mAllBorder, mAllBorder, mAllBorder + diameter, mAllBorder + diameter), radius,
                radius, paint);
        canvas.drawRoundRect(new RectF(right - diameter, bottom - diameter, right, bottom), radius,
                radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder + radius, right - diameter, bottom), paint);
        canvas.drawRect(new RectF(mAllBorder + diameter, mAllBorder, right, bottom - radius), paint);
    }

    private void drawDiagonalFromTopRightRoundRect(Canvas canvas, Paint paint, float right,
                                                   float bottom) {
        canvas.drawRoundRect(new RectF(right - diameter, mAllBorder, right, mAllBorder + diameter), radius,
                radius, paint);
        canvas.drawRoundRect(new RectF(mAllBorder, bottom - diameter, mAllBorder + diameter, bottom), radius,
                radius, paint);
        canvas.drawRect(new RectF(mAllBorder, mAllBorder, right - radius, bottom - radius), paint);
        canvas.drawRect(new RectF(mAllBorder + radius, mAllBorder + radius, right, bottom), paint);
    }


    public Bitmap roundedCorners(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int roundingRadius) {
        Preconditions.checkArgument(roundingRadius > 0, "roundingRadius must be greater than 0.");

        // Alpha is required for this transformation.
        Bitmap.Config safeConfig = getAlphaSafeConfig(inBitmap);
        Bitmap toTransform = getAlphaSafeBitmap(pool, inBitmap);
        Bitmap result = pool.get(toTransform.getWidth(), toTransform.getHeight(), safeConfig);

        result.setHasAlpha(true);

        BitmapShader shader = new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RectF rect = new RectF(0, 0, result.getWidth(), result.getHeight());
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawRoundRect(rect, roundingRadius, roundingRadius, paint);
        clear(canvas);

        if (!toTransform.equals(inBitmap)) {
            pool.put(toTransform);
        }

        return result;
    }


    @Override
    protected String getID() {
        return ID + radius + cornerType + mBorder1Breadth + mBorder1Color + mBorder2Breadth + mBorder2Color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorderRoundedCorners that = (BorderRoundedCorners) o;
        return radius == that.radius &&
                diameter == that.diameter &&
                cornerType == that.cornerType &&
                mBorder1Breadth == that.mBorder1Breadth &&
                mBorder1Color == that.mBorder1Color &&
                mBorder2Breadth == that.mBorder2Breadth &&
                mBorder2Color == that.mBorder2Color;
    }

}
