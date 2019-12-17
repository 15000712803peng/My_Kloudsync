package com.ub.techexcel.tools;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.kloudsync.techexcel.bean.CompanyContact;
import com.kloudsync.techexcel.bean.DocumentData;
import com.kloudsync.techexcel.bean.DocumentDetail;
import com.kloudsync.techexcel.bean.FavoriteData;
import com.kloudsync.techexcel.bean.InviteInfo;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.PhoneItem;
import com.kloudsync.techexcel.bean.RoleInTeam;
import com.kloudsync.techexcel.bean.RongCloudData;
import com.kloudsync.techexcel.bean.SyncBook;
import com.kloudsync.techexcel.bean.params.AcceptFriendsRequestParams;
import com.kloudsync.techexcel.bean.params.AcceptInvitationsParams;
import com.kloudsync.techexcel.bean.params.InviteMultipleParams;
import com.kloudsync.techexcel.bean.params.InviteTeamAdminParams;
import com.kloudsync.techexcel.bean.params.InviteToCompanyParams;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.ConvertingResult;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.MyFriend;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.response.BindTvStatusResponse;
import com.kloudsync.techexcel.response.CompanyContactsResponse;
import com.kloudsync.techexcel.response.DevicesResponse;
import com.kloudsync.techexcel.response.FavoriteDocumentResponse;
import com.kloudsync.techexcel.response.FriendResponse;
import com.kloudsync.techexcel.response.InvitationsResponse;
import com.kloudsync.techexcel.response.InviteResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.response.OrganizationsResponse;
import com.kloudsync.techexcel.response.TeamAndSpaceSearchResponse;
import com.kloudsync.techexcel.response.TeamMembersResponse;
import com.kloudsync.techexcel.response.TeamSearchResponse;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.kloudsync.techexcel.response.UserInCompanyResponse;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.bean.AudioActionBean;
import com.ub.techexcel.bean.ChannelVO;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.bean.PageActionBean;
import com.ub.techexcel.bean.Record;
import com.ub.techexcel.bean.RecordAction;
import com.ub.techexcel.bean.RecordDetail;
import com.ub.techexcel.bean.SectionVO;
import com.ub.techexcel.bean.SoundtrackBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceInterfaceTools {

    public static final int GETSOUNDITEM = 0x1101;
    public static final int GETSOUNDLIST = 0x1102;
    public static final int DELETESOUNDLIST = 0x1103;
    public static final int ADDSOUNDTOLESSON = 0x1104;
    public static final int ERRORMESSAGE = 0x1105;
    public static final int SHARESOUNDTOLESSON = 0x1106;
    public static final int CREATESOUNDTOLESSON = 0x1107;
    public static final int GETPAGEACTIONS = 0x1108;
    public static final int GETPAGEACTIONSTARTTIME = 0x1109;
    public static final int GETVIEWALLDOCUMENTS = 0x1110;
    public static final int QUERYCONVERTING = 0x0001;
    public static final int STARTCONVERTING = 0x1112;
    public static final int UPLOADNEWFILE = 0x1113;
    public static final int PREPAREDOWNLOADING = 0x1114;
    public static final int FINISHCONVERING = 0x1115;
    public static final int QUERYDOWNLOADING = 0x1116;
    public static final int UPLOADSPACENEWFILE = 0x1117;
    public static final int YINXIANGUPLOADNEWFILE = 0x1118;
    public static final int UPLOADFAVORITENEWFILE = 0x1119;
    public static final int LESSONSOUNDTRACK = 0x1120;
    public static final int GETONSTAGEMEMBERCOUNT = 0x1121;

    public static final int QUERYDOCUMENT = 0x1122;
    public static final int NOTIFYUPLOADED = 0x1123;
    public static final int GETSOUNDTRACKACTIONS = 0x1124;
    public static final int ENDSYNC = 0x1125;
    public static final int GETLESSONITEM = 0x1126;
    public static final int CREATEORUPDATEUSERSETTING = 0x1127;
    public static final int USERSETTINGS = 0x1128;
    public static final int CHANGEBINDTVSTATUS = 0x1129;
    public static final int ADDTEMPLESSONWITHORIGINALDOCUMENT = 0x1130;
    public static final int CREATEMEETINGFROMSYNCROOM = 0x1131;
    public static final int STARTRECORDING = 0x1132;
    public static final int ENDRECORDING = 0x1133;
    public static final int STARTAGORARECORDING = 0x1134;
    public static final int ENDAAGORARRECORDING = 0x1135;
    public static final int GETRECORDINGLIST = 0x1136;
    public static final int GETRECORDINGITEM = 0x1137;
    public static final int GETBINDTVS = 0x1138;
    public static final int GETNOTELIST = 0x1139;
    public static final int GETNOTEBYLOCALFILEID = 0x1140;
    public static final int GETNOTEBYNOTEID = 0x1141;
    public static final int GETSYNCROOMUSERLIST = 0x1142;
    public static final int IMPORTNOTE = 0x1143;
    public static final int GETNOTELISTV2 = 0x1144;
    public static final int GETNOTELISTV3 = 0x1145;
    public static final int REMOVENOTE = 0x1146;
    public static final int GETNOTEBYLINKID = 0x1147;
    public static final int GETLESSIONID = 0x1148;


    private ConcurrentHashMap<Integer, ServiceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static ServiceInterfaceTools serviceInterfaceTools;

    private ServiceInterfaceTools() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AppConfig.URL_PUBLIC)
                .build();
        request = retrofit.create(TempleteCourse_interface.class);
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code == ERRORMESSAGE) {
            } else {
                ServiceInterfaceListener serviceInterfaceListener = hashMap.get(code);
                if (serviceInterfaceListener != null) {
                    serviceInterfaceListener.getServiceReturnData(msg.obj);
                    hashMap.remove(code);
                }
            }
        }
    };


    public static ServiceInterfaceTools getinstance() {
        if (serviceInterfaceTools == null) {
            synchronized (ServiceInterfaceTools.class) {
                if (serviceInterfaceTools == null) {
                    serviceInterfaceTools = new ServiceInterfaceTools();
                }
            }
        }
        return serviceInterfaceTools;
    }

    public void getSoundtrackActions(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject1 = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("hhh", url + "    " + jsonObject1.toString());
                try {
                    if (jsonObject1.getInt("RetCode") == 0) {
                        JSONObject retdata = jsonObject1.getJSONObject("RetData");
                        JSONArray jsonArray = retdata.getJSONArray("SoundtackActions");
                        List<AudioActionBean> audioActionBeanList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject audiojson = jsonArray.getJSONObject(i);
                            AudioActionBean audioActionBean = new AudioActionBean();
                            audioActionBean.setTime(audiojson.getInt("Time"));
                            String action = audiojson.getString("Data").replaceAll("\"", "");
                            String msg2 = Tools.getFromBase64(action);
                            audioActionBean.setData(msg2);
                            audioActionBeanList.add(audioActionBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.obj = audioActionBeanList;
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = jsonObject1.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getRecordActions(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject1 = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("getRecordActions", url + "    " + jsonObject1.toString());
                try {
                    if (jsonObject1.getInt("RetCode") == 0) {
                        JSONObject retdata = jsonObject1.getJSONObject("RetData");
                        JSONArray jsonArray = retdata.getJSONArray("SoundtackActions");
                        List<RecordAction> recordActions = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject audiojson = jsonArray.getJSONObject(i);
                            RecordAction recordAction = new RecordAction();
                            recordAction.setTime(audiojson.getInt("Time"));
                            String data = audiojson.getString("Data").replaceAll("\"", "");
                            recordAction.setData(Tools.getFromBase64(data));
                            Log.e("data__", recordAction.getData());
                            recordAction.setSoundtrackID(audiojson.getInt("SoundtrackID"));
                            recordAction.setPageNumber(audiojson.getString("PageNumber"));
                            recordAction.setAttachmentID(audiojson.getInt("AttachmentID"));
                            recordActions.add(recordAction);
                        }
                        Message msg3 = Message.obtain();
                        msg3.obj = recordActions;
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = jsonObject1.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void createOrUpdateUserSetting(final String url, final int code, final String jsonarray, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject1 = ConnectService.submitDataByJson4(url, jsonarray);
                Log.e("userSettingChan", url + "  " + jsonarray.toString() + "   " + jsonObject1.toString());
                try {
                    if (jsonObject1.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.obj = "";
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = jsonObject1.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void userSettings(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject1 = ConnectService.getIncidentbyHttpGet(url);
                Log.e("userSettings", url + "   " + jsonObject1.toString());
                try {
                    if (jsonObject1.getInt("RetCode") == 0) {
                        JSONArray jsonArray = jsonObject1.getJSONArray("RetData");
                        Message msg3 = Message.obtain();
                        msg3.obj = jsonArray;
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = jsonObject1.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getSoundItem(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("hhh", url + "  获取音响item    " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject retdata = returnjson.getJSONObject("RetData");
                        SoundtrackBean soundtrackBean1 = new SoundtrackBean();
                        soundtrackBean1.setSoundtrackID(retdata.getInt("SoundtrackID"));
                        soundtrackBean1.setTitle(retdata.getString("Title"));
                        soundtrackBean1.setAttachmentId(retdata.getString("AttachmentID"));
                        soundtrackBean1.setCreatedDate(retdata.getString("CreatedDate"));
                        soundtrackBean1.setBackgroudMusicAttachmentID(retdata.getInt("BackgroudMusicAttachmentID"));
                        soundtrackBean1.setNewAudioAttachmentID(retdata.getInt("NewAudioAttachmentID"));
                        soundtrackBean1.setSelectedAudioAttachmentID(retdata.getInt("SelectedAudioAttachmentID"));
                        soundtrackBean1.setIsPublic(retdata.getInt("IsPublic"));

                        if (soundtrackBean1.getBackgroudMusicAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject = retdata.getJSONObject("BackgroudMusicInfo");
                                Document favoriteAudio = new Document();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID") + "");
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID") + "");
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setBackgroudMusicInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setBackgroudMusicInfo(new Document());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean1.getNewAudioAttachmentID() != 0) {
                            try {
                                if (!retdata.isNull("NewAudioInfo")) {

                                }
                                JSONObject jsonObject = retdata.getJSONObject("NewAudioInfo");
                                Document favoriteAudio = new Document();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID") + "");
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID") + "");
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setNewAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setNewAudioInfo(new Document());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean1.getSelectedAudioAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject = retdata.getJSONObject("SelectedAudioInfo");
                                Document favoriteAudio = new Document();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID") + "");
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID") + "");
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setSelectedAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setSelectedAudioInfo(new Document());
                                e.printStackTrace();
                            }
                        }
                        soundtrackBean1.setBackgroudMusicTitle(retdata.getString("BackgroudMusicTitle"));
                        soundtrackBean1.setSelectedAudioTitle(retdata.getString("SelectedAudioTitle"));
                        soundtrackBean1.setNewAudioTitle(retdata.getString("NewAudioTitle"));
                        soundtrackBean1.setDuration(retdata.getString("Duration"));
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = soundtrackBean1;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void getSoundList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener, final boolean isHidden, final boolean ishavepresenter) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        final JSONArray array = returnjson.getJSONArray("RetData");
                        List<SoundtrackBean> mlist = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            SoundtrackBean soundtrackBean = new SoundtrackBean();
                            soundtrackBean.setSoundtrackID(jsonObject.getInt("SoundtrackID"));
                            soundtrackBean.setTitle(jsonObject.getString("Title"));
                            soundtrackBean.setUserID(jsonObject.getString("UserID"));
                            soundtrackBean.setUserName(jsonObject.getString("UserName"));
                            soundtrackBean.setAvatarUrl(jsonObject.getString("AvatarUrl"));
                            soundtrackBean.setDuration(jsonObject.getString("Duration"));
                            soundtrackBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            soundtrackBean.setHidden(isHidden);
                            soundtrackBean.setHavePresenter(ishavepresenter);
                            mlist.add(soundtrackBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.obj = mlist;
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void deleteSound(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentDataattachment(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void addSoundToLesson(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void shareDocument(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getString("RetData");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void lessonSoundtrack(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        final JSONArray array = returnjson.getJSONArray("RetData");
                        List<SoundtrackBean> mlist = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            SoundtrackBean soundtrackBean = new SoundtrackBean();
                            soundtrackBean.setSoundtrackID(jsonObject.getInt("SoundtrackID"));
                            soundtrackBean.setTitle(jsonObject.getString("Title"));
                            soundtrackBean.setUserID(jsonObject.getString("UserID"));
                            soundtrackBean.setUserName(jsonObject.getString("UserName"));
                            soundtrackBean.setAvatarUrl(jsonObject.getString("AvatarUrl"));
                            soundtrackBean.setDuration(jsonObject.getString("Duration"));
                            soundtrackBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            mlist.add(soundtrackBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = mlist;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    public void getPageActions(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        PageActionBean pageActionBean = new PageActionBean();
                        JSONObject retdata = returnjson.getJSONObject("RetData");
                        pageActionBean.setPageNumber(retdata.getString("PageNumber"));
                        JSONArray jsonArray = retdata.getJSONArray("Actions");
                        String mmm = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String ddd = jsonObject.getString("Data");
                            if (!TextUtil.isEmpty(ddd)) {
                                String dd = "'" + Tools.getFromBase64(ddd) + "'";
                                if (i == 0) {
                                    mmm += "[" + dd;
                                } else {
                                    mmm += "," + dd;
                                }
                                if (i == jsonArray.length() - 1) {
                                    mmm += "]";
                                }
                            }
                        }
                        pageActionBean.setActions(mmm);

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = pageActionBean;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());


    }

    public void getPageActionStartTime(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getInt("RetData");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    public void getLessonItem(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getInt("RetData");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    private Retrofit retrofit;
    private TempleteCourse_interface request;

    public void getViewAllDocuments(final int topicId, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        Call<ResponseBody> call;
        call = request.getAllDocument(AppConfig.UserToken, topicId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responsedate = response.body().string();
                        Log.e("hhhre", responsedate);
                        JSONObject returnjson = new JSONObject(responsedate);
                        if (returnjson.getInt("RetCode") == 0) {
                            List<LineItem> items = new ArrayList<>();
                            JSONArray lineitems = returnjson.getJSONArray("RetData");
                            for (int j = 0; j < lineitems.length(); j++) {
                                JSONObject lineitem = lineitems.getJSONObject(j);
                                LineItem item = new LineItem();
                                item.setCreatedBy(lineitem.getString("CreatedBy"));
                                item.setCreatedByAvatar(lineitem.getString("CreatedByAvatar"));
                                item.setTopicId(lineitem.getInt("TopicID"));
                                item.setSyncRoomCount(lineitem.getInt("SyncCount"));
                                item.setFileName(lineitem.getString("Title"));
                                item.setUrl(lineitem.getString("AttachmentUrl"));
                                item.setHtml5(false);
                                item.setItemId(lineitem.getString("ItemID"));
                                item.setAttachmentID(lineitem.getString("AttachmentID"));
                                item.setCreatedDate(lineitem.getString("CreatedDate"));
                                item.setFlag(0);
                                if (lineitem.getInt("Status") == 0) {
                                    items.add(item);
                                }
                            }
                            Message msg3 = Message.obtain();
                            msg3.what = code;
                            msg3.obj = items;
                            handler.sendMessage(msg3);
                        } else {
                            Message msg3 = Message.obtain();
                            msg3.what = ERRORMESSAGE;
                            msg3.obj = returnjson.getString("ErrorMessage");
                            handler.sendMessage(msg3);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });

    }

    public void getOnstageMemberCount(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("code") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getInt("data");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    public void addTempLessonWithOriginalDocument(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        int itemId = 0;
                        JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
                        String lessonid = jsonObject1.getString("LessonID");
                        if (jsonObject1.has("ItemID")) {
                            itemId = jsonObject1.getInt("ItemID");
                        }
                        msg3.what = code;
                        msg3.obj = lessonid + "-" + itemId;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    public void createMeetingFromSyncRoom(final String url, final int code, final JSONObject jsonObject, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", url + "  " + jsonObject.toString() + "   " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        String lessonid = returnjson.getString("RetData");
                        msg3.what = code;
                        msg3.obj = lessonid;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    private void putInterface(int code, ServiceInterfaceListener serviceInterfaceListener) {
        ServiceInterfaceListener serviceInterfaceListener2 = hashMap.get(code);
        if (serviceInterfaceListener2 == null) {
            hashMap.remove(code);
            hashMap.put(code, serviceInterfaceListener);
        }
    }


    //创建音响
    public void createYinxiang(final String url, final int code, final String attachmentId, final String recordingId, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    if (TextUtils.isEmpty(attachmentId)) {
                        jsonObject.put("AttachmentID", 0);
                    } else {
                        jsonObject.put("AttachmentID", Integer.parseInt(attachmentId));
                    }
                    String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
                    jsonObject.put("Title", AppConfig.UserName + "_" + time);
                    jsonObject.put("EnableBackgroud", 1);
                    jsonObject.put("EnableSelectVoice", 1);
                    jsonObject.put("EnableRecordNewVoice", 1);
                    jsonObject.put("Type", 1);
                    jsonObject.put("RecordingID", recordingId);

                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);

                    Log.e("Agora", jsonObject.toString() + "      " + returnjson.toString());

                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
                        SoundtrackBean soundtrackBean = new SoundtrackBean();
                        soundtrackBean.setSoundtrackID(jsonObject1.getInt("SoundtrackID"));
                        soundtrackBean.setTitle(jsonObject1.getString("Title"));
                        soundtrackBean.setUserID(jsonObject1.getString("UserID"));
                        soundtrackBean.setUserName(jsonObject1.getString("UserName"));
                        soundtrackBean.setAvatarUrl(jsonObject1.getString("AvatarUrl"));
                        soundtrackBean.setDuration(jsonObject1.getString("Duration"));
                        soundtrackBean.setCreatedDate(jsonObject1.getString("CreatedDate"));

                        soundtrackBean.setBackgroudMusicAttachmentID(jsonObject1.getInt("BackgroudMusicAttachmentID"));
                        soundtrackBean.setNewAudioAttachmentID(jsonObject1.getInt("NewAudioAttachmentID"));
                        soundtrackBean.setSelectedAudioAttachmentID(jsonObject1.getInt("SelectedAudioAttachmentID"));

                        JSONObject pathinfo = jsonObject1.getJSONObject("PathInfo");
                        soundtrackBean.setFileId(pathinfo.getInt("FileID"));
                        soundtrackBean.setPath(pathinfo.getString("Path"));

                        if (soundtrackBean.getBackgroudMusicAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("BackgroudMusicInfo");
                                Document favoriteAudio = new Document();
                                favoriteAudio.setFileDownloadURL(jsonObject2.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject2.getInt("ItemID") + "");
                                favoriteAudio.setTitle(jsonObject2.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject2.getInt("AttachmentID") + "");
                                favoriteAudio.setDuration(jsonObject2.getString("VideoDuration"));
                                soundtrackBean.setBackgroudMusicInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean.setBackgroudMusicInfo(new Document());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject3 = jsonObject1.getJSONObject("SelectedAudioInfo");
                                Document favoriteAudio = new Document();
                                favoriteAudio.setFileDownloadURL(jsonObject3.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject3.getInt("ItemID") + "");
                                favoriteAudio.setTitle(jsonObject3.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject3.getInt("AttachmentID") + "");
                                favoriteAudio.setDuration(jsonObject3.getString("VideoDuration"));
                                soundtrackBean.setSelectedAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean.setSelectedAudioInfo(new Document());
                                e.printStackTrace();
                            }
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = soundtrackBean;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 查询转换进度
     *
     * @param url
     * @param code
     * @param uploadao
     */
    public void queryConverting(final String url, final int code, final Uploadao uploadao, final String key, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Key", key);
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhh", "   " + jsonObject.toString() + "      " + returnjson.toString());
                    if (returnjson.getBoolean("Success")) {
                        JSONObject data = returnjson.getJSONObject("Data");
                        ConvertingResult convertingResult = new ConvertingResult();
                        convertingResult.setCurrentStatus(data.getInt("CurrentStatus"));
                        convertingResult.setFinishPercent(data.getInt("FinishPercent"));
                        if (convertingResult.getCurrentStatus() == 5) {
                            JSONObject result = data.getJSONObject("Result");
                            convertingResult.setUrl(result.getString("Url"));
                            convertingResult.setFolderKey(result.getString("FolderKey"));
                            convertingResult.setCount(result.getInt("Count"));
                            convertingResult.setFileName(result.getString("FileName"));
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = convertingResult;
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    /**
     * @param url
     * @param code
     * @param uploadao
     */
    public void startConverting(final String url, final int code, final Uploadao uploadao, final String key, final String Title, final String targetFolderKey, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Key", key);
                    jsonObject.put("DocumentType", Title.substring(Title.lastIndexOf(".") + 1, Title.length()));
                    jsonObject.put("TargetFolderKey", targetFolderKey);
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString() + "  " + returnjson.getBoolean("Success"));
                    if (returnjson.getBoolean("Success")) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getBoolean("Success");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * @param url
     * @param code
     * @param uploadao
     */
    public void finishConvering(final String url, final int code, final Uploadao uploadao, final String key, final String targetFolderKey, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SourceKey", key);
                    jsonObject.put("TargetFolderKey", targetFolderKey);
                    jsonObject.put("Callback", "");
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhh", url + "    " + jsonObject.toString() + "      " + returnjson.toString() + "  ");
                    if (returnjson.getBoolean("Success")) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getBoolean("Success");
                        handler.sendMessage(msg3);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    /**
     * @param url
     * @param code
     */
    public void yinxiangUploadNewFile(final String url, final int code, final String lessonId,
                                      final String docItemId, final String fileName, final int fieldId, final int soundtrackID, final Uploadao uploadao, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("LessonID", lessonId);
                    jsonObject.put("DocItemID", docItemId);
                    jsonObject.put("Duration", "");
                    jsonObject.put("FileName", fileName);
                    jsonObject.put("FileID", fieldId);
                    jsonObject.put("SoundtrackID", soundtrackID);
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);

                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", url + "      " + jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    /**
     * @param url
     * @param code
     * @param uploadao
     */
    public void uploadNewFile(final String url, final int code, final String fileName, final Uploadao uploadao, final String lessonId,
                              final String key, final ConvertingResult convertingResult, final boolean isAddToFavorite, final int fieldId, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("LessonID", lessonId);
                    jsonObject.put("Title", fileName);
                    jsonObject.put("Hash", key);
                    jsonObject.put("IsAddToFavorite", isAddToFavorite ? 1 : 0);
                    jsonObject.put("SchoolID", AppConfig.SchoolID);
                    jsonObject.put("FileID", fieldId);
                    jsonObject.put("PageCount", convertingResult.getCount());
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    jsonObject.put("SourceKey", convertingResult.getFolderKey());
                    jsonObject.put("FileName", convertingResult.getFileName());
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.toString();
                        handler.sendMessage(msg3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void endSync(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh endSync", url + "      " + jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    /**
     * @param url
     * @param code
     * @param fileName
     * @param itemID
     * @param Description
     * @param key
     * @param convertingResult
     * @param fieldId
     * @param serviceInterfaceListener
     */
    public void uploadSpaceNewFile(final String url, final int code, final String fileName, final int itemID,
                                   final String Description,
                                   final String key, final ConvertingResult convertingResult, final int fieldId,
                                   ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SpaceID", itemID);
                    jsonObject.put("Title", fileName);
                    jsonObject.put("Description", Description);
                    jsonObject.put("Hash", key);
                    jsonObject.put("FileID", fieldId);
                    jsonObject.put("PageCount", convertingResult.getCount());
                    jsonObject.put("FileName", convertingResult.getFileName());
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    public void uploadFavoriteNewFile(final String url, final int code, final String fileName,
                                      final String Description,
                                      final String key, final ConvertingResult convertingResult, final int fieldId,
                                      ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Title", fileName);
                    jsonObject.put("Description", Description);
                    jsonObject.put("Hash", key);
                    jsonObject.put("FileID", fieldId);
                    jsonObject.put("PageCount", convertingResult.getCount());
                    jsonObject.put("FileName", convertingResult.getFileName());
                    jsonObject.put("SchoolID", -1);
//                    jsonObject.put("SchoolID", AppConfig.SchoolID);
//                    jsonObject.put("FolderID", convertingResult.getFileName());
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", url + jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void queryDownloading(final String url, final int code, final Uploadao uploadao, final String newPath, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            JSONObject keyJson = new JSONObject();
            keyJson.put("Option", 1);
            keyJson.put("Key", newPath);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(keyJson);
            jsonObject.put("Keys", jsonArray);
            JSONObject bucketJson = new JSONObject();
            bucketJson.put("ServiceProviderId", uploadao.getServiceProviderId());
            bucketJson.put("RegionName", uploadao.getRegionName());
            bucketJson.put("BucketName", uploadao.getBucketName());
            jsonObject.put("Bucket", bucketJson);
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhhhh", url + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void queryDocument(final String url, final int code, final String newPath, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            JSONObject keyJson = new JSONObject();
            keyJson.put("Option", 1);
            keyJson.put("Key", newPath);
            jsonObject.put("Key", keyJson);
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhhhh", url + "     " + jsonObject.toString() + "      " + returnjson.toString());
                    try {
                        if (returnjson.getBoolean("Success")) {
                            Message msg3 = Message.obtain();
                            msg3.what = code;
                            msg3.obj = returnjson.toString();
                            handler.sendMessage(msg3);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyUploaded(final String url, final int code, Uploadao uploadao, String key, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            JSONObject js = new JSONObject();
            js.put("Key", key);

            JSONObject bucketJson = new JSONObject();
            bucketJson.put("ServiceProviderId", uploadao.getServiceProviderId());
            bucketJson.put("RegionName", uploadao.getRegionName());
            bucketJson.put("BucketName", uploadao.getBucketName());

            js.put("Bucket", bucketJson);

            jsonArray.put(js);
            jsonObject.put("Documents", jsonArray);

            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhhhh", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void changeBindTvStatus(final String url, final int code, boolean status, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", status ? 1 : 0);
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("Tvdeviceslist", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBindTvs(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("Tvdeviceslist2", url + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始录制Meeting
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void startRecording(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("Recording start", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束录制Meeting
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void endRecording(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("Recording end", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制音频视频
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void startAgoraRecording(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("Recording  agora start", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束录制音频视频
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void endAgoraRecording(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("Recording agora end", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据lessonid  获取 录课的列表
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getRecordingList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("Recording_list", url + "  " + returnjson.toString());
                    if (returnjson.getInt("code") == 0) {
                        JSONArray data = returnjson.getJSONArray("data");
                        List<Record> list = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject = data.getJSONObject(i);
                            Record record = new Record();
                            record.setRecordingId(jsonObject.getInt("recordingId"));
                            record.setTitle(jsonObject.getString("title"));
                            record.setCreateDate(jsonObject.getLong("createDate"));
                            record.setDuration(jsonObject.getLong("duration"));
                            list.add(record);
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = list;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 根据lessonid  获取 笔记的列表
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<Note> items = new ArrayList<Note>();
                        for (int j = 0; j < lineitems.length(); j++) {

                            JSONObject lineitem = lineitems.getJSONObject(j);
                            Note item = new Note();
                            item.setLinkID(lineitem.getInt("LinkID"));
                            item.setNoteID(lineitem.getInt("NoteID"));
                            item.setLocalFileID(lineitem.getString("LocalFileID"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setFileID(lineitem.getInt("FileID"));
                            item.setFileName(lineitem.getString("FileName"));
                            item.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setAttachmentID(lineitem.getInt("AttachmentID"));
                            items.add(item);

                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    public List<NoteDetail> syncGetUserNotes(final String url) {
        List<NoteDetail> notes = new ArrayList<NoteDetail>();
            try {
                JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                Log.e("syncGetUserNotes", url + "  " + returnjson.toString());
                if (returnjson.getInt("RetCode") == 0) {
                    JSONArray _notes = returnjson.getJSONArray("RetData");

                    for (int j = 0; j < _notes.length(); j++) {
                        JSONObject note = _notes.getJSONObject(j);
                        NoteDetail _note = new Gson().fromJson(note.toString(), NoteDetail.class);
                        String noteUrl = _note.getAttachmentUrl().substring(0,_note.getAttachmentUrl().lastIndexOf("<")) + 1 + _note.getAttachmentUrl().substring(_note.getAttachmentUrl().lastIndexOf("."));
                        _note.setUrl(noteUrl);
                        notes.add(_note);

                    }
                }
            } catch (Exception e) {
                Log.e("getNoteListV2", "Exception:" + e);
                e.printStackTrace();
            }
            return notes;
    }



    public void getNoteListV2(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("TwinkleBookNote", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<NoteDetail> items = new ArrayList<NoteDetail>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            items.add(new Gson().fromJson(lineitem.toString(), NoteDetail.class));
                        }
                        Log.e("getNoteListV2", "items:" + items.size());
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    Log.e("getNoteListV2", "Exception:" + e);
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    public void getNoteListV3(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("TwinkleBookNote", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<NoteDetail> items = new ArrayList<NoteDetail>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            items.add(new Gson().fromJson(lineitem.toString(), NoteDetail.class));
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 根据localFileID获取Note
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteByLocalFileId(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList note", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject lineitem = returnjson.getJSONObject("RetData");
                        Note note = new Note();
                        note.setLocalFileID(lineitem.getString("LocalFileID"));
                        note.setPageNumber(lineitem.getInt("PageNumber"));
                        note.setDocumentItemID(lineitem.getInt("DocumentItemID"));
                        note.setFileName(lineitem.getString("Title"));
                        note.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                        note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                        note.setAttachmentID(lineitem.getInt("AttachmentID"));
                        Message msg = Message.obtain();
                        msg.obj = note;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    /**
     * 根据NoteId获取Note
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteByNoteId(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList note ", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject lineitem = returnjson.getJSONObject("RetData");
                        Note note = new Note();
                        note.setLocalFileID(lineitem.getString("LocalFileID"));
                        note.setFileName(lineitem.getString("Title"));
                        note.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                        note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                        note.setAttachmentID(lineitem.getInt("AttachmentID"));
                        note.setNoteID(lineitem.getInt("NoteID"));
                        Message msg = Message.obtain();
                        msg.obj = note;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 获取SyncRoom中的用户的Note数量
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getSyncRoomUserList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList note count ", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray userArray = returnjson.getJSONArray("RetData");
                        List<Customer> list = new ArrayList<>();
                        for (int i = 0; i < userArray.length(); i++) {
                            JSONObject userJson = userArray.getJSONObject(i);
                            Customer customer = new Customer();
                            customer.setUserID(userJson.getInt("UserID") + "");
                            customer.setName(userJson.getString("UserName"));
                            customer.setNoteCount(userJson.getInt("NoteCount"));
                            list.add(customer);
                        }
                        Message msg = Message.obtain();
                        msg.obj = list;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public List<Customer> syncGetDocUsers(final String url) {

        List<Customer> list = new ArrayList<>();
        try {
            JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
            Log.e("syncGetDocUsers ", url + "  " + returnjson.toString());
            if (returnjson.getInt("RetCode") == 0) {
                JSONArray userArray = returnjson.getJSONArray("RetData");
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject userJson = userArray.getJSONObject(i);
                    Customer customer = new Customer();
                    customer.setUserID(userJson.getInt("UserID") + "");
                    customer.setName(userJson.getString("UserName"));
                    customer.setNoteCount(userJson.getInt("NoteCount"));
                    list.add(customer);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }


    /**
     * 获取用户的Note列表
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getUserNoteList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<Note> items = new ArrayList<Note>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            Note item = new Note();
                            item.setNoteID(lineitem.getInt("NoteID"));
                            item.setLocalFileID(lineitem.getString("LocalFileID"));
                            item.setTitle(lineitem.getString("Title"));
                            item.setFileID(lineitem.getInt("FileID"));
                            item.setFileName(lineitem.getString("FileName"));
                            item.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setAttachmentID(lineitem.getInt("AttachmentID"));
                            item.setCreatedDate(lineitem.getString("CreatedDate"));
                            items.add(item);
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 导入 Note
     */
    public void importNote(final String url, final int code, String syncroomid,
                           String documentItemID, String pagenumber, int noteid, String linkproperty,
                           ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("SyncRoomID", syncroomid);
            jsonObject.put("DocumentItemID", documentItemID);
            jsonObject.put("PageNumber", pagenumber);
            jsonObject.put("NoteID", noteid);
            jsonObject.put("LinkProperty", linkproperty);
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("importNote", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    try {
                        int retCode = returnjson.getInt("RetCode");
                        if (retCode == 0) {
                            int linkid = returnjson.getInt("RetData");
                            Message msg3 = Message.obtain();
                            msg3.what = code;
                            msg3.obj = linkid;
                            handler.sendMessage(msg3);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据LinkID获取Note信息
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteByLinkID(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteByLinkID", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject lineitem = returnjson.getJSONObject("RetData");
                        Note note = new Note();
                        note.setLocalFileID(lineitem.getString("LocalFileID"));
                        note.setNoteID(lineitem.getInt("NoteID"));
                        note.setLinkID(lineitem.getInt("LinkID"));
                        note.setPageNumber(lineitem.getInt("PageNumber"));
                        note.setDocumentItemID(lineitem.getInt("DocumentItemID"));
                        note.setFileName(lineitem.getString("Title"));
                        note.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                        note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                        note.setAttachmentID(lineitem.getInt("AttachmentID"));
                        Message msg = Message.obtain();
                        msg.obj = note;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 根据LinkID删除Note信息
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void removeNote(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentDataattachment(url);
                    Log.e("removeNote", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg = Message.obtain();
                        msg.obj = 0;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 根据lessonid  获取 录课的信息
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getRecordingItem(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("Recording item", url + "  " + returnjson.toString());
                    if (returnjson.getInt("code") == 0) {
                        JSONObject data = returnjson.getJSONObject("data");
                        RecordDetail record = new RecordDetail();
                        record.setRecordingId(data.getInt("recordingId"));
                        record.setTitle(data.getString("title"));
                        record.setCreateDate(data.getLong("createDate"));
                        record.setDuration(data.getLong("duration"));
                        JSONArray channelVOListjs = data.getJSONArray("channelVOList");
                        List<ChannelVO> channelVOList = new ArrayList<>();
                        for (int i = 0; i < channelVOListjs.length(); i++) {
                            ChannelVO channelVO = new ChannelVO();
                            JSONObject channelVOJS = channelVOListjs.getJSONObject(i);
                            channelVO.setChannelId(channelVOJS.getInt("channelId"));
                            channelVO.setType(channelVOJS.getInt("type"));
                            channelVO.setUserId(channelVOJS.getInt("userId"));
                            JSONArray sectionVOListjs = channelVOJS.getJSONArray("sectionVOList");
                            List<SectionVO> sectionVOList = new ArrayList<>();
                            for (int j = 0; j < sectionVOListjs.length(); j++) {
                                SectionVO sectionVO = new SectionVO();
                                JSONObject sectionVOjs = sectionVOListjs.getJSONObject(j);
                                sectionVO.setId(sectionVOjs.getInt("id"));
                                sectionVO.setType(sectionVOjs.getInt("type"));
                                sectionVO.setSectionId(sectionVOjs.getInt("sectionId"));
                                sectionVO.setUserId(sectionVOjs.getInt("userId"));
                                sectionVO.setStartTime(sectionVOjs.getInt("startTime"));
                                sectionVO.setEndTime(sectionVOjs.getInt("endTime"));
                                sectionVO.setFileName(sectionVOjs.getString("fileName"));
                                sectionVO.setStatus(sectionVOjs.getInt("status"));
                                sectionVO.setCreateDate(sectionVOjs.getLong("createDate"));
                                sectionVO.setFileUrl(sectionVOjs.getString("fileUrl"));
                                sectionVO.setSid(sectionVOjs.getString("sid"));
                                sectionVOList.add(sectionVO);
                            }
                            channelVO.setSectionVOList(sectionVOList);
                            channelVOList.add(channelVO);
                        }
                        record.setChannelVOList(channelVOList);
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = record;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    public Call<TeamAndSpaceSearchResponse> searchSpacesAndDocs(int companyID, int teamID, String keyword) {
        return request.searchSpacesAndDocs(AppConfig.UserToken, companyID, teamID, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<TeamSearchResponse> searchTeams(String keyword) throws UnknownHostException {
        return request.searchTeams(AppConfig.UserToken, AppConfig.SchoolID, 1, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<NetworkResponse<List<TeamSpaceBean>>> searchSapces(String teamId, String keyword) throws UnknownHostException {
        return request.searchSpaces(AppConfig.UserToken, AppConfig.SchoolID, 2, teamId, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<InvitationsResponse> getInvitations() {
        return request.getInvitations(AppConfig.UserToken);
    }

    public Call<InviteResponse> inviteNewToCompany(String mobile, int type, int inviteTo, int requestAddFriend) {
        InviteToCompanyParams params = new InviteToCompanyParams();
        params.setCompanyID(AppConfig.SchoolID);
        params.setMobile(mobile);
        params.setInviteTo(inviteTo);
//        params.setInviteTo(AppConfig.SchoolID);
//        params.setInviteToType(0);
        params.setInviteToType(type);
        params.setRequestAddFriend(requestAddFriend);
        Log.e("duang123", params.toString() + "   :");
        return request.inviteNewToCompany(AppConfig.UserToken, params);
    }

    public Call<NetworkResponse> acceptInvitations(String[] companyIds) {
        AcceptInvitationsParams params = new AcceptInvitationsParams();
        int[] ids = new int[companyIds.length];
        for (int i = 0; i < companyIds.length; ++i) {
            ids[i] = Integer.parseInt(companyIds[i]);
        }
        params.setCompanyList(ids);
        return request.acceptInvitations(AppConfig.UserToken, params);
    }

    public Call<FriendResponse> friendRequest(int companyId) {
        return request.friendRequest(AppConfig.UserToken, companyId);
    }

    public Call<NetworkResponse> acceptFriendsRequest(String[] rongIds) {
        AcceptFriendsRequestParams params = new AcceptFriendsRequestParams();
        params.setRongCloudIDList(rongIds);
        return request.acceptFriendsRequest(AppConfig.UserToken, params);
    }

    public Call<OrganizationsResponse> searchOrganizations(String keyword) throws UnknownHostException {
        return request.searchOrganizations(AppConfig.UserToken, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<UserInCompanyResponse> getUserInfoInCompany(String schoolID, String userID) throws UnknownHostException {
        return request.getUserInfoInCompany(AppConfig.UserToken, schoolID, userID);
    }

    public Call<TeamsResponse> getCompanyTeams(String companyID) {
        return request.getCompanyTeams(AppConfig.UserToken, 1, companyID);
    }

    public Call<TeamsResponse> getAllTeams(String companyID) {
        return request.getAllTeams(AppConfig.UserToken, 1, companyID, 1);
    }

    public Call<TeamMembersResponse> getTeamMembers(String teamSpaceId) {
        return request.getTeamMembers(AppConfig.UserToken, teamSpaceId + "");
    }

    public Call<CompanyContactsResponse> searchCompanyContactInTeam(String teamId, String keyword) {
        return request.searchCompanyContactInTeam(AppConfig.UserToken, AppConfig.SchoolID + "", teamId, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<NetworkResponse> inviteCompanyMemberAsTeamAdmin(String teamId, List<CompanyContact> contacts) {
        InviteTeamAdminParams params = new InviteTeamAdminParams();
        params.setInviteTo(teamId);
        String[] ids = new String[contacts.size()];
        for (int i = 0; i < contacts.size(); ++i) {
            ids[i] = contacts.get(i).getUserID();
        }
        params.setUserIDList(ids);
        Log.e("duang111", params.toString() + "   aaaa");
        return request.inviteCompanyMemberAsTeamAdmin(AppConfig.UserToken, params);
    }

    public Call<TeamMembersResponse> getSpaceMembers(String teamSpaceId) {
        return request.getSpaceMembers(AppConfig.UserToken, teamSpaceId + "");
    }

    public Call<NetworkResponse<DocumentDetail>> searchDocumentsInSpace(String spaceId, String keyword) {
        return request.searchDocumentsInSpace(AppConfig.UserToken, 0, spaceId, 0, 100, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<NetworkResponse<FavoriteData>> searchFavoriteDocuments(String keyword) {
        return request.searchFavoriteDocuments(AppConfig.UserToken, 0, -1 + "", 0, 100, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<NetworkResponse<FavoriteDocumentResponse>> getFavoriteDocuments() {
        return request.getFavoriteDocuments(AppConfig.UserToken, 0);
    }


    public Call<NetworkResponse<DocumentData>> getAllDocumentList(String teamId) {
        return request.getAllDocumentList(AppConfig.UserToken, AppConfig.SchoolID + "", teamId, 0, 0, 1000);
    }

    public Call<NetworkResponse<LoginData>> login(String name,
                                                  String passsword) throws UnsupportedEncodingException, UnknownHostException {
        return request.login(name, getBase64Password(passsword), 1,
                AppConfig.DEVICE_ID, 2, getBase64Password(AppConfig.SystemModel));
    }

    public Call<NetworkResponse<RongCloudData>> getRongCloudInfo() throws UnknownHostException {
        return request.getRongCloudInfo(AppConfig.UserToken);
    }

    public Call<NetworkResponse<Integer>> getRongCloudOnlineStatus() {
        return request.getRongCloudOnlineStatus(AppConfig.UserToken);
    }

    public Call<NetworkResponse<List<MyFriend>>> getFriendList() {
        return request.getFriendList(AppConfig.UserToken);
    }

    public Call<NetworkResponse> inviteMultipleToCompany(String companyId, List<PhoneItem> items, int spaceId) {
        InviteMultipleParams params = new InviteMultipleParams();
        List<InviteInfo> infos = new ArrayList<>();
        for (PhoneItem phoneItem : items) {
            InviteInfo inviteInfo = new InviteInfo();
            inviteInfo.setMobile(phoneItem.getRegion() + phoneItem.getPhoneNumber());
            inviteInfo.setCompanyID(companyId);
            inviteInfo.setInviteTo(spaceId + "");
            if (phoneItem.getRole() == RoleInTeam.ROLE_MEMBER) {
                inviteInfo.setInviteToType(0);
            } else if (phoneItem.getRole() == RoleInTeam.ROLE_ADMIN)
                inviteInfo.setInviteToType(5);
            infos.add(inviteInfo);
        }
        params.setInviteInfos(infos);
        return request.inviteMultipleToCompany(AppConfig.UserToken, params);
    }

    public Call<NetworkResponse<DocumentDetail>> searchHelpDocuments(String keyword) {
        return request.searchHelpDocuments(AppConfig.UserToken, 280 + "", 0, Base64.encodeToString(keyword.trim().getBytes(), 0));
    }

    public Call<DevicesResponse> getBindTvs() {
        return request.getBindTvs(AppConfig.wssServer + "/tv/current_user_bind_tv_info", AppConfig.UserToken);
    }

    public Call<BindTvStatusResponse> changeBindTvStatus(int status) {
        return request.changeBindTvStatus(AppConfig.wssServer + "/tv/change_bind_tv_status", AppConfig.UserToken, status);
    }

    public Call<NetworkResponse<SyncBook>> getSyncbookOutline(String syncroomId) {
        return request.getSyncbookOutline(AppConfig.UserToken, syncroomId);
    }

    @SuppressLint("NewApi")
    public static String getBase64Password(String passsword) {
        if (TextUtils.isEmpty(passsword)) {
            return "";
        }
        String enToStr = Base64.encodeToString(passsword.getBytes(), Base64.DEFAULT);
        return enToStr;

    }

    public Call<ResponseBody> getBindTvs2() {
        return request.getBindTvs2(AppConfig.wssServer + "/tv/current_user_bind_tv_info", AppConfig.UserToken);
    }

    public Call<ResponseBody> getLessionIdByItemId(int itemId) {
        return request.getLessionIdByItemId(AppConfig.URL_PUBLIC + "Lesson/GetLessonIDByItemID?itemID=" + itemId, AppConfig.UserToken);
    }


    public void uploadLocalNoteFile(final String url, final int code, final String fileName,
                                    final String Description,
                                    final String key, final ConvertingResult convertingResult, final int fieldId, final String documentId,
                                    final String pageNumber, final String noteId, final String syncroomId, final String noteLinkProperty, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    int page = (int) (Float.parseFloat(pageNumber));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Title", fileName);
                    jsonObject.put("Description", Description);
                    jsonObject.put("Hash", key);
                    jsonObject.put("FileID", fieldId);
                    jsonObject.put("PageCount", convertingResult.getCount());
                    jsonObject.put("FileName", convertingResult.getFileName());
                    jsonObject.put("DocumentItemID", documentId);
                    jsonObject.put("PageNumber", page);
                    jsonObject.put("LocalFileId", noteId);
                    jsonObject.put("syncRoomID", syncroomId);
                    jsonObject.put("LinkProperty", noteLinkProperty);
//                    jsonObject.put("SchoolID", AppConfig.SchoolID);
//                    jsonObject.put("FolderID", convertingResult.getFileName());
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("addLocalNote", url + jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson;
                        handler.sendMessage(msg3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    public void getNoteDetailByLinkId(final String url, final String linkId, final OnJsonResponseReceiver jsonResponseReceiver) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("linkID", linkId);
                    JSONObject returnjson = ConnectService.getIncidentData(url + "?linkId=" + linkId);
                    Log.e("getNoteDetailByLinkId", url + jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        if (jsonResponseReceiver != null) {
                            jsonResponseReceiver.jsonResponse(returnjson);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    public interface OnJsonResponseReceiver {
        void jsonResponse(JSONObject jsonResponse);
    }

    public void getLessionId(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
        Log.e("getLessionId", url + "  " + returnjson.toString());
        try {
            if (returnjson.getInt("RetCode") == 0) {

                Message msg = Message.obtain();
                msg.obj = returnjson;
                msg.what = code;
                handler.sendMessage(msg);
            } else {
                Message msg3 = Message.obtain();
                msg3.what = ERRORMESSAGE;
                msg3.obj = returnjson.getString("ErrorMessage");
                handler.sendMessage(msg3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject syncGetFavoriteVedios(){
        String url  = AppConfig.URL_PUBLIC + "FavoriteAttachment/MyFavoriteAttachmentsNew?type=" + 2;
        JSONObject response = ConnectService.getIncidentbyHttpGet(url);
        return response;
    }

    public JSONObject syncGetMeetingMembers(String meetingId,int role) {

        String url = "";
        if(role == MeetingConfig.MeetingRole.MEMBER){
            url += AppConfig.URL_MEETING_BASE + "member/member_in_meeting_list?meetingId=" + meetingId;
        }else if(role == MeetingConfig.MeetingRole.AUDIENCE){
            url += AppConfig.URL_MEETING_BASE + "member/audience_list?meetingId=" + meetingId;
        }else if(role == MeetingConfig.MeetingRole.BE_INVITED){
            url += AppConfig.URL_MEETING_BASE + "member/invited_not_join_list?meetingId=" + meetingId;
        }

        JSONObject response = ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetMeetingMembers","url:" + url + ",result:" + response);
        return response;

    }


}
