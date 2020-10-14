package com.example.focus.planing;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.focus.R;

import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CreatedTaskAdapter extends RecyclerView.Adapter<CreatedTaskAdapter.OwnViewHolder>{

    Vector<CreatedTask> createdTasks;
    Activity context;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemDelete(int position);
        void onItemEdit(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CreatedTaskAdapter(Activity context, Vector<CreatedTask> createdTasks) {
        this.context=context;
        this.createdTasks=createdTasks;
    }

    @NonNull
    @Override
    public CreatedTaskAdapter.OwnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.created_task,parent,false);
        return new OwnViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CreatedTaskAdapter.OwnViewHolder holder, int position) {
        CreatedTask createdTask=createdTasks.get(position);

        holder.linearLayout.setBackgroundTintList(context.getResources().getColorStateList(createdTask.getColor()));
        holder.name.setText(createdTask.getName());
        holder.start.setText(createdTask.getStartAt());
        holder.end.setText(createdTask.getEndAt());
    }

    @Override
    public int getItemCount() {
        return createdTasks.size();
    }

    public static class OwnViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView start;
        public TextView end;
        public ImageView edit;
        public ImageView delete;
        public LinearLayout linearLayout;
        public OwnViewHolder(@NonNull View itemView, final OnItemClickListener mListener) {
            super(itemView);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.createdNewTasksContainer);
            name=(TextView) itemView.findViewById(R.id.createsTaskName);
            start=(TextView) itemView.findViewById(R.id.createsTaskStart);
            end=(TextView) itemView.findViewById(R.id.createsTaskEnd);
            edit=(ImageView) itemView.findViewById(R.id.modifyCreatedTask);
            delete=(ImageView) itemView.findViewById(R.id.deleteCreatedTask);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null){
                        int position=getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemEdit(position);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null){
                        int position=getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemDelete(position);
                        }
                    }
                }
            });

        }
    }

    @Override
    public long getItemId(int position) {
        return createdTasks.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
