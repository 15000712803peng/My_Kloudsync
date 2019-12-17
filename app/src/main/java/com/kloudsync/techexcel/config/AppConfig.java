package com.kloudsync.techexcel.config;

import com.kloudsync.techexcel.dialog.message.CourseMessage;
import com.kloudsync.techexcel.dialog.message.SpectatorMessage;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Knowledge;
import com.kloudsync.user.techexcel.pi.tools.ProvinceBean;
import com.lidroid.xutils.http.RequestParams;
import com.ub.techexcel.bean.CourseLesson;
import com.ub.techexcel.bean.NotifyBean;
import com.ub.techexcel.bean.ServiceBean;

import org.java_websocket.client.WebSocketClient;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Group;
import io.rong.message.LocationMessage;


public class AppConfig {

    //	public static String URL_PUBLIC = "http://101.231.103.198:12345/ubao/v1/";
//	public static String URL_PUBLIC = "http://192.168.22.5/UBAODebug/V1/";
//	public static String URL_PUBLIC = "http://ub.servicewise.net.cn/UBAO/V1/";
//	public static String URL_PUBLIC = "http://ub.servicewise.net.cn/ub/V1/";
//    public static String URL_PUBLIC = "http://ub.servicewise.net.cn:120/ub/V1/";
//    public static String URL_PUBLIC = "https://pt.techexcel.com/peertime/V1/";

    //上传专用
    public static String URL_LIVEDOC = "https://livedoc.peertime.cn/TxLiveDocumentApi/api/";
    public static String URL_PUBLIC_AUDIENCE = "https://wss.peertime.cn/";

//    public static String URL_PUBLIC = "https://testapi.peertime.cn/peertime/V1/";

//     正式
    public static String URL_PUBLIC = "https://api.peertime.cn/peertime/V1/";
    public static String URL_MEETING_BASE = "https://api.peertime.cn/MeetingServer/";
    public static String COURSE_SOCKET = "wss://wss.peertime.cn:8443/MeetingServer/websocket";
    public static String liveToken = "02912174-3dcb-49eb-b9fa-6d90b390d495";
    public static String  wssServer = "https://wss.peertime.cn:8443/MeetingServer";

//    // 测试
//    public static String URL_PUBLIC = "https://testapi.peertime.cn/peertime/V1/";
//    public static String COURSE_SOCKET = "wss://testwss.peertime.cn:8443/MeetingServer/websocket";
//    public static String liveToken = "01427aa4-396e-44b7-82ab-84d802099bb0";
//    public static String  wssServer = "https://testwss.peertime.cn:8443/MeetingServer";


    public static String URL_IMAGE = "http://ub.servicewise.net.cn/CWKnowledgeWise/ImagePreview.aspx?ProjectID=49&FileID=";
    //	public static String URL_IMAGE = "http://101.231.103.198:12345/CWKnowledgeWise/ImagePreview.aspx?ProjectID=49&FileID=";
    public static String URL_KNOWLEDGE = "http://ub.servicewise.net.cn/CWKnowledgeWise/ViewItem.aspx?49,";

//    public static String COURSE_SOCKET = "ws://testwss.peertime.cn:8080/MeetingServer/websocket";

//    public static String COURSE_SOCKET = "ws://wss.peertime.cn:8080/MeetingServer/websocket";


//    public static String COURSE_SOCKET = "wss://testwss.peertime.cn:8443/MeetingServer/websocket";

    // 访问的endpoint地址
    public static  String OSS_ENDPOINT = "oss-cn-shanghai.aliyuncs.com";
    public static final String BUCKET_NAME = "ptfiles";
    public static final String OSS_ACCESS_KEY_ID = "LTAIcw5JmsTiae9F";
    public static final String OSS_ACCESS_KEY_SECRET = "J0b1dc9RBwteROJoTiEMHr8w57SsyH";
    public static final String STS_SERVER_URL = "https://doc.peertime.cn/v1/";
    public static final String SHARE_DOCUMENT = "https://kloudsync.peertime.cn/document/";
    public static final String SHARE_SYNC = "https://kloudsync.peertime.cn/sync/";
    public static final String SHARE_RECORD = "https://kloudsync.peertime.cn/record/";
    public static final String SHARE_ATTACHMENT = "https://kloudsync.peertime.cn/attachment/";


    public static String LOGININFO = "LOGININFO";
    public static String COMMONUSEDINFO = "COMMONUSEDINFO";
    public static String Name = "UBAO小博士";
    public static String Robot = "Robot";
    public static String RobotName = "PeerTime Robot";
    public static String Authorization = "Bearer AtI_QF93Lyo.cwA.2Dg.bOhGvdCxY1Fo_kOs6ronW0cBuutC6ummvuje8QHfbr4";
    public static String conversationId = "3qprO91tUkgFRAZTdb6rPj";
    public static String Sharelive = "https://peertime.cn/live?share=";

    public static String RongUserID;
    public static String UserToken;
    public static String UserID;
    public static String UserName;
    public static String UserExpirationDate;
    public static String ClassRoomID;
    public static String RongUserToken;
    public static String SEND_SENTENCE;
    public static String GROUP_NAME;
    public static String DELETEGROUP_ID = "";
    public static String DELETEFRIEND_ID = "";
    public static String DEVICE_ID = "";
    public static String COUNTRY_NAME;
    public static String MYAVATARURL;
    public static String OUTSIDE_PATH;
    public static String Mobile;
    public static String SystemModel;
    public static boolean isSend = false;
    public static boolean isSendKnowledge = false;
    public static boolean isUpdateCustomer = false;
    public static boolean isUpdateDialogue = false;
    public static boolean ISLOCATIONS = false;
    public static boolean isChangeGroupName = false;
    public static boolean isDeleteGroup = false;
    public static boolean isDeletFRIEND = false;
    public static boolean isToPersonalCenter = false;
    public static boolean isCourse = false;
    public static boolean isSpectator = false;
    public static boolean isRefreshRed = false;
    public static int LANGUAGEID = -1;
    public static Knowledge KNOWLEDGE;
    public static List<String> UserIDs;
    public static int UserType;
    public static int UbaoMan;
    public static int COUNTRY_CODE;
    public static int Role;
    public static int SchoolID;
    public static String selectedSubSystemId;
    public static int deviceType;

    public static int Online;
    public static final int LOGIN = 1;
    public static final int NETERROR = 2;
    public static final int NO_NETWORK = 3;
    public static final int CHECKCODE = 4;
    public static final int RONGUSERTOKEN = 5;
    public static final int ACCESSCODE = 6;
    public static final int FAILED = 7;
    public static final int SUCCESSCHANGE = 8;
    public static final int GETCHECKCODE = 9;
    public static final int GETCUSTOMER = 10;
    public static final int GETMEMBER = 11;
    public static final int CONCERNHIERARCHY = 12;
    public static final int USEFULEXPRESSION = 13;
    public static final int GETKNOWLEDGE = 14;
    public static final int CUSTOMERDETAIL = 15;
    public static final int SEX = 16;
    public static final int CHECKC_MOBILE = 17;
    public static final int CREATE_USER = 18;
    public static final int MEMBERDETAIL = 19;
    public static final int RONGCONNECT_ERROR = 20;
    public static final int UPLOADHEAD = 21;
    public static final int FRIENDSINFO = 22;
    public static final int CREATE_GROUP = 23;
    public static final int QUIT_GROUP = 24;
    public static final int GROUP_DETAIL = 25;
    public static final int GETGROUPS = 26;
    public static final int ADD_GROUPMEMBER = 27;
    public static final int DISMISS_GROUP = 28;
    public static final int UPDATE_GROUPNAME = 29;
    public static final int BECOME_UBMAN = 30;
    public static final int CHANGE_REMARK = 31;
    public static final int MY_FAVOURITES = 32;
    public static final int UPLOADFAILD = 33;
    public static final int DELETESUCCESS = 34;
    public static final int SAVESUCCESS = 35;
    public static final int HasExisted = 36;
    public static final int NotExist = 37;
    public static final int AddFriend = 38;
    public static final int RobotReceive = 39;
    public static final int UserSchoolList = 40;
    public static final int SchoolContact = 41;
    public static final int AddTempLesson = 42;
    public static final int ShareDocument = 43;
    public static final int OnlineStatus = 44;
    public static final int OnlineGunDan = 45;
    public static final int AskConvert = 46;
    public static final int ConvertStatus = 47;
    public static final int DoneStatus = 48;
    public static final int AskResult = 49;
    public static final int TeamSpace = 50;
    public static final int AddMember = 51;
    public static final int GoTOWeixing = 52;
    public static final int Rename = 53;
    public static final int BeforeDeleteSpace = 54;
    public static final int BeforeDeleteTeam = 55;
    public static final int RenameAttachment = 56;
    public static final int InviteToCompany = 57;
    public static final int SearchContact = 58;
    public static final int CreateOrganization = 59;
    public static final int AddOrUpdateUserPreference = 60;
    public static final int GetUserPreference = 61;
    public static final int CreateTeamTopic = 62;
    public static final int SpaceAttachment = 63;
    public static final int SwitchSpace = 64;
    public static final int RemoveMember = 65;
    public static final int prepareUploading = 66;
    public static final int REGISTER_SUCC = 67;
    public static final int REGISTER_FAIL = 68;
    public static final int INVITE_FAIL = 69;
    public static final int Upload_NoExist = -6002;
    public static final int Upload_Exist = -6003;
    public static final int COURSE_PORT = 8082;
    public static final String RIGHT_RETCODE = "0";
    public static final String UserHasExisted = "-2004";
    public static final String UserNotExist = "-2002";
    public static LocationMessage LOCATIONMESSAGE;
    public static Group UPDATEGROUP;
    public static Socket SOCKET;
    public static CourseMessage COURSE;
    public static SpectatorMessage SPECTATOR;
    public static final int GET_INVITATIONS = 70;
    public static final int COMPANY_SUBSYSTEMS = 71;
    public static final int UUSERBINDTVINFO = 72;

    //  汪雄峰
//	public final static String targetUrl = "http://ub.servicewise.net.cn/CWKnowledgeWise/Preview.aspx?49,3602";  //正式版
//    public final static String targetUrl = "http://ub.servicewise.net.cn:120/CWKnowledgeWise/Preview.aspx?49,4690";  //正式版
    public final static String targetUrl = "https://api.peertime.cn/CWKnowledgeWise/Preview.aspx?49,4690";  //正式版
    public final static int LOAD_FINISH = 0X110;
    public final static int LOAD_FINISH2 = 0X1107;
    public final static int PULLREFRSH_FINISH = 0X111;
    public final static int JSON_SUCCESS = 0X112;
    public final static int NET_NOOPEN = 0X113;
    public final static int CHOICE_FINISH = 0X114;
    public final static int RETCODE_SUCCESS = 0;
    public static ServiceBean tempServiceBean = new ServiceBean();
    public static CourseLesson tempCourse = new CourseLesson();
    public static List<CourseLesson> templectures = new ArrayList<>();
    public final static int OptionalNo = 0; //不能选
    public final static int OptionalYes = 1;//能选
    public final static int Mandatory = 2;//强制性选择

    public static String ConcernValue = "添加关注点";
    public static boolean ISONRESUME = false;
    public static boolean ISCOURSE = false;
    public static boolean ISLECTURE = false;
    public static boolean isNewService = false;
    public static ArrayList<CommonUse> saveConcernList = new ArrayList<CommonUse>();
    public static boolean isDialogService = false;
    public static boolean isonresuce = false;
    public final static int CONFIRM_SERVICE = 0X115;
    public final static int UPDATE_SERVICE1 = 0X116;
    public final static int UPDATE_SERVICE2 = 0X117;
    public static boolean ISMODIFY_SERVICE = false;
    public static ServiceBean tempServiceBean2 = new ServiceBean();
    public static final String WX_APP_ID = "wx8f0e66d818fe6a60";

    public static boolean isConnect = false;
    public static List<Customer> auditorList = new ArrayList<>(); //老师学生
    public static boolean isUpdateAuditor = false;
    public static WebSocketClient webSocketClient;
    public static boolean ISSUCCESS = false;
    public static boolean newlesson = false;
    public static boolean netconnect = false;

    public static List<NotifyBean> progressCourse = new ArrayList<>();


    public static List<String> socketList = new ArrayList<>();


    public static boolean CHOISE_DATE = false;
    public static String DATE = "";
    public static int YEAR = 1985;
    public static int MONTH = 6;
    public static int DAY = 15;
    public static String NAME;
    public static String CURRENT_VALUES;
    public static String CURRENT_VALUESID;
    public static String PROVINCE;
    public static String CITY;
    public static String STREET;
    public static String IMAGEURL;
    public static String PHONE;
    public static String HEIGHT;
    public static final int GETSEX = 1;
    public static final int GTEPROVINCE = 2;
    public static final int GTECITY = 3;
    public static final String WHO_DO_WHAT = "User/Choices";
    public static final int SUCCESS = 1;
    public static final int SUCCESS2 = 0x2102;
    public static final int FALSE = -1;
    public static boolean ISFINISH_EXERCISE = false;
    public static boolean ISFINISH_JOGGING = false;
    public static ProvinceBean STATEBEAN = new ProvinceBean();
    public static ProvinceBean CITYBEAN = new ProvinceBean();
    public static final String GETMEMBERINFO = "User/Member";
    public static final String GETCUSTOMERINFO = "User/Customer";
    public static RequestParams PARAMS = new RequestParams();
    public static boolean UPLOADSTATIC = false;
    public static boolean HASUPDATAINFO = false;
    public static boolean JIANKANGCAO = true;
    public static boolean MANPAO = true;
    public static boolean CHIYAO = false;
    public static boolean HESHUI = false;
    public static boolean ISFINISH_CHIYAO = false;
    public static boolean ISFINISH_HESHUI = false;
    public static boolean HASUPDATESUMMERY = false;
    public static final int GETCUSTOMERDETAIL = 101;
    public static final int GETMEMBERDETAIL = 102;
    public static boolean CHANGETASK = false;
    public static boolean CHANGETASKLIST = false;

    public static boolean isPresenter = false;
    public static String status = "0";
    public static int currentLine = 0;
    public static String currentMode = "0";
    public static String currentPageNumber = "0";
    public static String currentDocId = "0";


    // 余迪凯
    public static final int PASSWORDSUCCESS = 0X1130;
    public static final int PASSWORDERROR = 0X1131;
    public static final int PASSWORDERROR2 = 0X112;

    public static boolean IsInMeeting = false;


}
