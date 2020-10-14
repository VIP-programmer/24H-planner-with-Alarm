package com.example.focus.randomTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.focus.BGS.BGManager;
import com.example.focus.R;
import com.example.focus.SessionManager;

import java.io.FileNotFoundException;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    Vector<String> stringVector;

    public ViewPagerAdapter(Context context) {
        SessionManager sessionManager=new SessionManager(context);
        this.context = context;
        BGManager bgManager=sessionManager.getUris();
        if (bgManager.mySize()==0)
            bgManager.init();
        stringVector=bgManager.getVector();
    }

    @Override
    public int getCount() {
        return stringVector.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.images_inside_view_pager, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageOfViewPager);
        //creating bg from string
        String element=stringVector.get(position);
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
        imageView.setImageBitmap(bitmap);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
