package com.cyk.camp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonitorActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private Context context = this;
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
            MonitorAdapter adapter = new MonitorAdapter(list, inflater);
            listView.setAdapter(adapter);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        myRef.child("teams").addValueEventListener(newTeamListener);
    }

    public void endGame(View view){
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference().child("status");
        myRef.setValue(4);

        Intent myIntent = new Intent(this, EndAdminActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        return;
    }
}
