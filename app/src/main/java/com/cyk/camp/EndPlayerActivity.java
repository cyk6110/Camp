package com.cyk.camp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_player);
        if(getSharedPreferences("data", MODE_PRIVATE)
                .getLong("time_spent", -1) != -1) {
            TextView tv_text = findViewById(R.id.tv_end_text);
            TextView tv_time = findViewById(R.id.tv_time);
            TextView tv_rank = findViewById(R.id.tv_rank);
            tv_text.setText("你完成了所有關卡！");

            long time = getSharedPreferences("data", MODE_PRIVATE)
                    .getLong("time_spent", -1);
            int rank = getSharedPreferences("data", MODE_PRIVATE)
                    .getInt("rank", -1);

            int seconds = (int) (time / 1000);
            int minutes = (seconds / 60) % 60;
            int hour = seconds / 3600;
            seconds = seconds % 60;

            tv_time.setVisibility(View.VISIBLE);
            tv_rank.setVisibility(View.VISIBLE);
            tv_time.setText(String.format("所花時間：%d:%02d:%02d", hour, minutes, seconds));
            tv_rank.setText(String.format("排名：%d", rank));

        }


    }
}
