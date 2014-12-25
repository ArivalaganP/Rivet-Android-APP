package com.rivet.app.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;

public class RemoteControlReciever extends BroadcastReceiver  {

	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// TODO Auto-generated method stub
		if(context!= null)
		  if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
			  final KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			    if (event.getAction() != KeyEvent.ACTION_DOWN) return;

			    switch (event.getKeyCode()) {
			        case KeyEvent.KEYCODE_MEDIA_STOP:
			  
			            break;
			            // introduce new key code after version 2.2 for play and pause
			        case KeyEvent.KEYCODE_MEDIA_PLAY:
			        	if(ContentManager.getContentManager()!=null){
			        		if(ContentManager.getContentManager().getPlaylistManager().getCurrentStory() != null){
			        		ContentManager.getContentManager().getPlaylistManager().play();
			        		}
			        	}
			        	break;
			        case KeyEvent.KEYCODE_MEDIA_PAUSE:
			        	if(ContentManager.getContentManager()!=null){
			        		if(ContentManager.getContentManager().getPlaylistManager().getCurrentStory() != null){
			        		ContentManager.getContentManager().getPlaylistManager().play();
			        		}
			        	}
			        	
			        	break;
			        case KeyEvent.KEYCODE_HEADSETHOOK:
			        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			        	if(ContentManager.getContentManager()!=null){
			        		if(ContentManager.getContentManager().getPlaylistManager().getCurrentStory() != null){
			        		ContentManager.getContentManager().getPlaylistManager().play();
			        		}
			        	}
			            break;
			        case KeyEvent.KEYCODE_MEDIA_NEXT:
			            // next track
			        	
			        	if(ContentManager.getContentManager()!=null){
			        		if (ContentManager.getContentManager().getPlaylistManager().getPlaylistCount() < RConstants.MINIMUM_STORIES_TO_PLAY) {
			        			ContentManager.getContentManager().moveToNextRuleAndUpdateStoriesForCurrentRule();

							}
			        			ContentManager.getContentManager().playNewStory();
			        	}
			            break;
			        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			            // previous track
			        	
			        	new Thread(new Runnable() {

							@Override
							public void run() {
								
								int rewindPositionOfTrack = RConstants.currentPosOfAudioPlayer
										- RConstants.REWIND_DURATION_PLAYER;
								ContentManager.getContentManager().rewindStoryAndPlay(rewindPositionOfTrack);
							}
						}).start();
			        	
			            break;
			    }
			    
	        }
		  
		} // end of onrecieve

	
	
	

}
