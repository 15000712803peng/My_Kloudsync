package com.kloudsync.techexcel.help;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.EventSoundtrackList;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/12/24.
 */

public class SoundtrackManager {

    private static SoundtrackManager instance;
    private MeetingConfig meetingConfig;

    public static SoundtrackManager getInstance() {
        if (instance == null) {
            synchronized (SocketMessageManager.class) {
                if (instance == null) {
                    instance = new SoundtrackManager();
                }
            }
        }
        return instance;
    }

    private SoundtrackManager() {

    }

    public void requestSoundtrackList(MeetingConfig meetingConfig, final OnSoundtrackResponse onSoundtrackResponse) {
        this.meetingConfig = meetingConfig;

        Observable.just(meetingConfig).observeOn(Schedulers.io()).map(new Function<MeetingConfig, EventSoundtrackList>() {
            @Override
            public EventSoundtrackList apply(MeetingConfig config) throws Exception {
                EventSoundtrackList soundtrackList = new EventSoundtrackList();

                JSONObject result = ServiceInterfaceTools.getinstance().syncGetSoundtrackList(config);
                if(result.has("RetCode")){
                    if(result.getInt("RetCode") == 0){
                        List<SoundTrack> soundTracks = new Gson().fromJson(result.getJSONArray("RetData").toString(), new TypeToken<List<SoundTrack>>() {
                        }.getType());
                        if(soundTracks != null && soundTracks.size() > 0){
                            soundtrackList.setSoundTracks(soundTracks);
                        }
                    }
                }
                if(onSoundtrackResponse != null){
                    onSoundtrackResponse.soundtrackList(soundtrackList);
                }
                return soundtrackList;
            }
        }).subscribe();
    }

    public interface OnSoundtrackResponse{
        void soundtrackList(EventSoundtrackList soundtrackList);
    }
}
