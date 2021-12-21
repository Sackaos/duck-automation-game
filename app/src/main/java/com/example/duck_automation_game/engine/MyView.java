package com.example.duck_automation_game.engine;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duck_automation_game.ui.CustomListItemModel;

import java.util.Map;

public class MyView extends AppCompatActivity implements AdapterView.OnItemClickListener {



    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    protected void onStop() {
        super.onStop();
    }
    public void showToast(String message, int length) {
        Toast.makeText(this, message,
                length).show();
    }
}
