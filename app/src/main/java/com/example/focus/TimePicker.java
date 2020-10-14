package com.example.focus;

import android.widget.TextView;

public class TimePicker {
    private int hour;
    private int min;
    private int sec;
    private TextView _hour;
    private TextView _min;
    private TextView _sec;

    public TimePicker(TextView _hour, TextView _min, TextView _sec) {
        this._hour = _hour;
        this._min = _min;
        this._sec = _sec;
        hour=0;
        min=0;
        sec=0;
        update(_hour,"0");
        update(_min,"0");
        update(_sec,"0");
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    public void incrementHours(){
        if (hour==23)
            hour=-1;
        hour ++;
        update(_hour,hour+"");
    }

    public void desincrementHours(){
        if (hour==0)
            hour=24;
        hour --;
        update(_hour,hour+"");
    }

    public void incrementMins(){
        if (min==59)
            min=-1;
        min ++;
        update(_min,min+"");
    }

    public void desincrementMins(){
        if (min==0)
            min=60;
        min --;
        update(_min,min+"");
    }

    public void incrementSecs(){
        if (sec==59)
            sec=-1;
        sec ++;
        update(_sec,sec+"");
    }

    public void desincrementSecs(){
        if (sec==0)
            sec=60;
        sec --;
        update(_sec,sec+"");
    }

    public void update(TextView textView, String text){
        textView.setText(text);
    }
}
