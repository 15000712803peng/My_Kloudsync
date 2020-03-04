package com.kloudsync.techexcel.help;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class SmallNoteViewHelper {
    private XWalkView smallNoteView;
    private RelativeLayout noteCotainer;
    private MeetingConfig meetingConfig;
    public SmallNoteViewHelper(RelativeLayout noteCotainer,XWalkView smallNoteView,MeetingConfig meetingConfig){
        this.noteCotainer = noteCotainer;
        this.smallNoteView = smallNoteView;
        this.meetingConfig = meetingConfig;
    }

    public void init(Context context){
        smallNoteView.setZOrderOnTop(false);
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
        smallNoteView.load(url, null);
    }

    private void initAfterPageLoad(){
        String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
        Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")");
        smallNoteView.load("javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
        smallNoteView.load("javascript:Record()", null);
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
        smallNoteView.load("javascript:ShowToolbar(" + false + ")", null);
        smallNoteView.load("javascript:FromApp('" + key + "'," + _data + ")", null);
        smallNoteView.load("javascript:Record()", null);
    }

    public class NoteJavascriptInterface {
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
                    initAfterPageLoad();
                }
            });


        }
    }

}
