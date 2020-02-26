package com.kloudsync.user.techexcel.pi.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.NetWorkHelp;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserGet {
	private static String CUSTOMERDETAIL_URL;
	private static String MEMBERDETAIL_URL;
	private static Context mContext;

	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AppConfig.GETCUSTOMERDETAIL:
				String result = (String) msg.obj;
				if (result != null) {
					mJsonCusDetail(result);
				} else {
//					Toast.makeText(
//							mActivity,
//							mActivity.getResources().getString(R.string.No_Data),
//							Toast.LENGTH_SHORT).show();
				}
				break;
			case AppConfig.GETMEMBERDETAIL:
				result = (String) msg.obj;
				if (result != null) {
					mJsonMemDetail(result);
				} else {
//					Toast.makeText(
//							mActivity,
//							mActivity.getResources().getString(R.string.No_Data),
//							Toast.LENGTH_SHORT).show();
				}
				break;
			case AppConfig.NO_NETWORK:
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.No_networking), Toast.LENGTH_SHORT)
						.show();
				break;
			case AppConfig.NETERROR:
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.NETWORK_ERROR), Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}
	};

	private static DetailListener detailgetlistener;

	public interface DetailListener {
		void getUser(Customer user);

		void getMember(MemberBean member);
	}

	public void setDetailListener(DetailListener detailgetlistener) {
		UserGet.detailgetlistener = detailgetlistener;
	}

	/**
	 * 用户详情获取
	 * 
	 * @param context
	 */
	public void CustomerDetailRequest(Context context, String UserID) {
		mContext = context;
		CUSTOMERDETAIL_URL = AppConfig.URL_PUBLIC + "User/Customer?UserID="
				+ UserID;
		newThreadGetResultBytoken(CUSTOMERDETAIL_URL,
				AppConfig.GETCUSTOMERDETAIL);
	}

	/**
	 * 会员详情获取
	 * 
	 * @param context
	 */
	public void MemberDetailRequest(Context context, String UserID) {
		mContext = context;
		MEMBERDETAIL_URL = AppConfig.URL_PUBLIC + "User/Member?UserID="
				+ UserID;
		newThreadGetResultBytoken(MEMBERDETAIL_URL, AppConfig.GETMEMBERDETAIL);
	}

	private static synchronized void newThreadGetResultBytoken(
			final String URL, final int config) {
		new ApiTask(new Runnable() {

			@Override
			public void run() {

				String result = null;
				Message msg = new Message();
				try {
					java.net.URL url = new URL(URL);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(5 * 1000);
					conn.setConnectTimeout(5 * 1000);
					conn.addRequestProperty("UserToken", AppConfig.UserToken);
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() == 200) {
						//先将服务器得到的流对象 包装 存入缓冲区，忽略了正在缓冲时间
						InputStream in = new BufferedInputStream(conn.getInputStream());
						// 得到servlet写入的头信息，response.setHeader("year", "2013");
//						String year = conn.getHeaderField("year");
//						System.out.println("year="+year);
//						byte[] bytes = readFromInput(in);	//封装的一个方法，通过指定的输入流得到其字节数据
						result = NetWorkHelp.InputStreamTOString(in);
						in.close();
						conn.disconnect();
					}
					msg.obj = result;
					msg.what = config;


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = AppConfig.NETERROR;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = AppConfig.NETERROR;
				} finally {
					if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
						msg.what = AppConfig.NO_NETWORK;
					}
					handler.sendMessage(msg);
				}

			}
		}).start(ThreadManager.getManager());
	}

	/**
	 * HttpGet超时设置
	 */
	private static int TIME_OUT_DELAY = 5000;

	public static HttpClient initHttp() {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				TIME_OUT_DELAY); // 超时设置
		client.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, TIME_OUT_DELAY);// 连接超时
		return client;
	}

	protected static void mJsonCusDetail(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			String RetCode = obj.getString("RetCode");
			if(RetCode.equals(AppConfig.RIGHT_RETCODE)){
				JSONObject RetData = obj.getJSONObject("RetData");
				String UserID = RetData.getString("UserID");
				String RongCloudUserID = RetData.getString("RongCloudUserID");
				String Name = RetData.getString("Name");
				String Sex = RetData.getString("Sex");
				String Birthday = RetData.getString("Birthday");
				String Age = RetData.getString("Age");
				String Phone = RetData.getString("Phone");
				String Address = RetData.getString("Address");
				String AvatarUrl = RetData.getString("AvatarUrl");
				String Height = RetData.getString("Height");
				String Weight = RetData.getString("Weight");
//				AvatarUrl= AvatarUrl.replace("4443", "120").replace("https", "http");
				String PersonalComment = RetData.getString("PersonalComment");
				String SelfDescription = RetData.getString("SelfDescription");


				Customer cus = new Customer(UserID, RongCloudUserID, Name, Sex, null, "A", Age);
				cus.setBirthday(Birthday);
				cus.setPhone(Phone);
				cus.setAddress(Address);
				cus.setUrl(AvatarUrl);
				cus.setPersonalComment(PersonalComment);
				cus.setSelfDescription(SelfDescription);
				cus.setHeight(Height);
				cus.setWeight(Weight);

				int UBAOPersonID = RetData.getInt("UBAOPersonID");
				if(UBAOPersonID > 0){
					String UBAOPersonName = RetData.getString("UBAOPersonName");
					cus.setUBAOPersonName(UBAOPersonName);
					cus.setUBAOPersonID(UBAOPersonID);
				}
				ArrayList<CommonUse> hclist = new ArrayList<CommonUse>();

				JSONArray HealthConcerns = RetData.getJSONArray("HealthConcerns");
				for (int i = 0; i < HealthConcerns.length(); i++){
					JSONObject HealthConcern = HealthConcerns.getJSONObject(i);
					String hName = HealthConcern.getString("Name");
					int hID = HealthConcern.getInt("ID");

					CommonUse cu = new CommonUse();
					cu.setID(hID);
					cu.setName(hName);
					hclist.add(cu);

				}
				cus.setHealthConcerns(hclist);

				detailgetlistener.getUser(cus);

			}else{
				detailgetlistener.getUser(new Customer());
				String ErrorMessage = obj.getString("ErrorMessage");
				String DetailMessage = obj.getString("DetailMessage");
				Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void mJsonMemDetail(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			String RetCode = obj.getString("RetCode");
			if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
				JSONObject RetData = obj.getJSONObject("RetData");

				String UserID = RetData.getString("UserID");
				String Name = RetData.getString("Name");
				String Sex = RetData.getString("Sex");
				String Birthday = RetData.getString("Birthday");
				String Phone = RetData.getString("Mobile");
				String State = RetData.getString("State");
				String City = RetData.getString("City");
				String Address = RetData.getString("Address");
				String AvatarUrl = RetData.getString("AvatarUrl");
				String SkilledFields = RetData.getString("SkilledFields");
				String Summary = RetData.getString("Summary");
				String MemberPoints = RetData.getString("MemberPoints");
				String ExpirationDate = RetData.getString("ExpirationDate");
				String ArticleCount = RetData.getString("ArticleCount");

				MemberBean member = new MemberBean();
				member.setName(Name);
				member.setSex(Sex);
				member.setUserID(UserID);
				member.setAvatarUrl(AvatarUrl);
				member.setBirthday(Birthday);
				member.setPhone(Phone);
				member.setAddress(Address);
				member.setState(State);
				member.setCity(City);
				member.setMemberPoints(MemberPoints);
				member.setExpirationDate(ExpirationDate);
				member.setSkilledFields(SkilledFields);
				member.setSummary(Summary);
				member.setArticleCount(ArticleCount);

				detailgetlistener.getMember(member);

			} else {
				String ErrorMessage = obj.getString("ErrorMessage");
				Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
