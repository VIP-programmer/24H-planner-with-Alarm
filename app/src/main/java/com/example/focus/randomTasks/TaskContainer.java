package com.example.focus.randomTasks;

import java.util.Vector;

public class TaskContainer {
    Vector<Task> tasks;

    public TaskContainer(Vector<Task> tasks) {
        this.tasks = tasks;
    }

    public TaskContainer() {
        this.tasks = new Vector<>();
    }

    public void setTasks(Vector<Task> tasks) {
        this.tasks = tasks;
    }

    public Vector<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public int getActiveTaskIndex(){
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isStart())
                return i;
        }
        return -1;
    }

    public int updateDuration(int duration){
        int i=getActiveTaskIndex();
        tasks.get(i).setDurationDone(duration+tasks.get(i).getDurationDone());
        return i;
    }
}
