package com.cyk.camp;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class WaitPlayerActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private Context context = this;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private ValueEventListener statusListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if(dataSnapshot.getValue(Integer.class) == 3){

                //跳轉到遊戲畫面
                Intent myIntent = new Intent(context, MapsActivity.class);
                startActivity(myIntent);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                return;
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_player);

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        String name = getSharedPreferences("data", MODE_PRIVATE).getString("team_name", "隊名");

        TextView tv = findViewById(R.id.tv_team_name);
        tv.setText(name);

        //加listener監聽狀態
        myRef.child("status").addValueEventListener(statusListener);

        //撈遊戲名稱&敘述來給玩家看
        myRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                ((TextView)findViewById(R.id.game_name)).setText(name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child("description").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String description = snapshot.getValue(String.class);
                ((TextView)findViewById(R.id.game_description)).setText(description);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final StorageReference main_img = storageRef.child("images/main.jpg");
        final ImageView img = findViewById(R.id.img_main_wait_player);

        Log.d("tag_url", main_img.getPath());

        GlideApp.with(this)
                .load(main_img)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("tag_load", "load failed");
                        img.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("tag_load", "ready");
                        if(resource.getIntrinsicHeight() > resource.getIntrinsicWidth())
                            img.getLayoutParams().height = 300;
                        return false;
                    }
                })

                .into(img);


    }





}
