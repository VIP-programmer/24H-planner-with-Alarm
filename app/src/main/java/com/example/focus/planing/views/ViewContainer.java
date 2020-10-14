package com.example.focus.planing.views;

import com.google.android.material.badge.BadgeDrawable;

import java.util.Vector;

public class ViewContainer {
    private Vector<ViewX> vector;

    public ViewContainer() {
        vector=new Vector<>();
    }
    public void addViewX(ViewX viewX){
        vector.add(viewX);
    }

    public Vector<ViewX> getVector() {
        return vector;
    }

    public ViewX getIndex(int ind){
        return vector.get(ind);
    }
    public ViewX getById(int id){
        for (ViewX viewX:vector){
            if (id==viewX.getId())
                return viewX;
        }
        return null;
    }
    public void inselectAll(){
        for (ViewX viewX:vector){
            if (viewX.isSelected())
                viewX.inselect();
        }
    }
}
