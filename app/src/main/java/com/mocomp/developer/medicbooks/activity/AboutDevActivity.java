package com.mocomp.developer.medicbooks.activity;

import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.utility.AppUtilities;

public class AboutDevActivity extends BaseActivity {
    TextView tagline,about_abb,appname,contact;
    ImageView gmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_about_dev);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Cairo-Regular.ttf");
        tagline=findViewById(R.id.tagline);
        appname=findViewById(R.id.app_name);
        about_abb=findViewById(R.id.about_app);
        contact=findViewById(R.id.contact);
        gmail=findViewById(R.id.mail);
        tagline.setTypeface(typeface);
        appname.setTypeface(typeface);
        about_abb.setTypeface(typeface);
        contact.setTypeface(typeface);
        initToolbar(true);
        setToolbarTitle(getString(R.string.about_dev));
        enableUpButton();
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.faceBookLink(AboutDevActivity.this);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

