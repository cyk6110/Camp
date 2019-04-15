package com.cyk.camp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void next(View view){
        Intent myIntent = new Intent(this, QuestEditActivity.class);
        startActivity(myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
