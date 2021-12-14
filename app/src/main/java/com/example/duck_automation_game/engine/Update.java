package com.example.duck_automation_game.engine;

        import android.os.Handler;
        import android.os.Looper;
        import android.os.Message;
        import android.os.SystemClock;
        import android.view.View;
        import android.widget.TextView;

        import androidx.annotation.NonNull;

public class Update extends Thread {
    int time;
    Handler handler;

    public Update(Handler handler
    ) {
        this.handler=handler;
    }

    public void run() {

        super.run();
        while(true) {
            if (!Thread.currentThread().isInterrupted()) {
                SystemClock.sleep(100);
                Message msg = new Message();
                msg.obj = "s";
                handler.sendMessage(msg);
            }
        }
    }


}

