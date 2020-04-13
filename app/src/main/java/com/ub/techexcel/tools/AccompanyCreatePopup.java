package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.SoundtrackBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wang on 2017/9/18.
 */

public class AccompanyCreatePopup implements View.OnClickListener {

	public Context mContext;
	public int width;
	public Dialog mPopupWindow;
	private View view;
	private ImageView close;
	private TextView addaudio, addrecord;
	//    private CheckBox checkBox1, checkBox2;
	private EditText edittext;
	private ImageView delete1, delete2;

	private TextView recordsync, cancel;
	private Document favorite = new Document();
	private Document recordfavorite = new Document();
	private CheckBox checkBox;
	private String attachmentId;
	private static FavoritePoPListener mFavoritePoPListener;
	private TextView recordname;
	private TextView bgname;
	private LinearLayout backgroundAudioLayout;
	private LinearLayout recordMyVoiceLayout;
	private RelativeLayout voiceItemLayout;
	private LinearLayout addVoiceLayout;
	private LinearLayout ishiddenll;
	private CheckBox isPublic;
	private TextView tv_bg_audio, tv_record_voice;
	private SharedPreferences sharedPreferences;

	private TextView isaccompanytv;
	private ImageView isaccompanytv_gou;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0x1001:
					mFavoritePoPListener.syncorrecord(checkBox.isChecked(), soundtrackBean);
					break;
			}
			super.handleMessage(msg);
		}
	};


	public interface FavoritePoPListener {

		void addrecord(int isrecord);

		void addaudio(int isrecord);

		void syncorrecord(boolean checked, SoundtrackBean soundtrackBean);
	}

	public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
		this.mFavoritePoPListener = documentPoPListener;
	}


	public void getPopwindow(Context context) {
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
		sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO, MODE_PRIVATE);
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		view = layoutInflater.inflate(R.layout.accompany_create_popup, null);
		close = (ImageView) view.findViewById(R.id.close);
		cancel = (TextView) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		tv_record_voice = (TextView) view.findViewById(R.id.tv_record_voice);
		tv_bg_audio = (TextView) view.findViewById(R.id.tv_bg_audio);
		addaudio = (TextView) view.findViewById(R.id.addaudio);
		addrecord = (TextView) view.findViewById(R.id.addrecord);
		recordname = (TextView) view.findViewById(R.id.recordname);
		bgname = (TextView) view.findViewById(R.id.bgname);
		isaccompanytv = (TextView) view.findViewById(R.id.isaccompanytv);
		isaccompanytv.setOnClickListener(this);
		isaccompanytv.setOnClickListener(this);
		isaccompanytv_gou = view.findViewById(R.id.isaccompanytv_gou);
		backgroundAudioLayout = (LinearLayout) view.findViewById(R.id.layout_background_audio);
		edittext = (EditText) view.findViewById(R.id.edittext);
		String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
		String name = AppConfig.UserName + "_" + time;
//        edittext.setText(name);
//        edittext.setSelection(name.length());
		voiceItemLayout = view.findViewById(R.id.layout_voice_item);
		checkBox = (CheckBox) view.findViewById(R.id.checkboxx);
		isPublic = (CheckBox) view.findViewById(R.id.isPublic);
		recordMyVoiceLayout = view.findViewById(R.id.layout_record_my_voice);
		addVoiceLayout = view.findViewById(R.id.layout_add_voice);
		ishiddenll = view.findViewById(R.id.ishiddenll);
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
//        setBindViewText();
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

	}

//    private void setBindViewText(){
//        String voice=getBindViewText(1020);
//        tv_record_voice.setText(TextUtils.isEmpty(voice)? "录制新的声音":"录制" +voice);
//        String audio=getBindViewText(1018);
//        tv_bg_audio.setText(TextUtils.isEmpty(audio)? "开启背景音频":"开启"+audio);
//    }


	private int actionBaseSoundtrackID;

	public void setAudioBean(Document favorite) {
		this.favorite = favorite;
		actionBaseSoundtrackID = Integer.parseInt(favorite.getItemID());
		delete1.setVisibility(View.VISIBLE);
		addaudio.setVisibility(View.INVISIBLE);
		backgroundAudioLayout.setVisibility(View.VISIBLE);
		isaccompanytv.setText("制作同步音乐伴奏带");
		if (favorite != null) {
			bgname.setVisibility(View.VISIBLE);
			bgname.setText(favorite.getTitle());
		}
		setCreateSyncText();
	}


	private SoundTrack  lastSoundtrack=new SoundTrack();
	public void setAudioBean1(final SoundTrack soundTrack) {
		lastSoundtrack=soundTrack;
		actionBaseSoundtrackID = soundTrack.getSoundtrackID();
		isaccompanytv.setText("重新同步");
		ServiceInterfaceTools.getinstance().getSoundItem(AppConfig.URL_PUBLIC + "Soundtrack/Item?soundtrackID=" + actionBaseSoundtrackID,
				ServiceInterfaceTools.GETSOUNDITEM,
				new ServiceInterfaceListener() {
					@Override
					public void getServiceReturnData(Object object) {
						SoundtrackBean soundtrackBean = (SoundtrackBean) object;
						favorite = new Document();
						favorite.setAttachmentID(soundtrackBean.getBackgroudMusicAttachmentID() + "");
						favorite.setTitle(soundTrack.getTitle());
						favorite.setItemID("0");
						bgname.setVisibility(View.VISIBLE);
						bgname.setText(soundTrack.getTitle());
						setCreateSyncText();
					}
				});
		delete1.setVisibility(View.VISIBLE);
		addaudio.setVisibility(View.INVISIBLE);
		backgroundAudioLayout.setVisibility(View.VISIBLE);
	}

	public void setRecordBean1(final SoundTrack soundTrack) {
		delete2.setVisibility(View.VISIBLE);
		addrecord.setVisibility(View.INVISIBLE);
		addVoiceLayout.setVisibility(View.INVISIBLE);
		ServiceInterfaceTools.getinstance().getSoundItem(AppConfig.URL_PUBLIC + "Soundtrack/Item?soundtrackID=" + soundTrack.getSoundtrackID(),
				ServiceInterfaceTools.GETSOUNDITEM,
				new ServiceInterfaceListener() {
					@Override
					public void getServiceReturnData(Object object) {
						SoundtrackBean soundtrackBean = (SoundtrackBean) object;

						recordfavorite = new Document();
						recordfavorite.setAttachmentID(soundtrackBean.getBackgroudMusicAttachmentID() + "");
						recordfavorite.setItemID("0");
						recordfavorite.setTitle(soundTrack.getTitle());

						voiceItemLayout.setVisibility(View.VISIBLE);
						recordname.setVisibility(View.VISIBLE);
						recordname.setText(recordfavorite.getTitle());
						setCreateSyncText();
					}
				});
	}


	public void setRecordBean(Document favorite) {
		this.recordfavorite = favorite;
		delete2.setVisibility(View.VISIBLE);
		addrecord.setVisibility(View.INVISIBLE);
		addVoiceLayout.setVisibility(View.INVISIBLE);
		if (recordfavorite != null) {
			voiceItemLayout.setVisibility(View.VISIBLE);
			recordname.setVisibility(View.VISIBLE);
			recordname.setText(recordfavorite.getTitle());
		}
		setCreateSyncText();
	}


	@SuppressLint("NewApi")
	public void StartPop(View v, String attachmentId) {
		if (mPopupWindow != null) {
			this.attachmentId = attachmentId;
			WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
			if (Tools.isOrientationPortrait((Activity) mContext)) {
				View root = ((Activity) mContext).getWindow().getDecorView();
				params.width = root.getMeasuredWidth() * 9 / 10;
				// params.height =mContext.getResources().getDisplayMetrics().heightPixels * 3 / 5;
			} else {
				params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
				View root = ((Activity) mContext).getWindow().getDecorView();
				params.height = root.getMeasuredHeight() * 4 / 5 + 30;
			}
			mPopupWindow.getWindow().setAttributes(params);
			mPopupWindow.show();
		}
	}

	public boolean isShowing() {
		return mPopupWindow.isShowing();
	}

	public void dismiss() {
		hideSoftKeyboard(mContext, edittext);
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	/**
	 * 隐藏软键盘(有输入框)
	 */
	public static void hideSoftKeyboard(Context context, EditText mEditText) {
		InputMethodManager inputmanger = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanger.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}

	private SoundtrackBean soundtrackBean = new SoundtrackBean();

	private void createSoundtrack() {
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("Title", edittext.getText().toString());
					jsonObject.put("AttachmentID", Integer.parseInt(attachmentId));
					jsonObject.put("BackgroudMusicAttachmentID", TextUtils.isEmpty(favorite.getAttachmentID()) ? 0 : favorite.getAttachmentID());
					jsonObject.put("SelectedAudioAttachmentID", TextUtils.isEmpty(recordfavorite.getAttachmentID()) ? 0 : recordfavorite.getAttachmentID());
					jsonObject.put("EnableBackgroud", TextUtils.isEmpty(favorite.getAttachmentID()) ? 0 : 1);
					jsonObject.put("EnableSelectVoice", TextUtils.isEmpty(recordfavorite.getAttachmentID()) ? 0 : 1);
					jsonObject.put("EnableRecordNewVoice", checkBox.isChecked() ? 1 : 0);
					jsonObject.put("SelectedAudioTitle", recordfavorite.getTitle());
					jsonObject.put("BackgroudMusicTitle", favorite.getTitle());
					jsonObject.put("IsPublic", 1);
					jsonObject.put("Type", 0);
					jsonObject.put("LessonID", 0);
					jsonObject.put("ItemID", 0);
					jsonObject.put("ActionBaseSoundtrackID", actionBaseSoundtrackID);

					int musicType=checkBox.isChecked() ? 1 : 0;
					if(musicType==0){
						if(!TextUtils.isEmpty(recordfavorite.getAttachmentID())){
							musicType=1;
						}
					}
					jsonObject.put("MusicType",musicType);  //0是伴奏，1是演唱
					JSONObject returnjson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Soundtrack/CreateSoundtrack", jsonObject);
					Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString());
					if (returnjson.getInt("RetCode") == 0) {
						JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
						soundtrackBean = new SoundtrackBean();
						soundtrackBean.setSoundtrackID(jsonObject1.getInt("SoundtrackID"));
						soundtrackBean.setTitle(jsonObject1.getString("Title"));
						soundtrackBean.setUserID(jsonObject1.getString("UserID"));
						soundtrackBean.setUserName(jsonObject1.getString("UserName"));
						soundtrackBean.setAvatarUrl(jsonObject1.getString("AvatarUrl"));
						soundtrackBean.setDuration(jsonObject1.getString("Duration"));
						soundtrackBean.setCreatedDate(jsonObject1.getString("CreatedDate"));
						soundtrackBean.setAttachmentId(jsonObject1.getInt("AttachmentID"));
						soundtrackBean.setIsPublic(jsonObject1.getInt("IsPublic"));
						soundtrackBean.setActionBaseSoundtrackID(actionBaseSoundtrackID);


						JSONObject pathinfo = jsonObject1.getJSONObject("PathInfo");
						soundtrackBean.setFileId(pathinfo.getInt("FileID"));
						soundtrackBean.setPath(pathinfo.getString("Path"));

						soundtrackBean.setBackgroudMusicAttachmentID(jsonObject1.getInt("BackgroudMusicAttachmentID"));
						soundtrackBean.setNewAudioAttachmentID(jsonObject1.getInt("NewAudioAttachmentID"));
						soundtrackBean.setSelectedAudioAttachmentID(jsonObject1.getInt("SelectedAudioAttachmentID"));

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
						msg3.obj = soundtrackBean;
						msg3.what = 0x1001;
						handler.sendMessage(msg3);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.close:
				dismiss();
				break;
			case R.id.addaudio:
				mFavoritePoPListener.addaudio(0);
				break;
			case R.id.addrecord:
				mFavoritePoPListener.addrecord(1);
				break;
			case R.id.cancel:
				dismiss();
				break;
			case R.id.recordsync:
				dismiss();
				createSoundtrack();
				break;
			case R.id.delete1:
				favorite = new Document();
				backgroundAudioLayout.setVisibility(View.INVISIBLE);
				delete1.setVisibility(View.INVISIBLE);
				addaudio.setVisibility(View.VISIBLE);
				bgname.setVisibility(View.INVISIBLE);
				setCreateSyncText();
				break;
			case R.id.delete2:
				delete2();
				break;
			case R.id.isaccompanytv:   //重新同步
				if (isAccompanyOrMusic == 1) {
					isAccompanyOrMusic = 0;
					actionBaseSoundtrackID=0;
					isaccompanytv_gou.setVisibility(View.VISIBLE);
					checkBox.setChecked(false);
					delete2();
					ishiddenll.setVisibility(View.GONE);
				} else {
					isAccompanyOrMusic = 1;
					actionBaseSoundtrackID=lastSoundtrack.getSoundtrackID();
					isaccompanytv_gou.setVisibility(View.GONE);
					ishiddenll.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
		}
	}

	private void delete2() {
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
	}


	private int isAccompanyOrMusic = 1;//0是伴奏，1是演唱

	private String getBindViewText(int fileId) {
		String appBindName = "";
		int language = sharedPreferences.getInt("language", 1);
		if (language == 1 && App.appENNames != null) {
			for (int i = 0; i < App.appENNames.size(); i++) {
				if (fileId == App.appENNames.get(i).getFieldId()) {
					System.out.println("Name->" + App.appENNames.get(i).getFieldName());
					appBindName = App.appENNames.get(i).getFieldName();
					break;
				}
			}
		} else if (language == 2 && App.appCNNames != null) {
			for (int i = 0; i < App.appCNNames.size(); i++) {
				if (fileId == App.appCNNames.get(i).getFieldId()) {
					System.out.println("Name->" + App.appCNNames.get(i).getFieldName());
					appBindName = App.appCNNames.get(i).getFieldName();
					break;
				}
			}
		}
		return appBindName;
	}
}
