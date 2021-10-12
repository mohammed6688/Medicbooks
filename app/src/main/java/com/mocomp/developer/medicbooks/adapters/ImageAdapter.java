package com.mocomp.developer.medicbooks.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> imageUri;

    public ImageAdapter(Context context, List<String> imageUri) {
        this.mContext = context;
        this. imageUri=imageUri;
    }
    @Override
    public int getCount() {
        return imageUri.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(mContext).load(imageUri.get(position)).thumbnail(0.2f).into(imageView);
        imageView.setMaximumScale(5.0F);
        imageView.setMediumScale(3.0F);
        container.addView(imageView, 0);
        return imageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}

