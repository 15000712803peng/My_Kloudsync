package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.PenDotTool;
import com.kloudsync.techexcel.personal.CurrentNoteActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.DrawView;
import com.kloudsync.techexcel.ui.NoteViewActivity;
import com.tqltech.tqlpencomm.Dot;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.kloudsync.techexcel.personal.MyNoteActivity.TYPE_LIVE_NOTE;
import static com.kloudsync.techexcel.personal.MyNoteActivity.TYPE_NOTE;


public class MyNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context mContext;
	private DrawView mCanvasImage;
	private boolean hasMeasured = false;

	private List<Note> mList;
	SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss");

	private OnNotesOperationsListener notesOperationsListener;
	private final Note mCurrentNote;

	public OnNotesOperationsListener getNotesOperationsListener() {
		return notesOperationsListener;
	}

	public void setNotesOperationsListener(OnNotesOperationsListener notesOperationsListener) {
		this.notesOperationsListener = notesOperationsListener;
	}

	public MyNotesAdapter(Context context, List<Note> list) {
		this.mContext = context;
		this.mList = list;
		mCurrentNote = list.get(0);
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_LIVE_NOTE;
		} else {
			return TYPE_NOTE;
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = null;
		switch (viewType) {
			case TYPE_LIVE_NOTE:
				view = LayoutInflater.from(mContext).inflate(R.layout.live_note_item, parent, false);
				final LiveNoteHolder liveNoteHolder = new LiveNoteHolder(view);
				mCanvasImage = new DrawView(mContext);
				ViewGroup.LayoutParams param = liveNoteHolder.noteLayout.getLayoutParams();
				liveNoteHolder.noteLayout.addView(mCanvasImage, param);
				Log.e("check_canvasImage", "canvasImage1:" + mCanvasImage);
				liveNoteHolder.mIvMyCurrentNote.setImageResource(R.drawable.p0);
				liveNoteHolder.mIvMyCurrentNote.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (hasMeasured == false) {
							hasMeasured = true;
							//计算
							/*BG_WIDTH = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_80);
							BG_HEIGHT = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_112);

							ViewGroup.LayoutParams para = liveNoteHolder.mIvMyCurrentNote.getLayoutParams();
							para.width = BG_WIDTH;
							para.height = BG_HEIGHT;
							gImageView.setLayoutParams(para);

							gcontentLeft = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getLeft();
							gcontentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

							int statusHeight = getStatusBarHeight();
							int statusHeight2 = getStatusBarHeight(OidActivity.this);

							Log.i(TAG, "onGlobalLayout: actionBarHeight=" + actionBarHeight + ",statusHeight=" + statusHeight + ",statusHeight2=" + statusHeight2);
							Log.i(TAG, "onGlobalLayout: mHeight=" + mHeight + ",mWidth=" + mWidth);
							Log.i(TAG, "onGlobalLayout: gcontentTop=" + gcontentTop + ",gcontentLeft=" + gcontentLeft);
							Log.i(TAG, "onGlobalLayout: BG_HEIGHT=" + BG_HEIGHT + ",BG_WIDTH=" + BG_WIDTH);
							A5_X_OFFSET = (int) (mWidth - gcontentLeft - BG_WIDTH) / 2;
							if (isNotchScreen) {
								A5_Y_OFFSET = (int) (mHeight - gcontentTop - BG_HEIGHT + notchscreenHeight) / 2 *//*- 12*//*;
							} else {
								A5_Y_OFFSET = (int) (mHeight - gcontentTop - BG_HEIGHT) / 2;
							}*/
						}
					}
				});
				return liveNoteHolder;
			case TYPE_NOTE:
				view = LayoutInflater.from(mContext).inflate(R.layout.user_note_item, parent, false);
				return new RecycleHolder2(view);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		int type = getItemViewType(position);
		switch (type) {
			case TYPE_LIVE_NOTE:
				if (holder instanceof LiveNoteHolder) {
					Note note = mList.get(position);
					final LiveNoteHolder noteHolder = (LiveNoteHolder) holder;
					noteHolder.mTvCurrentPage.setText(note.getTitle());
					noteHolder.mTvPageUpdateDate.setText(note.getCreatedDate());
					Log.e("check_canvasImage", "canvasImage2:" + mCanvasImage);
					noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String currentPage = noteHolder.mTvCurrentPage.getText().toString();
							Intent intent = new Intent(mContext, CurrentNoteActivity.class);
							intent.putExtra(CurrentNoteActivity.CURRENTPAGE, currentPage);
							mContext.startActivity(intent);
						}
					});
				}
				break;

			case TYPE_NOTE:
				if (mList.get(position) instanceof Note) {
					final Note noteDetail = mList.get(position);
					RecycleHolder2 holder2 = (RecycleHolder2) holder;
					holder2.title.setText(noteDetail.getTitle());
					String date = noteDetail.getCreatedDate();
					if (!TextUtils.isEmpty(date)) {
						long dd = Long.parseLong(date);
						String haha = mSimpleDateFormat.format(dd);
						holder2.date.setText(haha);
					}
					holder.itemView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Log.e("check_click", "item_view,clicked");
							handleBluetoothNote(noteDetail);
						}
					});
					String url = noteDetail.getAttachmentUrl();
					if (!TextUtils.isEmpty(url)) {
						url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
						Uri imageUri = null;
						if (!TextUtils.isEmpty(url)) {
							imageUri = Uri.parse(url);
						}
						holder2.img_url.setImageURI(imageUri);
					}
//        holder.operationmore.setVisibility(View.GONE);
					holder2.operationmore.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
				}
				break;
		}
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	public void onReceiveDot(Dot dot) {
		PenDotTool.processEachDot(dot, mCanvasImage);
	}

	public void onReceiveOfflineStrokes(Dot dot) {
		PenDotTool.processEachDot(dot, mCanvasImage);
	}

	private static class RecycleHolder2 extends RecyclerView.ViewHolder {
		TextView title;
		LinearLayout ll;
		SimpleDraweeView img_url;
		TextView date;
		ImageView operationmore;

		public RecycleHolder2(View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.title);
			date = itemView.findViewById(R.id.date);
			ll = itemView.findViewById(R.id.ll);
			img_url = itemView.findViewById(R.id.img_url);
			operationmore = itemView.findViewById(R.id.operationmore);
		}
	}

	private static class LiveNoteHolder extends RecyclerView.ViewHolder {

		public RelativeLayout noteLayout;
		private final TextView mTvCurrentPage;
		private final TextView mTvPageUpdateDate;
		private final ImageView mIvMyCurrentNote;

		public LiveNoteHolder(View itemView) {
			super(itemView);
			noteLayout = itemView.findViewById(R.id.layout_note);
			mIvMyCurrentNote = itemView.findViewById(R.id.iv_my_current_note);
			mTvCurrentPage = itemView.findViewById(R.id.item_tv_current_page);
			mTvPageUpdateDate = itemView.findViewById(R.id.item_tv_page_update_date);
		}
	}


	public interface OnNotesOperationsListener {

		void viewNote(Note note);

		void deleteNote(Note note);

		void moveNote(Note note);

		void renameNote(Note note);
	}

	private void goToViewNote(String lessonId, String itemId, Note note) {
		updateSocket();
		Intent intent = new Intent(mContext, NoteViewActivity.class);
//        Intent intent = new Intent(getActivity(), WatchCourseActivity3.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("userid", AppConfig.UserID);
		//-----
		intent.putExtra("meeting_id", Integer.parseInt(lessonId) + "," + AppConfig.UserID);
//        intent.putExtra("meeting_id", "Doc-" + AppConfig.UserID);
		intent.putExtra("document_id", itemId);
		intent.putExtra("meeting_type", 2);
		intent.putExtra("lession_id", Integer.parseInt(itemId));
		intent.putExtra("url", note.getAttachmentUrl());
		intent.putExtra("note_id", note.getNoteID());
		intent.putExtra("local_file_id", note.getLocalFileID());
		mContext.startActivity(intent);
	}

	private void updateSocket() {
		Intent service = new Intent(mContext, SocketService.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(service);
			mContext.startService(service);
		} else {
			mContext.startService(service);
		}
	}

	public void handleBluetoothNote(Note note) {

		if (note.getNoteID() > 0) {

			Observable.just(note.getNoteID() + "").observeOn(Schedulers.io()).map(new Function<String, Note>() {
				@Override
				public Note apply(String noteId) throws Exception {
					return MeetingServiceTools.getInstance().syncGetNoteByNoteId(noteId);
				}

			}).doOnNext(new Consumer<Note>() {
				@Override
				public void accept(final Note note) throws Exception {
					if (note.getAttachmentID() > 0) {
						String title = note.getTitle();
						if (TextUtils.isEmpty(title)) {
							title = "";
						}
						String url = AppConfig.URL_PUBLIC
								+ "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + note.getAttachmentID()
								+ "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(""), "UTF-8");
						JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetTempLessonWithOriginalDocument(url);
						if (jsonObject.has("RetCode")) {
							if (jsonObject.getInt("RetCode") == 0) {
								JSONObject data = jsonObject.getJSONObject("RetData");
								final String lessionId = data.optLong("LessonID") + "";
								final String itemId = data.optLong("ItemID") + "";
								if (!TextUtils.isEmpty(lessionId)) {
									Observable.just("view_note").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
										@Override
										public void accept(String s) throws Exception {
											goToViewNote(lessionId, itemId, note);
										}
									});
								}
							}
						}
					}
				}
			}).subscribe();
		}


	}

}
