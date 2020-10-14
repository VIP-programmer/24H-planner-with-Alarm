package com.example.focus.randomTasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.focus.BGS.BGManager;
import com.example.focus.planing.HourPickerManager;
import com.example.focus.R;
import com.example.focus.SessionManager;
import com.example.focus.planing.DayPlaner;
import com.example.focus.planing.menu.BottomNavigationDrawerFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Objects;
import java.util.Vector;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Vector<Task> tasks;
    TaskAdapter adapter;
    SessionManager sessionManager;

    InterstitialAd interstitialAd;
    AdView adView;
    /*
    NumberPicker hours;
    NumberPicker mins;
    NumberPicker secs;

     */
    int index=0;
    int[] images;
    Dialog addDialog;
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    ImageView bg_holder;
    TextView titile;
    private int dotscount;
    private ImageView[] dots;

    EditText input;
    //testing
    TextView test;

    Button button_add;
    Button button_cancel;

    BottomSheetBehavior bottomSheetBehavior;
    FloatingActionButton add;
    BottomAppBar bottomAppBar;
    Vector<String> stringVector;

    MediaPlayer mediaPlayer;
    Context context;
    public static boolean allowAlarm=true;

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

        //make it fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        context=this;

        Intent intent=getIntent();

        setActivityVisible();
        input=(EditText) findViewById(R.id.task_name);
        input.setVisibility(View.GONE);


        sessionManager=new SessionManager(this);
        recyclerView=(RecyclerView) findViewById(R.id.task_list);
        bottomAppBar=(BottomAppBar) findViewById(R.id.bottom_app_bar);
        bg_holder=(ImageView) findViewById(R.id.bg_holder);
        titile=(TextView) findViewById(R.id.title_template);
        bg_holder.setVisibility(View.GONE);
        adView=findViewById(R.id.adMainBottom);

        //for ads
        interstitialAd=new InterstitialAd(this);
        //interstitialAd.setAdUnitId("ca-app-pub-7832644521881287/7136150221");
        PrepareAd prepareAd=new PrepareAd(this,adView,interstitialAd);
        prepareAd.execute();

        BGManager bgManager=sessionManager.getUris();
        if (bgManager.mySize()==0)
            bgManager.init();
        stringVector=bgManager.getVector();

        setSupportActionBar(bottomAppBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.menu));

        ImageView to_day_plan=(ImageView) findViewById(R.id.to_day_plan);
        to_day_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DayPlaner.class));
            }
        });

        add =(FloatingActionButton) findViewById(R.id.add);

        prepareBottomSheet();

        tasks=new Vector<>();
        TaskContainer taskContainer=sessionManager.getTaskList();
        if (taskContainer==null)
            taskContainer=new TaskContainer();
        tasks=taskContainer.getTasks();
        if (tasks.size()==0){
            showBackground();
        }else{
            hideBackground();
        }

        Log.i("TAG", "id889 onCreate: ");
        adapter=new TaskAdapter(this,tasks);
        adapter.setHasStableIds(true);
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int position) {
                removeItem(position);
            }

            @Override
            public void onRedo(int position) {
                tasks.get(position).redo();
                adapter.notifyItemChanged(position);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemStartListener(new TaskAdapter.OnItemStartListener() {
            @Override
            public void onItemStart(int position) {
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        });

        adapter.setOnTaskStat(new TaskAdapter.OnTaskStat() {
            @Override
            public void started(Task task) {

                if (allowAlarm) {
                    Log.i("TAG", "id890 started: "+task);
                    startAlarmFor(task);
                    allowAlarm=false;
                }
            }

            @Override
            public void stoped() {
                cancelAlarm();
                allowAlarm=true;
            }

            @Override
            public void completed() {
                allowAlarm=true;
            }
        });

        LinearLayout linearLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        treatIntent(intent);


    }

    private void treatIntent(Intent intent) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
            manager.cancel(0);
        String action=intent.getAction();
        if (action!=null && action.equals("right")){
            if (sessionManager.isInProgress()){
                sessionManager.setInProgress(false);
                long current_time = Calendar.getInstance().getTimeInMillis();
                long pass_time = sessionManager.getTimeOnStop();
                long extra = 0;
                if (current_time < pass_time)
                    extra = 24;
                int secs_added = (int) (current_time - pass_time + extra) / 1000;
                adapter.resume(secs_added,sessionManager.getRedoTask());
                sessionManager.setRedoTask(false);
            }
        }
    }

    private void startAlarmFor(Task task) {

        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);

        intent.putExtra("name",task.getName());
        intent.putExtra("duration",task.getDuration());
        intent.putExtra("bg",task.getBackground());

        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,Calendar.getInstance().getTimeInMillis()+(task.getDuration()-task.getDurationDone())*1000,pendingIntent);

        //Toast.makeText(context,"Alarm started",Toast.LENGTH_SHORT).show();

    }
    private void cancelAlarm(){
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlertReciever.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.cancel(pendingIntent);
        //Toast.makeText(context,"Alarm canceld",Toast.LENGTH_SHORT).show();
    }

    private void hideBackground() {
        bg_holder.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        titile.setVisibility(View.VISIBLE);
    }

    public void addtoList(int hour,int min,int sec,String name,String bacground){
        Log.i("TAG", "addtoList: "+hour+","+min+","+sec);
        Task task=new Task(hour*3600+min*60+sec,name,bacground);
        TaskContainer container = sessionManager.getTaskList();
        container.addTask(task);
        sessionManager.setTaskList(container);
        if (tasks.isEmpty())
            hideBackground();
        tasks.add(task);
        adapter.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        adapter.killSafely(position);
        tasks.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position,adapter.getItemCount());
        //adapter.notifyDataSetChanged();
        sessionManager.setTaskList(new TaskContainer(tasks));
        //adapter.notifyItemRangeRemoved(position, tasks.size());
        if (tasks.isEmpty()){
            showBackground();
        }
    }

    public void showBackground(){
        bg_holder.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        titile.setVisibility(View.GONE);
    }

    public void prepareBottomSheet(){
        //viewPager = (ViewPager) findViewById(R.id.view_pager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        images= new int[]{R.drawable.bg0, R.drawable.bg1,R.drawable.bg2,R.drawable.bg3};

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.inactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        index=0;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.inactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                index=position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
/*
        hours = (NumberPicker) findViewById(R.id.pickerHours);
        mins = (NumberPicker) findViewById(R.id.pickerMinutes);
        secs = (NumberPicker) findViewById(R.id.pickerSeconds);

        hours.setMinValue(0);
        hours.setMaxValue(23);
        mins.setMinValue(0);
        mins.setMaxValue(59);
        secs.setMinValue(0);
        secs.setMaxValue(59);


 */
        final HourPickerManager picker=new HourPickerManager(true,(NumberPicker) findViewById(R.id.pickerHours),(NumberPicker) findViewById(R.id.pickerMinutes),(NumberPicker) findViewById(R.id.pickerSeconds));

        button_add=(Button) findViewById(R.id.valide);
        button_cancel=(Button) findViewById(R.id.cancel);
        //we have define input up

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                input.setText("");
                input.setVisibility(View.GONE);
                picker.resetInputs();
            }
        });
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input.getText().toString().equals("")){
                    if (picker.hour.getValue()==0 && picker.min.getValue()==0 && picker.sec.getValue()==0){
                        Toast.makeText(getApplicationContext(),"duration can't be 0",Toast.LENGTH_SHORT).show();
                    }else {
                        addtoList(picker.hour.getValue(),picker.min.getValue(),picker.sec.getValue(),input.getText().toString(),stringVector.get(index));
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        input.setText("");
                        picker.resetInputs();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"task name is empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStop() {
        setActivitysGone();
        sessionManager.setTaskList(new TaskContainer(tasks));
        adapter.setGetHolder(new TaskAdapter.GetHolder() {
            @Override
            public void getInfos(Vector<Task> tasks, int position) {
                sessionManager.setInProgress(true);
                sessionManager.setTaskIndex(position);
                sessionManager.setTaskList(new TaskContainer(tasks));
                sessionManager.setTimeAtOnstop(Calendar.getInstance().getTimeInMillis());
            }

            @Override
            public void getHolder(TaskAdapter.TaskViewHolder holder, int position) {

            }
        });
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setActivityVisible();
        if (sessionManager.isInProgress()){
            sessionManager.setInProgress(false);
            long current_time = Calendar.getInstance().getTimeInMillis();
            long pass_time = sessionManager.getTimeOnStop();
            long extra = 0;
            if (current_time < pass_time)
                extra = 24;
            int secs_added = (int) (current_time - pass_time + extra) / 1000;
            adapter.resume(secs_added,sessionManager.getRedoTask());
            sessionManager.setRedoTask(false);
        }
    }

    @Override
    protected void onDestroy() {
        setActivitysGone();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        setActivityVisible();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        sessionManager.setTaskList(new TaskContainer(tasks));
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else {
            interstitialAd.show();
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sessionManager.setTaskList(new TaskContainer(tasks));
    }
    //menu


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
    class PrepareAd extends AsyncTask<Void,AdRequest,AdRequest> {
        Context context;
        AdView adView;
        InterstitialAd interstitialAd;
        public PrepareAd(Context context,AdView adView,InterstitialAd interstitialAd){
            this.context=context;
            this.adView=adView;
            this.interstitialAd=interstitialAd;
        }

        @Override
        protected AdRequest doInBackground(Void... voids) {
            MobileAds.initialize(context,"ca-app-pub-7832644521881287~8918320977");
            AdRequest adRequest=new AdRequest.Builder().build();
            //onProgressUpdate(adRequest);
            interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            //AdRequest adRequest1=new AdRequest.Builder().build();
            return adRequest;
        }

        @Override
        protected void onProgressUpdate(AdRequest... ads) {
            super.onProgressUpdate(ads);
            adView.loadAd(ads[0]);
        }

        @Override
        protected void onPostExecute(AdRequest adRequest) {
            super.onPostExecute(adRequest);
            interstitialAd.loadAd(adRequest);
            adView.loadAd(adRequest);
        }
    }
}
