package com.example.focus.planing;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.focus.randomTasks.MainActivity;
import com.example.focus.Notification;
import com.example.focus.R;
import com.example.focus.SessionManager;

import java.util.Calendar;
import java.util.Objects;

import androidx.core.app.NotificationCompat;

public class DayAlert extends BroadcastReceiver {
    
    private String CONTENT_TEXT = "Expand me to see a detailed message!";
    String taskName;
    int color;
    Context context;
    int request;
    int music;
    @Override
    public void onReceive(Context context, Intent intent) {



        Log.i("TAG", "ID988 hello: ");
        Log.i("TAG", "ID596 Recieved Now!");
        this.context = context;
        String action=intent.getAction();
        /*
        if (action!=null && action.equals("left")){
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.cancel(0);
            Toast.makeText(context,"Task started ...",Toast.LENGTH_LONG).show();
        }else {*/
            //taskName=intent.getStringExtra("name");
            taskName = Objects.requireNonNull(intent.getExtras()).getString("name");
            color = intent.getIntExtra("color", 0);
            request = intent.getIntExtra("request", 0);
            String type = intent.getStringExtra("type");
            music = R.raw.simple_ringtone_3;
            SessionManager sessionManager = new SessionManager(context);
            assert type != null;
            if (type.equals("before")) {
                makeNotif("is about to start ", R.color.about_to_start);
            }
            if (type.equals("start")) {
                sessionManager.setRelax(false);
                sessionManager.setRunningTask(taskName);
                Intent intent1 = new Intent("GET_UP");
                intent1.putExtra("name", taskName);
                intent1.putExtra("request", request);
                context.sendBroadcast(intent1);
                makeNotif("is started", R.color.started);
            }
            if (type.equals("complete")) {
                sessionManager.setRelax(true);
                context.sendBroadcast(new Intent("RELAX"));
                if (MainActivity.isActivityVisible() || DayPlaner.isActivityVisible()) {
                    makeNotif("is Completed", R.color.done);
                } else
                    showActivity(taskName, "dayPlaner");
            }
        //}
    }


    private void makeNotif(String msg, int done) {

        Log.i("TAG", "ID596 Created Now!");
        RemoteViews expandedView=new RemoteViews(context.getPackageName(), R.layout.day_manager_notif);
        expandedView.setTextViewText(R.id.notif_task_name,taskName);
        expandedView.setTextViewText(R.id.notif_info,msg);
        expandedView.setInt(R.id.notif_wrapper,"setBackgroundResource",done);
        if (msg.equals("is completed"))
            expandedView.setViewVisibility(R.id.notif_actions,View.GONE);
        //expandedView.setImageViewResource(R.id.background_img, color);
        expandedView.setInt(R.id.background_img,"setBackground",color);
        // adding action to left button
        Intent leftIntent = new Intent(context, DayPlaner.class);
        leftIntent.setAction("left");
        expandedView.setOnClickPendingIntent(R.id.notif_cancel, PendingIntent.getActivity(context, 777, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        // adding action to right button
        Intent rightIntent = new Intent(context, DayPlaner.class);
        rightIntent.setAction("right");
        expandedView.setOnClickPendingIntent(R.id.notif_show, PendingIntent.getActivity(context, 7312, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT));


        RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.day_manager_notif);
        collapsedView.setTextViewText(R.id.notif_task_name,taskName);
        collapsedView.setTextViewText(R.id.notif_info,msg);
        if (msg.equals("is completed"))
            collapsedView.setViewVisibility(R.id.notif_actions,View.GONE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // these are the three things a NotificationCompat.Builder object requires at a minimum
                .setSmallIcon(R.drawable.new_task)
                // notification will be dismissed when tapped
                .setContentText("one task "+msg)
                .setPriority(1)
                .setCustomBigContentView(expandedView)
                .setAutoCancel(true)
                // tapping notification will open MainActivity
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                ;
        if (MainActivity.isActivityVisible() || DayPlaner.isActivityVisible()) {
            expandedView.setViewVisibility(R.id.notif_actions, View.GONE);
        }else {
            builder.setContentIntent(PendingIntent.getActivity(context, request, new Intent(context, DayPlaner.class), 0));
        }
        // retrieves android.app.NotificationManager
        NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(request, builder.build());
    }

    private void showActivity(String taskName, String activity) {
        Intent intent1=new Intent(context, Notification.class);
        intent1.putExtra("name",taskName);
        intent1.putExtra("activity",activity);
        intent1.putExtra("music",music);
        context.startActivity(intent1);
    }
}
