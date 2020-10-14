package com.example.focus.planing;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DayManager {
    private Vector<DayTask> dayTasks;
    private Vector<HourItem> hourItems;
    private Vector<CreatedTask> createdTasks;

    public DayManager() {
        this.dayTasks = new Vector<>();
        this.createdTasks=new Vector<>();
        init();
    }

    public DayManager(Vector<DayTask> dayTasks) {
        this.dayTasks = dayTasks;
        init();
    }

    public DayManager(Vector<DayTask> dayTasks,Vector<CreatedTask> createdTasks) {
        this.dayTasks = dayTasks;
        this.createdTasks=createdTasks;
        init();
    }

    public void init(){
        this.hourItems = new Vector<>(24);
        for (int i = 0; i < 24; i++) {
            hourItems.add(new HourItem(i));
        }
        buildItems();
    }
    private void buildItems(){
        for (DayTask task:dayTasks) {
            Vector<Integer> temps=task.getFullHours();
            for (int temp:temps) {
                hourItems.get(temp).setFull(task.getColor());
            }
        }
        for (int i = 0; i < 24; i++) {
            if (!hourItems.get(i).isFull()){
                Map<Integer,int[]> tempMap= new HashMap<>();
                for (DayTask task: dayTasks) {
                    if (task.hasThisAsHalfHour(i))
                        tempMap.putAll(task.getInBetween(i));
                }
                hourItems.get(i).buildMySelf(tempMap);
            }
        }
    }

    public Vector<DayTask> getDayTasks() {
        return dayTasks;
    }

    public Vector<HourItem> getHourItems() {
        return hourItems;
    }
    /*
    public void addTask(DayTask task){
        dayTasks.add(task);

        Vector<Integer> temps=task.getFullHours();
        for (int temp:temps) {
            hourItems.get(temp).setFull();
        }

        for (int i = 0; i < 24; i++) {
            if (! hourItems.get(i).isFull()){
                ArrayMap<Integer,int[]> tempMap= new ArrayMap<>();
                if (task.hasThisAsHalfHour(i))
                    tempMap.putAll(task.getInBetween(i));
                hourItems.get(i).buildMySelf(tempMap);
            }
        }
    }

     */

    public Vector<CreatedTask> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(Vector<CreatedTask> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public void setDayTasks(Vector<DayTask> dayTasks) {
        this.dayTasks = dayTasks;
        init();
    }
    
    public Vector<ResumedTask> resumedTasks(){
        Vector<ResumedTask> resumedTasks=new Vector<>();

        for (CreatedTask created:createdTasks) {
            resumedTasks.add(new ResumedTask(created,"before",(created.getEndHour()+1)*100+created.getEndMin()));
            resumedTasks.add(new ResumedTask(created,"start",(created.getEndHour()+1)*1000+created.getEndMin()));
            resumedTasks.add(new ResumedTask(created,"complete",(created.getEndHour()+1)*10000+created.getEndMin()));
        }

        return resumedTasks;
    }
    public Vector<Integer> getAllIndex(){
        Vector<Integer> vector=new Vector<>();
        for (CreatedTask cr :
                createdTasks) {
            vector.add((cr.getEndHour()+1)*100+cr.getEndMin());
            vector.add((cr.getEndHour()+1)*1000+cr.getEndMin());
            vector.add((cr.getEndHour()+1)*10000+cr.getEndMin());
        }
        return vector;
    }
    public DayTask belongsTo(int hour,int min){
        for (int i = 0; i < dayTasks.size() ; i++) {
            if (dayTasks.get(i).containsThis(hour,min))
                return dayTasks.get(i);
        }
        return null;
    }
    public CreatedTask findTaskByCoord(int endH,int endM){
        for (CreatedTask d :
                createdTasks) {
            if (d.getEndHour() == endH && d.getEndMin() == endM)
                return d;
        }
        return null;
    }
}
