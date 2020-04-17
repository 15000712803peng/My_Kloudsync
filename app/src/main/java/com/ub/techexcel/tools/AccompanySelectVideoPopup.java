package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSoundtrackList;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.filepicker.FilePickerActivity;
import com.kloudsync.techexcel.help.SoundtrackManager;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.kloudsync.activity.Document;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class AccompanySelectVideoPopup implements View.OnClickListener, SoundtrackManager.OnSoundtrackResponse {

	public Context mContext;
	public int width;
	public Dialog mPopupWindow;
	private List<SoundTrack> list1 = new ArrayList<SoundTrack>();
	private List<Document> list2 = new ArrayList<Document>();
	private AudiosAdapter1 adapter1;
	private AudiosAdapter adapter2;
	private ListView listView1, listView2;
	private LinearLayout accompanyll, accompanymusicll;
	private View accompanyll_line, accompanymusicll_line;
	private View view;
	private LinearLayout uploadfile,selectfile;
	private LinearLayout isshowfileupload;
	private TextView cancelText;
	private TextView tv1,tv2;
	private TextView fileSize;

	public void getPopwindow(Context context) {
		this.mContext = context;
		width = mContext.getResources().getDisplayMetrics().widthPixels;
		getPopupWindowInstance();
	}

	public AccompanySelectVideoPopup(Context context) {
		this.mContext = context;
		initPopuptWindow();
	}

	public void getPopupWindowInstance() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}


	private static FavoriteVideoPoPListener mFavoritePoPListener;


	public interface FavoriteVideoPoPListener {

		void save(Document document);

		void save1(SoundTrack document);

		void uploadFile();

	}

	public void setFavoritePoPListener(FavoriteVideoPoPListener documentPoPListener) {
		this.mFavoritePoPListener = documentPoPListener;
	}

	int selectPosition = -1;

	public void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		view = layoutInflater.inflate(R.layout.accompany_select_video, null);
		listView1 = view.findViewById(R.id.listview1);
		listView2 = view.findViewById(R.id.listview2);
		tv1 = view.findViewById(R.id.tv1);
		tv2 = view.findViewById(R.id.tv2);
		fileSize = view.findViewById(R.id.filesize);


		accompanyll = view.findViewById(R.id.accompanyll);
		accompanymusicll = view.findViewById(R.id.accompanymusicll);
		accompanyll_line = view.findViewById(R.id.accompanyll_line);
		accompanymusicll_line = view.findViewById(R.id.accompanymusicll_line);
		accompanymusicll.setOnClickListener(this);
		accompanyll.setOnClickListener(this);

		uploadfile = view.findViewById(R.id.uploadfile);
		isshowfileupload = view.findViewById(R.id.isshowfileupload);
		selectfile = view.findViewById(R.id.selectfile);
		cancelText = view.findViewById(R.id.cancel);
		cancelText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});

		adapter1 = new AudiosAdapter1(mContext, list1,
				R.layout.popup_video_item);
		listView1.setAdapter(adapter1);
		listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			                        int position, long id) {
				selectPosition = position;
				adapter1.notifyDataSetChanged();
			}
		});

		adapter2 = new AudiosAdapter(mContext, list2,
				R.layout.popup_video_item2);
		listView2.setAdapter(adapter2);
		listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			                        int position, long id) {
				selectPosition = position;
				adapter2.notifyDataSetChanged();
			}
		});

		view.findViewById(R.id.save).setOnClickListener(this);
		uploadfile.setOnClickListener(this);
		selectfile.setOnClickListener(this);

		mPopupWindow = new Dialog(mContext, R.style.my_dialog);
		mPopupWindow.setContentView(view);
		mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}


	private int role;

	@SuppressLint("NewApi")
	public void StartPop(MeetingConfig meetingConfig, int role) {
		this.role = role;
		if (mPopupWindow != null) {
			WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
			if (Tools.isOrientationPortrait((Activity) mContext)) {
				View root = ((Activity) mContext).getWindow().getDecorView();
				params.width = root.getMeasuredWidth() * 9 / 10;
				params.height = mContext.getResources().getDisplayMetrics().heightPixels * 1 / 2;
			} else {
				params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
				View root = ((Activity) mContext).getWindow().getDecorView();
				params.height = root.getMeasuredHeight() * 4 / 5 + 30;
			}
			mPopupWindow.getWindow().setAttributes(params);
			mPopupWindow.show();
			if (role == 2) { //老师身份  都显示

			} else {   // 学生身份  只能看伴奏带
//				accompanymusicll.setVisibility(View.GONE);
//				listView2.setVisibility(View.GONE);
			}
			getData(meetingConfig);
		}
	}


	public void getData(final MeetingConfig meetingConfig) {
		selectPosition = -1;
		LoginGet loginGet = new LoginGet();
		loginGet.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
			@Override
			public void getFavorite(ArrayList<Document> list) {
				list2.clear();
				list2.addAll(list);
				adapter2.notifyDataSetChanged();
			}
		});
		loginGet.MyFavoriteRequestNew(mContext, 3,0);
//		loginGet.MyFavoriteRequestNew(mContext, 3,meetingConfig.getDocument().getAttachmentID());

		MeetingConfig meetingConfig1=new MeetingConfig();
		meetingConfig1.setType(MeetingType.DOC);
		meetingConfig1.setDocument(meetingConfig.getDocument());
		SoundtrackManager.getInstance().requestSoundtrackList(meetingConfig1, this);

	}

	@Override
	public void soundtrackList(EventSoundtrackList soundtrackList) {
		Observable.just(soundtrackList).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<EventSoundtrackList>() {
			@Override
			public void accept(EventSoundtrackList soundtrackList) throws Exception {
				List<SoundTrack> soundTracks = soundtrackList.getSoundTracks();
				if (soundTracks.size() >= 0) {
					list1.clear();
					for (int i = 0; i < soundTracks.size(); i++) {
						SoundTrack soundTrack=soundTracks.get(i);
						if(soundTrack.getMusicType()==0){
							list1.add(soundTrack);
						}
					}
					if (adapter1 != null) {
						adapter1.notifyDataSetChanged();
					}
				}
			}
		}).subscribe();

	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.accompanyll:
				selectPosition = -1;
				accompanyll_line.setVisibility(View.VISIBLE);
				accompanymusicll_line.setVisibility(View.INVISIBLE);
				listView1.setVisibility(View.VISIBLE);
				listView2.setVisibility(View.GONE);
				adapter1.notifyDataSetChanged();
				isshowfileupload.setVisibility(View.INVISIBLE);
				tv1.setTextColor(mContext.getResources().getColor(R.color.skyblue));
				tv2.setTextColor(mContext.getResources().getColor(R.color.txt_color1));
				fileSize.setText("录制人");
				break;
			case R.id.accompanymusicll:
				selectPosition = -1;
				accompanyll_line.setVisibility(View.INVISIBLE);
				accompanymusicll_line.setVisibility(View.VISIBLE);
				listView1.setVisibility(View.GONE);
				listView2.setVisibility(View.VISIBLE);
				adapter2.notifyDataSetChanged();
				isshowfileupload.setVisibility(View.VISIBLE);
				tv1.setTextColor(mContext.getResources().getColor(R.color.txt_color1));
				tv2.setTextColor(mContext.getResources().getColor(R.color.skyblue));
				fileSize.setText("文件大小");
				break;
			case R.id.save:
				if (accompanyll_line.getVisibility() == View.VISIBLE) {
					if (selectPosition >= 0 && selectPosition < list1.size()) {
						mFavoritePoPListener.save1(list1.get(selectPosition));
					}
					mPopupWindow.dismiss();
				}
				if (accompanymusicll_line.getVisibility() == View.VISIBLE) {
					if (selectPosition >= 0 && selectPosition < list2.size()) {
						mFavoritePoPListener.save(list2.get(selectPosition));
					}
					mPopupWindow.dismiss();
				}
				break;
			case R.id.uploadfile:
				dismiss();
				mFavoritePoPListener.uploadFile();
				break;
			case R.id.selectfile:
				MyFavoriteVideoPopup favoritePopup = new MyFavoriteVideoPopup(mContext);
				favoritePopup.setFavoritePoPListener(new MyFavoriteVideoPopup.FavoriteVideoPoPListener() {
					@Override
					public void save(Document document) {
//						Toast.makeText(mContext,document.getTitle(),Toast.LENGTH_LONG).show();
					}
				});
				favoritePopup.StartPop(selectfile);
				break;
		}
	}


	public void dismiss() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	public class AudiosAdapter extends BaseAdapter {
		private Context context;
		private List<Document> mDatas;
		private int itemLayoutId;

		public AudiosAdapter(Context context, List<Document> mDatas,
		                     int itemLayoutId) {
			this.context = context;
			this.mDatas = mDatas;
			this.itemLayoutId = itemLayoutId;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
		                    ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						itemLayoutId, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.name);
				holder.size = (TextView) convertView
						.findViewById(R.id.filesize);
				holder.time = (TextView) convertView
						.findViewById(R.id.totalTime);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.imageview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(mDatas.get(position).getTitle());
			holder.time.setText(mDatas.get(position).getDuration());
			holder.size.setText(mDatas.get(position).getSize());
			if (selectPosition == position) {
				holder.imageview.setImageResource(R.drawable.accompany_select);
			} else {
				holder.imageview.setImageResource(R.drawable.accompany_unselect);
			}
			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView time;
			TextView size;
			ImageView imageview;
		}
	}


	public class AudiosAdapter1 extends BaseAdapter {
		private Context context;
		private List<SoundTrack> mDatas;
		private int itemLayoutId;

		public AudiosAdapter1(Context context, List<SoundTrack> mDatas,
		                      int itemLayoutId) {
			this.context = context;
			this.mDatas = mDatas;
			this.itemLayoutId = itemLayoutId;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
		                    ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						itemLayoutId, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.name);
				holder.size = (TextView) convertView
						.findViewById(R.id.filesize);
				holder.time = (TextView) convertView
						.findViewById(R.id.totalTime);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.imageview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(mDatas.get(position).getTitle());
			holder.time.setText(mDatas.get(position).getDuration());
			holder.size.setText(mDatas.get(position).getUserName());
			if (selectPosition == position) {
				holder.imageview.setImageResource(R.drawable.accompany_select);
			} else {
				holder.imageview.setImageResource(R.drawable.accompany_unselect);
			}
			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView time;
			TextView size;
			ImageView imageview;
		}
	}
}
