package com.zhongyi.mind.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.zhongyi.mind.R;
import com.zhongyi.mind.UserInfoActivity;
import com.zhongyi.mind.contant.NetConstant;
import com.zhongyi.mind.control.GlideCircleTransform;
import com.zhongyi.mind.control.GlideRoundTransform;
import com.zhongyi.mind.data.Artical;
import com.zhongyi.mind.data.DynamicBean;

public class DynamicHolder extends BaseViewHolder<Artical> {

    private ImageView pic;
    private TextView pic_title;
    private TextView pic_content;

    public DynamicHolder(View view) {
        super(view);
        pic = view.findViewById(R.id.pic);
        pic_title = view.findViewById(R.id.pic_title);
        pic_content = view.findViewById(R.id.pic_content);
    }

    @Override
    public void setData(Artical data) {
        super.setData(data);

//        switch (data.getType()){
//            case 2:{
                Glide.with(getContext()).load(NetConstant.BASE_IMGE_URL+data.getThumb()).transform(new GlideRoundTransform(getContext())).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        pic.setImageDrawable(resource); //显示图片
                    }
                });

//            }
//            break;
//            case 3:{
//                Glide.with(getContext()).load(NetConstant.BASE_IMGE_URL+data.getVideo_thumb()).into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource,
//                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
//                        pic.setImageDrawable(resource); //显示图片
//                    }
//                });
//
//
//            }
//            break;
//        }

        pic_title.setText(data.getTitle());
        //pic_content.setText(data.getContent());
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        //获取一张位图用来往上面绘制
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        //使用获取的位图新建一张画布
        Canvas canvas = new Canvas(result);
        //新建一个画笔
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, 10, 10, paint);
        //先画图片，再画圆角矩形，获取交集
        return result;
    }
}
