package com.kloudsync.techexcel.help;

import android.util.Log;

import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventPageActions;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.tools.MeetingServiceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/11/28.
 */

public class PageActionsAndNotesMgr {

    public static void requestActionsAndNote(MeetingConfig config){
        Observable.just(config).observeOn(Schedulers.io()).map(new Function<MeetingConfig, EventPageActions>() {
            @Override
            public EventPageActions apply(MeetingConfig config) throws Exception {
                return MeetingServiceTools.getInstance().syncGetPageActions(config);
            }
        }).doOnNext(new Consumer<EventPageActions>() {
            @Override
            public void accept(EventPageActions eventPageActions) throws Exception {
                EventBus.getDefault().post(eventPageActions);
            }
        }).subscribe();




    }

    public static void requestActionsSaved(final MeetingConfig config){

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC +
                        "Lesson/SaveInstantLesson?lessonID=" + config.getLessionId(), null);
                Log.e("save_changed","jsonObject:" + jsonObject);

            }
        }).start(ThreadManager.getManager());
    }
}
