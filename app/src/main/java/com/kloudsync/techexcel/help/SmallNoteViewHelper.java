package com.kloudsync.techexcel.help;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.DigitalNoteEventInSoundtrack;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SmallNoteViewHelper {

    public static final int EVENT_NOTE_SHOW_IN_SMALL_WINDOW = 300;
    public static final int EVENT_NOTE_SHOW_SWITCH_TO_MAIN_WINDOW = 301;
    public static final int EVENT_NOTE_CHANGE_PAGE = 302;
    public static final int EVENT_NOTE_SHOW_POSITION_CHANGED = 303;
    public static final int EVENT_NOTE_SHOW_LINE = 304;
    public static final int EVENT_NOTE_CLOSE_SMALL_WINDOW = 305;
    public static final int EVENT_NOTE_CLOSE_MAIN_WINDOW = 306;
    public static final int EVENT_NOTE_SHOW_SWITCH_TO_SMALL_WINDOW = 307;
    public static final int EVENT_NOTE_SHOW_IN_MIAI_WINDOW = 308;

    private WebView smallNoteView;
    private RelativeLayout noteCotainer;
    private MeetingConfig meetingConfig;
    private Context context;

    public SmallNoteViewHelper(RelativeLayout noteCotainer, WebView smallNoteView, MeetingConfig meetingConfig) {
        this.noteCotainer = noteCotainer;
        this.smallNoteView = smallNoteView;
        this.meetingConfig = meetingConfig;
    }

    public void init(Context context) {
        this.context = context;
//        smallNoteView.setZOrderOnTop(false);
        smallNoteView.getSettings().setJavaScriptEnabled(true);
        smallNoteView.getSettings().setDomStorageEnabled(true);
        smallNoteView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        smallNoteView.addJavascriptInterface(new NoteJavascriptInterface(), "AnalyticsWebInterface");
        int deviceType = DeviceManager.getDeviceType(context);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        final String url = indexUrl;
        smallNoteView.loadUrl(url, null);
    }

    private void initAfterPageLoad() {
        String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
        Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")");
        smallNoteView.loadUrl("javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
        smallNoteView.loadUrl("javascript:Record()", null);
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

        smallNoteView.loadUrl("javascript:ShowToolbar(" + false + ")", null);
        smallNoteView.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);
        smallNoteView.loadUrl("javascript:Record()", null);
        doProcess();
    }

    public class NoteJavascriptInterface {
        @JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
        }

        @JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);

        }

        @JavascriptInterface
        public synchronized void callAppFunction(final String action, final String data) {
            Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
            if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
                return;
            }
        }

        @JavascriptInterface
        public void afterLoadPageFunction() {
            Log.e("JavascriptInterface", "afterLoadPageFunction");
            Observable.just("init").delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    initAfterPageLoad();
                }
            });

        }
    }

    private CopyOnWriteArrayList noteContrllerDatas = new CopyOnWriteArrayList();

    private void doProcess() {

        noteContrllerDatas.clear();

        Observable.just("request_note_controller").observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return getCacheNoteData(context);
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String response) throws Exception {
                if (!TextUtils.isEmpty(response)) {
                    List<DigitalNoteEventInSoundtrack> noteEventsData = new Gson().fromJson(response, new TypeToken<List<DigitalNoteEventInSoundtrack>>() {
                    }.getType());
                    if (noteEventsData != null && noteEventsData.size() > 0) {
                        Log.e("noteEventsData", "noteEventsData_size:" + noteEventsData.size());
//                        doExecuteNoteEvent();
                    }

                }
            }
        }).subscribe();
    }

    private void doExecuteNoteEvent(DigitalNoteEventInSoundtrack noteEvent) {
        switch (noteEvent.getActionType()) {
            case EVENT_NOTE_SHOW_IN_SMALL_WINDOW:
                break;
            case EVENT_NOTE_SHOW_SWITCH_TO_MAIN_WINDOW:
                break;
            case EVENT_NOTE_CHANGE_PAGE:
                break;
            case EVENT_NOTE_SHOW_POSITION_CHANGED:
                break;
            case EVENT_NOTE_SHOW_LINE:
                break;
            case EVENT_NOTE_CLOSE_SMALL_WINDOW:
                break;
            case EVENT_NOTE_CLOSE_MAIN_WINDOW:
                break;
            case EVENT_NOTE_SHOW_SWITCH_TO_SMALL_WINDOW:
                break;
            case EVENT_NOTE_SHOW_IN_MIAI_WINDOW:
                break;

        }
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
            Log.e("getCacheNoteData","io_exepton:" + e);
            e.printStackTrace();
        }

        Log.e("getCacheNoteData",":result" + result);

        return result;
    }



}
