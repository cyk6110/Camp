package com.cyk.camp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EndPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_player);
        if(getSharedPreferences("data", MODE_PRIVATE)
                .getBoolean("complete", false)) {
            TextView tv = findViewById(R.id.tv_end_text);
            tv.setText("你完成了所有關卡！");
        }

    }
}
