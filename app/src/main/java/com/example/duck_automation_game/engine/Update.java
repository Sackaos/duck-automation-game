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
    public Update(Handler handler, GameState gameState, MainActivity mainActivity
    ) {
        this.mainActivity = mainActivity;
        this.gameState = gameState;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        Log.d("GAD", "run: " + Thread.currentThread().getId() + "    Priority:" + Thread.currentThread().getPriority());
        while (!this.isInterrupted()) {
            if (Thread.currentThread().isInterrupted()) Log.d("GAD", "run: RUN YOU FOOl");

            gameState.update(0.2);
            //this.mainActivity.notifyAdapter();

            Message msg = new Message();
            msg.obj = "REEEEEEEEEEEEEEEE";
            handler.sendMessage(msg);

            SystemClock.sleep(200);

         }


    }
}

