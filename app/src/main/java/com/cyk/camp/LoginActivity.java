package com.cyk.camp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cyk.camp.MapsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseDatabase.getInstance();


        //completed
        if(getSharedPreferences("data", MODE_PRIVATE)
                .getBoolean("complete", false)){
            Intent myIntent = new Intent(this, CompleteActivity.class);
            startActivity(myIntent);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        }
        //already joined
        else if(getSharedPreferences("data", MODE_PRIVATE)
                .getBoolean("has_team", false)){
            //start map activity
            Intent myIntent = new Intent(this, MapsActivity.class);
            startActivity(myIntent);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        }

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
    }

    public void newTeam(View view){
        EditText et = findViewById(R.id.et_team_name);
        String s = et.getText().toString();
        Log.d("tag_s", s);
        if(s.length() == 0){
            Toast.makeText(this, "請填入隊名", Toast.LENGTH_SHORT).show();
        }
        else if(s.equals("admin")){
            Intent myIntent = new Intent(this, AdminActivity.class);
            startActivity(myIntent);
            //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //finish();
            Log.d("tag_admin", "admin mode");
        }
        else {
            //push team info to db
            Team t = new Team(s);
            DatabaseReference myRef = db.getReference();
            String key = myRef.child("teams").push().getKey();
            if(key != null) myRef.child("teams").child(key).setValue(t);



            //store the team key
            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
            pref.edit().putString("team_key", key)
                    .putBoolean("has_team", true)
                    .apply();

            //start map activity
            Intent myIntent = new Intent(this, MapsActivity.class);
            startActivity(myIntent);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
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

