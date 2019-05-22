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

public class QuestEditActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private Context context = this;
    private ValueEventListener questListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            int n = (int) dataSnapshot.getChildrenCount();
            int cnt = 0;
            Quest[] list = new Quest[n];
            //update quest list
            for (DataSnapshot teamSnapshot: dataSnapshot.getChildren()) {
                Quest q = teamSnapshot.getValue(Quest.class);
                list[cnt] = q;
                cnt++;
            }

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, str);

            ListView listView = (ListView) findViewById(R.id.listview_quest);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            QuestAdapter adapter = new QuestAdapter(list, inflater);
            listView.setAdapter(adapter);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_edit);

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        myRef.child("quests").addValueEventListener(questListener);

    }

    public void last(View view){
        //Intent myIntent = new Intent(this, QuestEditActivity.class);
        //startActivity(myIntent);
        //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        return;
    }

    public void newQuest(View view){
        Intent myIntent = new Intent(this, AddQuestActivity.class);
        startActivity(myIntent);
        //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //finish();
    }
    public void deleteQuest(View view){
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        myRef.child("quests").removeValue();
    }

    public void complete(View view){
        //change status

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference().child("status");
        myRef.setValue(2);

        Intent myIntent = new Intent(this, WaitAdminActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        return;
    }

}