package com.mocomp.developer.medicbooks.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.activity.ChatActivity;
import com.mocomp.developer.medicbooks.activity.HistoryActivity;
import com.mocomp.developer.medicbooks.activity.HistoryChatActivity;
import com.mocomp.developer.medicbooks.models.content.FriendlyMessage;
import com.mocomp.developer.medicbooks.models.content.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mUserid;
    private List<FriendlyMessage> mUser;
    private FirebaseAuth mFirebaseAuth;
    private List<FriendlyMessage>list = new ArrayList<>();
    private FriendlyMessage cc;
    private boolean con;
    private List<Boolean> cond = new ArrayList<>();

    public HistoryAdapter(Context mContext, List<String> list, List<FriendlyMessage> mUser){
        this.mContext = mContext;
        this.mUserid = list;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sec_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "Cairo-Regular.ttf");
        mFirebaseAuth = FirebaseAuth.getInstance();
//        final FriendlyMessage user = mUsers.get(position);
        final String key= mUserid.get(position);

        int i= position+1;

        holder.title.setText("استشارة رقم "+i);
        holder.title.setTypeface(typeface);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PatientHistory").child(mFirebaseAuth.getUid()).child(key);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FriendlyMessage user = snapshot.getValue(FriendlyMessage.class);
                    assert user != null;

                    list.add(user);

                }
                cc =list.get(0);
                if (cc.getConsultationEnd()){   //consultation is ended
                    //Log.e("condition", String.valueOf(true));
                    holder.caseState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_baseline_lock_24));
                    con=true;

                }else {                   //consultation is in process
                    //Log.e("condition", String.valueOf(false));
                    holder.caseState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_baseline_lock_open_24));
                    con=false;
                }
                cond.add(con);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("consultationended", String.valueOf(cond.get(position)));
                        Intent go = new Intent(mContext, HistoryChatActivity.class);
                        go.putExtra("consultationEnd",cond.get(position));
                        go.putExtra("doctorid",cc.getDoctorid());
                        go.putExtra("key",key);
                        mContext.startActivity(go);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    @Override
    public int getItemCount() {
        return mUserid.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        CircleImageView caseState;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.post_title);
            caseState = itemView.findViewById(R.id.caseState);

        }
    }
}
