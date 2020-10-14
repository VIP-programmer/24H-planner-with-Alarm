package com.example.focus.planing.views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.focus.R;

import androidx.core.content.ContextCompat;

public class ViewX {
    private android.view.View view;
    private Context context;
    private boolean selected;
    private int color;
    private int id;

    public ViewX(Context context, int id, int color, int width, float weight) {
        this.context=context;
        this.color=color;
        this.id=id;
        selected=false;
        view=new android.view.View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.MATCH_PARENT,
                weight
        ));
        view.setId(id);
        view.setBackgroundResource(color);
    }

    public void select(){
        selected=true;
        view.setBackgroundResource(R.drawable.indicator_selected_task);
        view.setBackgroundTintList(ContextCompat.getColorStateList(context, color));
    }

    public void inselect(){
        selected=false;
        view.setBackgroundResource(color);
        view.setBackgroundTintList(ContextCompat.getColorStateList(context, color));
    }

    public boolean isSelected() {
        return selected;
    }

    public android.view.View getView() {
        return view;
    }

    public int getId() {
        return id;
    }
}
