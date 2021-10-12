package com.mocomp.developer.medicbooks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.adapters.CustomViewPager;
import com.mocomp.developer.medicbooks.adapters.ImageAdapter;

import java.util.List;

public class ImageSlider extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        List<String> imageUri =getIntent().getStringArrayListExtra("imageUri");
        //Log.e("size", String.valueOf(imageUri.size()));
        CustomViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(this,imageUri);
        viewPager.setAdapter(adapter);
    }
}