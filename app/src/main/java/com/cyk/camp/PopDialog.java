package com.cyk.camp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import static android.view.View.GONE;

public class PopDialog extends DialogFragment {

    public View view;
    public String question, key;
    public int answer = 0;

    private FirebaseDatabase db;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    public interface MyDialogFragmentListener {
        public void onReturnValue(String foo);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.quest_dialog, null);

        Bundle mArgs = getArguments();
        question = mArgs.getString("question");
        key = mArgs.getString("quest_key");

        Log.d("tag_question", question);

        TextView tv = view.findViewById(R.id.tv_quest_dialog_question);
        final EditText editText = view.findViewById(R.id.et_quest_dialog_answer);
        Spinner sp = view.findViewById(R.id.dialog_spinner_answer);
        final ImageView img_dialog = view.findViewById(R.id.img_dialog);

        final StorageReference main_img = storageRef.child("images/" + key + ".jpg");


        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        //撈圖
        GlideApp.with(this)
                .load(main_img)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("tag_load", main_img.getPath());
                        img_dialog.setVisibility(GONE);
                        view.findViewById(R.id.loadingPanel).setVisibility(GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("tag_load", "ready");
                        if(resource.getIntrinsicHeight() > resource.getIntrinsicWidth())
                            img_dialog.getLayoutParams().height = 300;
                        view.findViewById(R.id.loadingPanel).setVisibility(GONE);
                        return false;
                    }
                })
                .into(img_dialog);

        //撈影片
        myRef.child("quest_videoid").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final YouTubePlayerView youTubePlayerView = view.findViewById(R.id.youtube_player_dialog);
                youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                    @Override
                    public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                        String videoid = dataSnapshot.getValue(String.class);
                        if(videoid != null) {
                            youTubePlayerView.setVisibility(View.VISIBLE);
                            youTubePlayer.loadVideo(videoid, 0);
                        }
                        else
                            Log.d("tag_video", "no video");
                    }
                });
                getLifecycle().addObserver(youTubePlayerView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if(question.length() > 15 && question.substring(0,15).equals("multiple_choice")){
            // 選擇題

            sp.setVisibility(View.VISIBLE);
            // 設spinner選項
            // 設spinner listener
            ArrayAdapter<CharSequence> adapter_answer = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_answer,
                    android.R.layout.simple_spinner_item);
            adapter_answer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter_answer);

            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    answer = position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            String[] q = question.split("#");
            String s = getString(R.string.mul_choice, q[1], q[2], q[3], q[4], q[5]);
            tv.setText(s);
        }
        else{
            tv.setText(question);
            editText.setVisibility(View.VISIBLE);
        }
        builder.setView(view)
            .setPositiveButton("答題", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    if(question.length() < 15 || !question.substring(0,15).equals("multiple_choice")) {

                        if (!editText.getText().toString().matches("")) {
                            //Log.d("tag_dialog_answer", answer);

                            MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                            activity.onReturnValue(editText.getText().toString());
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "請輸入答案", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                            toast.show();
                        }
                    }
                    else {
                        MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                        activity.onReturnValue("multiple_choice#" + String.valueOf(answer));
                    }

                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    PopDialog.this.getDialog().cancel();
                }
            });

        // 3. Get the AlertDialog from create()
        return builder.create();
    }
}

