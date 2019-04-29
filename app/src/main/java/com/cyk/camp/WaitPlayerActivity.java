package com.cyk.camp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitPlayerActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private Context context = this;

    private ValueEventListener statusListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if(dataSnapshot.getValue(Integer.class) == 3){

                //跳轉到遊戲畫面
                Intent myIntent = new Intent(context, MapsActivity.class);
                startActivity(myIntent);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    }




}
