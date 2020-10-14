package com.example.focus.planing;

import android.widget.NumberPicker;

import com.example.focus.R;

import java.util.Calendar;

public class HourPickerManager {

    /**
     * Config:
     * for HH:MIN:SEC
     */
    public static final int HOUR_ID_FOR_LONG= R.id.pickerHours;
    public static final int MIN_ID_FOR_LONG=R.id.pickerMinutes;
    public static final int SEC_ID_FOR_LONG=R.id.pickerSeconds;

    /**
     * Config:
     * for HH:MM
     */
    public static final int START_HOUR_ID_FOR_SHORT=R.id.pickerHoursS;
    public static final int START_MIN_ID_FOR_SHORT=R.id.pickerMinutesS;
    public static final int END_HOUR_ID_FOR_SHORT=R.id.pickerHoursE;
    public static final int END_MIN_ID_FOR_SHORT=R.id.pickerMinutesE;

    public NumberPicker hour;
    public NumberPicker min;
    public NumberPicker sec;
    public boolean isStart;
    public boolean isFull;

    public HourPickerManager(boolean isFull,NumberPicker ...ids) {
        this.isFull=isFull;
        this.isStart=isStart;
        if (isFull){
            hour=ids[0];
            min=ids[1];
            sec=ids[2];
            min.setMaxValue(59);
            min.setMinValue(0);
            sec.setMaxValue(59);
            sec.setMinValue(0);
        }else{
            hour=ids[0];
            min=ids[1];
            min.setMaxValue(59);
            min.setMinValue(0);
            /*
            min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    picker.setValue((newVal < oldVal)?oldVal-5:oldVal+5);
                }

            });
             */
            sec=min;
        }
        hour.setMaxValue(23);
        hour.setMinValue(0);
    }

    public void resetInputs() {
        hour.setValue(0);
        min.setValue(0);
        sec.setValue(0);
    }
    public boolean isValid(){
        if (sec.getValue()!=0 || min.getValue()!=0 || hour.getValue()!=0)
            return true;
        return false;
    }
    public boolean isOlder(HourPickerManager picker){
        if (hour.getValue() < picker.hour.getValue())
            return true;
        if (hour.getValue() == picker.hour.getValue() && min.getValue() < picker.min.getValue())
            return true;
        return false;
    }

    public void setDefault() {
        Calendar calendar=Calendar.getInstance();
        hour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        min.setValue(calendar.get(Calendar.MINUTE));
        sec=min;
    }

    public void setDefault(int newHour, int newMin) {
        hour.setValue(newHour);
        min.setValue(newMin);
        sec=min;
    }
}
