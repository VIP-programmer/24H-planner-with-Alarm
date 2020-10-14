package com.example.focus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import com.example.focus.BGS.BGManager;
import com.example.focus.planing.DayManager;
import com.example.focus.randomTasks.TaskAdapter;
import com.example.focus.randomTasks.TaskContainer;
import com.google.gson.Gson;

public class SessionManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    private static final String PREFS_NAMES= "Focus_prefs";
    private static final int PRIVATE_MODE= 0;
    private static final String TASK_LIST= "tasks";
    public static final String THERE_IS_A_TASK_IN_PROGRESS="progress";
    public static final String TIME_AT_ONSTOP="time_at_on_stop";
    public static final String HOLDER="holder";
    public static final String TASK_INDEX="index";
    public static final String DAY_MGR="day_manager";
    public static final String MAX_ALARMS="max_alarms";
    public static final String RELAX="relax";
    public static final String RUNNING_TASK="running_task";
    public static final String REDO_TASK="redoing";
    public static final String BITMAPS="bitmapss";
    public static final String SHOW_INTRO="show_intro";

    public SessionManager(Context context) {
        this.context = context;
        preferences=context.getSharedPreferences(PREFS_NAMES,PRIVATE_MODE);
        editor=preferences.edit();
    }

    public void setTaskList(TaskContainer taskContainer){
        gson=new Gson();
        String string=gson.toJson(taskContainer,TaskContainer.class);
        /*if (taskContainer.getActiveTaskIndex() != -1){
            setInProgress(true);
        }

         */
        editor.putString(TASK_LIST,string);
        editor.apply();
    }

    public TaskContainer getTaskList(){
        gson=new Gson();
        String json=preferences.getString("tasks","{\"tasks\":[]}");
        return gson.fromJson(json,TaskContainer.class);
    }

    public void setInProgress(boolean progress){
        editor.putBoolean(THERE_IS_A_TASK_IN_PROGRESS,progress);
        editor.apply();
    }
    public boolean isInProgress(){
        return preferences.getBoolean(THERE_IS_A_TASK_IN_PROGRESS,false);
    }
    public void setTimeAtOnstop(long millSecs){
        editor.putLong(TIME_AT_ONSTOP,millSecs);
        editor.apply();
    }
    public long getTimeOnStop(){
        return preferences.getLong(TIME_AT_ONSTOP,0);
    }

    public void setHolder(TaskAdapter.TaskViewHolder holder){
        gson=new Gson();
        String string=gson.toJson(holder, TaskAdapter.TaskViewHolder.class);
        editor.putString(HOLDER,string);
        editor.apply();
    }
    public TaskAdapter.TaskViewHolder getHolder(){
        gson=new Gson();
        String json=preferences.getString("holder","{\"holder\":[]}");
        return gson.fromJson(json, TaskAdapter.TaskViewHolder.class);
    }

    public void setTaskIndex(int index){
        editor.putInt(TASK_INDEX,index);
        editor.apply();
    }

    public int getTaskIndex() {
        return preferences.getInt(TASK_INDEX,-1);
    }

    public void setRelax(Boolean relax){
        editor.putBoolean(RELAX,relax);
        editor.apply();
    }

    public boolean getRelax() {
        return preferences.getBoolean(RELAX,true);
    }

    public void setRedoTask(Boolean redo){
        editor.putBoolean(REDO_TASK,redo);
        editor.apply();
    }

    public boolean getRedoTask() {
        return preferences.getBoolean(REDO_TASK,false);
    }

    public void setRunningTask(String name){
        editor.putString(RUNNING_TASK,name);
        editor.apply();
    }

    public String getRunningTask() {
        return preferences.getString(RUNNING_TASK,"None");
    }


    public void setDayMgr(DayManager dayManager){
        gson=new Gson();
        String string=gson.toJson(dayManager,DayManager.class);
        /*if (taskContainer.getActiveTaskIndex() != -1){
            setInProgress(true);
        }

         */
        editor.putString(DAY_MGR,string);
        editor.apply();
    }

    public DayManager getDayMgr(){
        gson=new Gson();
        String json=preferences.getString("day_manager","{\"day_manager\":[]}");
        return gson.fromJson(json,DayManager.class);
    }

    public void setUris(BGManager bgManager){
        gson=new Gson();
        String string=gson.toJson(bgManager,BGManager.class);
        /*if (taskContainer.getActiveTaskIndex() != -1){
            setInProgress(true);
        }

         */
        editor.putString(BITMAPS,string);
        editor.apply();
    }

    public BGManager getUris(){
        gson=new Gson();
        String json=preferences.getString("bitmapss","{\"bitmapss\":[]}");
        return gson.fromJson(json, BGManager.class);
    }

    public void setMaxAlarms(int maxAlarms){
        editor.putInt(MAX_ALARMS,maxAlarms);
        editor.apply();
    }

    public int getMaxAlarms() {
        return preferences.getInt(MAX_ALARMS,-1);
    }

    public boolean showIntro(){
        return preferences.getBoolean(SHOW_INTRO,true);
    }

    public void desibleIntro(){
        editor.putBoolean(SHOW_INTRO,false);
        editor.apply();
    }
}
