package com.kloudsync.techexcel.bean;

import com.ub.techexcel.bean.AgoraMember;

/**
 * Created by tonyan on 2019/12/8.
 */

public class EventMute {
    private boolean isMute;
    private AgoraMember agoraMember;

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public AgoraMember getAgoraMember() {
        return agoraMember;
    }

    public void setAgoraMember(AgoraMember agoraMember) {
        this.agoraMember = agoraMember;
    }
}
