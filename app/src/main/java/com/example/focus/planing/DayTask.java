package com.example.focus.planing;

import android.util.ArrayMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DayTask {
    //identifecation
    private String Name;
    private int color;
    //duration
    private int startHour;
    private int endHour;
    private int startMin;
    private int endMin;
    //is it less then one hour
    private boolean isLessThenOneHour=false;
    //define the half hours
    private Vector<Integer> halfHours;
    //get values in between
    private Map<Integer,Map<Integer,int[]>> inBetweenMap;

    public DayTask(String name, int startHour, int endHour, int startMin, int endMin, int color) {
        Name = name;
        this.startHour = startHour;
        this.endHour = endHour;
        this.startMin = startMin;
        this.endMin = endMin;
        this.color=color;
        if (startHour == endHour)
            isLessThenOneHour=true;
        defineHalfHours();
        defineInBetween();
    }

    public Vector<Integer> getFullHours(){
        Vector<Integer> vector=new Vector<>();
        if (isLessThenOneHour)
            return vector;
        if (startMin == 0)
            vector.add(startHour);
        for (int i = startHour+1; i < endHour; i++) {
            vector.add(i);
        }

        return vector;
    }

    public void defineHalfHours(){
        halfHours=new Vector<>();
        if (isLessThenOneHour || startMin != 0)
            halfHours.add(startHour);

        if (endMin != 0 && !isLessThenOneHour)
            halfHours.add(endHour);

    }

    public void defineInBetween(){
        inBetweenMap = new HashMap<>();
        Map<Integer,int[]> temp=new HashMap<>();
        int k=startMin;
        if (k > 0){
            if (isLessThenOneHour) {
                temp.put(startMin, new int[]{endMin - startMin, color});
            }else{
                temp.put(startMin,new int[]{-1, color});
            }
            inBetweenMap.put(startHour,temp);
        }else{
            if (endMin != 0 && isLessThenOneHour) {
                temp.put(0, new int[]{endMin,color});
                inBetweenMap.put(startHour,temp);
            }
        }
        temp=new HashMap<>();
        if (endMin !=0 && !isLessThenOneHour) {
            temp.put(0, new int[]{endMin,color});
            inBetweenMap.put(endHour,temp);
        }
    }

    public Boolean hasThisAsHalfHour(int hour){
        if (halfHours.contains(hour))
            return true;
        return false;
    }
    public Map<Integer,int[]> getInBetween(int hour){
        if (inBetweenMap.containsKey(hour)) {
            return inBetweenMap.get(hour);
        }
        return null;
    }

    public Vector<Integer> getHalfHours(){
        return halfHours;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return Name;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public int getEndMin() {
        return endMin;
    }

    public boolean containsThis(int hour, int min) {
        //ila kano f nfs sa3a
        if (startHour==endHour){
            if (hour==startHour && min>=startMin && endMin>min)
                return true;
        }
        //ila b3adin
        else {
            //ila kant hour bhal start hour
            if (hour==startHour){
                if (min>=startMin)
                    return true;
            }
            //ila kant bhal end
            if (hour==endHour){
                if (min<endMin)
                    return true;
            }
            //ila kant binathom
            if (hour>startHour && hour<endHour){
                return true;
            }
        }
        return false;
    }

    public boolean isLessThenOneHour() {
        return isLessThenOneHour;
    }
}
