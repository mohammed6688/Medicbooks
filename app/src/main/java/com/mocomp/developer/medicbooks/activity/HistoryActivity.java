package com.mocomp.developer.medicbooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.adapters.HistoryAdapter;
import com.mocomp.developer.medicbooks.app.MyApplication;
import com.mocomp.developer.medicbooks.models.content.FriendlyMessage;
import com.mocomp.developer.medicbooks.models.content.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mocomp.developer.medicbooks.activity.ChatActivity.ANONYMOUS;
import static com.mocomp.developer.medicbooks.activity.ChatActivity.RC_SIGN_IN;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseUser user;
    private String mUsername,seckey;

    private RecyclerView recyclerView;
    private List<FriendlyMessage> mChat;
    private List<User> mUsersName;
    TextView mProggresbar;
    private List<String> mUsersid = new ArrayList<>();
    private List<String> UserName = new ArrayList<>();
    private List<FriendlyMessage>list = new ArrayList<>();
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);




        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        recyclerView=findViewById(R.id.mrecycleview);
        mProggresbar=findViewById(R.id.progress_txt);
        mProggresbar.setVisibility(View.VISIBLE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mChat = new ArrayList<>();
        mUsersName = new ArrayList<>();
        checkUser();
    }

    private void checkUser() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.e("pos","yes user");
                    onSignedInInitialize(user.getDisplayName());
                    //mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("PatientHistory").child(user.getUid());

                } else {
                    // User is signed out
                    Log.e("pos","no user");
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .setTheme(R.style.LoginTheme)
                                    .setLogo(R.mipmap.logo1)
                                    .build(),
                            RC_SIGN_IN);
                }
            }

        };
    }
    private void onSignedInInitialize (String username){
        mUsername=username;
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("PatientHistory").child(user.getUid());
        fetchDtaHelper();

    }

    private void onSignedOutCleanup (){
        mUsername = ANONYMOUS;
    }
    @Override
    public void onBackPressed() {
        Intent go = new Intent(HistoryActivity.this, ChooserActivity.class);
        startActivity(go);
        super.onBackPressed();
    }

    private void fetchDtaHelper() {

        mMessagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsersid.clear();
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FriendlyMessage user = snapshot.getValue(FriendlyMessage.class);
                    assert user != null;

                    mUsersid.add(snapshot.getKey());            //list of ids of each conversation of current user
                    list.add(user);
                    HistoryAdapter secondAdapter = new HistoryAdapter(HistoryActivity.this, mUsersid,list);
                    recyclerView.setAdapter(secondAdapter);
                    mProggresbar.setVisibility(View.GONE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //status("online");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        //status("offline");
    }
}