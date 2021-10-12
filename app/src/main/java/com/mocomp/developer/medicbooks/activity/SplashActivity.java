package com.mocomp.developer.medicbooks.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.utility.ActivityUtilities;
import com.mocomp.developer.medicbooks.view.PhenomenaTextView;


public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private ImageView mImageView;
    private Animation mAnimation_1;
    private ProgressBar mProgressBar;
    private RelativeLayout mRootLayout;
    private VideoView videoView;

    // Constants
    private static final int SPLASH_DURATION = 2500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVar();
        initView();
        playVideo();

    }

    private void initVar() {
        mContext = getApplicationContext();
        mActivity = SplashActivity.this;
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Cairo-SemiBold.ttf");
        TextView textView = findViewById(R.id.desc);
        textView.setTypeface(typeface);
       // PhenomenaTextView appname = findViewById(R.id.app_name);
        //PhenomenaTextView appdesc = findViewById(R.id.app_desc);
        //appname.setTypeface(typeface);
        //appdesc.setTypeface(typeface);
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
       // mRootLayout = (RelativeLayout) findViewById(R.id.splashBody);
        videoView = findViewById(R.id.videoView);
        //mImageView = (ImageView) findViewById(R.id.splashIcon);
        //mAnimation_1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

    }
    private void playVideo() {

        try{

            Uri path = Uri.parse( "android.resource://"+getPackageName()+"/"+ +R.raw.vid1);
            videoView.setVideoURI(path);

            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    jump();
                }
            });
            videoView.start();
        }catch (Exception e){
            jump();
        }
    }

    private void jump() {

        if(isFinishing())
            return;
        startActivity(new Intent(this,IntroActivity.class));
        finish();
    }
}

