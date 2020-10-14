package com.example.focus;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public static int SONG=R.id.tune1;
    public static int[] IMAGES;
    public static Map<Integer,Integer> TUNES;

    public static void init() {
        TUNES=new HashMap<>();
        TUNES.put(R.id.tune1,R.raw.simple_ringtone_1);
        TUNES.put(R.id.tune2,R.raw.simple_ringtone_2);
        TUNES.put(R.id.tune3,R.raw.simple_ringtone_3);
        TUNES.put(R.id.tune4,R.raw.simple_ringtone_4);
        TUNES.put(R.id.tune5,R.raw.simple_ringtone_5);
        TUNES.put(R.id.tune6,R.raw.simple_ringtone_6);
        TUNES.put(R.id.tune7,R.raw.simple_ringtone_7);
    }
    public static int getSONG(){
        return TUNES.get(SONG);
    }
}
