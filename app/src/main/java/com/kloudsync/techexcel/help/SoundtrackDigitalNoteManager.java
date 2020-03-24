package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.DigitalNoteEventInSoundtrack;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.MediaPlayPage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.kloudsync.techexcel.tool.DocumentPageCache;
import com.kloudsync.techexcel.tool.SyncNoteEventsCache;
import com.ub.techexcel.bean.WebAction;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import Decoder.BASE64Encoder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;



public class SoundtrackDigitalNoteManager {

    private static SoundtrackDigitalNoteManager instance;
    private volatile long playTime;
    private Activity context;
    private CopyOnWriteArrayList<DigitalNoteEventInSoundtrack> noteEvents = new CopyOnWriteArrayList<>();
    private volatile long totalTime = 0;
    private RelativeLayout smallNoteLayout;
    private XWalkView smallNoteWeb;
    private XWalkView mainNoteWeb;
    private MeetingConfig meetingConfig;
    private SyncNoteEventsCache noteEventsCache;

    public static final int EVENT_NOTE_SHOW_IN_SMALL_WINDOW = 300;
    public static final int EVENT_NOTE_SHOW_SWITCH_TO_MAIN_WINDOW = 301;
    public static final int EVENT_NOTE_CHANGE_PAGE = 302;
    public static final int EVENT_NOTE_SHOW_POSITION_CHANGED = 303;
    public static final int EVENT_NOTE_SHOW_LINE = 304;
    public static final int EVENT_NOTE_CLOSE_SMALL_WINDOW = 305;
    public static final int EVENT_NOTE_CLOSE_MAIN_WINDOW = 306;
    public static final int EVENT_NOTE_SHOW_SWITCH_TO_SMALL_WINDOW = 307;
    public static final int EVENT_NOTE_SHOW_IN_MIAI_WINDOW = 308;

    private SoundtrackDigitalNoteManager(Activity context) {
        this.context = context;
        noteEventsCache = SyncNoteEventsCache.getInstance(context);
        gson = new Gson();
    }

    public static SoundtrackDigitalNoteManager getInstance(Activity context) {
        if (instance == null) {
            synchronized (SoundtrackDigitalNoteManager.class) {
                if (instance == null) {
                    instance = new SoundtrackDigitalNoteManager(context);
                }
            }
        }
        return instance;
    }

    public void setPlayTime(final long playTime) {
        this.playTime = playTime;
        Observable.just("do_now").observeOn(Schedulers.io()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
//                syncRequestActions();
                executeNoteEvents(playTime);

            }
        });

    }

    private void executeNoteEvents(long playTime) {
        int index = 0;
        for (DigitalNoteEventInSoundtrack noteEvent : _noteEvents) {
            if (playTime <= noteEvent.getTime()) {
                index = _noteEvents.indexOf(noteEvent);
                break;
            }
        }

        for (int i = 0; i < index + 1; ++i) {
            executeEvent(_noteEvents.get(i));
        }
    }

    private List<DigitalNoteEventInSoundtrack> prepareNoteEvents(String url) {
        String noteEventsData = noteEventsCache.getCacheNoteEvents(url);
        List<DigitalNoteEventInSoundtrack> noteEvents = new ArrayList<>();
        if (!TextUtils.isEmpty(noteEventsData)) {
            noteEvents = gson.fromJson(noteEventsData, new TypeToken<List<DigitalNoteEventInSoundtrack>>() {
            }.getType());
        }
        return noteEvents;
    }


    private volatile CopyOnWriteArrayList<DigitalNoteEventInSoundtrack> _noteEvents = new CopyOnWriteArrayList<>();

    private void executeEvent(DigitalNoteEventInSoundtrack noteEvent) {
        Log.e("check_note_event", "action:" + noteEvent);
        if (noteEvent == null || noteEvent.isExecuted()) {
            return;
        }

        if (noteEvent.getTime() == 0 || (!noteEvent.isExecuted() && playTime >= noteEvent.getTime())) {
            Observable.just(noteEvent).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<DigitalNoteEventInSoundtrack>() {
                @Override
                public void accept(DigitalNoteEventInSoundtrack noteEvent) throws Exception {
                    doExecuteNoteEvent(noteEvent);
                }
            });
        }
    }

    public void initViews(MeetingConfig meetingConfig, RelativeLayout smallNoteLayout,
                          XWalkView smallNoteWeb,
                          XWalkView mainNoteWeb) {
        this.meetingConfig = meetingConfig;
        this.smallNoteLayout = smallNoteLayout;
        this.smallNoteWeb = smallNoteWeb;
        this.mainNoteWeb = mainNoteWeb;
        init(context);

    }

    private void doExecuteNoteEvent(DigitalNoteEventInSoundtrack noteEvent) {
        noteEvent.setExecuted(true);
        Log.e("do_execute_note_event", "event,time:" + noteEvent.getTime() + ",type:" + noteEvent.getActionType());
        switch (noteEvent.getActionType()) {
            case EVENT_NOTE_SHOW_IN_SMALL_WINDOW:
            case EVENT_NOTE_SHOW_SWITCH_TO_SMALL_WINDOW:
                handleNoteShowInSmallWindow((int) noteEvent.getData().getId(), noteEvent.getData().getLastStrokeId());
                break;
            case EVENT_NOTE_CHANGE_PAGE:
                break;
            case EVENT_NOTE_SHOW_POSITION_CHANGED:
                break;
            case EVENT_NOTE_SHOW_LINE:
                handleNoteShowLine(noteEvent.getData().getStrokeId());
                break;
            case EVENT_NOTE_CLOSE_SMALL_WINDOW:
                hideSmallPageNoteWeb();
                break;
            case EVENT_NOTE_CLOSE_MAIN_WINDOW:
                hideMainPageNoteWeb();
                break;
            case EVENT_NOTE_SHOW_IN_MIAI_WINDOW:
            case EVENT_NOTE_SHOW_SWITCH_TO_MAIN_WINDOW:
                handleNoteShowInMainWindow((int) noteEvent.getData().getId(), noteEvent.getData().getLastStrokeId());
                break;

        }

    }

    private int lastActionIndex = -1;

    public void setLoadingPage(boolean loadingPage) {
//        isLoadingPage = loadingPage;
    }


    private int currentPage = -1;


    private volatile MediaPlayPage mediaPlayPage;
    private Gson gson;


    class Request implements Comparable<Request> {
        long startTime;
        boolean hasRequest;
        boolean isRequesting;

        public Request(long startTime) {
            this.startTime = startTime;
        }

        public Request() {

        }

        @Override
        public int compareTo(@NonNull Request o) {
            return (int) (this.startTime - o.startTime);
        }

        @Override
        public String toString() {
            return "Request{" +
                    "startTime=" + startTime +
                    ", hasRequest=" + hasRequest +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Request request = (Request) o;

            return startTime == request.startTime;
        }

        @Override
        public int hashCode() {
            return (int) (startTime ^ (startTime >>> 32));
        }
    }

    private volatile Request request;
    private Request firstRequest = new Request(0);


    ;

    public void release() {
        _noteEvents = null;
        instance = null;
    }


    private Uploadao parseQueryResponse(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));
                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public void seekTo(int time) {
//        Observable.just(time).observeOn(Schedulers.io()).doOnNext(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer time) throws Exception {
//                requestActionsBySeek(time);
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer time) throws Exception {
//
//                clearExecuted();
//
//                executeActions(getActions(time), time);
//            }
//        }).subscribe();
        SoundtrackAudioManager.getInstance(context).seekTo(time);
    }


    JSONObject currentPageData;

    public void handleNoteShowInMainWindow(final int noteId, final String lastStokeId) {
        if(noteId <= 0){
            return;
        }

        Observable.just(noteId).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                showMainPageNoteWeb();
            }
        }).observeOn(Schedulers.io()).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer _noteId) throws Exception {
                return MeetingServiceTools.getInstance().syncGetNoteAttachmentUrlByNoteId(_noteId);
            }

        }).map(new Function<String, String>() {
            @Override
            public String apply(String url) throws Exception {
                if (TextUtils.isEmpty(url)) {
                    return "";
                }
                String newUrl = "";
                URL _url = new URL(url);
                Log.e("check_url_path", _url.getPath());
                String path = _url.getPath();
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    int index = path.lastIndexOf("/");
                    if (index >= 0 && index < path.length()) {
                        String centerPart = path.substring(0, index);
                        String fileName = path.substring(index + 1, path.length());
                        Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                        if (!TextUtils.isEmpty(centerPart)) {
                            JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                                    centerPart);
                            if (queryDocumentResult != null) {
                                Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                                String part = "";
                                if (uploadao != null) {
                                    if (1 == uploadao.getServiceProviderId()) {
                                        part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                                + "/" + fileName;
                                    } else if (2 == uploadao.getServiceProviderId()) {
                                        part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                                    }
                                    url = part;
                                    Log.e("check_transform_url", "url:" + url);
                                }
                            }
                        }
                    }
                }

                int checkIndex = url.lastIndexOf("/");
                if (checkIndex > 0 && checkIndex < url.length() - 2) {
                    newUrl = url.substring(0, checkIndex + 1) + "book_page_data.json";
                }
                return newUrl;
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(url)) {
                    Log.e("check_url", "url:" + url);
                    currentPageData = ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
                }
                Log.e("currentPageData", "currentPageData:" + currentPageData);
                return currentPageData;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                final String key = "ShowDotPanData";
                final JSONObject _data = new JSONObject();
                JSONObject datas = fetchExitedNotesData(currentPageData, lastStokeId);
                Log.e("check_exited_data", "datas:" + datas);
                _data.put("LinesData", datas);
                _data.put("ShowInCenter", false);
                _data.put("TriggerEvent", false);
                Observable.just("delay_load").delay(500, TimeUnit.MICROSECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
//                        smallNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                        mainNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
//                        handleNoteShowLine("397837A1-D305-4688-9DA5-ACF09374CDD0");
                    }
                });


            }

        }).observeOn(Schedulers.io()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetNoteP1Item(noteId);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        JSONArray dataArray = result.getJSONArray("data");
                        Observable.just(dataArray).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONArray>() {
                            @Override
                            public void accept(JSONArray _jsonArray) throws Exception {
                                for (int i = 0; i < _jsonArray.length(); ++i) {
                                    JSONObject data = _jsonArray.getJSONObject(i);
                                    addLinkBorderForDTNewInMainNoteWeb(data, noteId);
                                }
                            }
                        }).subscribe();
                    }
                }
            }
        }).subscribe();
    }


    public void handleNoteShowInSmallWindow(final int noteId, final String lastStokeId) {
        if(noteId <= 0){
            return;
        }
        Observable.just(noteId).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                showSmallPageNoteWeb();
            }
        }).observeOn(Schedulers.io()).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer _noteId) throws Exception {
                return MeetingServiceTools.getInstance().syncGetNoteAttachmentUrlByNoteId(_noteId);
            }

        }).map(new Function<String, String>() {
            @Override
            public String apply(String url) throws Exception {
                if (TextUtils.isEmpty(url)) {
                    return "";
                }
                String newUrl = "";
                URL _url = new URL(url);
                Log.e("check_url_path", _url.getPath());
                String path = _url.getPath();
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    int index = path.lastIndexOf("/");
                    if (index >= 0 && index < path.length()) {
                        String centerPart = path.substring(0, index);
                        String fileName = path.substring(index + 1, path.length());
                        Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                        if (!TextUtils.isEmpty(centerPart)) {
                            JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                                    centerPart);
                            if (queryDocumentResult != null) {
                                Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                                String part = "";
                                if (uploadao != null) {
                                    if (1 == uploadao.getServiceProviderId()) {
                                        part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                                + "/" + fileName;
                                    } else if (2 == uploadao.getServiceProviderId()) {
                                        part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                                    }
                                    url = part;
                                    Log.e("check_transform_url", "url:" + url);
                                }
                            }
                        }
                    }
                }

                int checkIndex = url.lastIndexOf("/");
                if (checkIndex > 0 && checkIndex < url.length() - 2) {
                    newUrl = url.substring(0, checkIndex + 1) + "book_page_data.json";
                }
                return newUrl;
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(url)) {
                    Log.e("check_url", "url:" + url);
                    currentPageData = ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
                }
                Log.e("currentPageData", "currentPageData:" + currentPageData);
                return currentPageData;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                final String key = "ShowDotPanData";
                final JSONObject _data = new JSONObject();
                JSONObject datas = fetchExitedNotesData(currentPageData, lastStokeId);
                Log.e("check_exited_data", "datas:" + datas);
                _data.put("LinesData", datas);
                _data.put("ShowInCenter", false);
                _data.put("TriggerEvent", false);
                Observable.just("delay_load").delay(500, TimeUnit.MICROSECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
//                        mainNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                        smallNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
//                        handleNoteShowLine("397837A1-D305-4688-9DA5-ACF09374CDD0");
                    }
                });

            }

        }).observeOn(Schedulers.io()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetNoteP1Item(noteId);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        JSONArray dataArray = result.getJSONArray("data");
                        Observable.just(dataArray).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONArray>() {
                            @Override
                            public void accept(JSONArray _jsonArray) throws Exception {
                                for (int i = 0; i < _jsonArray.length(); ++i) {
                                    JSONObject data = _jsonArray.getJSONObject(i);
                                    addLinkBorderForDTNewInMainNoteWeb(data, noteId);
                                }
                            }
                        }).subscribe();
                    }
                }
            }
        }).subscribe();
    }


    public void handleNoteShowLine(final String stokeId) {

        Observable.just(stokeId).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String stokeId) throws Exception {
                final String key = "ShowDotPanData";
                final JSONObject _data = new JSONObject();
                if (currentPageData != null) {
                    JSONObject datas = fetchOneLineNoteData(currentPageData, stokeId);
                    Log.e("check_exited_data", "datas:" + datas);
                    _data.put("LinesData", datas);
                    _data.put("ShowInCenter", false);
                    _data.put("TriggerEvent", false);
                    Observable.just("delay_load").delay(5000, TimeUnit.MICROSECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            mainNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                            smallNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);


                        }
                    });
                }
            }

        }).subscribe();
    }


    private void addLinkBorderForDTNewInMainNoteWeb(JSONObject p1Created, int noteId) throws JSONException {
        if (p1Created.has("noteId")) {
            if (noteId == p1Created.getInt("noteId")) {
//                noteWeb.load("javascript:whiteboard");
                JSONArray positionArray = new JSONArray(p1Created.getString("position"));
                Log.e("addLinkBorderForDTNew", "positionArray:" + positionArray);
                JSONObject info = new JSONObject();
                info.put("ProjectID", p1Created.getInt("projectId"));
                info.put("TaskID", p1Created.getInt("itemId"));
                for (int i = 0; i < positionArray.length(); ++i) {
                    JSONObject position = positionArray.getJSONObject(i);
                    doDrawDTNewBorder(position.getInt("left"), position.getInt("top"), position.getInt("width"), position.getInt("height"), info, mainNoteWeb);
                }
            }
        }
    }

    private void doDrawDTNewBorder(int x, int y, int width, int height, JSONObject info, XWalkView noteWeb) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("type", 40);
        message.put("CW", 678);
        message.put("x", x);
        message.put("y", y);
        message.put("width", width);
        message.put("height", height);
        message.put("info", info);
        JSONObject clearLastMessage = new JSONObject();
        clearLastMessage.put("type", 40);

        Log.e("doDrawDTNewBorder", "border_PlayActionByTxt:" + message);
        noteWeb.load("javascript:PlayActionByTxt('" + message + "')", null);
    }

    private void showMainPageNoteWeb() {
        smallNoteWeb.setVisibility(View.GONE);
        smallNoteLayout.setVisibility(View.GONE);
        mainNoteWeb.setVisibility(View.VISIBLE);
        mainNoteWeb.load("javascript:ClearPath()", null);
        smallNoteWeb.load("javascript:ClearPath()", null);
    }

    private void switchMainPageNoteWeb() {
        smallNoteWeb.setVisibility(View.GONE);
        smallNoteLayout.setVisibility(View.GONE);
        mainNoteWeb.setVisibility(View.VISIBLE);
    }

    private void switchSmallPageNoteWeb() {
        smallNoteWeb.setVisibility(View.VISIBLE);
        smallNoteLayout.setVisibility(View.VISIBLE);
        mainNoteWeb.setVisibility(View.GONE);
    }

    private void hideMainPageNoteWeb() {
        mainNoteWeb.setVisibility(View.GONE);

    }

    private void hideSmallPageNoteWeb() {
        smallNoteWeb.setVisibility(View.GONE);
        smallNoteLayout.setVisibility(View.GONE);
    }

    private void showSmallPageNoteWeb() {
        smallNoteWeb.setVisibility(View.VISIBLE);
        smallNoteLayout.setVisibility(View.VISIBLE);
        mainNoteWeb.setVisibility(View.GONE);
        smallNoteWeb.load("javascript:ClearPath()", null);
        mainNoteWeb.load("javascript:ClearPath()", null);
    }

    public void init(Activity context) {
        this.context = context;
        smallNoteWeb.setZOrderOnTop(false);
        smallNoteWeb.getSettings().setJavaScriptEnabled(true);
        smallNoteWeb.getSettings().setDomStorageEnabled(true);
        smallNoteWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        smallNoteWeb.addJavascriptInterface(new SmallNoteJavascriptInterface(), "AnalyticsWebInterface");
        int deviceType = DeviceManager.getDeviceType(context);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        final String url = indexUrl;
        smallNoteWeb.load(url, null);
        // --------------

        mainNoteWeb.setZOrderOnTop(false);
        mainNoteWeb.getSettings().setJavaScriptEnabled(true);
        mainNoteWeb.getSettings().setDomStorageEnabled(true);
        mainNoteWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mainNoteWeb.addJavascriptInterface(new MainNoteJavascriptInterface(), "AnalyticsWebInterface");
        mainNoteWeb.load(url, null);
    }


    public class SmallNoteJavascriptInterface {
        @org.xwalk.core.JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
        }

        @org.xwalk.core.JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);

        }

        @org.xwalk.core.JavascriptInterface
        public synchronized void callAppFunction(final String action, final String data) {
            Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
            if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
                return;
            }
        }

        @org.xwalk.core.JavascriptInterface
        public void afterLoadPageFunction() {
            Log.e("JavascriptInterface", "afterLoadPageFunction");
            Observable.just("init").delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    initSmallAfterPageLoad();
                }
            });

        }
    }

    public class MainNoteJavascriptInterface {
        @org.xwalk.core.JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
        }

        @org.xwalk.core.JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);

        }

        @org.xwalk.core.JavascriptInterface
        public synchronized void callAppFunction(final String action, final String data) {
            Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
            if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
                return;
            }
        }

        @org.xwalk.core.JavascriptInterface
        public void afterLoadPageFunction() {
            Log.e("JavascriptInterface", "afterLoadPageFunction");
            Observable.just("init").delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    initMainAfterPageLoad();
                }
            });

        }
    }

    private void initSmallAfterPageLoad() {
        String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
        Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")");
        smallNoteWeb.load("javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + true + ")", null);
        smallNoteWeb.load("javascript:Record()", null);
        String key = "ChangeMovePageButton";
        JSONObject _data = new JSONObject();
        JSONObject _left = new JSONObject();
        JSONObject _right = new JSONObject();
        try {
            _left.put("Show", false);
            _right.put("Show", false);
            _data.put("Left", _left);
            _data.put("Right", _right);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        smallNoteWeb.load("javascript:ShowToolbar(" + false + ")", null);
        smallNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
        smallNoteWeb.load("javascript:Record()", null);
    }


    private void initMainAfterPageLoad() {
        String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
        Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")");
        mainNoteWeb.load("javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + true + ")", null);
        mainNoteWeb.load("javascript:Record()", null);
        String key = "ChangeMovePageButton";
        JSONObject _data = new JSONObject();
        JSONObject _left = new JSONObject();
        JSONObject _right = new JSONObject();
        try {
            _left.put("Show", false);
            _right.put("Show", false);
            _data.put("Left", _left);
            _data.put("Right", _right);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mainNoteWeb.load("javascript:ShowToolbar(" + false + ")", null);
        mainNoteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
        mainNoteWeb.load("javascript:Record()", null);
    }

    private JSONObject fetchExitedNotesData(JSONObject pageData, String lastStokeId) throws JSONException {

        JSONObject result = new JSONObject();

        if (pageData != null) {
            if (pageData.has("PageTokenBackup")) {
                result.put("PageTokenBackup", pageData.getString("PageTokenBackup"));
            }
            if (pageData.has("PageToken")) {
                result.put("PageToken", pageData.getString("PageToken"));
            }

            if (pageData.has("PaintData")) {
                try {
                    JSONObject paintData = new JSONObject();
                    JSONObject data = pageData.getJSONObject("PaintData");
                    if (data.has("address")) {
                        paintData.put("address", data.getString("address"));
                    }
                    if (data != null && data.has("lines")) {
                        JSONArray linesArray = data.getJSONArray("lines");

                        if (linesArray != null && linesArray.length() > 0) {
                            JSONArray _lines = new JSONArray();
                            for (int i = 0; i < linesArray.length(); ++i) {
                                JSONObject line = linesArray.getJSONObject(i);
                                if (line.getString("id").equals(lastStokeId)) {
                                    _lines.put(line);
                                    Log.e("check_put", "breadk_put:" + i);
                                    paintData.put("lines", _lines);
                                    result.put("PaintData", paintData);
                                    break;
                                } else {
                                    Log.e("check_put", "put:" + i);
                                    _lines.put(line);
                                }
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private JSONObject fetchOneLineNoteData(JSONObject pageData, String stokeId) throws JSONException {

        JSONObject result = new JSONObject();

        if (pageData != null) {
            if (pageData.has("PageTokenBackup")) {
                result.put("PageTokenBackup", pageData.getString("PageTokenBackup"));
            }
            if (pageData.has("PageToken")) {
                result.put("PageToken", pageData.getString("PageToken"));
            }

            if (pageData.has("PaintData")) {
                try {
                    JSONObject paintData = new JSONObject();
                    JSONObject data = pageData.getJSONObject("PaintData");
                    if (data.has("address")) {
                        paintData.put("address", data.getString("address"));
                    }
                    if (data != null && data.has("lines")) {
                        JSONArray linesArray = data.getJSONArray("lines");

                        if (linesArray != null && linesArray.length() > 0) {
                            JSONArray _lines = new JSONArray();
                            for (int i = 0; i < linesArray.length(); ++i) {
                                JSONObject line = linesArray.getJSONObject(i);
                                if (line.getString("id").equals(stokeId)) {
                                    _lines.put(line);
                                    Log.e("check_put", "breadk_put:" + i);
                                    paintData.put("lines", _lines);
                                    result.put("PaintData", paintData);
                                    break;
                                }
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void doProcess(final String url) {
        String noteEvents = "";
        if (noteEventsCache.containNoteEvents(url)) {
            noteEvents = noteEventsCache.getCacheNoteEvents(url);
        }
        Observable.just(noteEvents).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String response) throws Exception {
                if (!TextUtils.isEmpty(response)) {
                    Log.e("check_noteEvents","note_events:" + response);

                    try {
                        List<DigitalNoteEventInSoundtrack> noteEventsData = new Gson().fromJson(response, new TypeToken<List<DigitalNoteEventInSoundtrack>>() {
                        }.getType());
                        if (noteEventsData != null && noteEventsData.size() > 0) {
                            _noteEvents.addAll(noteEventsData);
                        }
                    }catch (Exception e){

                    }

                } else {
                    Log.e("check_noteEvents","download_url:" + url);
                    String _resonse = ConnectService.getResponseStringbyHttpGet(url);
                    try {
                        if (!TextUtils.isEmpty(_resonse)) {
                            List<DigitalNoteEventInSoundtrack> noteEventsData = new Gson().fromJson(_resonse, new TypeToken<List<DigitalNoteEventInSoundtrack>>() {
                            }.getType());
                            if (noteEventsData != null && noteEventsData.size() > 0) {
                                _noteEvents.addAll(noteEventsData);
                            }
                            noteEventsCache.cacheNoteEvents(url,_resonse);

                        }
                    }catch (Exception e){

                    }

                }
                Log.e("check__noteEvents","_noteEvents_size:" +_noteEvents.size());
            }
        }).subscribe();
    }

    private String getCacheNoteData(Context context) {
        String result = "";
        InputStream is = null;
        try {
            is = context.getAssets().open("note_data.txt");
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            result = new String(buffer, "utf8");
            is.close();
        } catch (IOException e) {
            Log.e("getCacheNoteData", "io_exepton:" + e);
            e.printStackTrace();
        }

        Log.e("getCacheNoteData", ":result" + result);

        return result;
    }

    public CopyOnWriteArrayList<DigitalNoteEventInSoundtrack> getNoteEvents() {
        return _noteEvents;
    }

}
