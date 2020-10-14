package com.example.focus.planing;

public class CreatedTask extends DayTask {
    private String startAt;
    private String endAt;

    public CreatedTask(String name, int startHour, int endHour, int startMin, int endMin, int color) {
        super(name, startHour, endHour, startMin, endMin, color);
        startAt=startHour+"H: "+startMin+"min";
        endAt=endHour+"H: "+endMin+"min";
    }

    public String getStartAt() {
        return startAt;
    }

    public String getEndAt() {
        return endAt;
    }
}
