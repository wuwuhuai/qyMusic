package com.zhuoren.musicplayer;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {
    private final String TAG = "MyService";
    public  MediaPlayer mediaPlayer;
    private int startId=1;

    @Override
    public void onCreate() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this,R.raw.lovers);
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null && (mediaPlayer.isPlaying())) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        stopSelf(startId);
    }

    public void seekTo( int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    public int getPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class ServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private ServiceBinder binder = new ServiceBinder();
}