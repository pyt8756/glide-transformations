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
import android.graphics.Path;
import android.graphics.Shader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import jp.wasabeef.glide.transformations.path.SvgUtil;
import jp.wasabeef.glide.transformations.path.parser.PathInfo;

/**
 * svg
 */
public class SvgTransformation implements Transformation<Bitmap> {

    private static int DEFAULT_WIDTH = 0;
    private static int DEFAULT_COLOR = Color.BLACK;
    private static float DEFAULT_ALPHA = 1f;

    private final static int ALPHA_MAX = 255;

    private Context mContext;
    private BitmapPool mBitmapPool;
    private int mSvgId;
    //边框宽度
    private int mBorderWidth;
    //边框颜色
    private int mBorderColor;
    //边框透明度
    private float mBorderAlpha;

    public SvgTransformation(Context context, int svgId) {
        this(context, Glide.get(context).getBitmapPool(), svgId, DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public SvgTransformation(Context context, BitmapPool pool, int svgId) {
        this(context, pool, svgId, DEFAULT_WIDTH, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public SvgTransformation(Context context, int svgId, int borderWidth) {
        this(context, Glide.get(context).getBitmapPool(), svgId, borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public SvgTransformation(Context context, BitmapPool pool, int svgId, int borderWidth) {
        this(context, pool, svgId, borderWidth, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public SvgTransformation(Context context, int svgId, int borderWidth, int borderColor) {
        this(context, Glide.get(context).getBitmapPool(), svgId, borderWidth, borderColor, DEFAULT_ALPHA);
    }

    public SvgTransformation(Context context, BitmapPool pool, int svgId, int borderWidth, int borderColor) {
        this(context, pool, svgId, borderWidth, borderColor, DEFAULT_ALPHA);
    }

    public SvgTransformation(Context context, int svgId, int borderWidth, int borderColor, float borderAlpha) {
        this(context, Glide.get(context).getBitmapPool(), svgId, borderWidth, borderColor, borderAlpha);
    }

    public SvgTransformation(Context context, BitmapPool pool, int svgId, int borderWidth,
                             int borderColor, float borderAlpha) {
        mBitmapPool = pool;
        mContext = context.getApplicationContext();
        mSvgId = svgId;
        mBorderWidth = borderWidth;
        mBorderColor = borderColor;
        mBorderAlpha = borderAlpha;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {

        Bitmap result = mBitmapPool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);

        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(mBorderColor);
        borderPaint.setAlpha(Float.valueOf(mBorderAlpha * ALPHA_MAX).intValue());

        Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        imagePaint.setAntiAlias(true);

        Bitmap source = resource.get();
        int bitmapWidth = source.getWidth();
        int bitmapHeight = source.getHeight();

        PathInfo shapePath = null;
        if (mSvgId != -1) {
            shapePath = SvgUtil.readSvg(mContext, mSvgId);
        }

        Path path = new Path();
        Path borderPath = new Path();
        Matrix matrix = new Matrix();

        if (bitmapWidth > 0 && bitmapHeight > 0) {

            float width = outWidth - 2f * mBorderWidth;
            float height = outHeight - 2f * mBorderWidth;
            float scale;
            float translateX = 0;
            float translateY = 0;
            if (bitmapWidth * height > width * bitmapHeight) {
                scale = height / bitmapHeight;
                translateX = (width / scale - bitmapWidth) / 2f;
            } else {
                scale = width / (float) bitmapWidth;
                translateY = (height / scale - bitmapHeight) / 2f;
            }

            matrix.setScale(scale, scale);
            matrix.preTranslate(translateX, translateY);
            matrix.postTranslate(mBorderWidth, mBorderWidth);

            svgCalculate(shapePath, path, borderPath, matrix, width, height, outWidth, outHeight);

            BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            imagePaint.setShader(shader);
        }


        canvas.save();
        canvas.drawPath(borderPath, borderPaint);
        canvas.concat(matrix);
        canvas.drawPath(path, imagePaint);
        canvas.restore();
        path.reset();
        borderPath.reset();

        return BitmapResource.obtain(result, mBitmapPool);
    }

    private void svgCalculate(PathInfo shapePath, Path path, Path borderPath, Matrix matrix,
                              float width, float height, float outWidth, float outHeight) {
        Matrix pathMatrix = new Matrix();
        path.reset();
        borderPath.reset();
        float[] pathDimensions = new float[2];
        pathDimensions[0] = shapePath.getWidth();
        pathDimensions[1] = shapePath.getHeight();
        pathMatrix.reset();
        float scale = Math.min(width / pathDimensions[0], height / pathDimensions[1]);
        float translateX = (width - pathDimensions[0] * scale) * 0.5f;
        float translateY = (height - pathDimensions[1] * scale) * 0.5f;
        pathMatrix.setScale(scale, scale);
        pathMatrix.postTranslate(translateX, translateY);
        shapePath.transform(pathMatrix, path);
        path.offset(mBorderWidth, mBorderWidth);

        if (mBorderWidth > 0) {
            pathMatrix.reset();
            float newWidth;
            float newHeight;
            float d;
            newWidth = outWidth;
            newHeight = outHeight;
            d = 0;
            scale = Math.min(newWidth / pathDimensions[0], newHeight / pathDimensions[1]);
            translateX = (newWidth - pathDimensions[0] * scale) * 0.5f + d;
            translateY = (newHeight - pathDimensions[1] * scale) * 0.5f + d;
            pathMatrix.setScale(scale, scale);
            pathMatrix.postTranslate(translateX, translateY);
            shapePath.transform(pathMatrix, borderPath);
        }
        pathMatrix.reset();
        matrix.invert(pathMatrix);
        path.transform(pathMatrix);
    }

    @Override
    public String getId() {
        return "SvgTransformation(svgId=" + mContext.getResources().getResourceEntryName(mSvgId) +
                ",borderWidth=" + mBorderWidth + ", borderColor=" + mBorderColor + ", borderAlpha=" +
                mBorderAlpha + ")";
    }
}
