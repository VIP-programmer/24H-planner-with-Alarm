package com.example.focus.BGS;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.focus.R;
import com.example.focus.SessionManager;

import java.io.File;
import java.util.Objects;
import java.util.Vector;

public class BGManager {

    private Vector<String> vector;

    public BGManager() {
        this.vector = new Vector<>();
    }

    public void init(){
        Uri uri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://com.example.focus/drawable/bg0.jpg");
        vector.add(uri.toString());
        uri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://com.example.focus/drawable/bg1.jpg");
        vector.add(uri.toString());
        uri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://com.example.focus/drawable/bg2.jpg");
        vector.add(uri.toString());
        uri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://com.example.focus/drawable/bg3.jpg");
        vector.add(uri.toString());

        //vector.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.bg1));
    }

    public void add(Vector<String> uris){
        vector.addAll(uris);
    }
    public void add(String uri){
        vector.add(uri);
    }

    public Vector<String> getVector() {
        return vector;
    }

    public void clear(){
        vector.clear();
    }
    public int mySize(){
        return vector.size();
    }

    public void removeUnused(){
        /*
        for (String uri: vector){
            File imgF=new File(Objects.requireNonNull(uri.getPath()));
            if (!imgF.exists())
                vector.remove(uri);
        }

         */
    }
}
