package com.mocomp.developer.medicbooks.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.adapters.ContentAdapter;
import com.mocomp.developer.medicbooks.data.constant.AppConstant;
import com.mocomp.developer.medicbooks.data.sqlite.FavoriteDbController;
import com.mocomp.developer.medicbooks.listeners.ListItemClickListener;
import com.mocomp.developer.medicbooks.models.content.Contents;
import com.mocomp.developer.medicbooks.models.content.Item;
import com.mocomp.developer.medicbooks.models.favorite.FavoriteModel;
import com.mocomp.developer.medicbooks.utility.ActivityUtilities;
import com.mocomp.developer.medicbooks.utility.AdsUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<Contents> mContentList;
    private ArrayList<Contents> mSearchList;
    private ContentAdapter mAdapter = null;
    private RecyclerView mRecycler;


    // Favourites view
    private List<FavoriteModel> mFavoriteList;
    private List<Item> mUsers;
    private FavoriteDbController mFavoriteDbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        //initListener();
    }

    private void initVar() {
        mActivity = SearchActivity.this;
        mContext = mActivity.getApplicationContext();

        mContentList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
        mSearchList = new ArrayList<>();
        mUsers = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_item_list);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.search));
    }
    private void searchUsers(String s) {

        Query query = FirebaseDatabase.getInstance().getReference("articles").orderByChild("maintitle")
                .startAt(s)
                .endAt(s+"\uf8ff");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Item user = snapshot.getValue(Item.class);

                    assert user != null;

                    mUsers.add(user);
                    mAdapter = new ContentAdapter(mContext, mActivity, mUsers);
                    mRecycler.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void initFunctionality() {
        loadData();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
    }

    public void initListener() {
        // recycler list item click listener
        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Contents model = mSearchList.get(position);

                switch (view.getId()) {
                    case R.id.card_view_top:
                        ActivityUtilities.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, model, false);
                        break;
                    default:
                        break;
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        }, 200);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                //some texts here
                showLoader();
                mSearchList.clear();
                searchUsers(newText);
               /* for (int i = 0; i < mContentList.size(); i++) {
                    if (mContentList.get(i).getDetails().toLowerCase().contains(newText)) {
                        mSearchList.add(mContentList.get(i));
                    }
                }*/

//                mAdapter.notifyDataSetChanged();

                if (!mSearchList.isEmpty() && mSearchList.size() > 0) {
                    hideLoader();
                } else {
                    showEmptyView();
                }

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void loadJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstant.CONTENT_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parseJson(sb.toString());
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

}
