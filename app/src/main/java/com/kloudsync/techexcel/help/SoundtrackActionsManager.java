package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventPlayWebVedio;
import com.kloudsync.techexcel.bean.MediaPlayPage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.WebVedio;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.kloudsync.techexcel.tool.DocumentPageCache;
import com.kloudsync.techexcel.tool.SyncWebActionsCache;
import com.ub.techexcel.bean.PartWebActions;
import com.ub.techexcel.bean.WebAction;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Decoder.BASE64Encoder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/11/21.
 */

public class SoundtrackActionsManager {

    private static SoundtrackActionsManager instance;
    private volatile long playTime;
    private Activity context;
    private List<WebAction> webActions = new ArrayList<>();
    private List<WebVedio> webVedios = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private List<MediaPlayPage> mediaPlayPages = new ArrayList<>();
    private int recordId;
    private volatile long totalTime = 0;
    private WebView web;
    private SurfaceView surfaceView;
    private WebVedioManager webVedioManager;
    private String downloadUrlPre = "";
    private UserVedioManager userVedioManager;
    private MeetingConfig meetingConfig;
    private RelativeLayout webVedioPlayLayout;
    private SyncWebActionsCache webActionsCache;
	private DocumentPage currentDocumentPage;

    public void setUserVedioManager(UserVedioManager userVedioManager) {
        this.userVedioManager = userVedioManager;
    }

    public void setWeb(WebView web, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        this.web = web;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        if (webVedioManager != null) {
            webVedioManager.initSurface(surfaceView);
        }
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
//        requestActions();
    }

    //
    private SoundtrackActionsManager(Activity context) {
        this.context = context;
        pageCache = DocumentPageCache.getInstance(context);
        webVedioManager = WebVedioManager.getInstance(context);
        webActionsCache = SyncWebActionsCache.getInstance(context);
        gson = new Gson();
    }

    public static SoundtrackActionsManager getInstance(Activity context) {
        if (instance == null) {
            synchronized (SoundtrackActionsManager.class) {
                if (instance == null) {
                    instance = new SoundtrackActionsManager(context);
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
                executeActions(getActions(), playTime);
                WebVedio nearestVedio = getNearestWebvedio(playTime);
                if (nearestVedio != null) {
                    Log.e("nearestVedio", nearestVedio.getSavetime() + ",play_time:" + playTime + ",is_executed:" + nearestVedio.isExecuted());
                }
                webVedioManager.safePrepare(nearestVedio);
                if (nearestVedio != null && playTime >= nearestVedio.getSavetime() && !nearestVedio.isExecuted()) {
                    Log.e("nearestVedio", "start_play");
                    SoundtrackAudioManager.getInstance(context).pause();
                    nearestVedio.setExecuted(true);
                    EventPlayWebVedio eventPlayWebVedio = new EventPlayWebVedio();
                    eventPlayWebVedio.setWebVedio(nearestVedio);
                    EventBus.getDefault().post(eventPlayWebVedio);
                }
            }
        });

    }

    private WebVedio getNearestWebvedio(long playTime) {

        if (webVedios.size() > 0) {
            int index = 0;
            for (int i = 0; i < webVedios.size(); ++i) {
                WebVedio webVedio = webVedios.get(i);
                if (!webVedio.isExecuted()) {
                    return webVedio;
                }
                //4591,37302
                long interval = webVedio.getSavetime() - playTime;
                if (interval > 0) {
                    index = i;
                    break;
                }

            }
            return webVedios.get(index);

        }
        return null;
    }

    private volatile List<WebAction> actions = new ArrayList<>();

    private List<WebAction> getActions(boolean reset, long playTime) {
        actions.clear();
        for (WebAction action : webActions) {
            if (action.getTime() <= playTime) {
                if (reset) {
                    action.setExecuted(false);
                }
                actions.add(action);
            } else {
                break;
            }
        }
        return actions;
    }


    PartWebActions currentPartWebActions;


    public PartWebActions getCurrentPartWebActions() {
        return currentPartWebActions;
    }

    public void setCurrentPartWebActions(PartWebActions currentPartWebActions) {
        this.currentPartWebActions = currentPartWebActions;
    }

    private List<WebAction> getActions() {
        if (currentPartWebActions == null) {
            currentPartWebActions = webActionsCache.getPartWebActions(playTime, recordId);
        }
        if (currentPartWebActions != null) {
            if (!(playTime >= currentPartWebActions.getStartTime() && playTime <= currentPartWebActions.getEndTime())) {
                Log.e("check_part_actions", "get_new_part");
                currentPartWebActions = webActionsCache.getPartWebActions(playTime, recordId);
            }
        }

        if (currentPartWebActions == null) {
            return new ArrayList<>();
        }


//        int index = lastActionIndex;
//        if(index < 0){
//            index = 0;
//        }
//
//        for (int i = index;i < webActions.size(); ++i) {
//            WebAction action = webActions.get(i);
//            if (action.getTime() <= playTime) {
//                if(!action.isExecuted()){
//                    if(!_actions.contains(action)){
//                        _actions.add(action);
//                    }
//                }
//            } else {
//                break;
//
//            }
//        }
        return currentPartWebActions.getWebActions();
    }


    private List<WebAction> getActions(int playTime) {
        _actions.clear();
        int index = lastActionIndex;
        if (index < 0) {
            index = 0;
        }

        for (int i = index; i < webActions.size(); ++i) {
            WebAction action = webActions.get(i);
            if (action.getTime() <= playTime) {
                _actions.add(action);
            } else {
                break;
            }
        }
        return _actions;
    }

    private volatile CopyOnWriteArrayList<WebAction> _actions = new CopyOnWriteArrayList<>();

//    private WebAction getNearestAction() {
//        if (webActions.size() <= 0) {
//            return null;
//        }
//        if (lastActionIndex == -1) {
//            return webActions.get(0);
//        }
//
//        if (lastActionIndex < webActions.size() - 1) {
//            if (webActions.get(lastActionIndex + 1).getIndex() == lastActionIndex + 1) {
//                return webActions.get(lastActionIndex + 1);
//            }
//        }
//        return null;
//    }


    private void executeActions(List<WebAction> actions, long playTime) {
        for (final WebAction action : actions) {
//            Log.e("check_action", "action:" + action);
            Log.e("SoundtrackActionsManager", "executeActions" + actions + ",action_executed:" + action.isExecuted());
            if (action.isExecuted()) {
                continue;
            }

            if (!TextUtils.isEmpty(action.getData())) {
                try {
                    JSONObject data = new JSONObject(action.getData());
                    if (data.has("actionType")) {
                        int actionType = data.getInt("actionType");
                        switch (actionType) {
                            case 19:
//                                                Log.e("check_action","action:setWebVedio:" + action.getData());
//                                               action.setWebVedio(gson.fromJson(action.getData(), WebVedio.class));
                                action.setExecuted(true);
                                WebVedio webVedio = gson.fromJson(action.getData(), WebVedio.class);
                                if (!webVedios.contains(webVedio)) {
                                    webVedios.add(webVedio);
                                }
                                break;
                            case 202:
                                action.setExecuted(true);
                                if (userVedioManager != null) {
                                    userVedioManager.refreshUserInfo(data.getString("userId"), data.getString("userName"), data.getString("avatarUrl"));
                                }
                                break;
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            if (playTime < action.getTime() || isLoadingPage) {
                continue;
            }

            Observable.just(action).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WebAction>() {
                @Override
                public void accept(WebAction action) throws Exception {
                    doExecuteAction(action);
                }
            });
        }
    }

    private void executeAction(WebAction action) {
        Log.e("check_action", "action:" + action);
        if (action == null || action.isExecuted()) {
            return;
        }
        if (action.getTime() == 0 || (!action.isExecuted() && action.getTime() >= playTime)) {
            Observable.just(action).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WebAction>() {
                @Override
                public void accept(WebAction action) throws Exception {
                    doExecuteAction(action);
                }
            });
        }


    }

    private int lastActionIndex = -1;
    private boolean isLoadingPage;


    public void setLoadingPage(boolean loadingPage) {
        isLoadingPage = loadingPage;
    }

    private void doExecuteAction(WebAction action) {

        if (web == null) {
            action.setExecuted(false);
            return;
        }
        if (TextUtils.isEmpty(action.getData())) {
            return;
        }
        action.setExecuted(true);
        try {
            JSONObject data = new JSONObject(action.getData());
//            Log.e("doExecuteAction", "action," + action + ",playtime:" + playTime);
            if (data.getInt("type") == 2) {
                isLoadingPage = true;
                downLoadDocumentPageAndShow(data.getInt("page"));

            } else {
	            Log.e("doExecuteAction", "action," + action.getData() + ",playtime:" + playTime);
                web.loadUrl("javascript:PlayActionByTxt('" + action.getData() + "')", null);
                web.loadUrl("javascript:Record()", null);
            }
//                    Log.e("execute_action","action:" + action.getTime() + "--" + action.getData());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private int currentPage = -1;


    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void doChangePageAction(WebAction action) {
        try {
            JSONObject data = new JSONObject(action.getData());
            Log.e("doExecuteAction", "action," + action + ",playtime:" + playTime);
            if (data.getInt("type") == 2) {
                int page = data.getInt("page");
//                if(currentPage == page){
//                    return;
//                }
                isLoadingPage = true;
                /*if(currentPage == page){

                    //--
                }else {*/
                    downLoadDocumentPageAndShow(page);
//                }


            } else {
                web.loadUrl("javascript:PlayActionByTxt('" + action.getData() + "')", null);
                web.loadUrl("javascript:Record()", null);
            }
//                    Log.e("execute_action","action:" + action.getTime() + "--" + action.getData());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private volatile MediaPlayPage mediaPlayPage;
    private Gson gson;


    private void syncRequestActions() {
        Request r = getRequest();
        Log.e("SoundtrackActionsManager", "step_one:get_request:" + r);
        if (r == null) {
            return;
        }

        final String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + recordId + "&startTime=" + request.startTime + "&endTime=" + (request.startTime + 20000);
        final String cacheUrl = url + "__time__separator__" + request.startTime + "__" + (request.startTime + 20000) + "__" + recordId;
        boolean isContain = webActionsCache.containPartWebActions(cacheUrl);
        Log.e("SoundtrackActionsManager", "step_two:have_cache_by_url:" + isContain + ",url:" + url);

        if (isContain) {
            return;
        }

        if (requests.contains(r)) {
            r = requests.get(requests.indexOf(r));
        } else {
            requests.add(r);
        }

        if (r.hasRequest) {
            return;
        }

        if (r.isRequesting) {
            return;
        }

        r.isRequesting = true;
        request = r;
        Log.e("SoundtrackActionsManager", "step_three:no_cache_by_url_and_request" + url);
        List<WebAction> actions = ServiceInterfaceTools.getinstance().syncGetRecordActions(url);
        if (actions != null && actions.size() > 0) {
            PartWebActions partWebActions = new PartWebActions();
            partWebActions.setStartTime(request.startTime);
            partWebActions.setEndTime(request.startTime + 20000);
            partWebActions.setUrl(cacheUrl);
            partWebActions.setWebActions(actions);
            webActionsCache.cacheActions(partWebActions);
            Log.e("SoundtrackActionsManager", "step_four:request_success_and_cache:web_actions_size:" + partWebActions.getWebActions().size());
            if (!requests.contains(request)) {
                requests.add(request);
            }
            Collections.sort(requests);
//                    Collections.sort(webActions);
        }
    }

    public void preLoad(int recordId) {
        this.recordId = recordId;
        syncRequestActions();
    }

    private void requestActionsAndSave() {
        Request r = getRequest();
        if (r == null) {
            return;
        }
        if (requests.contains(r)) {
            r = requests.get(requests.indexOf(r));
        } else {
            requests.add(r);
        }

        if (r.hasRequest) {
            return;
        }

        if (r.isRequesting) {
            return;
        }

        r.isRequesting = true;
        request = r;
        String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + recordId + "&startTime=" + request.startTime + "&endTime=" + (request.startTime + 20000);
        ServiceInterfaceTools.getinstance().getRecordActions(url, ServiceInterfaceTools.GETSOUNDTRACKACTIONS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<WebAction> actions = (List<WebAction>) object;
                if (actions != null && actions.size() > 0) {
                    request.hasRequest = true;
                    if (!requests.contains(request)) {
                        requests.add(request);
                    }

                    if (actions != null && actions.size() > 0) {
                        for (WebAction action : actions) {
                            if (webActions.contains(action)) {
                                continue;
                            }

                            webActions.add(action);

                            if (!TextUtils.isEmpty(action.getData())) {
                                try {
                                    JSONObject data = new JSONObject(action.getData());
                                    if (data.has("actionType")) {
                                        int actionType = data.getInt("actionType");
                                        switch (actionType) {
                                            case 19:
//                                                Log.e("check_action","action:setWebVedio:" + action.getData());
//                                                action.setWebVedio(gson.fromJson(action.getData(), WebVedio.class));
                                                WebVedio webVedio = gson.fromJson(action.getData(), WebVedio.class);
                                                if (!webVedios.contains(webVedio)) {
                                                    webVedios.add(webVedio);
                                                }
                                                break;
                                            case 202:
                                                if (userVedioManager != null) {
                                                    userVedioManager.refreshUserInfo(data.getString("userId"), data.getString("userName"), data.getString("avatarUrl"));
                                                }
                                                break;
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    Collections.sort(requests);
                    Collections.sort(webActions);
                    Log.e("webActions", "webActions:" + webActions);
                }

            }
        });
    }


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

    private Request getRequest() {

        if (playTime == 0) {
            request = firstRequest;
            return request;
        }

        int secend = (int) (playTime / 1000);
        if (secend <= 2) {
            return null;
        }
        if (secend % 10 != 0) {
            return null;
        }

        long startTime = secend * 2 * 1000;
        if (startTime > totalTime) {
            return null;
        }

        request = new Request(secend * 2 * 1000);
        return request;
    }

    private Request getRequest(int playTime) {

        if (playTime == 0) {
            request = new Request(0);
            return request;
        }

        int secend = (int) (playTime / 1000);
        if (secend <= 2) {
            return null;
        }
        if (secend % 10 != 0) {
            return null;
        }

        long startTime = secend * 2 * 1000;
        if (startTime > totalTime) {
            return null;
        }
        request = new Request(secend * 2 * 1000);
        return request;
    }


    private DocumentPageCache pageCache;
    ;

    public void release() {
        currentPage = -1;
//        if (web != null) {
//            web.removeAllViews();
//            web.destroy();
//            web = null;
//        }
        webActions.clear();
        currentPartWebActions = null;
        mediaPlayPages.clear();
        requests.clear();
        if (webVedioManager != null) {
            webVedioManager.release();
        }
        instance = null;
    }

    private void downLoadDocumentPageAndShow(final int pageNumber) {
        if(meetingConfig == null || meetingConfig.getDocument() == null){
            return;
        }
        Observable.just(meetingConfig.getDocument()).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
            @Override
            public Object apply(MeetingDocument document) throws Exception {
                DocumentPage page = document.getDocumentPages().get(pageNumber - 1);
                queryAndDownLoadPageToShow(page, true);
                return page;
            }
        }).subscribe();
    }

    private void showCurrentPage(final DocumentPage documentPage) {
        Observable.just(documentPage).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DocumentPage>() {
            @Override
            public void accept(DocumentPage page) throws Exception {
	            if (web == null) {
                    return;
                }
	            Log.e("showCurrentPage", "page:" + documentPage);
	            web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                web.loadUrl("javascript:ShowPDF('" + documentPage.getShowingPath() + "'," + (documentPage.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
                web.loadUrl("javascript:Record()", null);
            }
        }).subscribe();

    }

    private void queryAndDownLoadPageToShow(final DocumentPage documentPage, final boolean needRedownload) {
        String pageUrl = documentPage.getPageUrl();
        DocumentPage page = pageCache.getPageCache(pageUrl);
        Log.e("-", "get_cach_page:" + page + "--> url:" + documentPage.getPageUrl());
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(documentPage.getDocumentId());
                page.setPageNumber(documentPage.getPageNumber());
                pageCache.cacheFile(page);
                showCurrentPage(page);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }
        MeetingDocument document = meetingConfig.getDocument();
        String meetingId = meetingConfig.getMeetingId();

        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                document.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);
            String part = "";
            if (1 == uploadao.getServiceProviderId()) {
                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + document.getNewPath()
                        + "/" + fileName;
            } else if (2 == uploadao.getServiceProviderId()) {
                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + document.getNewPath() + "/" + fileName;
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_" + (documentPage.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_<" + document.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            int pageIndex = 1;
            if (meetingConfig.getPageNumber() == 0) {
                pageIndex = 1;
            } else if (meetingConfig.getPageNumber() > 0) {
                pageIndex = meetingConfig.getPageNumber();
            }

            Log.e("-", "showUrl:" + showUrl);

            documentPage.setSavedLocalPath(pathLocalPath);

            Log.e("-", "page:" + documentPage);
            //保存在本地的地址

            DownloadUtil.get().download(pageUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    documentPage.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + documentPage);
                    pageCache.cacheFile(documentPage);
                    showCurrentPage(documentPage);

                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + documentPage);
                    if (needRedownload) {
                        queryAndDownLoadPageToShow(documentPage, false);
                    }
                }
            });
        }
    }

    public String encoderByMd5(String str) {
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
            return newstr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
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

    private void clearExecuted() {
        for (WebAction webAction : webActions) {
            webAction.setExecuted(false);
        }
    }

    private void requestActionsBySeek(int time) {
        Request r = getRequest(time);
        if (r == null) {
            return;
        }
        if (requests.contains(r)) {
            r = requests.get(requests.indexOf(r));
        } else {
            requests.add(r);
        }

        if (r.hasRequest) {
            return;
        }

        if (r.isRequesting) {
            return;
        }

        r.isRequesting = true;

        request = r;

        String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + recordId + "&startTime=" + request.startTime + "&endTime=" + (request.startTime + 20000);
        ServiceInterfaceTools.getinstance().getRecordActions(url, ServiceInterfaceTools.GETSOUNDTRACKACTIONS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<WebAction> actions = (List<WebAction>) object;

                if (actions != null && actions.size() > 0) {
                    request.hasRequest = true;
                    if (!requests.contains(request)) {
                        requests.add(request);
                    }

                    if (actions != null && actions.size() > 0) {
                        for (WebAction action : actions) {
                            if (webActions.contains(action)) {
                                continue;
                            }

                            webActions.add(action);

                            if (!TextUtils.isEmpty(action.getData())) {
                                try {
                                    JSONObject data = new JSONObject(action.getData());
                                    if (data.has("actionType")) {
                                        int actionType = data.getInt("actionType");
                                        switch (actionType) {
                                            case 19:
//                                                Log.e("check_action","action:setWebVedio:" + action.getData());
//                                                action.setWebVedio(gson.fromJson(action.getData(), WebVedio.class));
                                                WebVedio webVedio = gson.fromJson(action.getData(), WebVedio.class);
                                                if (!webVedios.contains(webVedio)) {
                                                    webVedios.add(webVedio);
                                                }
                                                break;
                                            case 202:
                                                if (userVedioManager != null) {
                                                    userVedioManager.refreshUserInfo(data.getString("userId"), data.getString("userName"), data.getString("avatarUrl"));
                                                }
                                                break;
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    Collections.sort(requests);
                    Collections.sort(webActions);
                    Log.e("webActions", "webActions:" + webActions);
                }

            }
        });
    }


}
