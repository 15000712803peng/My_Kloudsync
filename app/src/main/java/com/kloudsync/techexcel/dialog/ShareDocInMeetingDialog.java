package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;
import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventShareDocInMeeting;
import com.kloudsync.techexcel.bean.FavoriteDocumentsData;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.ub.techexcel.adapter.SelectFavoriteDocAdapter;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ShareDocInMeetingDialog implements View.OnClickListener, AdapterView.OnItemClickListener{
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ListView docList;
    private ImageView closeImage;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MeetingDocument document = (MeetingDocument) docAdapter.getItem(position);
        EventShareDocInMeeting shareDocInMeeting = new EventShareDocInMeeting();
        shareDocInMeeting.setDocument(document);
        EventBus.getDefault().post(shareDocInMeeting);
        cancel();
    }


    public ShareDocInMeetingDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_share_doc_in_meeting, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.8f));
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.85f));
        docList = view.findViewById(R.id.list_doc);
        docList.setOnItemClickListener(this);
        closeImage = view.findViewById(R.id.image_close);
        closeImage.setOnClickListener(this);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);

    }

    public boolean isShowing() {
        if(dialog == null){
            return false;
        }
        return dialog.isShowing();
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
            case R.id.image_close:
                dismiss();
                break;
            case R.id.image_back:
                break;
            case R.id.img_space_close:
                dismiss();
                break;
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            requestDocsAndShow();
        }
    }

    private void requestDocsAndShow(){
        Observable.just("Request").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetFavoriteAttachments(0);

                return jsonObject;
            }
        }).map(new Function<JSONObject,List<MeetingDocument>>() {
            @Override
            public List<MeetingDocument> apply(JSONObject jsonObject) throws Exception {
                List<MeetingDocument> docs = new ArrayList<>();
                if(jsonObject.has("RetCode")){
                    if(jsonObject.getInt("RetCode") == 0){
                        FavoriteDocumentsData documentsData = new Gson().fromJson(jsonObject.getJSONObject("RetData").toString(), FavoriteDocumentsData.class);
                        Log.e("requestDocsAndShow","documentsData:" + documentsData);
                        List<MeetingDocument>  favoriteDocs =  documentsData.getList();
                        if(favoriteDocs != null && favoriteDocs.size() > 0){
                            Log.e("requestDocsAndShow","size:" + documentsData.getList().size());
                            for(MeetingDocument document : favoriteDocs){
                                if(TextUtils.isEmpty(document.getTitle()) || document.getTitle().endsWith(".mp4")
                                        || document.getTitle().endsWith(".MP4") || document.getTitle().endsWith(".mp3")||
                                        document.getTitle().endsWith(".MP3")){
                                    continue;
                                }
                                docs.add(document);
                            }
                        }
                    }
                }
                return docs;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<List<MeetingDocument>>() {
            @Override
            public void accept(List<MeetingDocument> documents) throws Exception {
                if(documents.size() > 0){
                    docAdapter = new SelectFavoriteDocAdapter(mContext,documents);
                    docList.setAdapter(docAdapter);
                }
            }
        }).subscribe();
    }

    SelectFavoriteDocAdapter docAdapter;

    public void cancel(){
        if(dialog != null){
            dialog.cancel();
        }
    }



}
