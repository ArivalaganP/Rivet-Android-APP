package com.rivet.app.core.pojo;

/**
 * Created by Brian on 18/6/14.
 */
public class AudioServicePojo {

    private static boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        AudioServicePojo.isPlaying = isPlaying;
    }
}
