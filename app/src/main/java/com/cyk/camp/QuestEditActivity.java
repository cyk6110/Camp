package com.cyk.camp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Struct;

public class QuestEditActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private Context context = this;
    private String[] key_list;
    private int n;
    private ValueEventListener questListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            n = (int) dataSnapshot.getChildrenCount();
            int cnt = 0;
            Quest[] list = new Quest[n];
            key_list = new String[n];
            //update quest list
            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                Quest q = snapshot.getValue(Quest.class);
                list[cnt] = q;
                key_list[cnt] = snapshot.getKey();
                cnt++;
            }

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, str);

            ListView listView = (ListView) findViewById(R.id.listview_quest);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            QuestAdapter adapter = new QuestAdapter(list, inflater);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView,
                                        View view,
                                        final int position,
                                        long l) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("要做什麼呢？")
                            .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseReference myRef = db.getReference();
                                    myRef.child("quests").child(key_list[position]).removeValue();
                                }
                            })
                            .setNegativeButton("什麼也不做", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("確定要刪除嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = db.getReference();

                        myRef.child("quests").removeValue();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void complete(View view){

        //push start time

        long startTime = System.currentTimeMillis();
        db = FirebaseDatabase.getInstance();

        //還有quest數量（monitor判斷是否完成用
        db.getReference().child("start_time").setValue(startTime);
        getSharedPreferences("data", MODE_PRIVATE).edit()
                .putInt("number_of_quests", n)
                .apply();

        //change status

        db.getReference().child("status").setValue(2);

        Intent myIntent = new Intent(this, WaitAdminActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        return;
    }

}