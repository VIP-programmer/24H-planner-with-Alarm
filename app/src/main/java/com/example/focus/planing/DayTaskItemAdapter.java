package com.example.focus.planing;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.focus.R;
import com.example.focus.planing.views.NewAdapter;

import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DayTaskItemAdapter extends RecyclerView.Adapter<DayTaskItemAdapter.OwnViewHolder>{

    private final Vector<DayTask> tasks;
    private final Activity context;
    private onItemAction itemAction;
    static DayTask slected;
    static int allow=1;

    public interface onItemAction{
        void onItemSelected(DayTask dayTask, DayTask task, boolean b);
    }

    public void setOnItemAction(onItemAction itemAction){
        this.itemAction=itemAction;
    }

    public DayTaskItemAdapter(Activity context, Vector<DayTask> tasks) {
        this.tasks = tasks;
        this.context=context;

    }
    @NonNull
    @Override
    public OwnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_plan_item_menu,parent,false);
        return new OwnViewHolder(v,itemAction,tasks);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnViewHolder holder, int position) {
        final DayTask task=tasks.get(position);
        holder.container.setBackgroundTintList(context.getResources().getColorStateList(task.getColor()));
        holder.taskName.setText(task.getName());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class OwnViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        LinearLayout container;
        public OwnViewHolder(@NonNull View itemView, final onItemAction itemSelected, final Vector<DayTask> tasks) {
            super(itemView);
            taskName=(TextView) itemView.findViewById(R.id.task_name);
            container=(LinearLayout) itemView.findViewById(R.id.task_container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemSelected!=null) {
                        int pos=getAdapterPosition();
                        if (pos!= RecyclerView.NO_POSITION) {
                            if(DayTaskItemAdapter.slected == null){
                                itemSelected.onItemSelected(tasks.get(pos),null,true);
                                DayTaskItemAdapter.slected=tasks.get(pos);

                            }else {
                                boolean b=true;
                                if (tasks.get(pos)==DayTaskItemAdapter.slected) {
                                    b = false;
                                    DayTaskItemAdapter.slected=null;
                                    itemSelected.onItemSelected(tasks.get(pos),DayTaskItemAdapter.slected, b);
                                }else{
                                    itemSelected.onItemSelected(tasks.get(pos),DayTaskItemAdapter.slected, b);
                                    DayTaskItemAdapter.slected=tasks.get(pos);
                                }
                            }
                        }
                    }
                }
            });

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
