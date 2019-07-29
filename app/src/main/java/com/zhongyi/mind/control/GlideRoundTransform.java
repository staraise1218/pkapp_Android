package com.zhongyi.mind.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class GlideRoundTransform extends BitmapTransformation {
    public GlideRoundTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        float r = size/6f;

        //获取一张位图用来往上面绘制
        Bitmap result = pool.get(500, 500, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        }

        //使用获取的位图新建一张画布
        Canvas canvas = new Canvas(result);
        //新建一个画笔
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, 500,500);
        canvas.drawRoundRect(rectF, r, r, paint);
        //先画图片，再画圆角矩形，获取交集
        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
