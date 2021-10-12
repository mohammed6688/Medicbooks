package com.mocomp.developer.medicbooks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mocomp.developer.medicbooks.CustomDialogClass;
import com.mocomp.developer.medicbooks.R;

import java.util.HashMap;

public class Rating extends AppCompatActivity {

    CustomDialogClass errorDialog;
    RatingBar respondRating,respondTypeRating,alloverRating;
    String mrespondRating="",mrespondTypeRating="",malloverRating="",doctorId,patientId;
    EditText comment;
    Button endRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Cairo-Regular.ttf");
        doctorId = getIntent().getStringExtra("doctorId");
        patientId = getIntent().getStringExtra("patientId");


        TextView lay1 =findViewById(R.id.lay1);
        TextView lay2 =findViewById(R.id.lay2);
        TextView lay3 =findViewById(R.id.lay3);
        lay1.setTypeface(typeface);
        lay2.setTypeface(typeface);
        lay3.setTypeface(typeface);


        respondRating = findViewById(R.id.respondRating);
        respondTypeRating = findViewById(R.id.respondTypeRating);
        alloverRating = findViewById(R.id.alloverRating);
        comment = findViewById(R.id.comment);
        endRating= findViewById(R.id.endRating);
        endRating.setTypeface(typeface);


        respondRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mrespondRating = String.valueOf(rating);
                Log.e("y",mrespondRating);
            }
        });

        respondTypeRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mrespondTypeRating = String.valueOf(rating);
                Log.e("y",mrespondTypeRating);
            }
        });

        alloverRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                malloverRating = String.valueOf(rating);
                Log.e("y",malloverRating);
            }
        });

        endRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mrespondRating.equals("") && !mrespondTypeRating.equals("") && !alloverRating.equals("")){
                    String commentText=comment.getText().toString();
                    uploadRating(commentText);
                }else {
                    Toast.makeText(Rating.this, "استكمل التقييم", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void uploadRating(String commentText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rating").child(patientId);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PatientId", patientId);
        hashMap.put("DoctorId", doctorId);
        hashMap.put("respondRating", mrespondRating);
        hashMap.put("respondTypeRating", mrespondTypeRating);
        hashMap.put("alloverRating",malloverRating);
        if (commentText!=null){
            hashMap.put("commentText",commentText);
        }
        reference.setValue(hashMap);
        Bayus();
    }

    private void Bayus() {
        errorDialog=new CustomDialogClass(Rating.this);
        errorDialog.show();
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}