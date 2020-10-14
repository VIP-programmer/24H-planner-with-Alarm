package com.example.focus.planing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.CalendarContract;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focus.planing.menu.BottomNavigationDrawerFragment;
import com.example.focus.planing.views.NewAdapter;
import com.example.focus.planing.views.ViewContainer;
import com.example.focus.planing.views.ViewX;
import com.example.focus.randomTasks.MainActivity;
import com.example.focus.R;
import com.example.focus.SessionManager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;


public class DayPlaner extends AppCompatActivity {

    private static boolean isVisible;
    boolean drow=true;
    SessionManager sessionManager;

    private RecyclerView tasksView;
    private RecyclerView hoursView;
    private RecyclerView newOnesView;
    private Vector<CreatedTask> createdTasks;

    private DayTaskItemAdapter dayTaskItemAdapter;
    private NewAdapter dayTaskHourAdapter;
    private MySpinnerAdapter_ spinnerAdapter;
    private CreatedTaskAdapter createdTaskAdapter;

    private DayManager dayManager;
    Vector<DayTask> tasks;

    private Spinner simpeSpinner;
    private Dialog dialog;
    private int[] colors;

    private RelativeLayout taskName;
    private LinearLayout clickToAdd;
    private RelativeLayout holde;
    private RelativeLayout runningTask;
    private RelativeLayout relaxingView;
    private TextView runningTaskName;
    private TextView cancelRunningTask;
    private ImageView toTasks;
    private Button add_button;
    private Button cancel_button;

    private BottomSheetBehavior bottomSheetBehavior;
    private FloatingActionButton addOrModify;
    private BottomAppBar bottomAppBar;

    private HourPickerManager startPicker;
    private HourPickerManager endPicker;

    private boolean modifyIndicator=false;
    private int modifyPosition;

    Vector<CreatedTask> temp;
    Vector<HourItem> hourItems;

    private int maxAlarms;
    Vector<ViewContainer> containerVector;

    private static boolean activityVisible;



    public static boolean isActivityVisible(){
        return activityVisible;
    }

    public void setActivitysGone(){
        activityVisible=false;
    }

    public void setActivityVisible(){
        activityVisible=true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_day_planer);

        setActivityVisible();
        sessionManager=new SessionManager(this);


        taskName=(RelativeLayout) findViewById(R.id.taskOfNow);
        clickToAdd=(LinearLayout) findViewById(R.id.clickToAdd);
        relaxingView=(RelativeLayout) findViewById(R.id.taskPlaceHolder);
        runningTask=(RelativeLayout) findViewById(R.id.taskOfNow);
        runningTaskName=(TextView) findViewById(R.id.running_task_name);
        cancelRunningTask=(TextView) findViewById(R.id.cancel_current_task);
        holde=(RelativeLayout) findViewById(R.id.taskPlaceHolder);
        toTasks=(ImageView) findViewById(R.id.toTasks);
        add_button=(Button) findViewById(R.id.valideD);
        cancel_button=(Button) findViewById(R.id.cancelD);

        tasksView=(RecyclerView) findViewById(R.id.your_tasks);
        hoursView=(RecyclerView) findViewById(R.id.day_plan);
        newOnesView =(RecyclerView) findViewById(R.id.createdTasksContainer);
        bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_app_bar);


        setSupportActionBar(bottomAppBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        LinearLayout linearLayout = findViewById(R.id.bottom_sheet_day);
        addOrModify=(FloatingActionButton) findViewById(R.id.addOrModify) ;
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        temp=new Vector<>();
        addOrModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.clear();
                temp.addAll(createdTasks);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        /**
         * dealing with dialog
         */
        dialog=new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.create_new_day_task_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        /**
         * Creating recycleViews
         */
        //initialisation
        dayManager=sessionManager.getDayMgr();
        Log.i("TAG", "ID596 creating daymanager !");
        createdTasks=dayManager.getCreatedTasks();
        tasks = dayManager.getDayTasks();
        hourItems = dayManager.getHourItems();
        //nxofo ila kan xi task
        //Giving it a try
        containerVector=new Vector<>();
        prepareMyViews();
        //try ends
        if (sessionManager.getRelax()){
            relax();
        }else{
            runTask(sessionManager.getRunningTask());
        }
        registerIntents();
        //new created Task
        createdTaskAdapter=new CreatedTaskAdapter(this,createdTasks);
        createdTaskAdapter.setHasStableIds(true);
        newOnesView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        newOnesView.setAdapter(createdTaskAdapter);

        createdTaskAdapter.setOnItemClickListener(new CreatedTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int position) {
                removeItem(position);
            }

            @Override
            public void onItemEdit(int position) {
                modifyIndicator=true;
                modifyPosition=position;
                openDialog(createdTasks.get(position).getName(),createdTasks.get(position).getStartHour(),createdTasks.get(position).getStartMin(),createdTasks.get(position).getEndHour(),createdTasks.get(position).getEndMin());
            }
        });
        toTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        dayManager=new DayManager();
        /**
         * For Test
         */
        //day task item
        dayTaskItemAdapter=new DayTaskItemAdapter(this,tasks);
        dayTaskItemAdapter.setHasStableIds(true);
        //tasksView.setHasFixedSize(true);
        tasksView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        tasksView.setAdapter(dayTaskItemAdapter);

        //Day hours
        //hourItems.clear();
        //hourItems.addAll(dayManager.getHourItems());
        //dayTaskHourAdapter=new DayTaskHourAdapter(this,hourItems);
        dayTaskHourAdapter=new NewAdapter(this,containerVector);
        dayTaskHourAdapter.setHasStableIds(true);
        //hoursView.setHasFixedSize(true);
        hoursView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        hoursView.setAdapter(dayTaskHourAdapter);

        //navigating to hour task from day tasks
        dayTaskItemAdapter.setOnItemAction(new DayTaskItemAdapter.onItemAction() {
            @Override
            public void onItemSelected( DayTask task, DayTask oldTask, boolean b) {
                Vector<Integer> integerVector=new Vector<>();
                //DayTaskHourAdapter.selectedID.clear();
                if (b && oldTask!=null){
                    integerVector=getIds(oldTask);
                    for (int im :integerVector) {
                        int newID=im/100;
                        containerVector.get(newID).getById(im).inselect();
                        dayTaskHourAdapter.notifyItemChanged(newID);
                    }
                }
                integerVector=getIds(task);
                for (int im :integerVector) {
                    Log.i("TAG", "idKonItemSelected: " + im);
                    int newID=im/100;
                    if (b) {
                        containerVector.get(newID).getById(im).select();
                    }
                    else containerVector.get(newID).getById(im).inselect();
                    if (b) {
                        hoursView.smoothScrollToPosition(integerVector.get(0) / 100);
                        bottomAppBar.performHide();
                    }
                    dayTaskHourAdapter.notifyItemChanged(newID);
                }
            }
        });

        clickToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(null,0,0,0,0);
            }
        });
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAllAlarms(dayManager.getAllIndex());
                tasks.clear();
                hourItems.clear();
                tasks.addAll(createdTasks);
                dayManager=new DayManager(tasks,createdTasks);
                sessionManager.setDayMgr(dayManager);
                hourItems.addAll(dayManager.getHourItems());
                //Giving it a try
                //final Vector<ViewContainer> containerVector=new Vector<>();
                prepareMyViews();
                //try ends
                dayTaskHourAdapter.notifyDataSetChanged();
                dayTaskItemAdapter.notifyDataSetChanged();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (createdTasks.size()==0)
                    recycleAlarm(true);
                else recycleAlarm(false);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                createdTasks.clear();
                createdTasks.addAll(temp);
                createdTaskAdapter.notifyDataSetChanged();
            }
        });


        treatIntent(getIntent());
    }

    private void cancelAllAlarms(Vector<Integer> allIndex) {
        for (int i: allIndex)
            cancelAlarm(i);
    }


    private void openDialog(String name, int startHour, int startMin, int endHour, int endMin) {
        final EditText choiceName=dialog.findViewById(R.id.newName);
        Button valideChoice=dialog.findViewById(R.id.valideN);
        Button cancelChoice=dialog.findViewById(R.id.cancelN);
        startPicker=new HourPickerManager(false,(NumberPicker) dialog.findViewById(R.id.pickerHoursS),(NumberPicker) dialog.findViewById(R.id.pickerMinutesS));
        endPicker=new HourPickerManager(false,(NumberPicker) dialog.findViewById(R.id.pickerHoursE),(NumberPicker) dialog.findViewById(R.id.pickerMinutesE));
        if (name!=null) {
            choiceName.setText(name);
            startPicker.setDefault(startHour,startMin);
            endPicker.setDefault(endHour,endMin);
        }
        simpeSpinner=(Spinner) dialog.findViewById(R.id.colorSpinner);
        colors= new int[]{R.color.blue_light,
                R.color.brown_dark,
                R.color.brown_light,
                R.color.green_dark,
                R.color.green_light,
                R.color.nearBlue_light,
                R.color.orange_dark,
                R.color.orange_light,
                R.color.pink_dark,
                R.color.trial_light,
                R.color.trial_dark,
                R.color.red_light,
                R.color.red_dark,
                R.color.purpl_light,
                R.color.purpl_dark,
                R.color.pink_light
        };
        List<Integer> intList = new ArrayList<Integer>(colors.length);
        for (int i : colors)
        {
            intList.add(i);
        }
        spinnerAdapter=new MySpinnerAdapter_(dialog.getContext(),0,intList);
        simpeSpinner.setAdapter(spinnerAdapter);
        dialog.show();

        valideChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choiceName.getText().toString().equals("")){
                    Toast.makeText(dialog.getContext(),"name is empty",Toast.LENGTH_SHORT).show();
                }else {
                    if (!startPicker.isValid()){
                        Toast.makeText(dialog.getContext(),"please define start time",Toast.LENGTH_SHORT).show();
                    }else {
                        if (! endPicker.isValid()){
                            Toast.makeText(dialog.getContext(),"please define end time",Toast.LENGTH_SHORT).show();
                        }else {
                            if (endPicker.isOlder(startPicker)){
                                Toast.makeText(dialog.getContext(),"End time should not be older then or equal to start time",Toast.LENGTH_SHORT).show();
                            }else {
                                if (isItsInBetween(startPicker.hour.getValue(), endPicker.hour.getValue(), startPicker.min.getValue(), endPicker.min.getValue())){
                                    Toast.makeText(dialog.getContext(),"Another task took this (part of) this period",Toast.LENGTH_SHORT).show();
                                }else {
                                    int colorChoice = colors[simpeSpinner.getSelectedItemPosition()];
                                    CreatedTask newCreatedTask = new CreatedTask(choiceName.getText().toString(), startPicker.hour.getValue(), endPicker.hour.getValue(), startPicker.min.getValue(), endPicker.min.getValue(), colorChoice);
                                    if (modifyIndicator) {
                                        createdTasks.set(modifyPosition, newCreatedTask);
                                        //tasks.set(modifyPosition,newCreatedTask);
                                    } else {
                                        createdTasks.add(newCreatedTask);
                                        //tasks.add(newCreatedTask);
                                    }
                                    modifyIndicator = false;
                                    //dayManager=new DayManager(tasks);
                                    createdTaskAdapter.notifyDataSetChanged();
                                    //dayTaskHourAdapter.notifyDataSetChanged();
                                    //dayTaskItemAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }
                        }
                    }
                }
            }
        });

        cancelChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyIndicator=false;
                dialog.dismiss();
            }
        });
    }

    private void treatIntent(Intent intent) {
        Log.i("TAG", "ID596 treatIntent: Hi");
        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
            manager.cancel(0);
        String action=intent.getAction();
        if (action!=null && action.equals("left")){

            Log.i("TAG", "ID596 treatIntent: Hello");
            cancelCurrTask();
            //createForNextDay();
        }
    }
    private boolean isItsInBetween(int startHour, int endHour, int startMin, int endMin) {
        int startOldTime;
        int endOldTime;
        int startNewTime=startHour*60+startMin;
        int endNewTime=endHour*60+endMin;
        for (CreatedTask createdTask:createdTasks){
            if (modifyIndicator && createdTask.equals(createdTasks.get(modifyPosition)))
                continue;
            startOldTime=createdTask.getStartHour()*60+createdTask.getStartMin();
            endOldTime=createdTask.getEndHour()*60+createdTask.getEndMin();
            if((startOldTime<=startNewTime && startNewTime<endOldTime)||(startOldTime<=endNewTime && endNewTime<endOldTime)||(startOldTime>=startNewTime && endNewTime>endOldTime))
                return true;

        }
        return false;
    }
    public Vector<Integer> getIds(DayTask task){
        Vector<Integer> integerVector=new Vector<>();
        if (task.isLessThenOneHour()){
            int id = task.getStartHour() * 100+task.getStartMin();
            integerVector.add(id);
            //DayTaskHourAdapter.selectedID.add(id);
        }else {
            for (int i = task.getStartHour(); i < task.getEndHour(); i++) {
                int id = i * 100;
                if (i == task.getStartHour())
                    id += task.getStartMin();
                integerVector.add(id);
                //DayTaskHourAdapter.selectedID.add(id);
            }
            if (task.getEndMin() != 0) {
                int id = task.getEndHour() * 100;
                integerVector.add(id);
                //DayTaskHourAdapter.selectedID.add(id);
            }
        }
        return integerVector;
    }
    public void resetMyViews(){
        for (ViewContainer container:containerVector)
            container.inselectAll();
        dayTaskHourAdapter.notifyDataSetChanged();
    }
    public void prepareMyViews(){
        containerVector.clear();
        for (HourItem hourItem:hourItems) {
            ViewContainer viewContainer=new ViewContainer();
            if (hourItem.isFull()) {
                viewContainer.addViewX(new ViewX(getApplicationContext(),hourItem.getIndex()*100,hourItem.getColorForFull(),LinearLayout.LayoutParams.MATCH_PARENT,1));
            } else if (hourItem.isEmpty()) {
                viewContainer.addViewX(new ViewX(getApplicationContext(),hourItem.getIndex()*100,R.color.default_task_color,LinearLayout.LayoutParams.MATCH_PARENT,1));
            } else {
                Map<Integer, Object[]> map = new TreeMap<>(hourItem.getStructure());
                for (ArrayMap.Entry<Integer, Object[]> mapElement : map.entrySet()) {
                    float weight;
                    int partOfId;
                    try {
                        weight = (Float) mapElement.getValue()[0];
                        partOfId = (int) mapElement.getValue()[2];
                    } catch (Exception e) {
                        weight = ((Double) mapElement.getValue()[0]).floatValue();
                        partOfId = ((Double) mapElement.getValue()[2]).intValue();
                    }
                    String d = (String) mapElement.getValue()[1];
                    int i = Integer.parseInt(d);
                    viewContainer.addViewX(new ViewX(getApplicationContext(),hourItem.getIndex()* 100 + partOfId,i,0,weight));
                }
            }
            containerVector.add(viewContainer);
        }
    }
    public void removeItem(int position) {
        createdTasks.remove(position);
        createdTaskAdapter.notifyItemRemoved(position);
        createdTaskAdapter.notifyItemRangeChanged(position,createdTaskAdapter.getItemCount());
    }
    public void createForTomorrow(int index){
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(),DayAlert.class);

        intent.putExtra("name",dayManager.getDayTasks().get(index).getName());
        intent.putExtra("type","complete");
        intent.putExtra("color",dayManager.getDayTasks().get(index).getColor());
        intent.putExtra("request",index*3+2);

        Calendar cl=Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());

        cl.add(Calendar.DAY_OF_YEAR,1);

        cl.set(Calendar.HOUR_OF_DAY,dayManager.getDayTasks().get(index).getEndHour());
        cl.set(Calendar.MINUTE,dayManager.getDayTasks().get(index).getEndMin());
        cl.set(Calendar.SECOND,0);

        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,index*3+2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cl.getTimeInMillis(),pendingIntent);
    }
    public void createAlarm(ResumedTask resumedTask,int request,boolean cancel){
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(),DayAlert.class);

        intent.putExtra("name",resumedTask.getName());
        intent.putExtra("type",resumedTask.getType());
        intent.putExtra("color",resumedTask.getColor());
        intent.putExtra("request",request);

        Calendar cl=Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        /*
        if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)>=resumedTask.getHour())
            if (Calendar.getInstance().get(Calendar.MINUTE)<resumedTask.getMin() && Calendar.getInstance().get(Calendar.HOUR_OF_DAY)==resumedTask.getHour())
                cl.add(Calendar.DAY_OF_YEAR,0);
            else cl.add(Calendar.DAY_OF_YEAR,1);

         */
        if (resumedTask.timeOut() || cancel)
            cl.add(Calendar.DAY_OF_WEEK,1);

        cl.set(Calendar.HOUR_OF_DAY,resumedTask.getHour());
        cl.set(Calendar.MINUTE,resumedTask.getMin());
        cl.set(Calendar.SECOND,0);

        Log.i("TAG", "ID596 Alarm should start at : "+cl.get(Calendar.DAY_OF_WEEK)+" "+cl.get(Calendar.HOUR_OF_DAY)+"H "+cl.get(Calendar.MINUTE)+"min "+cl.get(Calendar.SECOND)+", For type:"+resumedTask.getType()+", request is: "+request);

        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,request,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        assert alarmManager != null;
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cl.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,cl.getTimeInMillis(),pendingIntent);
    }
    public void cancelAlarm(int request){
        Log.i("TAG", "ID596 canceling : "+request);
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,DayAlert.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,request,intent,PendingIntent.FLAG_NO_CREATE);

        if(alarmManager != null && pendingIntent!=null)
            alarmManager.cancel(pendingIntent);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK,-1);
        Log.i("TAG", "ID596 Alarm should start at : "+calendar.get(Calendar.DAY_OF_WEEK)+" "+calendar.get(Calendar.HOUR_OF_DAY)+"H "+calendar.get(Calendar.MINUTE)+"min "+calendar.get(Calendar.SECOND)+" request is: "+request);

        //alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    public void recycleAlarm(boolean none){
        Vector<ResumedTask> resumedTasks=dayManager.resumedTasks();
        for (int i = 0; i < resumedTasks.size(); i++)
            createAlarm(resumedTasks.get(i), resumedTasks.get(i).getIndex(),false);
    }

    public void relax(){
        runningTask.setVisibility(View.GONE);
        cancelRunningTask.setVisibility(View.GONE);
        relaxingView.setVisibility(View.VISIBLE);
    }

    public void runTask(String name){
        runningTask.setVisibility(View.VISIBLE);
        cancelRunningTask.setVisibility(View.VISIBLE);
        relaxingView.setVisibility(View.GONE);
        runningTaskName.setText(name);
        cancelRunningTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCurrTask();
                //createForNextDay();
            }
        });
    }

    private BroadcastReceiver startReg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runTask(intent.getStringExtra("name"));
        }
    };

    private BroadcastReceiver relaxReg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            relax();
            createForNextDay();
        }
    };

    public void createForNextDay(){
        int endH=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int endM=Calendar.getInstance().get(Calendar.MINUTE);
        CreatedTask curr=dayManager.findTaskByCoord(endH,endM);
        if (curr!=null) {
            Vector<ResumedTask> resumedTasks = new Vector<>();
            resumedTasks.add(new ResumedTask(curr, "before", (curr.getEndHour()+1) * 100 + curr.getEndMin()));
            resumedTasks.add(new ResumedTask(curr, "start", (curr.getEndHour()+1)* 1000 + curr.getEndMin()));
            resumedTasks.add(new ResumedTask(curr, "complete", (curr.getEndHour()+1) * 10000 + curr.getEndMin()));
            for (ResumedTask r :
                    resumedTasks) {
                createAlarm(r, r.getIndex(), true);
            }
        }
    }

    public void cancelCurrTask(){

        Log.i("TAG", "ID596 Using daymanager !");
        DayTask runningOne=sessionManager.getDayMgr().belongsTo(Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE));
        if (runningOne != null){

            Log.i("TAG", "ID596 treatIntent: running one not null");
            cancelAlarm((runningOne.getEndHour()+1)*10000+runningOne.getEndMin());
            Vector<ResumedTask> resumedTasks=new Vector<>();
            resumedTasks.add(new ResumedTask(new CreatedTask(runningOne.getName(),runningOne.getStartHour(),runningOne.getEndHour(),runningOne.getStartMin(),runningOne.getEndMin(),runningOne.getColor()),"before",(runningOne.getEndHour()+1)*100+runningOne.getEndMin()));
            resumedTasks.add(new ResumedTask(new CreatedTask(runningOne.getName(),runningOne.getStartHour(),runningOne.getEndHour(),runningOne.getStartMin(),runningOne.getEndMin(),runningOne.getColor()),"start",(runningOne.getEndHour()+1)*1000+runningOne.getEndMin()));
            resumedTasks.add(new ResumedTask(new CreatedTask(runningOne.getName(),runningOne.getStartHour(),runningOne.getEndHour(),runningOne.getStartMin(),runningOne.getEndMin(),runningOne.getColor()),"complete",(runningOne.getEndHour()+1)*10000+runningOne.getEndMin()));
            for (ResumedTask r :
                    resumedTasks) {
                createAlarm(r,r.getIndex(),true);
            }
            sessionManager.setRelax(true);
            relax();
        }
    }

    public void registerIntents(){
        try {
            registerReceiver(relaxReg,new IntentFilter("RELAX"));
        }catch (Exception ignored){}
        try {
            registerReceiver(startReg,new IntentFilter("GET_UP"));
        }catch (Exception ignored){}
    }

    public void unRegisterIntents(){
        try {
            unregisterReceiver(startReg);
        }catch (Exception ignored){}
        try {
            unregisterReceiver(relaxReg);
        }catch (Exception ignored){}
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActivityVisible();
        registerIntents();
        if (sessionManager.getRelax()){
            relax();
        }else{
            runTask(sessionManager.getRunningTask());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setActivityVisible();
        registerIntents();
        if (sessionManager.getRelax()){
            relax();
        }else{
            runTask(sessionManager.getRunningTask());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setActivitysGone();
        unRegisterIntents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setActivitysGone();
        unRegisterIntents();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buttom_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu){
            BottomNavigationDrawerFragment drawerFragment;
            drawerFragment = new BottomNavigationDrawerFragment();
            drawerFragment.show(getSupportFragmentManager(),drawerFragment.getTag());

        }
        return super.onOptionsItemSelected(item);
    }
}
