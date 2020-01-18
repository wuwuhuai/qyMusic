package com.zhuoren.musicplayer;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.AbstractSequentialList;
import java.util.Map;
import java.util.AbstractMap;
import java.util.SortedMap;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Queue;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.AbstractSet;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.Stack;
import java.util.Vector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Object;








public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    MyService mService;
    Button play,pause,stop;
    Timer timer = new Timer();
    int MAX=0;
    ObjectAnimator animator;

    TextView timeCurrent,timeTotal;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");


    Date day = new Date();
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            if(msg==null)return;
            switch (msg.what) {
                case 1:
                    if (seekBar.getProgress() <= seekBar.getMax()) {
                        int currentPosition = mService.getPosition();
                        seekBar.setProgress(currentPosition);
                        timeCurrent.setText(time.format(mService.mediaPlayer.getCurrentPosition()));

                        break;
                    }
                    super.handleMessage(msg);
            }
        }
    }

    MyHandler handler=new MyHandler();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = Message.obtain();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        ImageView imageView = (ImageView) findViewById(R.id.Image);
        animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);




        
        Intent intent = new Intent( MainActivity.this,MyService.class);
        bindService(intent,conn, Service.BIND_AUTO_CREATE);

        MyOnClickListener myOn=new MyOnClickListener();
        play=(Button) findViewById(R.id.play);
        play.setOnClickListener(myOn);
        pause=(Button) findViewById(R.id.pause);
        pause.setOnClickListener(myOn);
        stop=(Button) findViewById(R.id.stop);
        stop.setOnClickListener(myOn);

        timeCurrent=(TextView) findViewById(R.id.time_current);
        timeTotal=(TextView) findViewById(R.id.time_total);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.getThumb().setColorFilter(Color.parseColor("#000000"), PorterDuff. Mode. SRC_ATOP);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //按住拖动：考虑显示歌词
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //按下：考虑显示歌词
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //松开
                int progress = seekBar.getProgress();
                if(mService!=null) mService.seekTo(progress);
            }
        });

        timer.schedule(task, 1000, 1000);



    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play:
                    playMusic();
                    break;
                case R.id.pause:
                    pauseMusic();
                    break;
                case R.id.stop:
                    stopMusic();
                    break;

            }
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MyService.ServiceBinder) service).getService();
            if(mService==null) Log.e("TAG","ser_is_null");
            else {
                MAX=mService.getDuration();
                seekBar.setMax(MAX);
                timeTotal.setText(time.format(mService.mediaPlayer.getDuration()));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mService = null;
        }
    };

    public void playMusic() {
        if(mService!=null) {
            mService.play();
            animator.start();
        }
        animator.start();
    }

    public void pauseMusic() {
        if(mService!=null) {
            mService.pause();
            animator.pause();
        }
    }

    public void stopMusic() {
        if(mService!=null) {
            mService.stop();
            stopTimer();
        }
        MainActivity.this.finish();
    }

    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}