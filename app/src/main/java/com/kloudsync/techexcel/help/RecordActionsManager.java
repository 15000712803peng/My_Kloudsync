package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.kloudsync.techexcel.bean.EventPlayWebVedio;
import com.kloudsync.techexcel.bean.MediaPlayPage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.PreloadPage;
import com.kloudsync.techexcel.bean.WebVedio;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.RecordingPageCache;
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
import org.xwalk.core.XWalkView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/11/21.
 */

public class RecordActionsManager {

    private static RecordActionsManager instance;
    private volatile long playTime;
    private Activity context;
    private List<WebAction> webActions = new ArrayList<>();
    private List<WebVedio> webVedios = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private List<MediaPlayPage> mediaPlayPages = new ArrayList<>();
    private int recordId;
    private volatile long totalTime = 0;
    private XWalkView web;
    private SurfaceView surfaceView;
    private WebVedioManager webVedioManager;
    private String downloadUrlPre = "";
    private UserVedioManager userVedioManager;
    private MeetingConfig meetingConfig;
    private RelativeLayout webVedioPlayLayout;
    private SyncWebActionsCache webActionsCache;

    public void setUserVedioManager(UserVedioManager userVedioManager) {
        this.userVedioManager = userVedioManager;
    }

    public void setWeb(XWalkView web, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        this.web = web;
    }

    public void setWeb(XWalkView web) {
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
    private RecordActionsManager(Activity context) {
        this.context = context;
	    pageCache = RecordingPageCache.getInstance(context);
        webVedioManager = WebVedioManager.getInstance(context);
        webActionsCache = SyncWebActionsCache.getInstance(context);
        gson = new Gson();
    }

    public static RecordActionsManager getInstance(Activity context) {
        if (instance == null) {
            synchronized (RecordActionsManager.class) {
                if (instance == null) {
                    instance = new RecordActionsManager(context);
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
	            Log.e("check_part_actions", "currentPartWebActions=" + currentPartWebActions.toString());
	            handleSpecialWebActions(currentPartWebActions.getWebActions());
            }
        }

        if (currentPartWebActions == null) {
            return new ArrayList<>();
        }


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
	        Log.e("executeActions", "executeActions" + actions + ",action_executed:" + action.isExecuted());
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
            Log.e("doExecuteAction", "data:" + data);
            if (data.has("actionType")) {

                switch (data.getInt("actionType")) {
                    case 8:
                        MediaPlayPage page = new MediaPlayPage();
                        page.setTime(action.getTime());
                        int index = mediaPlayPages.indexOf(page);
                        if (index >= 0) {
                            MediaPlayPage mediaPlayPage = mediaPlayPages.get(index);
                            mediaPlayPage = pageCache.getPageCache(mediaPlayPage.getPageUrl());
                            if (mediaPlayPage != null) {
                                if (!TextUtils.isEmpty(mediaPlayPage.getSavedLocalPath())) {
                                    web.load("javascript:ShowPDF('" + mediaPlayPage.getShowUrl() + "', " + mediaPlayPage.getPageNumber() + ",0,'" + 0 + "'," + false + ")", null);
                                    downloadUrlPre = mediaPlayPage.getPageUrl().substring(0, mediaPlayPage.getPageUrl().lastIndexOf("/"));
                                    Log.e("ShowPDF", mediaPlayPage + "");
                                    web.load("javascript:ShowToolbar(" + false + ")", null);
                                    web.load("javascript:Record()", null);
                                }
                            }
                        }
                        //just show pdf
                        break;
                    case 19:
                        webVedioManager.execute(action.getWebVedio(), playTime);
                        break;
                    case 202:
                        break;

                }
            } else {
                web.load("javascript:PlayActionByTxt('" + action.getData() + "')", null);
                web.load("javascript:Record()", null);
            }
//                    Log.e("execute_action","action:" + action.getTime() + "--" + action.getData());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private volatile MediaPlayPage mediaPlayPage;

	private int currentPage = -1;



    private Gson gson;


    private void syncRequestActions() {
        Request r = getRequest();
	    Log.e("syncRequestActions", "step_one:get_request:" + r);
        if (r == null) {
            return;
        }
        final String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + recordId + "&startTime=" + request.startTime + "&endTime=" + (request.startTime + 20000);
        final String cacheUrl = url + "__time__separator__" + request.startTime + "__" + (request.startTime + 20000) + "__" + recordId;
        boolean isContain = webActionsCache.containPartWebActions(cacheUrl);

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
	    Log.e("syncRequestActions", "step_three:no_cache_by_url_and_request" + url);
        List<WebAction> actions = ServiceInterfaceTools.getinstance().syncGetRecordActions(url);
        if (actions != null && actions.size() > 0) {

            PartWebActions partWebActions = new PartWebActions();
            partWebActions.setStartTime(request.startTime);
            partWebActions.setEndTime(request.startTime + 20000);
            partWebActions.setUrl(cacheUrl);
            partWebActions.setWebActions(actions);
            webActionsCache.cacheActions(partWebActions);
	        Log.e("syncRequestActions", "step_four:request_success_and_cache:web_actions_size:" + partWebActions.getWebActions().size());
            if (!requests.contains(request)) {
                requests.add(request);
            }
	        handleSpecialWebActions(actions);
            Collections.sort(requests);
//                    Collections.sort(webActions);
        }

    }

	private void handleSpecialWebActions(List<WebAction> actions) {
		if (actions != null && actions.size() > 0) {
			for (WebAction action : actions) {
				if (!TextUtils.isEmpty(action.getData())) {
					try {
						JSONObject data = new JSONObject(action.getData());
						if (data.has("actionType")) {
							int actionType = data.getInt("actionType");
							switch (actionType) {
								case 8: {
									mediaPlayPage = new MediaPlayPage();
									mediaPlayPage.setPageNumber(data.getInt("pageNumber"));
									mediaPlayPage.setTime(action.getTime());
									mediaPlayPage.setItemId(data.getString("itemId"));

									if (mediaPlayPages.contains(mediaPlayPage)) {
										continue;
									}
									mediaPlayPages.add(mediaPlayPage);
									String _url = data.getString("attachmentUrl");

									if (!TextUtils.isEmpty(_url)) {
										String endfix = _url.substring(_url.lastIndexOf("."), _url.length());
										String path = _url.substring(0, _url.lastIndexOf("<"));
										mediaPlayPage.setPageUrl(path + mediaPlayPage.getPageNumber() + endfix);
										mediaPlayPage.setShowUrl(FileUtils.getBaseDir() + "/recording/" + _url.substring(_url.lastIndexOf("/"), _url.length()));
									}

									Log.e("check_page", "page:" + mediaPlayPage);
									downLoadPage(mediaPlayPage, true);
								}
								break;
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
	}

	public void preLoad(int recordId) {
		this.recordId = recordId;
		syncRequestActions();
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

	private synchronized void downLoadPage(final MediaPlayPage mediaPlayPage, final boolean needRedownload) {

		if (mediaPlayPage.isDownloading()) {
			return;
		}
		String pageUrl = mediaPlayPage.getPageUrl();
		final MediaPlayPage page = pageCache.getPageCache(pageUrl);

        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
		        && !TextUtils.isEmpty(page.getSavedLocalPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }

		mediaPlayPage.setSavedLocalPath(FileUtils.getBaseDir() + "/recording/" + pageUrl.substring(pageUrl.lastIndexOf("/"), pageUrl.length()));
		mediaPlayPage.setDownloading(true);

		Log.e("downLoadPage", "get cach page:" + page);
		DownloadUtil.get().download(pageUrl, mediaPlayPage.getSavedLocalPath(), new DownloadUtil.OnDownloadListener() {
			@SuppressLint("LongLogTag")
			@Override
			public void onDownloadSuccess(int arg0) {
				Log.e("downLoadPage", "success:" + mediaPlayPage);
				mediaPlayPage.setDownloading(false);
				pageCache.cachePageFile(mediaPlayPage);
			}

			@Override
			public void onDownloading(final int progress) {

			}

			@Override
			public void onDownloadFailed() {

				mediaPlayPage.setDownloading(false);
				if (needRedownload) {
					downLoadPage(mediaPlayPage, false);
				}
			}
		});

	}

	public void preloadFile(String url, int pageNumber) {
		PreloadPage preloadPage = new PreloadPage();
		preloadPage.setPageNumber(pageNumber);
		if (!TextUtils.isEmpty(downloadUrlPre)) {
			String pageUrl = downloadUrlPre;
			String type = url.substring(url.lastIndexOf("."));
			String end = url.substring(url.lastIndexOf("/"));
			end = end.substring(0, end.lastIndexOf("<")) + pageNumber + type;
			preloadPage.setPageUrl(pageUrl + end);
        }
		preloadPage.setNotifyUrl(url);
		downPreoadPage(preloadPage, true);

    }

	private synchronized void downPreoadPage(final PreloadPage preloadPage, final boolean needRedownload) {
		Log.e("downLoadPreloadPage", "page:" + preloadPage);
		if (preloadPage == null || (this.preloadPage != null && this.preloadPage.equals(preloadPage))) {
			return;
        }

		String pageUrl = preloadPage.getPageUrl();
		String localSavePage = pageCache.getPreloadCache(pageUrl);
		if (!TextUtils.isEmpty(localSavePage)) {
			if (new File(localSavePage).exists()) {
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (web != null) {
							Log.e("downLoadPreloadPage", "AfterDownloadFile,page:" + preloadPage);
							web.load("javascript:AfterDownloadFile('" + preloadPage.getNotifyUrl() + "', " + preloadPage.getPageNumber() + ")", null);

						}
					}
				});
				return;
			} else {
				pageCache.removePreloadFile(pageUrl);
			}
		}

		preloadPage.setSavedLocalPath(FileUtils.getBaseDir() + "/recording/" + pageUrl.substring(pageUrl.lastIndexOf("/"), pageUrl.length()));
		preloadPage.setDownloading(true);

		Log.e("downLoadPreloadPage", "download,page:" + preloadPage);
		this.preloadPage = preloadPage;
		DownloadUtil.get().download(pageUrl, preloadPage.getSavedLocalPath(), new DownloadUtil.OnDownloadListener() {
			@SuppressLint("LongLogTag")
			@Override
			public void onDownloadSuccess(int arg0) {
				Log.e("downPreoadPage", "success:" + mediaPlayPage);
				pageCache.cachePreloadFile(preloadPage.getPageUrl(), preloadPage.getSavedLocalPath());
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (web != null) {
							web.load("javascript:AfterDownloadFile('" + preloadPage.getNotifyUrl() + "', " + preloadPage.getPageNumber() + ")", null);

						}
					}
				});
			}

			@Override
			public void onDownloading(final int progress) {

            }

			@Override
			public void onDownloadFailed() {

				mediaPlayPage.setDownloading(false);
				if (needRedownload) {
					downPreoadPage(preloadPage, false);
				}
			}
		});

    }

	private PreloadPage preloadPage;


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

	private RecordingPageCache pageCache;


	public void release() {
		currentPage = -1;
		if (web != null) {
			web.removeAllViews();
			web.onDestroy();
			web = null;
		}
		webActions.clear();
		currentPartWebActions = null;
		mediaPlayPages.clear();
		requests.clear();
		if (webVedioManager != null) {
			webVedioManager.release();
		}
		instance = null;
	}


}
