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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.view.UISwitchButton;
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

public class AccompanyCreatePopup implements View.OnClickListener , AccompanyMoreOperations.OnSoundtrackOperationListener {

	public Context mContext;
	public int width;
	public Dialog mPopupWindow;
	private View view;
	private ImageView close;
	private TextView addaudio;
	//    private CheckBox checkBox1, checkBox2;
	private EditText edittext;
	private TextView recordsync, cancel;
	private Document favorite = new Document();
	private Document recordfavorite = new Document();
	private CheckBox checkBox;
	private String attachmentId;
	private static FavoritePoPListener mFavoritePoPListener;
	private TextView recordname;
	private TextView bgname;
	private RelativeLayout backgroundAudioLayout;
	private ImageView moreOpation,morerecordnewvoice;
	private LinearLayout recordMyVoiceLayout;
	private LinearLayout uploadsoundfilell;
	private LinearLayout uploadsoundfile;
	private LinearLayout selectsoundfile;
	private RelativeLayout voiceItemLayout;
	private LinearLayout ishiddenll;
	private UISwitchButton isPublic;
	private TextView tv_bg_audio, tv_record_voice;
	private SharedPreferences sharedPreferences;
	private Spinner spinner;

	@SuppressLint("HandlerLeak")
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

	private int selectVoiceQuality=0;

	public void initPopuptWindow() {
		sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO, MODE_PRIVATE);
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		view = layoutInflater.inflate(R.layout.accompany_create_popup, null);
		close = (ImageView) view.findViewById(R.id.close);
		cancel = (TextView) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		spinner=view.findViewById(R.id.xz_sn_type);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(mContext, R.array.xzname, R.layout.spinner_item);
		//设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.dropdown_stytle);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectVoiceQuality=position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		tv_record_voice = (TextView) view.findViewById(R.id.tv_record_voice);
		tv_bg_audio = (TextView) view.findViewById(R.id.tv_bg_audio);
		addaudio = (TextView) view.findViewById(R.id.addaudio);
		selectsoundfile = (LinearLayout) view.findViewById(R.id.selectsoundfile);
		recordname = (TextView) view.findViewById(R.id.recordname);
		bgname = (TextView) view.findViewById(R.id.bgname);
		backgroundAudioLayout = (RelativeLayout) view.findViewById(R.id.layout_background_audio);
		moreOpation = view.findViewById(R.id.moreOpation);
		moreOpation.setOnClickListener(this);
		morerecordnewvoice = view.findViewById(R.id.morerecordnewvoice);
		morerecordnewvoice.setOnClickListener(this);
		edittext = (EditText) view.findViewById(R.id.edittext);
		String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
		String name = AppConfig.UserName + "_" + time;
//        edittext.setText(name);
//        edittext.setSelection(name.length());
		voiceItemLayout = view.findViewById(R.id.layout_voice_item);
		checkBox = (CheckBox) view.findViewById(R.id.checkboxx);
		isPublic = (UISwitchButton) view.findViewById(R.id.isPublic);
		isPublic.setChecked(true);
		recordMyVoiceLayout = view.findViewById(R.id.layout_record_my_voice);
		uploadsoundfilell = view.findViewById(R.id.uploadsoundfilell);
		uploadsoundfile = view.findViewById(R.id.uploadsoundfile);
		ishiddenll = view.findViewById(R.id.ishiddenll);
		recordMyVoiceLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkBox.setChecked(!checkBox.isChecked());
				if (checkBox.isChecked()) {
					uploadsoundfilell.setVisibility(View.INVISIBLE);
					recordMyVoiceLayout.setVisibility(View.VISIBLE);
				} else {
					uploadsoundfilell.setVisibility(View.VISIBLE);
					recordMyVoiceLayout.setVisibility(View.INVISIBLE);
				}
				setCreateSyncText();
			}
		});

//        setBindViewText();

		recordsync = (TextView) view.findViewById(R.id.recordsync);
		recordsync.setOnClickListener(this);
		uploadsoundfile.setOnClickListener(this);
		close.setOnClickListener(this);
		addaudio.setOnClickListener(this);
		selectsoundfile.setOnClickListener(this);
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
	private int selectAccompanyType=0;

	public void setAudioBean(Document favorite) {
		selectAccompanyType=0;
		this.favorite = favorite;
		actionBaseSoundtrackID = Integer.parseInt(favorite.getItemID());
		addaudio.setVisibility(View.INVISIBLE);
		backgroundAudioLayout.setVisibility(View.VISIBLE);

		if (favorite != null) {
			bgname.setVisibility(View.VISIBLE);
			bgname.setText(favorite.getTitle());
		}
		setCreateSyncText();
	}


	private SoundTrack  lastSoundtrack=new SoundTrack();
	public void setAudioBean1(final SoundTrack soundTrack) {
		selectAccompanyType=1;
		lastSoundtrack=soundTrack;
		actionBaseSoundtrackID = soundTrack.getSoundtrackID();
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
		addaudio.setVisibility(View.INVISIBLE);
		backgroundAudioLayout.setVisibility(View.VISIBLE);


	}

	public void setRecordBean1(final SoundTrack soundTrack) {

		morerecordnewvoice.setVisibility(View.VISIBLE);
		uploadsoundfilell.setVisibility(View.INVISIBLE);
		recordMyVoiceLayout.setVisibility(View.INVISIBLE);

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
						recordname.setText(recordfavorite.getTitle());
						setCreateSyncText();
					}
				});
	}


	public void setRecordBean(Document favorite) {
		this.recordfavorite = favorite;
		uploadsoundfilell.setVisibility(View.INVISIBLE);
		recordMyVoiceLayout.setVisibility(View.INVISIBLE);

		if (recordfavorite != null) {
			voiceItemLayout.setVisibility(View.VISIBLE);
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

	private void createSoundtrack(final boolean issend) {
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
					jsonObject.put("IsPublic", isPublic.isChecked()?1:0);
					jsonObject.put("Type", 0);
					jsonObject.put("LessonID", 0);
					jsonObject.put("ItemID", 0);
					int musicType=checkBox.isChecked() ? 1 : 0;
					if(musicType==0){
						if(!TextUtils.isEmpty(recordfavorite.getAttachmentID())){
							musicType=1;
						}
					}
					if(checkBox.isChecked()){ //录制新声音
						actionBaseSoundtrackID=0;
					}
					jsonObject.put("ActionBaseSoundtrackID", actionBaseSoundtrackID);
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
						soundtrackBean.setVoiceQuality(selectVoiceQuality);


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
						if(issend){
							handler.sendMessage(msg3);
						}

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
			case R.id.selectsoundfile:
				mFavoritePoPListener.addrecord(1);
				break;
			case R.id.cancel:
				dismiss();
				break;
			case R.id.recordsync:
				dismiss();
				createSoundtrack(true);
				break;
			case R.id.moreOpation:
				showOperationsPop(moreOpation,isAccompanyOrMusic,selectAccompanyType);
				break;
			case R.id.morerecordnewvoice:
				showOperationsNewPop(morerecordnewvoice);
				break;
			case R.id.uploadsoundfile:
				checkBox.setChecked(true);
				delete2();
				break;
			default:
				break;
		}
	}

	private  AccompanyMoreOperations accompanyMoreOperations;
	private void showOperationsPop(View moreOpation,int isAccompanyOrMusic,int selectAccompanyType) {
		if (accompanyMoreOperations != null) {
			if (accompanyMoreOperations.isShowing()) {
				accompanyMoreOperations.dismiss();
				accompanyMoreOperations = null;
			}
		}
		accompanyMoreOperations = new AccompanyMoreOperations(mContext);
		accompanyMoreOperations.setSoundtrackOperationListener(this);
		accompanyMoreOperations.show(moreOpation,isAccompanyOrMusic,selectAccompanyType);
	}

	private void showOperationsNewPop(View moreOpation) {
		if (accompanyMoreOperations != null) {
			if (accompanyMoreOperations.isShowing()) {
				accompanyMoreOperations.dismiss();
				accompanyMoreOperations = null;
			}
		}
		accompanyMoreOperations = new AccompanyMoreOperations(mContext);
		accompanyMoreOperations.setSoundtrackOperationListener(this);
		accompanyMoreOperations.showNewVoice(moreOpation);
	}

	@Override
	public void syncAccompany() {
		isAccompanyOrMusic = 0;  //点击同步了
		actionBaseSoundtrackID=0;
		checkBox.setChecked(false);
		delete2();
		ishiddenll.setVisibility(View.GONE);
	}

	@Override
	public void cancel() {
		isAccompanyOrMusic = 1;  // 取消同步
		actionBaseSoundtrackID=lastSoundtrack.getSoundtrackID();
		ishiddenll.setVisibility(View.VISIBLE);
	}
	@Override
	public void cancelNewVoice() {
		delete2();
	}

	@Override
	public void sync() {  //保存为伴奏（无动作）
		isAccompanyOrMusic = 0;  //点击同步了
		actionBaseSoundtrackID=0;
		checkBox.setChecked(false);
		delete2();
		ishiddenll.setVisibility(View.GONE);
		dismiss();
		createSoundtrack(false);
	}

	@Override
	public void delete() {  //删除已选的伴奏
		favorite = new Document();
		backgroundAudioLayout.setVisibility(View.INVISIBLE);
		addaudio.setVisibility(View.VISIBLE);
		bgname.setVisibility(View.INVISIBLE);
		setCreateSyncText();
	}


	private void delete2() {
		recordfavorite = new Document();
		voiceItemLayout.setVisibility(View.INVISIBLE);
		if (checkBox.isChecked()) {
			uploadsoundfilell.setVisibility(View.INVISIBLE);
			recordMyVoiceLayout.setVisibility(View.VISIBLE);
		} else {
			uploadsoundfilell.setVisibility(View.VISIBLE);
			recordMyVoiceLayout.setVisibility(View.INVISIBLE);

		}
		setCreateSyncText();
	}


	private int isAccompanyOrMusic = 1;  //是否重新同步

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
