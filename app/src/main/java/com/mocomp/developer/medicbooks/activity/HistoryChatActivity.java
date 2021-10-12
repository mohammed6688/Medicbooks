package com.mocomp.developer.medicbooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mocomp.developer.medicbooks.CustomDialogClass;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.adapters.MessageAdapter;
import com.mocomp.developer.medicbooks.app.MyApplication;
import com.mocomp.developer.medicbooks.models.content.FriendlyMessage;
import com.mocomp.developer.medicbooks.notification.APIService;
import com.mocomp.developer.medicbooks.notification.Client;
import com.mocomp.developer.medicbooks.notification.Data;
import com.mocomp.developer.medicbooks.notification.MyResponse;
import com.mocomp.developer.medicbooks.notification.Sender;
import com.mocomp.developer.medicbooks.notification.Token;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mocomp.developer.medicbooks.activity.ChatActivity.RC_SIGN_IN;

public class HistoryChatActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseUser user;
    APIService apiService;
    private ProgressBar mProgressBar;
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private List<FriendlyMessage> friendlyMessages;
    private boolean consultationEnd;


    private FirebaseStorage mfirebaseStorage;
    private StorageReference mChatphotosStorageRerances;
    private StorageReference mChataudiosStorageRerances;


    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;

    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private FloatingActionButton mSendButton;
    private Button record;
    private String mUsername;
    private static final int RC_PHOTO_PICKER =  2;
    private static final int RC_AUDIO_PICKER =  3;

    boolean notify = false;
    private String doctorid;
    private LinearLayout linearlayout;
    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_chat);

        consultationEnd =getIntent().getBooleanExtra("consultationEnd",false);
        key = getIntent().getStringExtra("key");
        doctorid = getIntent().getStringExtra("doctorid");

        Log.e("doctorid",doctorid);
        initializeVariable();
        initializeListeners();
        initializeAuth();

    }

    private void initializeVariable() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(HistoryChatActivity.this,HistoryActivity.class);
                startActivity(go);
            }
        });
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        // Initialize references to views
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mProgressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.postrecyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        // Initialize message ListView and its adapter
        friendlyMessages = new ArrayList<>();

        /////////////////////////////////////////////
        linearlayout= findViewById(R.id.linearLayout);
        mProgressBar = findViewById(R.id.progressBar);
        mPhotoPickerButton = findViewById(R.id.photoPickerButton);
        mMessageEditText =  findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        record = findViewById(R.id.record);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseStorage = FirebaseStorage.getInstance();

        mChatphotosStorageRerances = mfirebaseStorage.getReference().child("chat_photos");
        mChataudiosStorageRerances = mfirebaseStorage.getReference().child("chat_audios");




        if (!consultationEnd){   //handling the UI
            linearlayout.setVisibility(View.GONE);
        }else {
            linearlayout.setVisibility(View.VISIBLE);
        }

    }

    private void initializeListeners() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_AUDIO_PICKER);

            }
        });

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });


        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMessageEditText.getText().toString().equals("")){
                    FriendlyMessage friendlyMessage = new FriendlyMessage("",user.getUid(),mMessageEditText.getText().toString(), mUsername, null,null,false,false,getDate());
                    mMessagesDatabaseReference.push().setValue(friendlyMessage);
                    notify = true;
                    sendNotification(mMessageEditText.getText().toString(),doctorid);
                    mMessageEditText.setText("");  // Clear input box
                }
            }
        });


    }

    private void initializeAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    historyModeSetup();
                    updateToken(FirebaseInstanceId.getInstance().getToken());
                    currentUser(user.getUid());
                    mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("PatientHistory").child(user.getUid()).child(key);

                } else {
                    // User is signed out
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

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(user.getUid()).setValue(token1);
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void sendNotification(final String message, final String doctorid) {
        if (notify) {
            DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
            Query query = tokens.orderByKey().equalTo(doctorid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(user.getUid(), R.mipmap.logo1, mUsername+": "+message, "رسالة جديدة",
                                doctorid);

                        Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200){
                                            if (response.body().success != 1){
                                                //Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        notify = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed In", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode==RC_PHOTO_PICKER && resultCode==RESULT_OK){
            Toast.makeText(this, "uploading PHOTO...", Toast.LENGTH_SHORT).show();
            Uri selctimageuri = data.getData();
            final StorageReference photoref = mChatphotosStorageRerances.child(selctimageuri.getLastPathSegment());
            UploadTask uploadTask = photoref.putFile(selctimageuri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(HistoryChatActivity.this, "photo uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        FriendlyMessage friendlyMessage = new FriendlyMessage("",user.getUid(),null, mUsername, downloadUri.toString(),null,false,false,getDate());
                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
                        notify = true;
                        sendNotification("صورة",doctorid);

                    } else {
                        // Handle failures
                        Toast.makeText(HistoryChatActivity.this, "photo failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }if (requestCode==RC_AUDIO_PICKER && resultCode==RESULT_OK){
            Toast.makeText(this, "uploading AUDIO...", Toast.LENGTH_SHORT).show();
            Uri selctimageuri = data.getData();
            final StorageReference photoref = mChataudiosStorageRerances.child(selctimageuri.getLastPathSegment());
            UploadTask uploadTask = photoref.putFile(selctimageuri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(HistoryChatActivity.this, "audio uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        FriendlyMessage friendlyMessage = new FriendlyMessage("",user.getUid(),null, mUsername, null,downloadUri.toString(),false,false,getDate());
                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
                        notify = true;
                        sendNotification("تسجيل صوتي",doctorid);

                    } else {
                        // Handle failures
                        Toast.makeText(HistoryChatActivity.this, "audio failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    private void historyModeSetup() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PatientHistory").child(user.getUid()).child(key);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendlyMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    FriendlyMessage friendlyMessage = snapshot.getValue(FriendlyMessage.class);
                    friendlyMessages.add(friendlyMessage);
                }

                adapter = new MessageAdapter(HistoryChatActivity.this,friendlyMessages);
                recyclerView.setAdapter(adapter);
                mProgressBar.setVisibility(ProgressBar.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}