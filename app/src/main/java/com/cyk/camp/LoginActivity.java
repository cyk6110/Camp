package com.cyk.camp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cyk.camp.MapsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private int n = 0;
    private Team t;
    private Context context;
    private int status = 0;
    private String correct_game_name;
    private String correct_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance();
        context = this;
        final DatabaseReference myRef = db.getReference();

        //check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //permission granted

            Log.d("tag_permission", "granted");

        } else {
            //request permission.
            Log.d("tag_permission", "request");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }



        myRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                status = snapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                correct_game_name = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                correct_pw = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //completed
        if(getSharedPreferences("data", MODE_PRIVATE)
                .getBoolean("end", false))
            playerLoggedIn(true);
            //already joined
        else if(getSharedPreferences("data", MODE_PRIVATE)
                .getBoolean("has_team", false)) {
            playerLoggedIn(false);
            Log.d("tag_debug", "has team");
        }

        setContentView(R.layout.activity_login);

        EditText et_password = findViewById(R.id.et_login_password);
        et_password.setTransformationMethod(new PasswordTransformationMethod());
        et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void newTeam(View view){
        EditText et = findViewById(R.id.et_team_name);
        EditText et_password = findViewById(R.id.et_login_password);
        EditText et_game_name = findViewById(R.id.et_login_game);
        final String s = et.getText().toString();
        final String pw = et_password.getText().toString();
        final String game_name = et_game_name.getText().toString();

        Log.d("tag_s", s);
        if(s.length() == 0){
            Toast.makeText(this, "請填入隊名", Toast.LENGTH_SHORT).show();
        }
        else if(pw.length() == 0){
            Toast.makeText(this, "請輸入密碼", Toast.LENGTH_SHORT).show();
        }
        else if(s.equals("admin")){
            if(game_name.equals("admin") && pw.equals("admin")) {
                Intent myIntent;

                //if status = 1
                if (status == 1) {
                    myIntent = new Intent(this, SettingActivity.class);
                    startActivity(myIntent);
                }
                //if status = 2
                //waiting
                else if (status == 2) {
                    myIntent = new Intent(this, WaitAdminActivity.class);
                    startActivity(myIntent);
                }
                //if status = 3
                //game monitor
                else if (status == 3) {
                    myIntent = new Intent(this, MonitorActivity.class);
                    startActivity(myIntent);
                } else if (status == 4) {
                    myIntent = new Intent(this, EndAdminActivity.class);
                    startActivity(myIntent);
                }

                //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //finish();
            }

        }
        else { //new player
            //if status = 1
            //can't join

            //if status = 2
            if(status == 2) {

                //驗證密碼和遊戲名稱
                if (game_name.equals(correct_game_name) && pw.equals(correct_pw)) {

                    t = new Team(s);
                    final DatabaseReference myRef = db.getReference();
                    final String key = myRef.child("teams").push().getKey();

                    Query q = myRef.child("quests");
                    //get total number of quests
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            n = (int) snapshot.getChildrenCount();
                            Log.d("tag_children_count", String.valueOf(n));

                            //random shuffle
                            List<Integer> random = new ArrayList<>();

                            for (int i = 0; i < n; i++)
                                random.add(i);

                            Collections.shuffle(random);

                            t.current_quest = random.get(0);

                            //push team info to db
                            if (key != null) myRef.child("teams").child(key).setValue(t);

                            //store the team key & random order
                            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();

                            edit.putInt("total_quests", n);

                            for (int i = 0; i < n; i++) {
                                edit.putInt("order" + i, random.get(i));
                                Log.d("tag_order", Integer.toString(random.get(i)));
                            }

                            edit.putString("team_key", key)
                                    .putString("team_name", s)
                                    .putBoolean("has_team", true)
                                    .apply();

                            //start map activity
                            Intent myIntent = new Intent(context, WaitPlayerActivity.class);
                            startActivity(myIntent);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            return;

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }
            //if status = 3
            //can't join
        }

    }
    public void playerLoggedIn(boolean completed){


        Intent myIntent;

        //completed 已玩完，不管是否完成所有關卡
        if(completed){
            myIntent = new Intent(this, EndPlayerActivity.class);
            startActivity(myIntent);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            return;
        }
        //already joined
        else {

            //if status = 2
            //wait
            if(status == 2) {
                myIntent = new Intent(this, WaitPlayerActivity.class);
                startActivity(myIntent);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                return;
            }
            //if status = 3
            //start map activity
            else if(status == 3) {
                myIntent = new Intent(this, MapsActivity.class);
                startActivity(myIntent);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                return;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                    System.exit(0);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}

