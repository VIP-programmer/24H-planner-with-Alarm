package com.example.focus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.focus.planing.DayPlaner;
import com.example.focus.randomTasks.MainActivity;

public class Notification extends AppCompatActivity {

    private TextView taskName;
    private Button showMe;
    private Button cool;
    private Context context;

    private String inComeName;
    private String inComeActivity;
    private int inComeMusic;
    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }
        setContentView(R.layout.activity_notification);
        context=this;

        Intent intent=getIntent();
        inComeName=intent.getStringExtra("name");
        inComeActivity=intent.getStringExtra("activity");
        inComeMusic=intent.getIntExtra("music",0);
        mediaPlayer=playMusic(context);

        taskName=(TextView)findViewById(R.id.doneTask);
        showMe=(Button)findViewById(R.id.showAct);
        cool=(Button)findViewById(R.id.cool);

        taskName.setText(inComeName);

        cool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
                finish();
            }
        });
        showMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
                if (inComeActivity.equals("dayPlaner"))
                    context.startActivity(new Intent(context, DayPlaner.class));
                else context.startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });
        CountDownTimer countDownTimer=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                finish();
            }
        };countDownTimer.start();
    }

    private void stopMusic() {
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }

        if (mediaPlayer.isPlaying())
            mediaPlayer.release();

    }


    private MediaPlayer playMusic(Context context) {
        final MediaPlayer mediaPlayer=MediaPlayer.create(context,inComeMusic);
       countDownTimer=new CountDownTimer(13000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mediaPlayer.start();
            }

            @Override
            public void onFinish() {
                mediaPlayer.release();
            }
        };countDownTimer.start();
        return mediaPlayer;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)||(keyCode==KeyEvent.KEYCODE_VOLUME_UP)||(keyCode==KeyEvent.KEYCODE_HOME)||(keyCode==KeyEvent.KEYCODE_POWER)){
            stopMusic();
        }
        return super.onKeyDown(keyCode, event);
    }
}
