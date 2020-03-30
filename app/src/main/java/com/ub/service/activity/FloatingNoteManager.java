package com.ub.service.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.RecordNoteActionManager;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static io.rong.imkit.utilities.RongUtils.screenWidth;

/**
 * Created by wang on 2017/6/19.
 */

public class FloatingNoteManager implements View.OnClickListener {
    /**
     * 定义浮动窗口布局
     */
    LinearLayout mlayout1;
    /**
     * 悬浮窗控件
     */
    private MeetingConfig meetingConfig;
    private ImageView backImage;
    private WebView floatwebview;
    private TextView title;
    private ImageView changefloatingnote;
    /**
     * 悬浮窗的布局
     */
    LayoutInflater inflater;
    WindowManager mWindowManager;
    WindowManager.LayoutParams layoutParams;
    GestureDetector mGestureDetector;

    public static  FloatingNoteManager instance;
    private Context mContext;

    public interface  FloatingChangeListener{
        void changeHomePage(int noteId);
    }

    private FloatingChangeListener floatingChangeListener;

    public void  setFloatingChangeListener(FloatingChangeListener floatingChangeListener){
        this.floatingChangeListener=floatingChangeListener;
    }

    public static FloatingNoteManager getManager(Context context) {
        if (instance == null) {
            synchronized (FloatingNoteManager.class) {
                if (instance == null) {
                    instance = new FloatingNoteManager(context);
                }
            }
        }
        return instance;
    }


    public FloatingNoteManager(Context context) {
        this.mContext = context;
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        inflater = LayoutInflater.from(mContext);
        mlayout1 = (LinearLayout) inflater.inflate(R.layout.floatingnotedialog, null);
        initFloating();
        layoutParams=getParams();
    }

    public LayoutParams getParams() {
        WindowManager.LayoutParams    layoutParams = new WindowManager.LayoutParams();
        layoutParams.format =  PixelFormat.RGBA_8888;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = screenWidth * 3 / 4;
        layoutParams.height =screenWidth * 3 / 4;
        return layoutParams;
    }
    private void initFloating() {
        mGestureDetector = new GestureDetector(mContext, new MyOnGestureListener1());
        mlayout1.setOnTouchListener(new FloatingListener());
        backImage = mlayout1.findViewById(R.id.back);
        backImage.setOnClickListener(this);
        floatwebview = mlayout1.findViewById(R.id.xwalkview);
        title = mlayout1.findViewById(R.id.title);
        changefloatingnote = mlayout1.findViewById(R.id.changefloatingnote);
        changefloatingnote.setOnClickListener(this);
        initWeb();
    }

    private void initWeb() {
//        floatwebview.setZOrderOnTop(false);
        floatwebview.getSettings().setJavaScriptEnabled(true);
        floatwebview.getSettings().setDomStorageEnabled(true);
        floatwebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        floatwebview.addJavascriptInterface(new FloatNoteJavascriptInterface(), "AnalyticsWebInterface");
//        XWalkPreferences.setValue("enable-javascript", true);
//        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
//        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        Log.e("floatingnote", "加载浮窗");
        String indexUrl = "file:///android_asset/index.html";
        floatwebview.loadUrl(indexUrl, null);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                closeFloating();
                break;
            case R.id.changefloatingnote:  //跳到主界面
                if(currentNote!=null){
                    RecordNoteActionManager.getManager(mContext).sendDisplayPopupHomepageActions(currentNote.getNoteID(),lastjsonObject);
                }
                if(floatingChangeListener!=null){
                    floatingChangeListener.changeHomePage(currentNote.getNoteID());
                }
                dismiss();
                break;
        }
    }


    public void  closeFloating(){
        if(currentNote!=null){
            RecordNoteActionManager.getManager(mContext).sendClosePopupActons(currentNote.getNoteID());
        }
        dismiss();
    }


    public void show(final long noteid, final MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWindowManager.addView(mlayout1,layoutParams);
                mlayout1.setVisibility(View.VISIBLE);
                process(noteid, meetingConfig);
            }
        },100);
    }

    public boolean isShowing(){
        if(mlayout1!=null){
            if(mlayout1.getVisibility()==View.VISIBLE){
                return true;
            }
        }
        return false;

    }


    public void dismiss() {
        if (isShowing()) {
            // 移除悬浮窗口
            mlayout1.setVisibility(View.INVISIBLE);
            mWindowManager.removeView(mlayout1);
        }
    }


    private Note currentNote;
    private JSONObject lastjsonObject=new JSONObject();

    private void process(final long noteId, final MeetingConfig meetingConfig) {
        if (meetingConfig.getDocument() == null) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "DocumentNote/Item?noteID=" + noteId;
        MeetingServiceTools.getInstance().getBlueToothNoteDetail(url, MeetingServiceTools.GETBLUETOOTHNOTEDETAIL, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                currentNote= (Note) object;
                title.setText(currentNote.getTitle());
                String lastModifiedDate=currentNote.getLastModifiedDate();
                String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
                Log.e("floatingnote", localNoteBlankPage);
                floatwebview.loadUrl("javascript:ShowPDF('" + localNoteBlankPage + "'," +1 + ",''," + currentNote.getAttachmentID() + "," + true + ")", null);
                handleBluetoothNote(currentNote,lastModifiedDate);
            }
        });
    }


    /**
     *
     * @param noteId
     * @param noteData  编码后为  {"lines":[{"id":"D342A8CB-DB21-4990-BD62-987A4D0419CC","points":[[3004,5136,500,1583830777.256],[3014,5139,880,1583830777.2579999],
     */
    public void followDrawNewLine(long noteId,String noteData){
        if(currentNote.getNoteID()==noteId){
            if (floatwebview != null) {
                String key = "ShowDotPanData";
                try {
                    JSONObject _data = new JSONObject();
                    _data.put("LinesData", Tools.getFromBase64(noteData));
                    _data.put("ShowInCenter", true);
                    _data.put("TriggerEvent", true);
                    floatwebview.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);
                    lastjsonObject=new JSONObject(Tools.getFromBase64(noteData));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleBluetoothNote(final Note note, final String lastModifiedDate) {
        final String url=note.getSourceFileUrl();
        //https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D80370/book_page_data.json?_=1583735802772
        Observable.just(url).observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                String newUrl = "";
                int index = url.lastIndexOf("/");
                if (index > 0 && index < url.length() - 2) {
                    newUrl = url.substring(0, index + 1) + "book_page_data.json?="+lastModifiedDate;
                }
                return newUrl;
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(url)) {
                    jsonObject = ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
                    lastjsonObject=jsonObject.getJSONObject("PaintData");
                    Log.e("floatingnote", "url:" + url+"   "+jsonObject.toString());
                }
                return jsonObject;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                String key = "ShowDotPanData";
                JSONObject _data = new JSONObject();
                _data.put("LinesData", jsonObject);
                _data.put("ShowInCenter", false);
                _data.put("TriggerEvent", false);
                Log.e("floatingnote", "ShowDotPanData");
                floatwebview.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);
                RecordNoteActionManager.getManager(mContext).sendDisplayPopupActions(note.getNoteID(),lastjsonObject);
            }
        }).subscribe();
    }


    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;

    private class FloatingListener implements OnTouchListener {

        @Override
        public boolean onTouch(View arg0, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    layoutParams.x += mTouchCurrentX - mTouchStartX;
                    layoutParams.y += mTouchCurrentY - mTouchStartY;
                    mWindowManager.updateViewLayout(mlayout1, layoutParams);
                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return mGestureDetector.onTouchEvent(event);  // 此处必须返回false，否则OnClickListener获取不到监听
        }
    }



    class MyOnGestureListener1 extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    }

    public class FloatNoteJavascriptInterface {

        @JavascriptInterface
        public void afterLoadPageFunction() {
            Log.e("floatingnote", "afterLoadPageFunction");
        }


        @JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
//            NoteViewManager.getInstance().getNotePageActionsToShow(meetingConfig);
        }

        @JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);

        }

    }


}
