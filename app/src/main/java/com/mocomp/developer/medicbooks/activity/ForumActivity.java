package com.mocomp.developer.medicbooks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.models.content.FriendlyMessage;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.rengwuxian.materialedittext.MaterialEditText;


public class ForumActivity extends AppCompatActivity {

    String[] languages = { "باطنة","اطفال","جراحة","نساء وتوليد","عيون","انف واذن وحنجرة","جلدية","عظام"};
    String[] countrys = { "مصر","الجزائر","السودان","العراق","المغرب","المملكة العربية السعودية","اليمن","سوريا","تونس","الأردن","الإمارات العربية المتحدة","لبنان","ليبيا","فلسطين","سلطنة عمان","موريتانيا","الكويت","دولة قطر","البحرين","جيبوتي","جزر القمر"};
    MaterialRadioButton me,otherOne,female,male,fy,fn,sy,sn,ty,tn,foy,fon;
    EditText otherName ,otherAge,problem,problemInc,problemDec,problemPeriod,medicine,fivet,myAge;
    Button sendRequest;
    TextView Title;
    ImageView back;
    String message1="" , message2="",message3="",message4="",message5="",message6="",message7="",message8="",message9="",message10="",message11="",message12="",message13="",message14="",message15="",message16="",message17="";
    AutoCompleteTextView acTextView,countryName;
    boolean condition;
    String userId;
    LinearLayout coronaLayout,tlay,melay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        initialization();
        sendRequest.setOnClickListener(view -> startSending());
    }

    private void initialization() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, languages);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, countrys);
        condition = getIntent().getBooleanExtra("autoOpened",false);
        userId=getIntent().getStringExtra("userId");
        //Find TextView control
        acTextView = findViewById(R.id.branch);
        countryName = findViewById(R.id.countryName);
        back=findViewById(R.id.back);
        //Set the number of characters the user must type before the drop down list is shown
        acTextView.setThreshold(1);
        countryName.setThreshold(1);
        //Set the adapter
        acTextView.setAdapter(adapter);
        countryName.setAdapter(countryAdapter);


        myAge= findViewById(R.id.myAge);
        me=findViewById(R.id.me);
        melay=findViewById(R.id.melay);
        melay.setVisibility(View.GONE);
        otherOne =findViewById(R.id.otherone);
        female=findViewById(R.id.female);
        male=findViewById(R.id.male);
        otherName=findViewById(R.id.otherName);
        otherAge=findViewById(R.id.otherAge);
        problem=findViewById(R.id.problem);
        problemInc=findViewById(R.id.probleminc);
        problemDec=findViewById(R.id.problemdec);
        problemPeriod=findViewById(R.id.problemPeriod);
        medicine=findViewById(R.id.medicine);
        sendRequest=findViewById(R.id.sendrequest);
        Title =findViewById(R.id.startingtxt);
        otherName.setVisibility(View.GONE);
        otherAge.setVisibility(View.GONE);
        tlay=findViewById(R.id.tlay);
        coronaLayout=findViewById(R.id.coronaLayout);
        fy=findViewById(R.id.fy);
        fn=findViewById(R.id.fn);
        sy=findViewById(R.id.sy);
        sn=findViewById(R.id.sn);
        ty=findViewById(R.id.ty);
        tn=findViewById(R.id.tn);
        foy=findViewById(R.id.foy);
        fon=findViewById(R.id.fon);
        fivet=findViewById(R.id.fivet);
        coronaLayout.setVisibility(View.GONE);


        me.setOnClickListener(view -> {
            otherName.setVisibility(View.GONE);
            otherAge.setVisibility(View.GONE);
            melay.setVisibility(View.VISIBLE);
            message9="انا اطلب استشارة خاصة بي";
        });
        otherOne.setOnClickListener(view -> {
            otherName.setVisibility(View.VISIBLE);
            otherAge.setVisibility(View.VISIBLE);
            melay.setVisibility(View.GONE);
            message9="انا اطلب استشارة خاصة بشخص اخر";
        });
        fy.setOnClickListener(view -> {
            coronaLayout.setVisibility(View.VISIBLE);
            acTextView.setVisibility(View.GONE);
            tlay.setVisibility(View.GONE);
            message11="نعم انا مريض كورونا";
        });
        fn.setOnClickListener(view -> {
            coronaLayout.setVisibility(View.GONE);
            problem.setVisibility(View.VISIBLE);
            tlay.setVisibility(View.VISIBLE);
            message11="لا انا لست مريض كورونا";
        });
        /*countryName.setOnClickListener(v -> {
            CountryPicker picker = CountryPicker.newInstance("اختر دولة");  // dialog title
            picker.setListener((name, code, dialCode, flagDrawableResID) -> {
                // Implement your code here
                countryName.setText(name);
                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        });*/
        sy.setOnClickListener(v -> message12="نعم لدي نتيجة فحص مؤكدة");
        sn.setOnClickListener(v -> message12="لا ليست لدي نتيجة فحص مؤكدة");
        ty.setOnClickListener(v -> message13="نعم لدي ارتفاع في درجت الحرارة");
        tn.setOnClickListener(v -> message13="لا ليس لدي ارتفاع في درجة الحرارة");
        foy.setOnClickListener(v -> message14="نعم اعاني من ضيق في التنفس");
        fon.setOnClickListener(v -> message14="لا اعاني من ضيق في التنفس");
        back.setOnClickListener(v -> {
            Intent go;
            if (condition){
                go = new Intent(ForumActivity.this,ChooserActivity.class);
            }else {
                go = new Intent(ForumActivity.this,ChatActivity.class);
            }
            startActivity(go);
            finish();
        });
    }

    private void startSending() {
        message1 =otherName.getText().toString();
        message2 =otherAge.getText().toString();
        message3 =acTextView.getText().toString();
        message4 =problem.getText().toString();
        message5 =problemInc.getText().toString();
        message6 =problemDec.getText().toString();
        message7 =problemPeriod.getText().toString();
        message8 =medicine.getText().toString();
        message15 =fivet.getText().toString();
        message16 =countryName.getText().toString();
        message17 =myAge.getText().toString();

        String message = submitorder(message1 , message2,message3 , message4 , message5,message6,message7,message8,message15,message16,message17);


        if (message != null){
            Log.e("message",message);
            Intent go = new Intent(ForumActivity.this,ChatActivity.class);
            go.putExtra("forum",message);
            go.putExtra("condition",true);
            startActivity(go);
        }
    }

    private String submitorder(String OtherName , String OtherAge ,String specialization , String Problem  ,String ProblemInc , String ProblemDec,String ProblemPeriod,String Medicine,String moreDesc,String myCountry,String myAge){
        String message;
        if (me.isChecked()) {
            /*if (fy.isChecked()){
                if (message9.equals("")||message11.equals("")||message12.equals("")||message13.equals("")||message14.equals("")||message15.equals("")){
                    Toast.makeText(this, "fill all fields", Toast.LENGTH_SHORT).show();
                }else {
                    message ="نوع الاستشارة: "+message9+"\n"+
                            "نعم انا مصاب بالكورونا " +"\n"
                            +"هل لديك نتيجة فحص مؤكدة: "+message12+"\n"
                            + "هل لديك ارتفاع في الحرارة: "+message13+"\n"+
                            "هل تعاني من ضيق في التنفس: "+ message14+"\n"+
                            "هل تعاني من اعراض اخرى: "+message15;
                    return message;
                }
            }else {*/
                if (acTextView.getText().equals("")||problem.getText().equals("")||problemInc.getText().equals("")||problemDec.getText().equals("")||problemPeriod.getText().equals("")||medicine.getText().equals("")||message9 .equals("")||message10.equals("")||message16.equals("")||message17.equals("")){
                    Toast.makeText(this, "برجاء ملئ كل الخانات", Toast.LENGTH_SHORT).show();
                }else {
                    message = "نوع الاستشارة: "+message9+"\n"
                            +"البلد "+myCountry+"\n"
                            +"العمر "+myAge+"\n"
                            +message10+"\n"+"تخصص الاستشارة:"+specialization+"\n"+
                            "وصف المشكلة: "+ Problem+"\n"+
                            "عوامل تزيد المشكلة: "+ProblemInc+"\n"+
                            "عوامل تخفف المشكلة: "+ProblemDec+"\n"+
                            "المدة الزمنية للمشكلة: "+ProblemPeriod+"\n"+
                            "سوابق دوائية: "+Medicine;
                    return message;
                }


        } else {
            if (fy.isChecked()){
                if (otherName.equals("")||otherAge.equals("")||message9.equals("")||message11.equals("")||message12.equals("")||message13.equals("")||message14.equals("")||message15.equals("")){
                    Toast.makeText(this, "fill all fields", Toast.LENGTH_SHORT).show();
                }else {
                    message = "نوع الاستشارة: "+message9+"\n"+
                            "اسم الشخص الاخر: "+OtherName+"\n"+
                            "عمر الشخص الاخر: "+OtherAge+"\n"+
                            "نعم الشخص مصاب بالكورونا " +"\n"
                            +"هل لديك نتيجة فحص مؤكدة: "+message12+"\n"
                            +"هل لديك ارتفاع في الحرارة: "+message13+"\n"+
                            "هل تعاني من ضيق في التنفس: "+ message14+"\n"+
                            "هل تعاني من اعراض اخرى: "+message15;

                    return message;
                }
            }else {
                if (otherName.getText().equals("")||otherAge.getText().equals("")||acTextView.getText().equals("")||problem.getText().equals("")||problemInc.getText().equals("")||problemDec.getText().equals("")||problemPeriod.getText().equals("")||medicine.getText().equals("")||message9.equals("")||message10.equals("")){
                    Toast.makeText(this, "fill all fields", Toast.LENGTH_SHORT).show();
                }else {
                    message = "نوع الاستشارة: "+message9+"\n"+
                            "اسم الشخص الاخر: "+OtherName+"\n"+
                            "عمر الشخص الاخر: "+OtherAge+"\n"+
                            "لا الشخص ليس مصاب بالكورونا "+"\n"
                            +message10+"\n"+"تخصص الاستشارة:"+specialization+"\n"+
                            "وصف المشكلة: "+ Problem+"\n"+
                            "عوامل تزيد المشكلة: "+ProblemInc+"\n"+
                            "عوامل تخفف المشكلة: "+ProblemDec+"\n"+
                            "المدة الزمنية للمشكلة: "+ProblemPeriod+"\n"+
                            "سوابق دوائية: "+Medicine;
                    return message;
                }
            }


        }
        return null;
    }



    @Override
    public void onBackPressed() {
        Intent go;
        if (condition){
            go = new Intent(ForumActivity.this, MainActivity.class);
        }else {
            go = new Intent(ForumActivity.this, ChatActivity.class);
        }
        startActivity(go);
        finish();

        super.onBackPressed();
    }

    public void onSecondRadioButtonClicked(View view ) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked)
                    message10="الجنس: ذكر";
                break;
            case R.id.female:
                if (checked)
                    message10="الجنس: انثي";
                break;
        }
    }
}