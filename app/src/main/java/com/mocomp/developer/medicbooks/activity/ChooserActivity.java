package com.mocomp.developer.medicbooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.utility.ActivityUtilities;
import com.mocomp.developer.medicbooks.utility.AppUtilities;


public class ChooserActivity extends AppCompatActivity {

    ImageView articals,consult,pay,settings,about,terms,share,rate;

    private Activity mActivity= ChooserActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        articals = findViewById(R.id.articals);
        consult = findViewById(R.id.consult);
        pay = findViewById(R.id.pay);
        terms = findViewById(R.id.terms);
        settings = findViewById(R.id.settings);
        about = findViewById(R.id.about);
        share=findViewById(R.id.share);
        rate=findViewById(R.id.rate);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.shareApp(mActivity);
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.rateThisApp(mActivity);
            }
        });

        articals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ChooserActivity.this, MainActivity.class));
                finish();

            }
        });
        consult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] colors = {"استشر طبيب", "استشارات سابقة"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ChooserActivity.this,R.style.AlertDialogCustom);
                builder.setTitle("أختر نوع الاستشارة");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            ActivityUtilities.getInstance().invokeNewActivity(mActivity, ChatActivity.class, true);
                        }else if(which==1){
                            ActivityUtilities.getInstance().invokeNewActivity(mActivity, HistoryActivity.class, false);
                        }
                        // the user clicked on colors[which]
                    }
                });
                builder.show();


            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityUtilities.getInstance().invokeNewActivity(mActivity, Payments.class, false);
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityUtilities.getInstance().invokeCustomUrlActivity(mActivity, CustomUrlActivity.class, getResources().getString(R.string.privacy), getResources().getString(R.string.privacy_url), false);

            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityUtilities.getInstance().invokeNewActivity(mActivity, SettingsActivity.class, false);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityUtilities.getInstance().invokeNewActivity(mActivity, AboutDevActivity.class, false);
            }
        });

    }
    @Override
    public void onBackPressed() {
        AppUtilities.tapPromptToExit(mActivity);
    }
}