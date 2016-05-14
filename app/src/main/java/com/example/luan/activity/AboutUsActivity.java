package com.example.luan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Admin on 5/14/2016.
 */
public class AboutUsActivity extends AppCompatActivity {
    private TextView description, created_by, copyright;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        description = (TextView) findViewById(R.id.description);
        created_by = (TextView) findViewById(R.id.created_by);
        copyright = (TextView) findViewById(R.id.copyright);
        description.setText("    YuYo is a new inovative app that helps you learn language(s) by meeting, hangout and practice speaking with a native speaker or someone who already fluent in that language that lives near you.\n\n" +
                "    We believe that practice makes perfect and it is essential to learning a new language. Have fun learning and feel free to give us any feedbacks, even the negative ones, we'd love to hear it.");
        created_by.setText("\n App created by: Togen Hikaru, Vo Van Luan");
        copyright.setText("\u00A9 YuYo Inc. All rights reserved");
    }
}
