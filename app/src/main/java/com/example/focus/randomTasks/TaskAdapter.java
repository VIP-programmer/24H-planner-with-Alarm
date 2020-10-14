package com.example.focus.randomTasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focus.R;

import java.util.HashMap;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    static boolean onProgress=false;
    static Vector<Task> tasks;
    private Activity context;
    Thread thread;
    HashMap<Integer,TaskViewHolder> holderlist = new HashMap<>();
    private volatile int sec_added;
    private volatile boolean activeResume;
    private volatile boolean redoing;
    //Test
    public TaskViewHolder taskViewHoldering;

    /**
     * declaring interface vars
     */
    private OnItemClickListener mListener;
    private GetHolder getHolder;
    private OnTaskStat taskStat;
    private OnItemStartListener startListener;

    /**
     * interfaces
     */
    public interface OnItemClickListener {
        void onItemDelete(int position);

        void onRedo(int position);
    }

    public interface OnItemStartListener {
        void onItemStart(int position);
    }

    public interface GetHolder {
        void getInfos(Vector<Task> tasks, int position);
        void getHolder(TaskViewHolder holder,int position);
    }

    public interface OnTaskStat {
        void started(Task task);
        void stoped();
        void completed();
    }

    /**
     * interface setters
     * @param listener
     */
    public void setOnItemStartListener(OnItemStartListener listener){
        startListener=listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setGetHolder(GetHolder getHolder) {
        this.getHolder = getHolder;
    }

    public void setOnTaskStat(OnTaskStat taskStat) {
        this.taskStat = taskStat;
    }

    public void resume(int secs_added, boolean redoTask) {
        this.sec_added=secs_added;
        activeResume=true;
        redoing=redoTask;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public TextView taskName;
        public TextView percent;
        public TextView time;
        public TextView action;
        public RelativeLayout container;
        public RelativeLayout parent;
        public ImageView background;
        public Button delete;
        public Button redo;



        public TaskViewHolder(View itemView, final OnItemClickListener listener, final GetHolder getHolder) {
            super(itemView);

            taskName=(TextView) itemView.findViewById(R.id.task_name);
            percent=(TextView) itemView.findViewById(R.id.percent);
            time=(TextView) itemView.findViewById(R.id.time);
            action=(TextView) itemView.findViewById(R.id.action);
            background=(ImageView) itemView.findViewById(R.id.background_img);
            container=(RelativeLayout) itemView.findViewById(R.id.container);
            parent=(RelativeLayout) itemView.findViewById(R.id.Parent_layout);
            delete=(Button) itemView.findViewById(R.id.delete);
            redo=(Button) itemView.findViewById(R.id.redo);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            onProgress=false;
                            listener.onItemDelete(position);
                        }
                    }
                }
            });
            redo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redo.setVisibility(View.GONE);
                    if (listener!=null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRedo(position);
                            //initTask();
                            action.setVisibility(View.VISIBLE);
                            percent.setTextSize(17);
                            percent.setText("0%");
                            container.getBackground().setLevel(0);
                            parent.setBackgroundResource(R.drawable.task_layout_shape);
                            time.setTextColor(Color.WHITE);
                            percent.setTextColor(Color.BLACK);
                            taskName.setTextColor(Color.BLACK);
                            taskName.setBackgroundResource(tasks.get(position).getNameBg());
                        }
                    }
                }
            });
        }
    }

    public TaskAdapter(Activity context, Vector<Task> tasks) {
        this.tasks = tasks;
        this.context=context;

    }

    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);
        return new TaskViewHolder(v,mListener,getHolder);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {
        final Task task=tasks.get(position);
        if(!holderlist.containsKey(position)){
            holderlist.put(position,holder);
        }

        ViewOutlineProvider mViewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(final View view, final Outline outline) {
                float cornerRadiusDP = 13f;
                float cornerRadius = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, cornerRadiusDP, context.getResources().getDisplayMetrics());
                outline.setRoundRect(0, 0, view.getWidth(), (int)(view.getHeight() + cornerRadius), cornerRadius);
            }
        };

        if(getHolder!=null)
            getHolder.getHolder(holder,tasks.indexOf(task));


        int hour=task.getDuration()/3600;
        int min=(task.getDuration()-hour*3600)/60;
        int sec=task.getDuration()-hour*3600-min*60;
        holder.taskName.setText(task.getName());
        holder.background.setOutlineProvider(mViewOutlineProvider);
        holder.background.setClipToOutline(true);
        holder.percent.setText(task.getPercent()+"%");
        holder.time.setText(hour+"H "+min+"min "+sec+"s");
        //holder.action.setText("Start");
        holder.background.setImageBitmap(task.getBitmap(context));
        String msg;
        if (task.isStart()) msg="stop";
        else msg="start";
        updateBgFg(holder,task,msg);
        holder.container.getBackground().setLevel(task.getPercent()*100);

        if (task.isCompleted())
            setCompletedTask(holder);
        if(!task.isStart() && task.getDurationDone()>0)
            holder.redo.setVisibility(View.VISIBLE);

        if (position==0){
            taskViewHoldering=holder;
            if ((task.isStart() && activeResume)|| redoing) {
                redoing=false;
                doUrTask(task, holder, sec_added);
                activeResume=false;
            }
        }

        holder.action.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (task.isStart()) {
                    if (taskStat!=null)
                        taskStat.stoped();
                    task.pause();
                    updateBgFg(holder,task,"Start");
                    //holder.action.setText("Start");
                    if (thread != null && thread.isAlive())
                        thread.interrupt();
                    onProgress=false;
                    holder.redo.setVisibility(View.VISIBLE);
                } else {
                    if (onProgress){
                        Toast.makeText(context,"Another task is on progress",Toast.LENGTH_SHORT).show();
                    }else {
                        if (startListener!=null) {
                            Task removedTask=tasks.get(position);
                            tasks.remove(position);
                            notifyItemRemoved(position);
                            tasks.add(0,removedTask);
                            notifyItemMoved(position,0);
                            notifyDataSetChanged();
                            startListener.onItemStart(position);
                        }
                        doUrTask(task,holder, 0);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public void setCompletedTask(TaskViewHolder holder){
        holder.action.setVisibility(View.INVISIBLE);
        holder.percent.setText("Completed !");
        holder.percent.setTextSize(16);
        holder.percent.setTextColor(Color.parseColor("#263238"));
        holder.container.getBackground().setLevel(0);
        holder.parent.setBackgroundResource(R.drawable.tast_done_shape);
        holder.time.setTextColor(Color.WHITE);
        holder.percent.setTextColor(Color.WHITE);
        holder.taskName.setTextColor(Color.WHITE);
    }
    static void initTask(TaskViewHolder holder){

    }

    public void updateBgFg(TaskViewHolder holder,Task task,String msg){
        holder.taskName.setBackgroundResource(task.getNameBg());
        holder.background.setForeground(context.getDrawable(task.getForeground()));
        Log.i(TAG, "id8890 updateBgFg: text exist "+holder.action.getText());
        Log.i(TAG, "id8890 updateBgFg: msg to put "+msg);
        Log.i(TAG, "id8890 updateBgFg: task "+tasks.indexOf(task));
        holder.action.setText(msg);
        Log.i(TAG, "id8890 updateBgFg: text became "+holder.action.getText());
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    public void doUrTask(final Task task, final TaskViewHolder holder, int secs_added){
        Log.i(TAG, "id890 doUrTask,: "+task.getName());

        task.setDurationDone(task.getDurationDone()+secs_added);
        if (taskStat!=null && task.getDurationDone()<task.getDuration())
            taskStat.started(task);
        onProgress=true;
        task.setStart(true);
        holder.redo.setVisibility(View.GONE);
        updateBgFg(taskViewHoldering,task,"Stop");
        thread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted() && task.isStart()) {
                    try {
                        Thread.sleep(1000);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(getHolder!=null)
                                    getHolder.getInfos(tasks,tasks.indexOf(task));
                                task.move();
                                taskViewHoldering.percent.setText(task.getPercent() + "%");
                                Drawable backgr=taskViewHoldering.container.getBackground();
                                Log.i(TAG, "idFUCKrun: BG ->"+backgr.toString());
                                Log.i(TAG, "idFUCKrun: LVL ->"+backgr.getLevel());
                                taskViewHoldering.container.getBackground().setLevel(task.getPercent() * 100);
                                if (task.isCompleted()) {
                                    taskStat.completed();
                                    updateBgFg(taskViewHoldering,task,"Start");
                                    setCompletedTask(taskViewHoldering);
                                    thread.interrupt();
                                    onProgress=false;
                                    holder.redo.setVisibility(View.VISIBLE);
                                }
                                Log.i("TAG", "id889 run: " + task.getPercent()+", secs: "+task.getDurationDone());
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        //hta lhna
    }

    public void killSafely(int position){
        Task task=tasks.get(position);
        if (task.isStart()) {
            thread.interrupt();
            task.stop();
        }
    }


}
