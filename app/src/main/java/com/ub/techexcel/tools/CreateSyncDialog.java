package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.SoundtrackBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class CreateSyncDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private ImageView close;
    private TextView addaudio, addrecord;
    private EditText edittext;
    private ImageView delete1, delete2;
    private TextView recordsync, cancel;
    private Document favorite = new Document();
    private Document recordfavorite = new Document();
    private CheckBox checkBox;
    private String attachmentId;
    private TextView recordname, recordtime;
    private TextView bgname, bgtime;
    private LinearLayout backgroundAudioLayout;
    private LinearLayout recordMyVoiceLayout;
    private RelativeLayout voiceItemLayout;
    private LinearLayout addVoiceLayout;
    private CheckBox isPublic;
    public CreateSyncDialog(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.cancel();
            return;
        } else {
            initPopuptWindow();
        }
    }


    private void setCreateSyncText() {
        if (!checkBox.isChecked()) {
            if (TextUtils.isEmpty(favorite.getItemID()) && TextUtil.isEmpty(recordfavorite.getItemID())) {
                recordsync.setEnabled(false);
            }
            if (!TextUtil.isEmpty(favorite.getItemID()) || !TextUtils.isEmpty(recordfavorite.getItemID())) {
                recordsync.setEnabled(true);
            }
        }

        if (checkBox.isChecked()) {
            recordsync.setText(R.string.mtRecordSync);
            recordsync.setEnabled(true);
        } else {
            recordsync.setText(R.string.sync);
        }
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.yinxiang_create_popup, null);
        close = (ImageView) view.findViewById(R.id.close);
        cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        addaudio = (TextView) view.findViewById(R.id.addaudio);
        addrecord = (TextView) view.findViewById(R.id.addrecord);
        recordname = (TextView) view.findViewById(R.id.recordname);
        recordtime = (TextView) view.findViewById(R.id.recordtime);
        bgname = (TextView) view.findViewById(R.id.bgname);
        bgtime = (TextView) view.findViewById(R.id.bgtime);
        backgroundAudioLayout = (LinearLayout) view.findViewById(R.id.layout_background_audio);
        edittext = (EditText) view.findViewById(R.id.edittext);
        String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
        edittext.setText(AppConfig.UserName + "_" + time);
        voiceItemLayout = view.findViewById(R.id.layout_voice_item);
        checkBox = (CheckBox) view.findViewById(R.id.checkboxx);
        isPublic = (CheckBox) view.findViewById(R.id.isPublic);
        recordMyVoiceLayout = view.findViewById(R.id.layout_record_my_voice);
        addVoiceLayout = view.findViewById(R.id.layout_add_voice);
        recordMyVoiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
                if (checkBox.isChecked()) {
                    addrecord.setVisibility(View.INVISIBLE);
                } else {
                    addrecord.setVisibility(View.VISIBLE);
                }
                setCreateSyncText();
            }

        });
        delete1 = (ImageView) view.findViewById(R.id.delete1);
        delete2 = (ImageView) view.findViewById(R.id.delete2);
        delete1.setOnClickListener(this);
        delete2.setOnClickListener(this);

        recordsync = (TextView) view.findViewById(R.id.recordsync);
        recordsync.setOnClickListener(this);
        close.setOnClickListener(this);
        addaudio.setOnClickListener(this);
        addrecord.setOnClickListener(this);
        setCreateSyncText();
//        recordsync.setText("Sync");
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight() * 4 / 5 + 30;
        mPopupWindow.getWindow().setAttributes(params);

    }


    public void setAudioBean(Document favorite) {
        Log.e("Yinxiang", "setAudioBean");
        this.favorite = favorite;
        delete1.setVisibility(View.VISIBLE);
        addaudio.setVisibility(View.INVISIBLE);
        backgroundAudioLayout.setVisibility(View.VISIBLE);
        if (favorite != null) {
            bgname.setVisibility(View.VISIBLE);
            bgtime.setVisibility(View.VISIBLE);
            bgtime.setText("size:" + favorite.getSize() + "M");
            bgname.setText("Audio: " + favorite.getTitle());
        }
        setCreateSyncText();
    }

    public void setRecordBean(Document favorite) {
        Log.e("Yinxiang", "setRecordBean");
        this.recordfavorite = favorite;
        delete2.setVisibility(View.VISIBLE);
        addrecord.setVisibility(View.INVISIBLE);
        addVoiceLayout.setVisibility(View.INVISIBLE);
//        checkBox.setVisibility(View.INVISIBLE);
//        checkBox.setChecked(false);
//        recordsync.setText("Sync");

        if (recordfavorite != null) {
            voiceItemLayout.setVisibility(View.VISIBLE);
            recordname.setVisibility(View.VISIBLE);
            recordtime.setVisibility(View.VISIBLE);
            recordname.setText("Voice: " + recordfavorite.getTitle());
        }
        setCreateSyncText();
    }


    @SuppressLint("NewApi")
    public void show(String attachmentId) {
        if (mPopupWindow != null) {
            this.attachmentId = attachmentId;
            mPopupWindow.show();
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private SoundtrackBean soundtrackBean = new SoundtrackBean();

    private JSONObject prepareCreateSoundtrackRequest(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AttachmentID", Integer.parseInt(attachmentId));
            jsonObject.put("Title", edittext.getText().toString());
            if (recordfavorite == null) {
                recordfavorite = new Document();
                recordfavorite.setAttachmentID(0 + "");
            }
            jsonObject.put("SelectedAudioAttachmentID", "0");
            jsonObject.put("SelectedAudioTitle",  "");
            if(backgroudAudioInfo != null){
                jsonObject.put("BackgroudMusicAttachmentID", backgroudAudioInfo.audioId +"");
                jsonObject.put("BackgroudMusicTitle", backgroudAudioInfo.audioTitle);
            }else {
                jsonObject.put("BackgroudMusicAttachmentID", "0");
                jsonObject.put("BackgroudMusicTitle", "");
            }
            jsonObject.put("EnableBackgroud", 1);
            jsonObject.put("EnableSelectVoice", 1);
            jsonObject.put("EnableRecordNewVoice", 1);
            jsonObject.put("IsPublic", isPublic.isChecked() ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void createSoundtrack() {
        Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                return ServiceInterfaceTools.getinstance().syncRequstCreateSoundtrack(prepareCreateSoundtrackRequest());
            }
        }).subscribe();
//        new ApiTask(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    JSONObject returnjson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Soundtrack/CreateSoundtrack", prepareCreateSoundtrackRequest());
//                    Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString());
//                    if (returnjson.getInt("RetCode") == 0) {
//                        JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
//                        soundtrackBean = new SoundtrackBean();
//                        soundtrackBean.setSoundtrackID(jsonObject1.getInt("SoundtrackID"));
//                        soundtrackBean.setTitle(jsonObject1.getString("Title"));
//                        soundtrackBean.setUserID(jsonObject1.getString("UserID"));
//                        soundtrackBean.setUserName(jsonObject1.getString("UserName"));
//                        soundtrackBean.setAvatarUrl(jsonObject1.getString("AvatarUrl"));
//                        soundtrackBean.setDuration(jsonObject1.getString("Duration"));
//                        soundtrackBean.setCreatedDate(jsonObject1.getString("CreatedDate"));
//                        JSONObject pathinfo = jsonObject1.getJSONObject("PathInfo");
//                        soundtrackBean.setFileId(pathinfo.getInt("FileID"));
//                        soundtrackBean.setPath(pathinfo.getString("Path"));
//                        soundtrackBean.setBackgroudMusicAttachmentID(jsonObject1.getInt("BackgroudMusicAttachmentID"));
//                        soundtrackBean.setNewAudioAttachmentID(jsonObject1.getInt("NewAudioAttachmentID"));
//                        soundtrackBean.setSelectedAudioAttachmentID(jsonObject1.getInt("SelectedAudioAttachmentID"));
//                        if (soundtrackBean.getBackgroudMusicAttachmentID() != 0) {
//                            try {
//                                JSONObject jsonObject2 = jsonObject1.getJSONObject("BackgroudMusicInfo");
//                                Document favoriteAudio = new Document();
//                                favoriteAudio.setFileDownloadURL(jsonObject2.getString("AttachmentUrl"));
//                                favoriteAudio.setItemID(jsonObject2.getInt("ItemID") + "");
//                                favoriteAudio.setTitle(jsonObject2.getString("Title"));
//                                favoriteAudio.setAttachmentID(jsonObject2.getInt("AttachmentID") + "");
//                                favoriteAudio.setDuration(jsonObject2.getString("VideoDuration"));
//                                soundtrackBean.setBackgroudMusicInfo(favoriteAudio);
//                            } catch (Exception e) {
//                                soundtrackBean.setBackgroudMusicInfo(new Document());
//                                e.printStackTrace();
//                            }
//                        }
//                        if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
//                            try {
//                                JSONObject jsonObject3 = jsonObject1.getJSONObject("SelectedAudioInfo");
//                                Document favoriteAudio = new Document();
//                                favoriteAudio.setFileDownloadURL(jsonObject3.getString("AttachmentUrl"));
//                                favoriteAudio.setItemID(jsonObject3.getInt("ItemID") + "");
//                                favoriteAudio.setTitle(jsonObject3.getString("Title"));
//                                favoriteAudio.setAttachmentID(jsonObject3.getInt("AttachmentID") + "");
//                                favoriteAudio.setDuration(jsonObject3.getString("VideoDuration"));
//                                soundtrackBean.setSelectedAudioInfo(favoriteAudio);
//                            } catch (Exception e) {
//                                soundtrackBean.setSelectedAudioInfo(new Document());
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start(ThreadManager.getManager());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.addaudio:
                showSelectAudioDialog();
                break;
            case R.id.addrecord:
                showSelectAudioDialog();
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.recordsync:
                Toast.makeText(mContext,"开发中",Toast.LENGTH_SHORT).show();
//                dismiss();
//                createSoundtrack();

                break;
            case R.id.delete1:
                favorite = new Document();
                backgroundAudioLayout.setVisibility(View.INVISIBLE);
                delete1.setVisibility(View.INVISIBLE);
                addaudio.setVisibility(View.VISIBLE);
                bgname.setVisibility(View.INVISIBLE);
                bgtime.setVisibility(View.INVISIBLE);
                setCreateSyncText();
                break;
            case R.id.delete2:
                recordfavorite = new Document();
                voiceItemLayout.setVisibility(View.INVISIBLE);
                addVoiceLayout.setVisibility(View.VISIBLE);
                delete2.setVisibility(View.INVISIBLE);
                if (checkBox.isChecked()) {
                    addrecord.setVisibility(View.INVISIBLE);
                } else {
                    addrecord.setVisibility(View.VISIBLE);
                }
                setCreateSyncText();
//                addrecord.setVisibility(View.VISIBLE);
//                recordname.setVisibility(View.INVISIBLE);
//                recordtime.setVisibility(View.INVISIBLE);
//                recordsync.setText("Sync");
                break;
            default:
                break;
        }
    }

    SelectAudioForCreatingSyncDialog selectAudioDialog;
    private void showSelectAudioDialog(){
        if(selectAudioDialog != null){
            selectAudioDialog.cancel();
        }
        selectAudioDialog = new SelectAudioForCreatingSyncDialog(mContext);
        selectAudioDialog.show();
    }

    class BackgroudAudioInfo{
        public String audioId;
        public String audioTitle;
    }

    private BackgroudAudioInfo backgroudAudioInfo;

}
