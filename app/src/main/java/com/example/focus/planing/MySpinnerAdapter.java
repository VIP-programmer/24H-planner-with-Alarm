package com.example.focus.planing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.example.focus.R;

import androidx.annotation.NonNull;

class MySpinnerAdapter extends BaseAdapter {
    int[] colors;
    Context context;
    LayoutInflater inflter;

    public MySpinnerAdapter(@NonNull Context context,int[] colors) {
        this.colors=colors;
        this.context=context;
        inflter = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.simple_spinner_item, null);
        LinearLayout linearLayout=(LinearLayout) view.findViewById(R.id.simpleSpinnerItem);
        linearLayout.setBackgroundTintList(context.getResources().getColorStateList(colors[position]));
        return view;
    }
}
