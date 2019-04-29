package com.cyk.camp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EndAdminActivity extends AppCompatActivity {

    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_admin);
    }

    public void reset(View view){
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();
        myRef.child("status").setValue(1);
        myRef.child("teams").removeValue();
        myRef.child("quests").removeValue();

        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        return;
    }
    public void resetTeam(View view){
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();
        myRef.child("status").setValue(2);
        myRef.child("teams").removeValue();

        Intent myIntent = new Intent(this, WaitAdminActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        return;
    }
}
