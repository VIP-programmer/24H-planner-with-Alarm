package com.example.focus.planing;

import android.util.ArrayMap;
import android.util.Log;

import com.example.focus.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static android.content.ContentValues.TAG;

public class HourItem {
    private int index;
    private boolean full=false;
    private boolean empty;
    private boolean allow=true;
    private int colorForFull;
    private Map<Integer,Object[]> structure;

    public HourItem(int index) {
        this.index = index;
    }

    public void setFull(int color) {
        full=true;
        colorForFull=color;
    }

    public boolean isFull() {
        return full;
    }

    private float toPercent(int value){
        return (float) value*100/60;
    }

    public void buildMySelf(Map<Integer, int[]> map) {
        int currentDuration=0;
        int spaceBetween=0;
        Vector<Integer> keys = new Vector<>(map.keySet());
        Collections.sort(keys);
        //Object[] keys = map.keySet().toArray();
        structure=new HashMap<>();
        if (map.size()==0){
            empty=true;
            //structure.put(0, new Object[]{toPercent(60), R.color.default_task_color});
        }else {
            int retard = (int)keys.get(0);

            /*
            structure.put(0, new Object[]{toPercent(retard), R.color.default_task_color+"",0});
            /*/

            int index = 1;
            int stape=0;
            if (retard==0) {
                stape++;
                currentDuration = Objects.requireNonNull(map.get((int)keys.get(0)))[0];
                Log.i(TAG, "idHbuildMySelf: "+currentDuration);
                structure.put(0, new Object[]{toPercent(Objects.requireNonNull(map.get((int)keys.get(0)))[0]), Objects.requireNonNull(map.get((int)keys.get(0)))[1]+"",0});
                if (1 == map.size()) {
                    spaceBetween = 60 - (currentDuration + (int)keys.get(0));
                } else {
                    spaceBetween = (int)keys.get(1) - (currentDuration + (int)keys.get(0));
                }

                Log.i(TAG, "idHbuildMySelf: "+spaceBetween);
                if (spaceBetween > 0) {
                    index++;
                    structure.put(1, new Object[]{toPercent(spaceBetween), R.color.default_task_color+"",keys.get(0)+currentDuration});
                }
            }else
                structure.put(0, new Object[]{toPercent(retard), R.color.default_task_color+"",0});


            for (int i = stape; i < map.size(); i++) {
                currentDuration = Objects.requireNonNull(map.get((int)keys.get(i)))[0];
                if (currentDuration == -1) {
                    structure.put(index, new Object[]{toPercent(60 - (int)keys.get(i)), Objects.requireNonNull(map.get((int)keys.get(i)))[1]+"",keys.get(i)});
                    return;
                }
                structure.put(index, new Object[]{toPercent(Objects.requireNonNull(map.get((int)keys.get(i)))[0]), Objects.requireNonNull(map.get((int)keys.get(i)))[1]+"",keys.get(i)});
                if (i == map.size() - 1) {
                    spaceBetween = 60 - (currentDuration + (int)keys.get(i));
                } else {
                    spaceBetween = (int)keys.get(i+1) - (currentDuration + (int)keys.get(i));
                }

                if (spaceBetween > 0) {
                    index++;
                    structure.put(index, new Object[]{toPercent(spaceBetween), R.color.default_task_color+"",keys.get(i)+currentDuration});
                }
                index++;
            }
        }
    }

    public void buildFull(){
        structure=new HashMap<>();
        structure.put(0, new Object[]{toPercent(60), colorForFull+"",0});
    }

    public int getIndex() {
        return index;
    }

    public int getColorForFull() {
        return colorForFull;
    }

    public boolean isEmpty() {
        return empty;
    }

    public Map<Integer, Object[]> getStructure() {
        return structure;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public boolean isAllow() {
        return allow;
    }
}
