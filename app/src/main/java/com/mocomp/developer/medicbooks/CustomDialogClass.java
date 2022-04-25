package com.mocomp.developer.medicbooks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mocomp.developer.medicbooks.activity.MainActivity;
import com.mocomp.developer.medicbooks.activity.Payments;
import com.mocomp.developer.medicbooks.utility.AppUtilities;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    ImageButton exit;
    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        Typeface Maintypeface = Typeface.createFromAsset(c.getAssets(), "Cairo-Regular.ttf");
        TextView textView =findViewById(R.id.txt);
        textView.setTypeface(Maintypeface);
        no = (Button) findViewById(R.id.btn_no);
        exit=findViewById(R.id.exit);
        no.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                AppUtilities.rateThisApp(c);
                /*Intent pay = new Intent(c, Payments.class);
                c.startActivity(pay);*/
                c.finish();
                break;
            case R.id.exit:

                //Intent go = new Intent(c, MainActivity.class);
                //c.startActivity(go);
                //c.finish();
                break;
            default:
                break;
        }
        dismiss();
    }
}

