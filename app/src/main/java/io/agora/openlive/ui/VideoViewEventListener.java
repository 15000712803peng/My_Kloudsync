package io.agora.openlive.ui;

import com.ub.techexcel.bean.AgoraBean;

public interface VideoViewEventListener {

    void onItemDoubleClick(Object item);

    void onSwitchVideo(AgoraBean item);

    void isEnlarge(AgoraBean user);

    void closeOtherAudio(AgoraBean user);  //presenter 关闭别人的Audio

    void closeOtherVideo(AgoraBean user);  //presenter 关闭别人的Video

    void openMyAudio(AgoraBean user);

    void openMyVideo(AgoraBean user);
}
