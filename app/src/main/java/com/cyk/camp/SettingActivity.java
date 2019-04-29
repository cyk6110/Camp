package com.cyk.camp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingActivity extends AppCompatActivity {

    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void next(View view){

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        EditText et_name, et_radius, et_text, et_password;
        et_name = findViewById(R.id.et_setting_name);
        et_radius = findViewById(R.id.et_setting_radius);
        et_text = findViewById(R.id.et_setting_text);
        et_password = findViewById(R.id.et_setting_password);

        String name = et_name.getText().toString();
        String radius = et_radius.getText().toString();
        String text = et_text.getText().toString();
        String password = et_password.getText().toString();

        if(name.length() > 0 && password.length() > 0) {

            myRef.child("name").setValue(name);

            if(radius.length() > 0)
                myRef.child("radius").setValue(Integer.parseInt(radius));
            else
                myRef.child("radius").setValue(10);

            if(text.length() > 0)
                myRef.child("text").setValue(text);

            myRef.child("password").setValue(password);

        }

        


        Intent myIntent = new Intent(this, QuestEditActivity.class);
        startActivity(myIntent);
        //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //finish();
        //return;
    }
}
