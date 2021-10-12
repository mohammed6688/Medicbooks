package com.mocomp.developer.medicbooks.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.adapters.ContentAdapter;
import com.mocomp.developer.medicbooks.data.constant.AppConstant;
import com.mocomp.developer.medicbooks.data.sqlite.FavoriteDbController;
import com.mocomp.developer.medicbooks.data.sqlite.NotificationDbController;
import com.mocomp.developer.medicbooks.listeners.ListItemClickListener;
import com.mocomp.developer.medicbooks.models.content.Contents;
import com.mocomp.developer.medicbooks.models.content.Item;
import com.mocomp.developer.medicbooks.models.favorite.FavoriteModel;
import com.mocomp.developer.medicbooks.models.notification.NotificationModel;
import com.mocomp.developer.medicbooks.utility.ActivityUtilities;
import com.mocomp.developer.medicbooks.utility.AdsUtilities;
import com.mocomp.developer.medicbooks.utility.AppUtilities;
import com.mocomp.developer.medicbooks.utility.CustomizedExceptionHandler;
import com.mocomp.developer.medicbooks.utility.RateItDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;
    Typeface typeface;

    private RelativeLayout mNotificationView;
    private ImageButton mImgBtnSearch;

    private ArrayList<Contents> mContentList;
    private ContentAdapter mAdapter = null;
    private RecyclerView mRecycler;

    // Favourites view
    private List<FavoriteModel> mFavoriteList;
    private List<Item> mUsers;
    private FavoriteDbController mFavoriteDbController;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(
                "/mnt/sdcard/"));

        RateItDialogFragment.show(this, getSupportFragmentManager());

        initVar();
        initView();
        loadData();
        initListener();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newNotificationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register broadcast receiver
        IntentFilter intentFilter = new IntentFilter(AppConstant.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

        initNotification();

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    // received new broadcast
    private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
        }
    };


    @Override
    public void onBackPressed() {

        startActivity(new Intent(MainActivity.this, ChooserActivity.class));
        finish();
    }

    private void initVar() {
        mActivity = MainActivity.this;
        mContext = getApplicationContext();

        mUsers = new ArrayList<>();
        mContentList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        typeface = Typeface.createFromAsset(getAssets(), "Cairo-Black.ttf");
        progressBar = findViewById(R.id.progress_par);
        progressBar.setVisibility(View.VISIBLE);
        mNotificationView = (RelativeLayout) findViewById(R.id.notificationView);
        mImgBtnSearch = (ImageButton) findViewById(R.id.imgBtnSearch);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        readData();
        initToolbar(true);
        //initDrawer();
        initLoader();
    }

    private void readData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("articles");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Item user = snapshot.getValue(Item.class);
                    assert user != null;

                    mUsers.add(user);

                    mAdapter = new ContentAdapter(mContext, mActivity, mUsers);
                    mRecycler.setAdapter(mAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void initListener() {
        //notification view click listener
        mNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Search button click listener
        mImgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, SearchActivity.class, false);
            }
        });


    }

    private void loadData() {
        showLoader();

        // Initialize Favorite Database
        mFavoriteDbController = new FavoriteDbController(mContext);
        mFavoriteList.addAll(mFavoriteDbController.getAllData());

        //loadJson();

        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    private void parseJson(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray1 = jsonObjMain.getJSONArray(AppConstant.JSON_KEY_ITEMS);

            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObj = jsonArray1.getJSONObject(i);

                String title = jsonObj.getString(AppConstant.JSON_KEY_TITLE);
                String subTitle = jsonObj.getString(AppConstant.JSON_KEY_SUB_TITLE);
                String imageUrl = jsonObj.getString(AppConstant.JSON_KEY_IMAGE_URL);
                String details = jsonObj.getString(AppConstant.JSON_KEY_DETAILS);

                // Check for favorite
                boolean isFavorite = false;
                for (int j = 0; j < mFavoriteList.size(); j++) {
                    if (mFavoriteList.get(j).getTitle().equals(title)) {
                        isFavorite = true;
                        break;
                    }
                }

                mContentList.add(new Contents(title, subTitle, imageUrl, details, isFavorite));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        hideLoader();
        mAdapter.notifyDataSetChanged();
    }

    public void initNotification() {
        NotificationDbController notificationDbController = new NotificationDbController(mContext);
        TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.INVISIBLE);

        ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

        if (notiArrayList != null && !notiArrayList.isEmpty()) {
            int totalUnread = notiArrayList.size();
            if (totalUnread > 0) {
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(totalUnread));
            } else {
                notificationCount.setVisibility(View.INVISIBLE);
            }
        }

    }

}