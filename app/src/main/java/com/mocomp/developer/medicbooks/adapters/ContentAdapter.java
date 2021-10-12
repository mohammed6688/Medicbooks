package com.mocomp.developer.medicbooks.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.activity.DetailsActivity;
import com.mocomp.developer.medicbooks.listeners.ListItemClickListener;
import com.mocomp.developer.medicbooks.models.content.Contents;
import com.mocomp.developer.medicbooks.models.content.Item;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private List<Item> mContentList;
    private ListItemClickListener mItemClickListener;

    public ContentAdapter(Context mContext, Activity mActivity, List<Item> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    public void setItemClickListener(ListItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recycler, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private ImageView imgPost;
        private TextView tvTitle, tvDescription;
        private ImageButton btnFav;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            cardView = (CardView) itemView.findViewById(R.id.card_view_top);
            imgPost = (ImageView) itemView.findViewById(R.id.post_img);
            tvTitle = (TextView) itemView.findViewById(R.id.title_text);
            tvDescription = (TextView) itemView.findViewById(R.id.description_text);
            btnFav = (ImageButton) itemView.findViewById(R.id.btn_fav);

            btnFav.setOnClickListener(this);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mContentList ? mContentList.size() : 0);

    }

    @Override
    public void onBindViewHolder(ContentAdapter.ViewHolder mainHolder, int position) {
        final Item model = mContentList.get(position);

        // setting data over views
        String imgUrl = model.getPhotoUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgPost);
        }

        /*if (model.isFavorite()) {
            mainHolder.btnFav.setImageResource(R.drawable.ic_fav);
        } else {
            mainHolder.btnFav.setImageResource(R.drawable.ic_un_fav);
        }*/


        mainHolder.tvTitle.setText(Html.fromHtml(model.getMaintitle()));
        mainHolder.tvDescription.setText(Html.fromHtml(model.getSmalldesc()));

        mainHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent singleActivity = new Intent(mContext, DetailsActivity.class);
                singleActivity.putExtra("maintitle", String.valueOf(model.getMaintitle()));
                singleActivity.putExtra("smalldesc", String.valueOf(model.getSmalldesc()));
                singleActivity.putExtra("photo", String.valueOf(model.getPhotoUrl()));
                singleActivity.putExtra("desc", String.valueOf(model.getDesc()));
                singleActivity.putExtra("inertitel", String.valueOf(model.getInertitle()));

                singleActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(singleActivity);
            }
        });

    }
}