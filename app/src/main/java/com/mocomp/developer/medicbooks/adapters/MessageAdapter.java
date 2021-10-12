package com.mocomp.developer.medicbooks.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.activity.ImageSlider;
import com.mocomp.developer.medicbooks.data.preference.AppPreference;
import com.mocomp.developer.medicbooks.models.content.FriendlyMessage;
import com.mocomp.developer.medicbooks.utility.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<FriendlyMessage> mUsers;
    private MediaPlayer mediaPlayer;
    private boolean wasplaying;
    private AudioManager mAudioManager;
    FirebaseUser fuser;


    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //play.setVisibility(View.VISIBLE);
            //pause.setVisibility(View.GONE);
            //seekBar.setProgress(0);
            //releaseMediaPlayer();
        }
    };
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.
                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                if (mediaPlayer != null){
                    if (mediaPlayer.isPlaying()){
                        wasplaying = true;
                    }
                    mediaPlayer.pause();
                    //play.setVisibility(ImageButton.VISIBLE);
                    //pause.setVisibility(ImageButton.GONE);
                }

            }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                //the volume of song is decreased when notifications receved
                if (mediaPlayer.isPlaying()){
                    wasplaying = true;
                }
                mediaPlayer.setVolume(0.1f, 0.1f);

            }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                if (wasplaying){
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    mediaPlayer.start();
                    wasplaying = false;
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                //releaseMediaPlayer();
                mediaPlayer.pause();
                //play.setVisibility(ImageButton.VISIBLE);
                //pause.setVisibility(ImageButton.GONE);
            }
        }
    };


    public MessageAdapter(Context mContext, List<FriendlyMessage> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final FriendlyMessage user = mUsers.get(position);
        boolean isText = user.getText() != null;
        boolean isPhoto = user.getPhotoUrl() != null;
        boolean isrecord = user.getRecordUrl() != null;



        if (isPhoto) {
            ArrayList<String> images = new ArrayList<>();
            String[] imgOther = user.getPhotoUrl().toString().split("\n");
            images.addAll(Arrays.asList(imgOther));
            holder.playercard.setVisibility(View.GONE);
            holder.messageTextView.setVisibility(View.GONE);
            holder.photoImageView.setVisibility(View.VISIBLE);
            holder.seekbarlayout.setVisibility(View.GONE);
            Glide.with(holder.photoImageView.getContext())
                    .load(user.getPhotoUrl())
                    .into(holder.photoImageView);
            holder.photoImageView.setOnClickListener(v -> {
                Intent go = new Intent(mContext, ImageSlider.class);
                go.putStringArrayListExtra("imageUri",images);
                mContext.startActivity(go);
            });
        }
        if (isrecord){
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    holder.handler.removeCallbacks(holder.mUpdateTimeTask);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mediaPlayer != null){
                        holder.handler.removeCallbacks(holder.mUpdateTimeTask);
                        int totalDuration = mediaPlayer.getDuration();
                        int currentPosition = holder.utils.progressToTimer(seekBar.getProgress(), totalDuration);

                        // forward or backward to certain seconds
                        mediaPlayer.seekTo(currentPosition);

                        // update timer progress again
                        holder.updateProgressBar();
                    }
                }
            });
            holder.seekbarlayout.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.messageTextView.setVisibility(View.GONE);
            holder.pause.setVisibility(View.GONE);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.play.setVisibility(View.GONE);
                    holder.pause.setVisibility(View.VISIBLE);
                    mediaPlayer = new MediaPlayer();
                    try {
                        if (user.getRecordUrl() != null) {
                            mediaPlayer.setDataSource(user.getRecordUrl());
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    int result = holder.maudiomanger.requestAudioFocus(mOnAudioFocusChangeListener,
                                            AudioManager.STREAM_MUSIC,
                                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                                    if (result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                                        //mediaPlayer.setOnCompletionListener(onCompletionListener);
                                        if (mediaPlayer != null){
                                            mediaPlayer.start();
                                            String timer = millisecodtotimer(mediaPlayer.getDuration());
                                            holder.duration.setText(timer);
                                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                @Override
                                                public void onCompletion(MediaPlayer mp) {
                                                    holder.play.setVisibility(ImageButton.VISIBLE);
                                                    holder.pause.setVisibility(ImageButton.GONE);
                                                    holder.seekBar.setProgress(0);
                                                    //releaseMediaPlayer();

                                                }

                                            });
                                        }
                                    }
                                    holder.togglePlayPause(mediaPlayer , holder.play , holder.pause);
                                }
                            });
                            mediaPlayer.prepareAsync();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            holder.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.play.setVisibility(View.VISIBLE);
                    holder.pause.setVisibility(View.GONE);
                }
            });
        }
        if (isText){
            holder.playercard.setVisibility(View.GONE);
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.seekbarlayout.setVisibility(View.GONE);
            holder.messageTextView.setText(user.getText());
        }

        FriendlyMessage usertest = mUsers.get(0);

        if (usertest.getChecked()){   //patient is accepted
            Intent intent = new Intent("patientCondition");
            intent.putExtra("condition",true);
            intent.putExtra("consultationEnd",usertest.getConsultationEnd());
            intent.putExtra("doctorid",usertest.getDoctorid());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }else {                   //patient is denied
            Intent intent = new Intent("patientCondition");
            intent.putExtra("condition",false);
            intent.putExtra("doctorid","");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        if (AppPreference.getInstance(mContext).getTextSize().equals(mContext.getResources().getString(R.string.small_text))) {
            holder.messageTextView.setTextSize(15);
        } else if (AppPreference.getInstance(mContext).getTextSize().equals(mContext.getResources().getString(R.string.default_text))) {
            holder.messageTextView.setTextSize(18);
        } else if (AppPreference.getInstance(mContext).getTextSize().equals(mContext.getResources().getString(R.string.large_text))) {
            holder.messageTextView.setTextSize(22);
        }

        holder.authorTextView.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("uid",fuser.getUid());
        Log.e("listid",mUsers.get(position).getUserid());
        if (mUsers.get(position).getUserid().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView pause;
        private ImageView play;
        public SeekBar seekBar;
        private TextView duration;
        private TextView counter;
        private ImageView photoImageView;
        private TextView messageTextView;
        private TextView authorTextView;
        private LinearLayout seekbarlayout;
        private Handler handler;
        private Utilities utils;
        private CardView playercard;
        private AudioManager maudiomanger = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);


        private void togglePlayPause(final MediaPlayer mediaPlayer, final ImageView play , final ImageView pause ) {

            updateProgressBar();
            play.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (mediaPlayer!=null){
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                            play.setVisibility(ImageButton.VISIBLE);
                            pause.setVisibility(ImageButton.GONE);
                        }else {
                            int result = maudiomanger.requestAudioFocus(mOnAudioFocusChangeListener,
                                    AudioManager.STREAM_MUSIC,
                                    AudioManager.AUDIOFOCUS_GAIN);
                            if (result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                                mediaPlayer.setOnCompletionListener(onCompletionListener);
                                mediaPlayer.start();
                                mediaPlayer.setOnCompletionListener(onCompletionListener);
                                play.setVisibility(ImageButton.GONE);
                                pause.setVisibility(ImageButton.VISIBLE);
                            }
                        }
                    }
                }
            });


            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (mediaPlayer != null){
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                            play.setVisibility(ImageButton.VISIBLE);
                            pause.setVisibility(ImageButton.GONE);
                        }else {
                            mediaPlayer.start();
                            play.setVisibility(ImageButton.GONE);
                            pause.setVisibility(ImageButton.VISIBLE);
                        }
                    }
                }
            });

        }

        public void updateProgressBar() {
            handler.postDelayed(mUpdateTimeTask, 100);
        }
        /**
         * Background Runnable thread
         * */
        private Runnable mUpdateTimeTask = new Runnable() {
            @SuppressLint("SetTextI18n")
            public void run() {
                if (mediaPlayer != null){
                    long totalDuration = mediaPlayer.getDuration();
                    long currentDuration = mediaPlayer.getCurrentPosition();
                    // Updating progress bar
                    counter.setText(""+utils.milliSecondsToTimer(currentDuration));

                    int progress = (utils.getProgressPercentage(currentDuration, totalDuration));
                    seekBar.setProgress(progress);

                    // Running this thread after 100 milliseconds
                    handler.postDelayed(this, 100);
                }
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);

            mAudioManager= (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            playercard = itemView.findViewById(R.id.playercard);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            messageTextView =  itemView.findViewById(R.id.messageTextView);
            messageTextView.setLinksClickable(true);
            Linkify.addLinks(messageTextView, Linkify.WEB_URLS);
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
            authorTextView =  itemView.findViewById(R.id.nameTextView);
            seekbarlayout =  itemView.findViewById(R.id.seekBarlayout);
            play = itemView.findViewById(R.id.play);
            pause = itemView.findViewById(R.id.pause);
            seekBar = itemView.findViewById(R.id.seekBar);
            counter = itemView.findViewById(R.id.counter);
            duration = itemView.findViewById(R.id.durration);
            handler = new Handler();
            utils = new Utilities();
        }
    }


    public String millisecodtotimer(long miliseconds){
        String finaltimerstring ="";
        String secondsString;

        int hours = (int)(miliseconds/(1000*60*60));
        int minutes= (int)(miliseconds%(1000*60*60))/(1000*60);
        int seconds = (int)((miliseconds%(1000*60*60))%(1000*60)/1000);



        if (hours>0) {
            finaltimerstring = hours + ":";
        }

        if (seconds<10){
            secondsString = "0"+seconds;
        }else {
            secondsString= ""+seconds;
        }
        finaltimerstring=finaltimerstring+minutes+":"+secondsString;


        return finaltimerstring;
    }

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;
            // Abandon audio focus when playback complete
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }
}
