package com.example.focus.planing;

import com.example.focus.planing.CreatedTask;

import java.util.Calendar;

public class ResumedTask {
    private String name;
    private String type;
    private int hour;
    private int min;
    private int color;
    private int index;

    public ResumedTask(CreatedTask createdTask, String type,int index){
        name=createdTask.getName();
        color=createdTask.getColor();
        this.type=type;
        makeAt(createdTask);
        this.index=index;
    }

    private void makeAt(CreatedTask createdTask) {
        if (type.equals("before"))
            hour=createdTask.getStartHour();
            min=createdTask.getStartMin()-1;
        if (type.equals("start")){
            hour=createdTask.getStartHour();
            min=createdTask.getStartMin();
        }
        if (type.equals("complete")){
            hour=createdTask.getEndHour();
            min=createdTask.getEndMin();
        }
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }
    public boolean timeOut(){
        Calendar calendar=Calendar.getInstance();
        int hour =calendar.get(Calendar.HOUR_OF_DAY);
        int min =calendar.get(Calendar.MINUTE);
        if (hour>this.hour)
            return true;
        if (hour == this.hour) {
            if (min >= this.min)
                return true;
        }
        return false;
    }
}
