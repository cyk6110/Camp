package com.cyk.camp;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WaitAdminActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private Context context = this;


    private ValueEventListener newTeamListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            int n = (int) dataSnapshot.getChildrenCount();
            int cnt = 0;
            String[] str = new String[n];
            //update team list
            for (DataSnapshot teamSnapshot: dataSnapshot.getChildren()) {
                Team t = teamSnapshot.getValue(Team.class);
                str[cnt] = t.name;
                cnt++;
                //Log.d("tag_get_answer", q.answer);
            }


            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout., str);

            ListView listView = (ListView) findViewById(R.id.listview_team);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WaitAdminAdapter adapter = new WaitAdminAdapter(str, inflater);
            listView.setAdapter(adapter);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_admin);

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();
        //監聽team加入狀況
        myRef.child("teams").addValueEventListener(newTeamListener);
    }

    public void start(View view){
        //change status

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference().child("status");
        myRef.setValue(3);

        Intent myIntent = new Intent(this, MonitorActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        return;
    }

    //加listener 持續更新team加入狀況





}
