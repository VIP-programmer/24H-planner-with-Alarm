package com.example.focus.randomTasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.focus.R;

import java.io.FileNotFoundException;

import androidx.annotation.NonNull;

public class Task {
    private int duration;
    private String name;
    private boolean completed;
    private int durationDone;
    private boolean start;
    private int percent;
    private String background;
    private int foreground;
    private int nameBg;

    public Task(int duration, String name,String background) {
        this.duration = duration;
        this.name = name;
        completed=false;
        durationDone=0;
        this.background=background;
        this.foreground= R.drawable.image_forground_stoped;
        nameBg=R.drawable.name_shape_doing;
        updatePercent();
        start=false;
    }


    public Bitmap getBitmap(Activity context) {
        Uri uri=Uri.parse(background);
        Bitmap bitmap = null;

        try {
            bitmap= BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        if (bitmap==null){
            String part=background.substring(background.lastIndexOf('/')+1,background.lastIndexOf('.'));
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

    public int getPercent() {
        return percent;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getDurationDone() {
        return durationDone;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
        if (start)
            foreground=R.drawable.image_forground_doing;
        else
            foreground=R.drawable.image_forground_stoped;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed)
            foreground=R.drawable.image_forground_done;
        else
            foreground= R.drawable.image_forground_stoped;
    }

    public void setDurationDone(int durationDone) {
        this.durationDone = durationDone;
        updatePercent();
    }

    public void move(){
        if (durationDone>=duration){
            stop();
            return;
        }
        durationDone++;
        updatePercent();
    }

    public void pause(){
        setStart(false);
    }

    public String getBackground() {
        return background;
    }

    public int getForeground() {
        return foreground;
    }

    public void reset(){
        setStart(false);
        setCompleted(false);
        setDurationDone(0);
        nameBg=R.drawable.name_shape_doing;
    }

    public int getNameBg() {
        return nameBg;
    }

    public void stop(){
        start=false;
        completed=true;
        foreground=R.drawable.image_forground_done;
        nameBg=R.drawable.name_shape_done;
    }

    public void updatePercent(){
        percent=100*durationDone/duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "{name: "+name+", duration"+duration+", duration done"+durationDone+"}";
    }

    public void redo() {
        reset();
    }
}
