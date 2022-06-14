package com.example.duck_automation_game.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.duck_automation_game.R;
//import com.example.duck_automation_game.classOftheTest;

public class WinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        TextView tv = findViewById(R.id.tv_Win);
        tv.setMovementMethod(new ScrollingMovementMethod());

    }
}