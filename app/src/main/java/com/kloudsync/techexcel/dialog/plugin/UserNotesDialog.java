package com.kloudsync.techexcel.dialog.plugin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.EventHighlightNote;
import com.kloudsync.techexcel.bean.EventNote;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.bean.UserNotes;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.PageActionsAndNotesMgr;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.kloudsync.techexcel.view.spinner.NiceSpinner;
import com.kloudsync.techexcel.view.spinner.OnSpinnerItemSelectedListener;
import com.kloudsync.techexcel.view.spinner.UserNoteTextFormatter;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.NoteOperatorPopup;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.SyncRoomOtherNoteListPopup;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserNotesDialog implements View.OnClickListener, OnSpinnerItemSelectedListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private RecyclerView noteList;
    private NoteAdapter noteAdapter;
    private NiceSpinner usersSpinner;
    private MeetingConfig meetingConfig;
    private ImageView backImage;


    public UserNotesDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_user_notes, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        noteList = view.findViewById(R.id.list_note);
        backImage = view.findViewById(R.id.back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        noteList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        usersSpinner = view.findViewById(R.id.spinner_users);
        usersSpinner.setOnSpinnerItemSelectedListener(this);
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels);
        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        if (Tools.isOrientationPortrait((Activity) mContext)) {
            //竖屏
            Log.e("check_oritation", "oritation:portrait");
            dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            params.width = mContext.getResources().getDisplayMetrics().widthPixels;
            params.height = Tools.dip2px(mContext, 420);
        } else {
            Log.e("check_oritation", "oritation:landscape");
            dialog.getWindow().setGravity(Gravity.RIGHT);
            params.height = heigth;
            params.width = Tools.dip2px(mContext, 300);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setWindowAnimations(R.style.anination3);
        }

        dialog.getWindow().setAttributes(params);
    }


    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;

    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    public void show(String userId, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            process(userId, meetingConfig);
        }
    }

    @Override
    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
        if (users == null || users.size() <= 0) {
            return;
        }
        if (this.user == null) {
            return;
        }
        UserNotes user = users.get(position);
        if (user.getUserId().equals(this.user.getUserId())) {
            Log.e("onItemSelected", "the same");
            return;
        }
        changeUser(user);

    }


    UserNotes user;
    List<UserNotes> users;

    private void changeUser(UserNotes user) {

        if (user.getNotes() != null && user.getNotes().size() >= 0) {
            showUserNotes(user);
        } else {
            Observable.just(user).observeOn(Schedulers.io()).doOnNext(new Consumer<UserNotes>() {
                @Override
                public void accept(UserNotes userNotes) throws Exception {

                    if (users == null || users.size() <= 0 || !users.contains(userNotes)) {
                        return;
                    }

                    String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" +
                            userNotes.getParamsId() + "&pageNumber=0&userID=" + userNotes.getUserId();
                    userNotes.setNotes(ServiceInterfaceTools.getinstance().syncGetUserNotes(url));
                    refreshUserList(userNotes);

                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<UserNotes>() {
                @Override
                public void accept(UserNotes userNotes) throws Exception {

                    if (userNotes.getNotes() == null) {
                        return;
                    }
                    showUserNotes(userNotes);
                }
            }).subscribe();
        }
    }

    private void process(final String userId, final MeetingConfig meetingConfig) {

        user = new UserNotes();
        user.setUserId(userId);
        if (meetingConfig.getDocument() == null) {
            return;
        }
        Observable.just(user).observeOn(Schedulers.io()).doOnNext(new Consumer<UserNotes>() {
            @Override
            public void accept(UserNotes userNotes) throws Exception {
                String url = "";
                String paramsId = "";
                if (meetingConfig.getType() == MeetingType.DOC || meetingConfig.getType() == MeetingType.MEETING) {
                    url = AppConfig.URL_PUBLIC + "DocumentNote/DocViewUserList?attachmentID=" + meetingConfig.getDocument().getAttachmentID();
                    paramsId = meetingConfig.getDocument().getAttachmentID() + "";
                }
                userNotes.setParamsId(paramsId);
                List<Customer> members = ServiceInterfaceTools.getinstance().syncGetDocUsers(url);
                Log.e("process", "one:" + members.size());
                if (members != null && members.size() > 0) {
                    users = new ArrayList<>();
                    for (int i = 0; i < members.size(); ++i) {
                        Customer member = members.get(i);
                        if (user.getUserId().equals(member.getUserID())) {
                            user.setNoteCount(member.getNoteCount());
                            user.setUserName(member.getName());
                            refreshUserList(user);
                        } else {
                            UserNotes _user = new UserNotes();
                            _user.setParamsId(paramsId);
                            _user.setNoteCount(member.getNoteCount());
                            _user.setUserName(member.getName());
                            _user.setUserId(member.getUserID());
                            refreshUserList(_user);
                        }

                    }
                }

            }
        }).doOnNext(new Consumer<UserNotes>() {
            @Override
            public void accept(UserNotes userNotes) throws Exception {
                Log.e("process", "two");
                if (users == null || users.size() <= 0 || !users.contains(userNotes)) {
                    return;
                }

                String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" +
                        userNotes.getParamsId() + "&pageNumber=0&userID=" + userNotes.getUserId();
                userNotes.setNotes(ServiceInterfaceTools.getinstance().syncGetUserNotes(url));
                refreshUserList(userNotes);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<UserNotes>() {
            @Override
            public void accept(UserNotes userNotes) throws Exception {
                Log.e("process", "three:" + userNotes);
                if (userNotes.getNotes() == null) {
                    return;
                }
                showUserNotes(userNotes);
            }
        }).subscribe();
    }

    private void showUserNotes(UserNotes user) {
        this.user = user;
        usersSpinner.setVisibility(View.VISIBLE);
        noteAdapter = new NoteAdapter(mContext, user.getNotes());
        noteList.setAdapter(noteAdapter);
        usersSpinner.attachDataSource(users, new UserNoteTextFormatter());
        usersSpinner.setTextInternal(user);

    }

    private void refreshUserList(UserNotes user) {
        if (users != null) {
            if (!users.contains(user)) {
                users.add(user);
            } else {
                UserNotes _user = users.get(users.indexOf(user));
                if (user.getNotes() != null) {
                    _user.setNotes(user.getNotes());
                }
            }
        }
    }

    public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {

        private Context context;

        private List<NoteDetail> list = new ArrayList<>();

        public NoteAdapter(Context context, List<NoteDetail> list) {
            this.context = context;
            this.list.clear();
            this.list.addAll(list);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
            Holder holder = new NoteAdapter.Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final NoteAdapter.Holder holder, final int position) {
            final NoteDetail noteDetail = list.get(position);
            holder.title.setText(noteDetail.getTitle());
            String date = noteDetail.getCreatedDate();
            if (!TextUtils.isEmpty(date)) {
                long dd = Long.parseLong(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss");
                String haha = simpleDateFormat.format(dd);
                holder.date.setText(haha);
            }

            holder.pagenumber.setText("Page " + noteDetail.getPageNumber());
            String url = noteDetail.getAttachmentUrl();
            if (!TextUtils.isEmpty(url)) {
                url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                Uri imageUri = null;
                if (!TextUtils.isEmpty(url)) {
                    imageUri = Uri.parse(url);
                }
                holder.img_url.setImageURI(imageUri);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventHighlightNote note = new EventHighlightNote();
                    note.setPageNumber(noteDetail.getPageNumber());
                    note.setNote(noteDetail);
                    EventBus.getDefault().post(note);
                }
            });

            holder.viewer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    EventNote note = new EventNote();
//                    note.setLinkId(noteDetail.getLinkID());
//                    note.setNote(parseNote(noteDetail));
//                    EventBus.getDefault().post(note);
                    PageActionsAndNotesMgr.getNoteDetail(mContext, noteDetail);
                    dismiss();

                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView title;
            RelativeLayout ll;
            SimpleDraweeView img_url;
            TextView date;
            TextView pagenumber;
            ImageView viewer;

            public Holder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                date = itemView.findViewById(R.id.date);
                ll = itemView.findViewById(R.id.ll);
                img_url = itemView.findViewById(R.id.img_url);
                viewer = itemView.findViewById(R.id.image_view);
                pagenumber = itemView.findViewById(R.id.pagenumber);
            }
        }
    }


}
