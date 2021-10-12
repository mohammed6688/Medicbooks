package com.mocomp.developer.medicbooks.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private FirebaseStorage mfirebaseStorage;
    private StorageReference mChatphotosStorageRerances;
    private StorageReference mChataudiosStorageRerances;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseUser user;
    private DatabaseReference mCreateUserRefrance;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    APIService apiService;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private FloatingActionButton mSendButton;
    private Button record;
    private String mUsername;
    private static final int RC_PHOTO_PICKER =  2;
    private static final int RC_AUDIO_PICKER =  3;

    private MessageAdapter adapter;
    boolean notify = false;
    private boolean condition;
    private boolean gate=false;
    private TextView pending;
    private String forum;
    private String doctorid;
    private Button rateus;
    private RecyclerView recyclerView;
    private LinearLayout linearlayout;
    private LinearLayout  ratingLay;
    private List<FriendlyMessage> friendlyMessages;
    private CustomDialogClass errorDialog;
    boolean consultationEnd;
    private List<String>list = new ArrayList<>();
    private CircleImageView profile_image;

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "Cairo-Regular.ttf");

            boolean condition = intent.getBooleanExtra("condition",false);
            consultationEnd = intent.getBooleanExtra("consultationEnd",false);
            doctorid = intent.getStringExtra("doctorid");

            if (condition){               //if true then patient is accepted
                MyApplication.setPreferencesBoolean("Accepted",true);
                linearlayout.setVisibility(View.VISIBLE);
                pending.setVisibility(View.GONE);
                profile_image.setVisibility(View.GONE);
            }else {                       //patient is pending
                MyApplication.setPreferencesBoolean("Accepted",false);
                linearlayout.setVisibility(View.INVISIBLE);
                profile_image.setVisibility(View.VISIBLE);
                pending.setVisibility(View.VISIBLE);
                pending.setTypeface(typeface);
            }
            if (consultationEnd){
                consultationEnding();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("patientCondition"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeVariable();
        initializeListeners();
    }

    private void initializeVariable() {
        forum = getIntent().getStringExtra("forum");
        condition = getIntent().getBooleanExtra("condition",false);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(ChatActivity.this,ChooserActivity.class);
                startActivity(go);
            }
        });
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        mUsername = ANONYMOUS;
        // Initialize references to views
        pending=findViewById(R.id.pending);
        profile_image=findViewById(R.id.profile_image);
        linearlayout= findViewById(R.id.linearLayout);
        //toolbarTitle=findViewById(R.id.toolbarTitle);
        rateus=findViewById(R.id.rateUs);
        mProgressBar = findViewById(R.id.progressBar);
        mPhotoPickerButton = findViewById(R.id.photoPickerButton);
        ratingLay=findViewById(R.id.ratingLay);

        mMessageEditText =  findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        record = findViewById(R.id.record);

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        //ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseStorage = FirebaseStorage.getInstance();


        mChatphotosStorageRerances = mfirebaseStorage.getReference().child("chat_photos");
        mChataudiosStorageRerances = mfirebaseStorage.getReference().child("chat_audios");

        recyclerView = findViewById(R.id.postrecyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        // Initialize message ListView and its adapter
        friendlyMessages = new ArrayList<>();
        ratingLay.setVisibility(View.GONE);
        if (MyApplication.getPrefranceDataBoolean("Accepted")){
            pending.setVisibility(View.GONE);
            profile_image.setVisibility(View.GONE);
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

        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go =new Intent(ChatActivity.this,Rating.class);
                go.putExtra("doctorId",doctorid);
                go.putExtra("patientId",user.getUid());
                startActivity(go);
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

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    MyApplication.setPreferencesBoolean("userOnline",true);
                    //startChooser();                           //view chooser contain previous consultation or new one
                    onSignedInInitialize(user.getDisplayName());
                    mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(user.getUid());
                    mCreateUserRefrance = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                    updateToken(FirebaseInstanceId.getInstance().getToken());
                    currentUser(user.getUid());
                    createUser();
                    checkForum();                              //if its the first time for current user open forum activity
                    //sendForum();
                } else {
                    // User is signed out
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

    private void consultationEnding() {

        if (!gate){
            errorDialog=new CustomDialogClass(ChatActivity.this);
            if(!((Activity) ChatActivity.this).isFinishing())
            {
                errorDialog.show();
            }
            errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            gate=true;
        }
        linearlayout.setVisibility(View.INVISIBLE);
        pending.setVisibility(View.GONE);
        profile_image.setVisibility(View.GONE);
        //ratingLay.setVisibility(View.VISIBLE);
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

    private void createUser() {
        if (mUsername==null||mUsername.equals("")){
            mUsername=user.getPhoneNumber();
        }
        mCreateUserRefrance = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", user.getUid());
        hashMap.put("username", mUsername);
        hashMap.put("imageURL", "default");
        hashMap.put("status", "offline");
        hashMap.put("search", mUsername);
        hashMap.put("deActivate", String.valueOf(false));
        mCreateUserRefrance.setValue(hashMap);
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


    private void checkForum(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("messages");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(user.getUid())) {
                    Log.e("log","no child");
                }else {
                    if (!condition){
                        Intent go = new Intent(ChatActivity.this,ForumActivity.class);
                        go.putExtra("autoOpened",true);
                        go.putExtra("userId",user.getUid());
                        startActivity(go);
                    }else {
                        sendForum();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendForum() {
        if (condition){
            AddToHistory(user.getUid());
            friendlyMessages.clear();
            deleteOldForum();
            FriendlyMessage friendlyMessage = new FriendlyMessage("",user.getUid(),forum.toString(), mUsername, null,null,false,false,getDate());
            mMessagesDatabaseReference.push().setValue(friendlyMessage);
            sendDoctorNotificationsHelper();
            condition=false;
        }
    }

    private void sendDoctorNotificationsHelper() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctors");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getKey());
                }
                sendDoctorNotifications(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void sendDoctorNotifications(List<String> DocotrIds) {
        String doctorid;
        int loop;
        for (loop = 0;loop<DocotrIds.size();loop++){
            doctorid = DocotrIds.get(loop);
            notify=true;
            sendNotification("هناك طلب استشارة جديد",doctorid);
        }
    }

    private void deleteOldForum() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages").child(user.getUid());
        reference.removeValue();
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
                        Toast.makeText(ChatActivity.this, "photo uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        FriendlyMessage friendlyMessage = new FriendlyMessage("",user.getUid(),null, mUsername, downloadUri.toString(),null,false,false,getDate());
                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
                        notify = true;
                        sendNotification("صورة",doctorid);

                    } else {
                        // Handle failures
                        Toast.makeText(ChatActivity.this, "photo failed to upload", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ChatActivity.this, "audio uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        FriendlyMessage friendlyMessage = new FriendlyMessage("",user.getUid(),null, mUsername, null,downloadUri.toString(),false,false,getDate());
                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
                        notify = true;
                        sendNotification("تسجيل صوتي",doctorid);

                    } else {
                        // Handle failures
                        Toast.makeText(ChatActivity.this, "audio failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.new_forum:
                if (consultationEnd){      //consultation ended from doctor
                    deleteOldForum();
                    checkForum();
                }else {
                    //AddToHistory(user.getUid());
                    //deleteOldForum();
                    //checkForum();
                    launchForum(false);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchForum(boolean state) {
        Intent go = new Intent(ChatActivity.this,ForumActivity.class);
        go.putExtra("autoOpened",state);
        startActivity(go);
    }

    private void AddToHistory(String userId) {

        int loop ;
        DatabaseReference uniqueKey;

        if (friendlyMessages.size()!=0){
            uniqueKey = FirebaseDatabase.getInstance().getReference().child("PatientHistory").child(userId).push();

            for ( loop = 0;loop<friendlyMessages.size();loop++) {
                final FriendlyMessage item = friendlyMessages.get(loop);
                doconfirmation(item,uniqueKey);
            }
        }

    }
    private void doconfirmation(FriendlyMessage user,DatabaseReference random) {
        FriendlyMessage friendlyMessage = new FriendlyMessage(user.getDoctorid(), user.getUserid(), user.getText(),user.getName(),user.getPhotoUrl(),user.getRecordUrl(),user.getChecked(),user.getConsultationEnd(),getDate());
        random.push().setValue(friendlyMessage);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent go = new Intent(ChatActivity.this , ChooserActivity.class);
        startActivity(go);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //status("online");
        MyApplication.setPreferencesBoolean("userOnline",true);
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        //status("offline");
        MyApplication.setPreferencesBoolean("userOnline",false);
        detachDatabaseReadListener();
    }

    private void onSignedInInitialize (String username){
        mUsername=username;
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(user.getUid());
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup (){
        mUsername = ANONYMOUS;
    }

    private void attachDatabaseReadListener(){
        if (mChildEventListener ==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    friendlyMessages.add(friendlyMessage);

                    Log.e("size", String.valueOf(friendlyMessages.size()));
                    if (friendlyMessages.size()==0){
                        launchForum(true);
                    }else {
                        sendForum();
                    }
                    adapter = new MessageAdapter(ChatActivity.this,friendlyMessages);
                    recyclerView.setAdapter(adapter);
                    mProgressBar.setVisibility(ProgressBar.GONE);

                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }

    private void detachDatabaseReadListener(){
        if (mChildEventListener != null){
            friendlyMessages.clear();
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null ;
        }
    }
}