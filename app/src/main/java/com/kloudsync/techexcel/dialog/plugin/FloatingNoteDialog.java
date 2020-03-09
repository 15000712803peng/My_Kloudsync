package com.kloudsync.techexcel.dialog.plugin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventHighlightNote;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.bean.UserNotes;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.RecordNoteActionManager;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.NoteViewManager;
import com.kloudsync.techexcel.help.PageActionsAndNotesMgr;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.kloudsync.techexcel.view.spinner.NiceSpinner;
import com.kloudsync.techexcel.view.spinner.OnSpinnerItemSelectedListener;
import com.kloudsync.techexcel.view.spinner.UserNoteTextFormatter;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class FloatingNoteDialog implements View.OnClickListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private MeetingConfig meetingConfig;
    private ImageView backImage;
    private XWalkView floatwebview;
    private TextView title;
    private ImageView changefloatingnote;

    public interface  FloatingListener{
        void changeHomePage(int noteId);
    }

    private FloatingListener floatingListener;

    public void  setFloatingListener(FloatingListener floatingListener){
        this.floatingListener=floatingListener;
    }

    public FloatingNoteDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.floatingnotedialog, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        backImage = view.findViewById(R.id.back);
        backImage.setOnClickListener(this);
        floatwebview = view.findViewById(R.id.xwalkview);
        title = view.findViewById(R.id.title);
        changefloatingnote = view.findViewById(R.id.changefloatingnote);
        changefloatingnote.setOnClickListener(this);
        initWeb();
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels);
        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        if (Tools.isOrientationPortrait((Activity) mContext)) {
            //竖屏
            Log.e("check_oritation", "oritation:portrait");
            dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            params.width = mContext.getResources().getDisplayMetrics().widthPixels;
            params.height = Tools.dip2px(mContext, 420);
        } else {
            Log.e("check_oritation", "oritation:landscape");
            dialog.getWindow().setGravity(Gravity.RIGHT);
            params.height = heigth;
            params.width = Tools.dip2px(mContext, 300);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setWindowAnimations(R.style.anination3);
        }
        dialog.getWindow().setAttributes(params);
    }

    private void initWeb() {
        floatwebview.setZOrderOnTop(false);
        floatwebview.getSettings().setJavaScriptEnabled(true);
        floatwebview.getSettings().setDomStorageEnabled(true);
        floatwebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        floatwebview.addJavascriptInterface(new FloatNoteJavascriptInterface(), "AnalyticsWebInterface");
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        Log.e("floatingnote", "加载浮窗");
        String indexUrl = "file:///android_asset/index.html";
        floatwebview.load(indexUrl, null);
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;

    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if(currentNote!=null){
                    RecordNoteActionManager.getManager(mContext).sendClosePopupActons(currentNote.getNoteID());
                }
                dismiss();
                break;
            case R.id.changefloatingnote:
                if(currentNote!=null){
                    RecordNoteActionManager.getManager(mContext).sendDisplayPopupHomepageActions(currentNote.getNoteID(),lastjsonObject);
                }
//                NoteViewManager.getInstance().requestNoteToShow(currentNote.getNoteID());
                if(floatingListener!=null){
                    floatingListener.changeHomePage(currentNote.getNoteID());
                }
                dismiss();
                break;
        }
    }

    public void show(final long noteid, final MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        if (dialog != null && !dialog.isShowing()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                    process(noteid, meetingConfig);
                }
            },100);
        }
    }

    Note currentNote;
    private JSONObject lastjsonObject=new JSONObject();
    private void process(final long noteId, final MeetingConfig meetingConfig) {
        if (meetingConfig.getDocument() == null) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "DocumentNote/Item?noteID=" + 1915234;
        MeetingServiceTools.getInstance().getBlueToothNoteDetail(url, MeetingServiceTools.GETBLUETOOTHNOTEDETAIL, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                currentNote= (Note) object;
                title.setText(currentNote.getTitle());
                String lastModifiedDate=currentNote.getLastModifiedDate();
                String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
                Log.e("floatingnote", localNoteBlankPage);
                floatwebview.load("javascript:ShowPDF('" + localNoteBlankPage + "'," +1 + ",''," + currentNote.getAttachmentID() + "," + true + ")", null);
//                floatwebview.load("javascript:ShowToolbar(" + false + ")", null);
//                floatwebview.load("javascript:StopRecord()", null);
                handleBluetoothNote(currentNote,lastModifiedDate);
            }
        });
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
                    lastjsonObject=ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
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
                floatwebview.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                RecordNoteActionManager.getManager(mContext).sendDisplayPopupActions(note.getNoteID(),jsonObject);
            }
        }).subscribe();
    }



    public class FloatNoteJavascriptInterface {

        @org.xwalk.core.JavascriptInterface
        public void afterLoadPageFunction() {
            Log.e("floatingnote", "afterLoadPageFunction");
        }


        @org.xwalk.core.JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
            NoteViewManager.getInstance().getNotePageActionsToShow(meetingConfig);
        }

        @org.xwalk.core.JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);

        }

    }





}
