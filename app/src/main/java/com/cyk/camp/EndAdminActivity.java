package com.cyk.camp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class EndAdminActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private ValueEventListener rankListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            int n = (int) dataSnapshot.getChildrenCount();
            int cnt = 0;
            Rank[] list = new Rank[n];

            for (DataSnapshot rankSnapshot: dataSnapshot.getChildren()) {
                Rank r = rankSnapshot.getValue(Rank.class);
                list[cnt] = r;
                cnt++;
            }

            Arrays.sort(list, new RankComparator());

            ListView listView = (ListView) findViewById(R.id.listview_end_admin);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            EndAdminAdapter adapter = new EndAdminAdapter(list, inflater);
            listView.setAdapter(adapter);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_admin);

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        myRef.child("teams_complete").addListenerForSingleValueEvent(rankListener);
    }

    public void reset(View view){
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        myRef.removeValue();
        myRef.child("status").setValue(1);

        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        return;
    }
    public void resetTeam(View view){
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();
        myRef.child("status").setValue(2);
        myRef.child("teams").removeValue();
        myRef.child("start_time").removeValue();
        myRef.child("teams_complete").removeValue();

        Intent myIntent = new Intent(this, WaitAdminActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        return;
    }
}

class RankComparator implements Comparator<Rank> {
    @Override
    public int compare(Rank a, Rank b) {
        return (a.time < b.time ? -1 : a.time == b.time ? 0 : 1);
        //return Long.compare(a.time, b.time);
    }
}
