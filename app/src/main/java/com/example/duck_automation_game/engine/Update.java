package com.example.duck_automation_game.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.duck_automation_game.MainActivity;

public class Update extends Thread {
    int time;
    Handler handler;
    GameState gameState;
    MainActivity mainActivity;
    Double updateTime;

    public Update(Handler handler, GameState gameState, MainActivity mainActivity, Double updateTime) {
        this.mainActivity = mainActivity;
        this.gameState = gameState;
        this.handler = handler;
        this.updateTime = updateTime;
    }

    @Override
    public void run() {
        super.run();
        Log.d("GAD", "run: " + Thread.currentThread().getId() + "    Priority:" + Thread.currentThread().getPriority());
        while (!this.isInterrupted()) {
            
            gameState.update(this.updateTime);
            //this.mainActivity.notifyAdapter();

            Message msg = new Message();
            msg.obj = "REEEEEEEEEEEEEEEE";
            handler.sendMessage(msg);

            SystemClock.sleep((int) (this.updateTime * 1000));

        }


    }
}

