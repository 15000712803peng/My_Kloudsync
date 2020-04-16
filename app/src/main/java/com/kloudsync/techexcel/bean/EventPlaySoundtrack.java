package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/25.
 */

public class EventPlaySoundtrack {

    private SoundTrack soundTrack;
    private SoundtrackDetail soundtrackDetail;

    public SoundtrackDetail getSoundtrackDetail() {
        return soundtrackDetail;
    }

    public void setSoundtrackDetail(SoundtrackDetail soundtrackDetail) {
        this.soundtrackDetail = soundtrackDetail;
    }

    public SoundTrack getSoundTrack() {
        return soundTrack;
    }

    public void setSoundTrack(SoundTrack soundTrack) {
        this.soundTrack = soundTrack;
    }
}
