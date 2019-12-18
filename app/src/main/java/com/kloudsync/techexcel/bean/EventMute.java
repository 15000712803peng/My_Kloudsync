package com.kloudsync.techexcel.bean;

import com.ub.techexcel.bean.AgoraMember;

/**
 * Created by tonyan on 2019/12/8.
 */

public class EventMute {
    public static final int TYPE_MUTE_AUDIO = 1;
    public static final int TYPE_MUTE_VEDIO = 2;
    private int type;
    private boolean isMuteVedio;
    private boolean isMuteAudio;
    private AgoraMember agoraMember;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isMuteVedio() {
        return isMuteVedio;
    }

    public void setMuteVedio(boolean muteVedio) {
        isMuteVedio = muteVedio;
    }

    public boolean isMuteAudio() {
        return isMuteAudio;
    }

    public void setMuteAudio(boolean muteAudio) {
        isMuteAudio = muteAudio;
    }

    public AgoraMember getAgoraMember() {
        return agoraMember;
    }

    public void setAgoraMember(AgoraMember agoraMember) {
        this.agoraMember = agoraMember;
    }
}
