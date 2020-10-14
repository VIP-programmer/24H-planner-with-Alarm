package com.example.focus.planing.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.focus.R;
import com.example.focus.planing.DayPlaner;
import com.example.focus.planing.HourItem;

import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.OwnViewHolder> {
    Context context;
    Vector<ViewContainer> hourItems;
    public NewAdapter(Context context, Vector<ViewContainer> hourItems) {
        this.context=context;
        this.hourItems=hourItems;
    }

    @NonNull
    @Override
    public NewAdapter.OwnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_plan_item,parent,false);
        return new NewAdapter.OwnViewHolder(v,context);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnViewHolder holder, int position) {
        ViewContainer viewContainer=hourItems.get(position);
        holder.hourOfDay.setText(position+"");
        clearAll(holder.linearLayout);
        for (ViewX element:viewContainer.getVector()){
            if(element.getView().getParent() != null) {
                ((ViewGroup)element.getView().getParent()).removeView(element.getView());
            }
            holder.linearLayout.addView(element.getView());
        }

    }
    private void clearAll(ViewGroup linearLayout) {
        if (((LinearLayout)linearLayout).getChildCount()>0)
            ((LinearLayout)linearLayout).removeAllViews();
    }

    @Override
    public int getItemCount() {
        return hourItems.size();
    }

    public class OwnViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView hourOfDay;
        Context context;
        public OwnViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context=context;
            linearLayout=(LinearLayout) itemView.findViewById(R.id.full_day_task);
            hourOfDay=(TextView) itemView.findViewById(R.id.hour_of_day);
        }
    }
    @Override
    public long getItemId(int position) {
        return hourItems.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
