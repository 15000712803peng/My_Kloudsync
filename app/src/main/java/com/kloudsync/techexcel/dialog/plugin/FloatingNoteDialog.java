package com.kloudsync.techexcel.dialog.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.RecordNoteActionManager;
import com.kloudsync.techexcel.help.NoteViewManager;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.json.JSONException;
import org.json.JSONObject;



import java.io.File;
import java.net.URL;

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
    private WebView floatwebview;
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

    @SuppressLint("JavascriptInterface")
    private void initWeb() {

        floatwebview.getSettings().setJavaScriptEnabled(true);
        floatwebview.getSettings().setDomStorageEnabled(true);
        floatwebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        floatwebview.addJavascriptInterface(new FloatNoteJavascriptInterface(), "AnalyticsWebInterface");
        Log.e("floatingnote", "加载浮窗");
        String indexUrl = "file:///android_asset/index.html";
        floatwebview.loadUrl(indexUrl, null);
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
                closeFloating();
                break;
            case R.id.changefloatingnote:  //跳到主界面
                if(currentNote!=null){
                    RecordNoteActionManager.getManager(mContext).sendDisplayPopupHomepageActions(currentNote.getNoteID(),lastjsonObject);
                }
                if(floatingListener!=null){
                    floatingListener.changeHomePage(currentNote.getNoteID());
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
//                floatwebview.load("javascript:ShowToolbar(" + false + ")", null);
//                floatwebview.load("javascript:StopRecord()", null);
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

    /**
     * 笔记先于音想打开
     */
    public void displayPopupActions(){
        RecordNoteActionManager.getManager(mContext).sendDisplayPopupActions(currentNote.getNoteID(),lastjsonObject);
    }

    private int oldNoteId=0;

    public void setOldNoteId(int oldNoteId){
        this.oldNoteId=oldNoteId;
    }

    private void handleBluetoothNote(final Note note, final String lastModifiedDate) {
        final String url=note.getSourceFileUrl();
        if(TextUtils.isEmpty(url)){
            //https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D80370/book_page_data.json?_=1583735802772
            return;
        }
        Observable.just(url).observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String url) throws Exception {
                String newUrl = "";
                URL _url = new URL(url);
                Log.e("floatingnote", _url.getPath());
                String path = _url.getPath();
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    int index = path.lastIndexOf("/");
                    if (index >= 0 && index < path.length()) {
                        String centerPart = path.substring(0, index);
                        String fileName = path.substring(index + 1, path.length());
                        Log.e("floatingnote", "centerPart:" + centerPart + ",fileName:" + fileName);
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
                                    Log.e("floatingnote", "url:" + url);
                                }

                            }
                        }
                    }
                }
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
                if(oldNoteId==0){
                    RecordNoteActionManager.getManager(mContext).sendDisplayPopupActions(note.getNoteID(),lastjsonObject);
                }else{
                    RecordNoteActionManager.getManager(mContext).sendChangePageActions(note.getNoteID(),oldNoteId,lastjsonObject);
                    oldNoteId=0;
                }
            }
        }).subscribe();
    }


    public class FloatNoteJavascriptInterface {

        @JavascriptInterface
        public void afterLoadPageFunction() {
            Log.e("floatingnote", "afterLoadPageFunction");
        }


        @JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
            NoteViewManager.getInstance().getNotePageActionsToShow(meetingConfig);
        }

        @JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);

        }
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


}
