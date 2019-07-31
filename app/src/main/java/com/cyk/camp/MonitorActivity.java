package com.cyk.camp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonitorActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private Context context = this;
    TextView tv_time;
    long startTime = 0, millis = 0;
    private int number_of_quests = -1;
    private ValueEventListener newTeamListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            int n = (int) dataSnapshot.getChildrenCount();
            int cnt = 0;
            //String[] str = new String[n];
            Team[] list = new Team[n];
            //更新各隊狀態
            for (DataSnapshot teamSnapshot: dataSnapshot.getChildren()) {
                Team t = teamSnapshot.getValue(Team.class);
                //str[cnt] = t.name + " 在第 " + (t.current_quest + 1) + " 關，共完成 " + (t.quest_number) + " 關";
                list[cnt] = t;
                cnt++;
            }

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, str);

            ListView listView = (ListView) findViewById(R.id.listview_monitor);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            MonitorAdapter adapter = new MonitorAdapter(list, inflater, number_of_quests);
            listView.setAdapter(adapter);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = (seconds / 60) % 60;
            int hour = seconds / 3600;
            seconds = seconds % 60;

            tv_time.setText(String.format("%d:%02d:%02d", hour, minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        myRef.child("teams").addValueEventListener(newTeamListener);

        tv_time = findViewById(R.id.tv_timer);

        startTime = getSharedPreferences("data", MODE_PRIVATE)
                .getLong("start_time", -1);
        number_of_quests = getSharedPreferences("data", MODE_PRIVATE)
                .getInt("number_of_quests", -1);

        myRef.child("start_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                startTime = snapshot.getValue(Long.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child("quests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                number_of_quests = (int)snapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        timerHandler.postDelayed(timerRunnable, 0);

    }

    public void endGame(View view){
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference().child("status");
        myRef.setValue(4);

        Intent myIntent = new Intent(this, EndAdminActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        return;
    }
}
