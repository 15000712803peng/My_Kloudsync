package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.bean.AudioActionBean;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingServiceTools {

    public static final int ERRORMESSAGE = 0x1105;
    public static final int GETPDFLIST = 0x1101;
    public static final int GETGETPAGEOBJECTS = 0x1102;
    public static final int ENTERTEACHERONGOINGMEETING = 0x1103;
    public static final int UPLOADFILEWITHHASH = 0x1104;
    public static final int GETTOPICATTACHMENT = 0x1106;

    private ConcurrentHashMap<Integer, ServiceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static MeetingServiceTools meetingServiceTools;

    public static MeetingServiceTools getInstance() {
        if (meetingServiceTools == null) {
            synchronized (MeetingServiceTools.class) {
                if (meetingServiceTools == null) {
                    meetingServiceTools = new MeetingServiceTools();
                }
            }
        }
        return meetingServiceTools;
    }

    private void putInterface(int code, ServiceInterfaceListener serviceInterfaceListener) {
        ServiceInterfaceListener serviceInterfaceListener2 = hashMap.get(code);
        if (serviceInterfaceListener2 == null) {
            hashMap.put(code, serviceInterfaceListener);
        }

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


    public void getPdfList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONObject service = returnJson.getJSONObject("RetData");
                        JSONArray lineitems = service.getJSONArray("AttachmentList");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setNewPath(lineitem.getString("NewPath"));
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getTopicAttachment(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnJson.getJSONArray("RetData");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setTopicId(lineitem.getInt("TopicID"));
                            item.setSyncRoomCount(lineitem.getInt("SyncCount"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setCreatedDate(lineitem.getString("CreatedDate"));
                            String attachmentUrl = lineitem.getString("AttachmentUrl");
                            if (!TextUtils.isEmpty(attachmentUrl)) {
                                String newPath = attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/"));
                                item.setNewPath(newPath);
                            }
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getGetPageObjects(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + "   " + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONArray data = returnJson.getJSONArray("RetData");
                        String mmm = "";
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = data.getJSONObject(i);
                            String ddd = jsonObject1.getString("Data");
                            if (!TextUtil.isEmpty(ddd)) {
                                String dd = "'" + Tools.getFromBase64(ddd) + "'";
                                if (i == 0) {
                                    mmm += "[" + dd;
                                } else {
                                    mmm += "," + dd;
                                }
                                if (i == data.length() - 1) {
                                    mmm += "]";
                                }
                            }
                        }
                        Message msg = Message.obtain();
                        msg.what = code;
                        msg.obj = mmm;
                        handler.sendMessage(msg);

                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void enterTeacherOnGoingMeeting(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONObject service = returnJson.getJSONObject("RetData");
                        ServiceBean bean = new ServiceBean();
                        bean.setId(service.getInt("LessonID"));
                        String des = service.getString("Description");
                        bean.setDescription(des);
                        int statusID = service.getInt("StatusID");
                        bean.setStatusID(statusID);
                        bean.setRoleinlesson(service.getInt("RoleInLesson"));
                        if (bean.getRoleinlesson() == 3) {
                            bean.setRoleinlesson(1);
                        }
                        JSONArray memberlist = service.getJSONArray("MemberInfoList");
                        for (int i = 0; i < memberlist.length(); i++) {
                            JSONObject jsonObject = memberlist.getJSONObject(i);
                            int role = jsonObject.getInt("Role");
                            if (role == 2) { //teacher
                                bean.setTeacherName(jsonObject.getString("MemberName"));
                                bean.setTeacherId(jsonObject.getString("MemberID"));
                            } else if (role == 1) {
                                bean.setUserName(jsonObject.getString("MemberName"));
                                bean.setUserId(jsonObject.getString("MemberID"));
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = bean;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    public void uploadFileWithHash(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.submitDataByJson(url, null);
                Log.e("meetingservicrtools", url + returnJson.toString());
                Message msg = Message.obtain();
                msg.obj = returnJson;
                msg.what = code;
                handler.sendMessage(msg);

            }
        }).start(ThreadManager.getManager());

    }


}
