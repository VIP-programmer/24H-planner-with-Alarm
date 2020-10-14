package com.example.focus.randomTasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.focus.BGS.BGManager;
import com.example.focus.Config;
import com.example.focus.Notification;
import com.example.focus.R;
import com.example.focus.SessionManager;
import com.example.focus.planing.DayPlaner;

import java.io.FileNotFoundException;
import java.util.Calendar;

import androidx.core.app.NotificationCompat;
import androidx.transition.Visibility;

public class AlertReciever extends BroadcastReceiver {
    private String NOTIFICATION_TITLE = "One Task is Done";
    private String CONTENT_TEXT = "Expand me to see a detailed message!";
    private int mediaSong;
    private SessionManager sessionManager;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        sessionManager=new SessionManager(context);
        this.context=context;
        Config.init();
        String action=intent.getAction();
        if (action!=null && action.equals("left")){
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.cancel(0);
            sessionManager.setInProgress(true);
            sessionManager.setTimeAtOnstop(Calendar.getInstance().getTimeInMillis());
            sessionManager.setRedoTask(true);
            Toast.makeText(context,"Task started ...",Toast.LENGTH_LONG).show();
        }else {
            Log.i("TAG", "ID988 hello: ");
            mediaSong = Config.getSONG();
            String name = intent.getStringExtra("name");
            int duration = intent.getIntExtra("duration", 0);
            String bg = intent.getStringExtra("bg");
            if (MainActivity.isActivityVisible() || DayPlaner.isActivityVisible()) {
                //playMusic(context);
                makeNotif(context, name, duration, bg);
            } else {
                //showActivity(context, name, "main");
                playMusic(context);
                makeNotif(context, name, duration, bg);
            }
        }

    }

    private void playMusic(Context context) {
        final MediaPlayer mediaPlayer=MediaPlayer.create(context,mediaSong);
        CountDownTimer countDownTimer=new CountDownTimer(13000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mediaPlayer.start();
            }

            @Override
            public void onFinish() {
                mediaPlayer.release();
            }
        };countDownTimer.start();
    }

    private void showActivity(Context context, String name, String main) {
        Intent intent1=new Intent(context, Notification.class);
        intent1.putExtra("name",name);
        intent1.putExtra("activity",main);
        intent1.putExtra("music",mediaSong);
        context.startActivity(intent1);
    }


    private void makeNotif(Context context, String name, int duration, String bg) {
        int hours=(int) (duration/3600);
        int mins=(int) (duration-hours*3600)/60;
        int secs=(int) (duration-hours*3600-mins*60);


        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.expanded_notif);
        expandedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        expandedView.setTextViewText(R.id.time, hours+"H "+mins+"min "+secs+"s");
        expandedView.setTextViewText(R.id.task_name, name);

        expandedView.setImageViewBitmap(R.id.background_img,makeMeBitmap(bg));
        // adding action to left button
        Intent leftIntent = new Intent(context, AlertReciever.class);
        leftIntent.setAction("left");
        expandedView.setOnClickPendingIntent(R.id.redo, PendingIntent.getBroadcast(context, 999999, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        // adding action to right button
        Intent rightIntent = new Intent(context, MainActivity.class);
        rightIntent.setAction("right");
        expandedView.setOnClickPendingIntent(R.id.show, PendingIntent.getActivity(context, 1, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.collapsed_notif);
        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // these are the three things a NotificationCompat.Builder object requires at a minimum
                .setSmallIcon(R.drawable.new_task)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(CONTENT_TEXT)
                // notification will be dismissed when tapped
                .setAutoCancel(true)
                // tapping notification will open MainActivity
                // setting the custom collapsed and expanded views
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                ;
                // setting style to DecoratedCustomViewStyle() is necessary for custom views to display

        if (MainActivity.isActivityVisible() || DayPlaner.isActivityVisible()) {
            expandedView.setViewVisibility(R.id.lin_cont, View.GONE);
        }else {
            builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));
        }
        // retrieves android.app.NotificationManager
        NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private Bitmap makeMeBitmap(String element) {
        Uri uri=Uri.parse(element);
        Bitmap bitmap = null;
        try {
            bitmap= BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap==null){
            String part=element.substring(element.lastIndexOf('/')+1,element.lastIndexOf('.'));
            int image=0;
            switch (part){
                case "bg0":
                    image=R.drawable.bg0;
                    break;
                case "bg1":
                    image=R.drawable.bg1;
                    break;
                case "bg2":
                    image=R.drawable.bg2;
                    break;
                case "bg3":
                    image=R.drawable.bg3;
                    break;
            }
            bitmap=BitmapFactory.decodeResource(context.getResources(),image);
        }
        return bitmap;
    }
}
