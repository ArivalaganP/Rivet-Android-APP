package com.rivet.app.services;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.util.Log;

import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.rivet.app.common.ConnectionDetector;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.pojo.Story;

public class AudioService extends Service implements OnCompletionListener,
		OnPreparedListener, OnErrorListener {

	public static final String TAG = "AudioService";
	ResultReceiver resultReceiver;
	Timer timer;
	MediaPlayer player;
	WifiLock wifiLock;

	String url = "";
	String prevUrl = "";
	int duration = 0;
	private int lastBufferingPercent;
	private int latestBufferingPercent;
	private int bufferingCount = 0;
	boolean alreadyInitialized = false;
	boolean alreadyPrepared = false;
	private int totalDuration;
	private boolean isInActualQueue = true ;
	private int seekPos;
	private boolean isOnCompletion;
	private boolean isOrientationChanged;
	private int retryCount;

	


	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		initializeMediaPlayer();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (intent != null) {
			if (intent.getAction() != null) {

				String action = intent.getAction();
				retryCount = 0;
				isOnCompletion = false;
				isOrientationChanged = false;
				// check if we just have to player pause
				this.resultReceiver = intent.getParcelableExtra("receiver");

				if (action.equals(RConstants.playAudio)) {

					startPlayingFromPause();
					isOnCompletion = true;

				} else if (action.equals(RConstants.pauseAudio)) {

					pausePlaying();
					isOnCompletion = true;

				} else if (action.equals(RConstants.seekAudio)) {
					int seekPos = intent.getIntExtra(RConstants.PROGRESS, 0);
					player.seekTo(seekPos);
					isOnCompletion = true;
					resultReceiver.send(RConstants.UPDATE_UI_REWIND_ACTION,
							null);

				} else if (action.equals(RConstants.startNewAudio)) {
					seekPos = 0;
					RConstants.shouldUpdateTimer = false;
					isInActualQueue = intent.getBooleanExtra(
							RConstants.ACTUAL_QUEUE_FLAG, true);
					url = intent.getStringExtra(RConstants.DOWNLOAD_URL);
					if (player.isPlaying()) {
						player.stop();
					}

					resultReceiver.send(RConstants.UPDATE_UI_ACTION, null);
					player.reset();
					playSong(url);

				} else if (action.equals(RConstants.seekAudioByRewindQueue)) {
					// rewind with last played track

					RConstants.shouldUpdateTimer = false;
					url = intent.getStringExtra(RConstants.DOWNLOAD_URL);
					seekPos = intent.getIntExtra(RConstants.PROGRESS, 0);
					isInActualQueue = intent.getBooleanExtra(
							RConstants.ACTUAL_QUEUE_FLAG, true);
					isOrientationChanged = intent.getBooleanExtra(
							"orientationChanged", false);

					resultReceiver.send(RConstants.UPDATE_UI_ACTION, null);
					if (player.isPlaying()) {
						player.stop();
					}
					player.reset();
					playSong(url);
				}

			}
		}
		return 0;
	}

	@Override
	public void onDestroy() {		
		
		if(timer != null){
			timer.cancel();
		}
		if (player.isPlaying()) {
			player.stop();
		}
		if (wifiLock != null)
		{	
			wifiLock.release();
		}
		player.release();
		
		super.onDestroy();
	}

	

	private void initializeMediaPlayer() {
		if (!alreadyInitialized) {
			RConstants.shouldUpdateTimer = true ;
			player = new MediaPlayer();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setOnCompletionListener(this);
			player.setOnPreparedListener(this);
			player.setOnErrorListener(this);
			player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
			wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
			
			if (wifiLock != null)
				wifiLock.acquire();
			alreadyInitialized = true;
		
		}

		player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

		

			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				Log.i("Buffering", "" + percent);
				
				if(bufferingCount == 0){
				lastBufferingPercent = percent ;
				}
				
				latestBufferingPercent = percent ;
				bufferingCount ++ ;
				
				
	
				//send notification to user if buffering is not completed and Internet connection is low
			if((percent != 100) ){
					if(!ConnectionDetector.isConnectedFast(AudioService.this) && (mp.isPlaying()) ){
						
						if((lastBufferingPercent == latestBufferingPercent) && (bufferingCount == RConstants.MAXIMUM_BUFFERING_COUNT)){		
								resultReceiver.send(RConstants.SEND_NOTIFICATION_WHEN_INTERNET_CONNECTION_LOW, null);
						}else{
							
							if(MySpinServerSDK.sharedInstance().isConnected())
							{	resultReceiver.send(RConstants.SEND_NOTIFICATION_WHEN_STRONG_INTERNET_CONNECTION , null); }
						
						}
					
					}
				}
					if(bufferingCount == RConstants.MAXIMUM_BUFFERING_COUNT){
						bufferingCount = 0 ;
					}
			
			}
			
			
		});
	}

	private void pausePlaying() {
		if (player.isPlaying()) {
			player.pause();
		}
		
	}

	public void startPlayingFromPause() {
		try {
			player.start();
		} catch (IllegalArgumentException e) {
		
			e.printStackTrace();
		} catch (SecurityException e) {
			
			e.printStackTrace();
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		}
		executeTimerTask();
	}
	
	public void playSong(String songURl) {

		try {
			
			
			player.setDataSource(songURl);
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// update progress bar code

	public void executeTimerTask() {
		timer = new Timer();
		ProgressTimerTask timerTask = new ProgressTimerTask();
		timer.scheduleAtFixedRate(timerTask, 1000, 100);
	}

	class ProgressTimerTask extends TimerTask {

		@Override
		public void run() {

			while (true) {
				try {
					int currentPosition = player.getCurrentPosition();
					if (currentPosition <= totalDuration) {
						Bundle bundle = new Bundle();
						bundle.putInt(RConstants.currentPosition,currentPosition);
						resultReceiver.send(RConstants.resultIsProgress, bundle);
					}
					break;

				} catch (IllegalStateException e) {
					e.printStackTrace();
				}

			}

		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		if(isOnCompletion && !isOrientationChanged){
			
			// send notification when new story started
			resultReceiver.send(RConstants.START_NEW_STORY, null);
			
			Story currentStory = ContentManager.getContentManager().getPlaylistManager().getCurrentStory();
			if(currentStory != null){
		
			}
			//cancel the timer
			if(timer!=null){
				timer.cancel();
			}
		}
		
 
	}


	@Override
	public void onPrepared(MediaPlayer mp) {
		player.start();
		resultReceiver.send(RConstants.BLUETOOTH_META_CHANGE , null );
		//this will make sure that if On completion is called then we go to next story because if we are in rewind queue and usr hit next palyer calls onCompletion automaticallys
		isOnCompletion=true;
		
		if(!isInActualQueue || !isOrientationChanged){
			//if im in rewind queue and i start next story the if i seek it was skipping the story. so no seek if user do next.
			if(seekPos!=0){
				player.seekTo((player.getDuration()+seekPos));
			}
		}
		

	
		totalDuration = player.getDuration();
		Bundle bundle = new Bundle();
		bundle.putInt(RConstants.audioDuration, totalDuration);
		resultReceiver.send(RConstants.UPDATE_UI_NEXT_ENABLE_ACTION, bundle);

		// execute timer here to update media player progress bar
		if (timer != null)
			timer.cancel();
		
		RConstants.shouldUpdateTimer = true ;
		executeTimerTask();

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		
		if(RConstants.BUILD_DEBUBG){
		Log.i("AudioService",RConstants.ERROR_IN_PLAYING_FILE + what);
		}
		// stop player and reset it
		// we need to notify the user
		
		if(retryCount <2){
			
			retryCount ++;
			player.reset();
			playSong(url);
			
		}
		//unlock UI if error comes are we are not able to play song.
		
		resultReceiver.send(RConstants.UPDATE_UI_UNLOCK_ACTION , null);
		
		
		return false;
	}

}