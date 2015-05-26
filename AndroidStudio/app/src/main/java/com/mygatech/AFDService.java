package com.mygatech;

import com.mygatech.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

public class AFDService extends Service {
	private static MediaPlayer mMediaPlayer;
	
	public int onStartCommand(Intent intent, int flag, int startId){
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = {100, 0, 100, 0, 100, 0, 100, 0, 100,0};
		v.vibrate(pattern, 1);
		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    int maxVolumeMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeMusic,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	    mMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.iamdoingthat);
	    mMediaPlayer.start();
	    Camera camera = Camera.open();
		Parameters p = camera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(p);
		camera.startPreview();
		if(getSharedPreferences(MainActivity.USER_DETAILS,0).getString("username", null).equals("forkjoseph"))
			return START_NOT_STICKY;
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
