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
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * 圆形裁剪
 */
public class CropCircleTransformation implements Transformation<Bitmap> {

    private static int DEFAULT_WIDTH = 0;
    private static int DEFAULT_COLOR = Color.BLACK;
    private static float DEFAULT_ALPHA = 1f;

    private final static int ALPHA_MAX = 255;

    private BitmapPool mBitmapPool;

    //边框宽度
    private float mBorderWidth;
    //边框颜色
    private int mBorderColor;
    //边框透明度
    private float mBorderAlpha;

    public CropCircleTransformation(Context context) {
        this(Glide.get(context).getBitmapPool(), DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public CropCircleTransformation(BitmapPool pool) {
        this(pool, DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public CropCircleTransformation(Context context, float borderWidth) {
        this(Glide.get(context).getBitmapPool(), borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public CropCircleTransformation(BitmapPool pool, float borderWidth) {
        this(pool, borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public CropCircleTransformation(Context context, float borderWidth, int borderColor) {
        this(Glide.get(context).getBitmapPool(), borderWidth, borderColor, DEFAULT_ALPHA);
    }

    public CropCircleTransformation(BitmapPool pool, float borderWidth, int borderColor) {
        this(pool, borderWidth, borderColor, DEFAULT_ALPHA);
    }

    public CropCircleTransformation(Context context, float borderWidth, int borderColor, float borderAlpha) {
        this(Glide.get(context).getBitmapPool(), borderWidth, borderColor, borderAlpha);
    }

    /**
     * 构造方法
     *
     * @param borderWidth 边框宽度
     * @param borderColor 边框颜色
     * @param borderAlpha 边框透明度
     */
    public CropCircleTransformation(BitmapPool pool, float borderWidth, int borderColor, float borderAlpha) {
        this.mBitmapPool = pool;
        this.mBorderWidth = borderWidth;
        this.mBorderColor = borderColor;
        this.mBorderAlpha = borderAlpha;

    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();
        int size = Math.min(source.getWidth(), source.getHeight());

        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;

        Bitmap bitmap = mBitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if (width != 0 || height != 0) {
            // source isn't square, move viewport to center
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);

        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(mBorderColor);
        borderPaint.setAlpha(Float.valueOf(mBorderAlpha * ALPHA_MAX).intValue());

        float r = size / 2f;
        float radius = r;
        if (mBorderWidth > 0) {
            radius = r - mBorderWidth;
            canvas.drawCircle(r, r, r, borderPaint);
        }
        if (radius > 0) {
            canvas.drawCircle(r, r, radius, paint);
        }
        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override
    public String getId() {
        return "CropCircleTransformation(borderWidth=" + mBorderWidth + ", borderColor=" + mBorderColor +
                ", borderAlpha=" + mBorderAlpha + ")";
    }
}
