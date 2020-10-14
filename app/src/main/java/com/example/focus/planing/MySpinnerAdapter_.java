package com.example.focus.planing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.example.focus.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MySpinnerAdapter_ extends ArrayAdapter<Integer> {
    int[] colors;
    Context context;
    LayoutInflater inflter;

    public MySpinnerAdapter_(@NonNull Context context, int resource, @NonNull List<Integer> objects) {
        super(context, resource, objects);
        this.context=context;
        inflter = (LayoutInflater.from(context));
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

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.simple_spinner_item, parent, false
            );
        }

        LinearLayout linearLayout=(LinearLayout) convertView.findViewById(R.id.simpleSpinnerItem);
        if (getItem(position) != null) {
            linearLayout.setBackgroundResource(colors[position]);
        }
        return convertView;
    }
}
