package com.example.focus.BGS;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.focus.R;

import java.io.FileNotFoundException;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BGAdapter extends RecyclerView.Adapter<BGAdapter.TaskViewHolder> {

    Vector<String> vector;
    Activity context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemDelete(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public BGAdapter(Activity context,Vector<String> vector) {
        this.vector = vector;
        this.context = context;
    }

    @NonNull
    @Override
    public BGAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bg_grid_item,parent,false);
        return new TaskViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BGAdapter.TaskViewHolder holder, int position) {
        String element=vector.get(position);
        Uri uri=Uri.parse(element);
        Bitmap bitmap = null;
        try {
            bitmap= BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap==null){
            String part=element.substring(element.lastIndexOf('/')+1,element.lastIndexOf('.'));
            int image=0;
            switch (part){
                case "bg0":
                    image=R.drawable.bg0;
                    break;
                case "bg1":
                    image=R.drawable.bg1;
                    break;
                case "bg2":
                    image=R.drawable.bg2;
                    break;
                case "bg3":
                    image=R.drawable.bg3;
                    break;
            }
            bitmap=BitmapFactory.decodeResource(context.getResources(),image);
        }
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return vector.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        Button button;

        public TaskViewHolder(@NonNull View itemView, final OnItemClickListener mListener) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.bg_item_photo);
            button=(Button) itemView.findViewById(R.id.delete_bg_item);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION)
                        if (mListener!=null)
                            mListener.onItemDelete(position);
                }
            });
        }

    }
    @Override
    public long getItemId(int position) {
        return vector.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
