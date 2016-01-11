package com.nanami.android.blackhistory.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nanami.android.blackhistory.utils.PicassoImage;

/**
 * Created by atsumi on 2016/01/10.
 */
public class PicassoImageView extends ImageView {
    public PicassoImageView(Context context) {
        this(context, null);
    }

    public PicassoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void loadImage(String url){
        PicassoImage.Builder(getContext())
                .with(getContext()).load(url)
//                .placeholder(R.drawable.progress_animation)
                .into(this);
    }
}
