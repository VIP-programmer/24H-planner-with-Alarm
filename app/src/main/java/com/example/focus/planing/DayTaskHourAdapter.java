package com.example.focus.planing;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.focus.R;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DayTaskHourAdapter extends RecyclerView.Adapter<DayTaskHourAdapter.OwnViewHolder>{
    private Vector<HourItem> hourItems;
    private Activity context;
    static Vector<Integer> selectedID=new Vector<>();

    public DayTaskHourAdapter(Activity context,Vector<HourItem> hourItems) {
        this.hourItems = hourItems;
        this.context=context;
    }

    @NonNull
    @Override
    public DayTaskHourAdapter.OwnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_plan_item,parent,false);
        return new DayTaskHourAdapter.OwnViewHolder(v,context);
    }

    @Override
    public void onBindViewHolder(@NonNull DayTaskHourAdapter.OwnViewHolder holder, int position) {
        HourItem hourItem=hourItems.get(position);
        holder.hourOfDay.setText(hourItem.getIndex()+"");

        if (hourItem.isAllow()) {

            clearAll(holder.linearLayout);
            hourItem.setAllow(false);
            View view=new View(context);
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            view.setId(position*100);
            if (hourItem.isFull()) {
                redefineColor(view,hourItem.getColorForFull());
                holder.linearLayout.addView(view);
            }else if (hourItem.isEmpty()) {
                redefineColor(view,R.color.default_task_color);
                holder.linearLayout.addView(view);
            }else {
                Map<Integer, Object[]> map = new TreeMap<>(hourItem.getStructure());
                for (ArrayMap.Entry<Integer, Object[]> mapElement : map.entrySet()) {
                    float weight;
                    int partOfId;
                    try {
                        weight = (Float) mapElement.getValue()[0];
                        partOfId=(int)mapElement.getValue()[2];
                    }catch (Exception e){
                        weight = ((Double) mapElement.getValue()[0]).floatValue();
                        partOfId=((Double) mapElement.getValue()[2]).intValue();
                    }

                    View view1=new View(context);
                    view1.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            weight
                    ));
                    view1.setId(position*100+partOfId);
                    String d= (String) mapElement.getValue()[1];
                    int i= Integer.parseInt(d);
                    redefineColor(view1,i);
                    //view1.setBackgroundResource(i);
                    holder.linearLayout.addView(view1);
                    /*
                    String d= (String) mapElement.getValue()[1];
                    int i= Integer.parseInt(d);
                    try {
                        holder.addViewItem(i, ((Double) mapElement.getValue()[0]).floatValue());
                    }catch (Exception e){
                        holder.addViewItem(i, (float) mapElement.getValue()[0]);
                    }

         */
                }

            }
        }

    }

    private void redefineColor(View view, int color) {
        if (selectedID.contains(view.getId())){
            view.setBackgroundResource(R.drawable.indicator_selected_task);
            view.setBackgroundTintList(ContextCompat.getColorStateList(context, color));
        }else{
            view.setBackgroundResource(color);
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

    public static class OwnViewHolder extends RecyclerView.ViewHolder {
         LinearLayout linearLayout;
         TextView hourOfDay;
         Activity context;
        public OwnViewHolder(@NonNull View itemView, Activity context) {
            super(itemView);
            this.context=context;
            linearLayout=(LinearLayout) itemView.findViewById(R.id.full_day_task);
            hourOfDay=(TextView) itemView.findViewById(R.id.hour_of_day);
        }

        public void addViewItem(int color,float weight){
            View layout = LayoutInflater.from(context).inflate(R.layout.view_inside_linierlayout, linearLayout, false);
            View element=(View) layout.findViewById(R.id.element_i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,weight);
            element.setBackgroundTintList(context.getResources().getColorStateList(color));
            element.setLayoutParams(lp);
            linearLayout.addView(layout);
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
