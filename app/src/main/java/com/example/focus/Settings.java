package com.example.focus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.focus.BGS.BGAdapter;
import com.example.focus.BGS.BGManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Settings extends AppCompatActivity {

    private Button tune;
    private Button backg;

    //tune variables
    private RadioGroup radioGroup;
    //private int[] tunes;
    private Map<Integer, Integer> tunes;
    private Button okTune;
    private Button cancelTune;
    private Button addBG;
    private Button okBG;
    MediaPlayer mediaPlayer;
    BottomSheetBehavior bottomSheetBehaviorTune;
    BottomSheetBehavior bottomSheetBehaviorBg;
    LinearLayout tune_sheet;
    RecyclerView recyclerViewBg;
    BGManager bgManager;
    BGAdapter bgAdapter;
    Activity context;
    Vector<String> uris;
    SessionManager sessionManager;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_settings);
        sessionManager=new SessionManager(this);
        context=this;
        Config.init();
        //tunes= new int[]{R.raw.simple_ringtone_1,R.raw.simple_ringtone_2,R.raw.simple_ringtone_3,R.raw.simple_ringtone_4,R.raw.simple_ringtone_5,R.raw.simple_ringtone_6,R.raw.simple_ringtone_7};

        tunes= new HashMap<>();
        tunes.put(R.id.tune1,R.raw.simple_ringtone_1);
        tunes.put(R.id.tune2,R.raw.simple_ringtone_2);
        tunes.put(R.id.tune3,R.raw.simple_ringtone_3);
        tunes.put(R.id.tune4,R.raw.simple_ringtone_4);
        tunes.put(R.id.tune5,R.raw.simple_ringtone_5);
        tunes.put(R.id.tune6,R.raw.simple_ringtone_6);
        tunes.put(R.id.tune7,R.raw.simple_ringtone_7);


        tune=(Button)findViewById(R.id.choseTune);
        okTune=(Button)findViewById(R.id.valideTune);
        okBG=(Button)findViewById(R.id.valideBG);
        addBG=(Button)findViewById(R.id.addBG);
        cancelTune=(Button)findViewById(R.id.cancelTune);
        backg=(Button)findViewById(R.id.choseBg);
        radioGroup=(RadioGroup) findViewById(R.id.tuneGroup);
        tune_sheet=findViewById(R.id.tune_sheet);
        bottomSheetBehaviorTune=BottomSheetBehavior.from(tune_sheet);
        bottomSheetBehaviorBg=BottomSheetBehavior.from(findViewById(R.id.bg_sheet));
        recyclerViewBg=(RecyclerView) findViewById(R.id.bgs);
        adView=findViewById(R.id.adSetting);

        //for ads
        PrepareAd prepareAd=new PrepareAd(this,adView);
        prepareAd.execute();

        bgManager=sessionManager.getUris();
        uris =new Vector<>();
        if (bgManager.mySize()==0)
            bgManager.init();

        uris.addAll(bgManager.getVector());
        bgAdapter=new BGAdapter(this, uris);
        bgAdapter.setHasStableIds(true);
        recyclerViewBg.setHasFixedSize(true);
        recyclerViewBg.setLayoutManager(new GridLayoutManager(this,2));
        recyclerViewBg.setAdapter(bgAdapter);

        bgAdapter.setOnItemClickListener(new BGAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int position) {
                uris.remove(position);
                bgAdapter.notifyItemRemoved(position);
                bgAdapter.notifyItemRangeChanged(position,bgAdapter.getItemCount());
            }
        });


        tune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehaviorTune.setState(BottomSheetBehavior.STATE_EXPANDED);
                radioGroup.check(Config.SONG);
            }
        });

        cancelTune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehaviorTune.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        addBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(context);

            }
        });
        okBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgManager.clear();
                bgManager.add(uris);
                sessionManager.setUris(bgManager);
                bottomSheetBehaviorBg.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        backg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bgManager.removeUnused();
                uris.clear();
                uris.addAll(bgManager.getVector());
                bgAdapter.notifyDataSetChanged();
                bottomSheetBehaviorBg.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        okTune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                Config.SONG=radioGroup.getCheckedRadioButtonId();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mediaPlayer!=null && mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                playMusic(tunes.get(checkedId));
            }
        });
    }

    private void playMusic(int tune) {
        mediaPlayer=MediaPlayer.create(this,tune);
        mediaPlayer.start();
        /*
        CountDownTimer countDownTimer=new CountDownTimer(13000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mediaPlayer.start();
            }

            @Override
            public void onFinish() {
                mediaPlayer.release();
            }
        };countDownTimer.start();

         */
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                addThisBg(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    private void addThisBg(Uri uri) {
        uris.add(uri.toString());
        bgAdapter.notifyDataSetChanged();
    }
    class PrepareAd extends AsyncTask<Void,Void,AdRequest>{
        Context context;
        AdView adView;
        public PrepareAd(Context context,AdView adView){
            this.context=context;
            this.adView=adView;
        }

        @Override
        protected AdRequest doInBackground(Void... voids) {
            MobileAds.initialize(context,"ca-app-pub-7832644521881287~8918320977");
            AdRequest adRequest=new AdRequest.Builder().build();
            return adRequest;
        }

        @Override
        protected void onPostExecute(AdRequest adRequest) {
            super.onPostExecute(adRequest);
            adView.loadAd(adRequest);
        }
    }
}
