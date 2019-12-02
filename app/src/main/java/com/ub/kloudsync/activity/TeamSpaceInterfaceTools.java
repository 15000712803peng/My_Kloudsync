package com.ub.kloudsync.activity;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.bean.SyncRoomMember;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TeamSpaceInterfaceTools {

    public static final int ERRORMESSAGE = 0x1205;
    public static final int CREATETEAMSPACE = 0x1101;
    public static final int GETTEAMSPACELIST = 0x1102;
    public static final int GETTEAMITEM = 0x1103;
    public static final int GETSPACEDOCUMENTLIST = 0x1104;
    public static final int GETALLDOCUMENTLIST = 0x1105;
    public static final int UPLOADFROMSPACE = 0x1106;
    public static final int GETSYNCROOMLIST = 0x1107;
    public static final int TOPICLIST = 0x1108;
    public static final int CREATESYNCROOM = 0x1109;
    public static final int SWITCHSPACE = 0x1110;
    public static final int GETMEMBERLIST = 0x1111;
    public static final int UPDATETEAMTOPIC = 0x1112;
    public static final int GETAUDIENCELIST = 0x1113;
    public static final int GET_SPACE_LIST_IN_HOME_PAGE = 0x1120;

    private ConcurrentHashMap<Integer, TeamSpaceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static TeamSpaceInterfaceTools serviceInterfaceTools;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code != ERRORMESSAGE) {
                TeamSpaceInterfaceListener serviceInterfaceListener = hashMap.get(code);
                if (serviceInterfaceListener != null) {
                    serviceInterfaceListener.getServiceReturnData(msg.obj);
                    hashMap.remove(code);
                }
            } else {
                String message = "";
                try {
                    message = (String) msg.obj;
                } catch (Exception e) {
                    message = "";
                }

                EventBus.getDefault().post(message);

            }
        }
    };


    public static TeamSpaceInterfaceTools getinstance() {
        if (serviceInterfaceTools == null) {
            synchronized (TeamSpaceInterfaceTools.class) {
                if (serviceInterfaceTools == null) {
                    serviceInterfaceTools = new TeamSpaceInterfaceTools();
                }
            }
        }
        return serviceInterfaceTools;
    }


    /**
     * 创建Team或者Space
     */
    public void createTeamSpace(final String url, final int code, int companyID, int type, String name, int parentId, int teamType, TeamSpaceInterfaceListener teamSpaceInterfaceListener) {
        putInterface(code, teamSpaceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CompanyID", companyID);
            jsonObject.put("Type", type);
            jsonObject.put("Name", name);
            jsonObject.put("ParentID", parentId);
            if (teamType != 0) {
                jsonObject.put("TeamType", teamType);
            }
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                        Log.e("hhh", url + "  " + jsonObject.toString() + "     " + returnjson.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 切换Space
     */
    public void switchSpace(final String url, final int code, TeamSpaceInterfaceListener teamSpaceInterfaceListener) {
        putInterface(code, teamSpaceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();

            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                        Log.e("hhh", url + "  " + jsonObject.toString() + "     " + returnjson.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建Team或者Space
     */
    public void uploadFromSpace(final String url, final int code, TeamSpaceInterfaceListener teamSpaceInterfaceListener) {
        putInterface(code, teamSpaceInterfaceListener);
        try {
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                        Log.e("hhh", url + "  " + "     " + returnjson.toString());
                        if (returnjson.getInt("RetCode") == 0) {
                            Message msg3 = Message.obtain();
                            msg3.what = code;
                            msg3.obj = returnjson;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取 team    space 列表
     */
    public void getTeamSpaceList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("getTeamSpaceList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        List<TeamSpaceBean> list = new ArrayList<>();
                        JSONArray jsonArray = returnjson.getJSONArray("RetData");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
                            teamSpaceBean.setItemID(jsonObject.getInt("ItemID"));
                            teamSpaceBean.setName(jsonObject.getString("Name"));
                            teamSpaceBean.setCompanyID(jsonObject.getInt("CompanyID"));
                            teamSpaceBean.setType(jsonObject.getInt("Type"));
                            teamSpaceBean.setParentID(jsonObject.getInt("ParentID"));
                            teamSpaceBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            teamSpaceBean.setCreatedByName(jsonObject.getString("CreatedByName"));
                            teamSpaceBean.setAttachmentCount(jsonObject.getInt("AttachmentCount"));
                            teamSpaceBean.setMemberCount(jsonObject.getInt("MemberCount"));
                            teamSpaceBean.setSyncRoomCount(jsonObject.getInt("SyncRoomCount"));

                            list.add(teamSpaceBean);
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
     * 获取 topic 列表
     */
    public void getTopicList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        List<TeamSpaceBean> list = new ArrayList<>();
                        JSONArray jsonArray = returnjson.getJSONArray("RetData");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
                            teamSpaceBean.setItemID(jsonObject.getInt("ItemID"));
                            teamSpaceBean.setTopicType(jsonObject.getInt("TopicType"));
                            teamSpaceBean.setName(jsonObject.getString("Name"));
                            teamSpaceBean.setCompanyID(jsonObject.getInt("CompanyID"));
                            teamSpaceBean.setType(jsonObject.getInt("Type"));
                            teamSpaceBean.setParentID(jsonObject.getInt("ParentID"));
                            teamSpaceBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            teamSpaceBean.setCreatedByName(jsonObject.getString("CreatedByName"));
//                            teamSpaceBean.setAttachmentCount(jsonObject.getInt("AttachmentCount"));

                            list.add(teamSpaceBean);
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
     * detail
     */
    public void getTeamItem(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        TeamSpaceBean teamSpaceBean = new TeamSpaceBean();

                        JSONObject jsonObject = returnjson.getJSONObject("RetData");
                        teamSpaceBean.setItemID(jsonObject.getInt("ItemID"));
                        teamSpaceBean.setName(jsonObject.getString("Name"));
                        teamSpaceBean.setCompanyID(jsonObject.getInt("CompanyID"));
                        teamSpaceBean.setType(jsonObject.getInt("Type"));
                        teamSpaceBean.setParentID(jsonObject.getInt("ParentID"));
                        teamSpaceBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                        teamSpaceBean.setCreatedByName(jsonObject.getString("CreatedByName"));

                        JSONArray jsonArray = jsonObject.getJSONArray("MemberList");
                        List<TeamUser> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            TeamUser teamUser = new TeamUser();
                            teamUser.setMemberID(jsonObject2.getInt("MemberID"));
                            teamUser.setMemberName(jsonObject2.getString("MemberName"));
                            teamUser.setMemberAvatar(jsonObject2.getString("MemberAvatar"));
                            teamUser.setJoinDate(jsonObject2.getString("JoinDate"));
                            list.add(teamUser);
                        }
                        teamSpaceBean.setMemberList(list);

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = teamSpaceBean;
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


    public void getSpaceDocumentList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        JSONObject ss = returnjson.getJSONObject("RetData");
                        JSONArray jsonArray = ss.getJSONArray("DocumentList");

                        List<Document> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Document teamSpaceBeanFile = new Document();
                            teamSpaceBeanFile.setSpaceID(jsonObject.getInt("SpaceID") + "");
                            teamSpaceBeanFile.setItemID(jsonObject.getInt("ItemID") + "");
                            teamSpaceBeanFile.setAttachmentID(jsonObject.getInt("AttachmentID") + "");
                            teamSpaceBeanFile.setCreatedDate(jsonObject.getString("CreatedDate"));
                            teamSpaceBeanFile.setFileType(jsonObject.getInt("FileType"));
                            teamSpaceBeanFile.setSourceFileUrl(jsonObject.getString("SourceFileUrl"));
                            teamSpaceBeanFile.setSyncCount(jsonObject.getInt("SyncCount"));
                            teamSpaceBeanFile.setTitle(jsonObject.getString("Title"));
                            teamSpaceBeanFile.setFileName(jsonObject.getString("FileName"));
                            teamSpaceBeanFile.setAttachmentUrl(jsonObject.getString("AttachmentUrl"));
                            list.add(teamSpaceBeanFile);
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


    public void getAllDocumentList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        JSONObject dd = returnjson.getJSONObject("RetData");
                        JSONArray jsonArray = dd.getJSONArray("DocumentList");

                        List<Document> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Document teamSpaceBeanFile = new Document();

                            teamSpaceBeanFile.setSpaceID(jsonObject.getInt("SpaceID") + "");
                            teamSpaceBeanFile.setItemID(jsonObject.getInt("ItemID") + "");
                            teamSpaceBeanFile.setAttachmentID(jsonObject.getInt("AttachmentID") + "");
                            teamSpaceBeanFile.setCreatedDate(jsonObject.getString("CreatedDate"));
                            teamSpaceBeanFile.setFileType(jsonObject.getInt("FileType"));
                            teamSpaceBeanFile.setSourceFileUrl(jsonObject.getString("SourceFileUrl"));
                            teamSpaceBeanFile.setSyncCount(jsonObject.getInt("SyncCount"));
                            teamSpaceBeanFile.setFileName(jsonObject.getString("FileName"));
                            teamSpaceBeanFile.setTitle(jsonObject.getString("Title"));
                            list.add(teamSpaceBeanFile);
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


    public void getSyncRoomList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("getSyncRoomList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        JSONObject dd = returnjson.getJSONObject("RetData");
                        JSONArray jsonArray = dd.getJSONArray("RoomList");

                        List<SyncRoomBean> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            SyncRoomBean syncRoomBean = new SyncRoomBean();
                            syncRoomBean.setName(jsonObject.getString("Name"));
                            syncRoomBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            syncRoomBean.setCreatedByName(jsonObject.getString("CreatedByName"));

                            syncRoomBean.setItemID(jsonObject.getInt("ItemID"));
                            syncRoomBean.setType(jsonObject.getInt("Type"));
                            syncRoomBean.setCompanyID(jsonObject.getInt("CompanyID"));
                            syncRoomBean.setTopicType(jsonObject.getInt("TopicType"));
                            syncRoomBean.setParentID(jsonObject.getInt("ParentID"));
                            syncRoomBean.setLinkedDocTeamID(jsonObject.getInt("LinkedDocTeamID"));
                            syncRoomBean.setSynchronizeMember(jsonObject.getInt("SynchronizeMember"));
                            syncRoomBean.setMemberType(jsonObject.getInt("MemberType"));
                            syncRoomBean.setMemberCount(jsonObject.getInt("MemberCount"));
                            syncRoomBean.setDocumentCount(jsonObject.getInt("DocumentCount"));
                            syncRoomBean.setMeetingCount(jsonObject.getInt("MeetingCount"));
                            list.add(syncRoomBean);
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

    public void getMemberList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray jsonArray = returnjson.getJSONArray("RetData");
                        List<SyncRoomMember> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            SyncRoomMember syncRoomBean = new SyncRoomMember();
                            syncRoomBean.setSyncroomId(jsonObject.getInt("SyncRoomID"));
                            syncRoomBean.setMemberID(jsonObject.getInt("MemberID"));
                            syncRoomBean.setMemberName(jsonObject.getString("MemberName"));
                            syncRoomBean.setMemberAvatar(jsonObject.getString("MemberAvatar"));
                            syncRoomBean.setJoinDate(jsonObject.getString("JoinDate"));
                            syncRoomBean.setMemberType(jsonObject.getInt("MemberType"));
                            list.add(syncRoomBean);
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

    public void getAudienceList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("code") == 0) {
                        JSONArray jsonArray = returnjson.getJSONArray("data");
                        List<Customer> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Customer customer = new Customer();
                            customer.setUserID(jsonObject.getString("userId"));
                            customer.setName(jsonObject.getString("userName"));
                            customer.setUrl(jsonObject.getString("avatarUrl"));
                            customer.setOnline(jsonObject.getInt("isOnline") == 1 ? true : false);
                            list.add(customer);
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


    public void createSyncRoom(final String url, final int code, final int companyID, final int teamid, final int spaceId, final String title, final String attachmentid,
                               final String attachmentname, final List<Customer> customerList,
                               TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("CompanyID", companyID);
                    jsonObject.put("TeamID", teamid);
                    jsonObject.put("SpaceID", spaceId);
                    jsonObject.put("Title", title);
                    jsonObject.put("AttachmentID", attachmentid);
                    jsonObject.put("AttachmentTitle", attachmentname);
                    jsonObject.put("AttachmentList", null);
                    jsonObject.put("Type", 1);

                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < customerList.size(); i++) {
                        Customer cus = customerList.get(i);
                        if (cus.isSelected()) {
                            JSONObject j = new JSONObject();
                            j.put("MemberID", cus.getUserID());
                            j.put("Role", 1);
                            jsonArray.put(j);
                        }
                    }
                    JSONObject j2 = new JSONObject();
                    j2.put("MemberID", AppConfig.UserID);
                    j2.put("Role", 2);
                    jsonArray.put(j2);
                    jsonObject.put("MemberList", jsonArray);

                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", url + jsonObject.toString() + "     " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.obj = returnjson.getInt("RetData");
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

    public void updateTeamTopic(final String url, final int code, final String id, final String name, final int topicType, final String note,
                                TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ID", id);
                    jsonObject.put("Name", name);
                    jsonObject.put("TopicType", topicType);
                    jsonObject.put("Note", note);
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", url + jsonObject.toString() + "     " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.obj = returnjson.getString("RetData");
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

    private void putInterface(int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        if (hashMap.get(code) != null) {
            hashMap.remove(code);
        }
        hashMap.put(code, serviceInterfaceListener);
    }


}
