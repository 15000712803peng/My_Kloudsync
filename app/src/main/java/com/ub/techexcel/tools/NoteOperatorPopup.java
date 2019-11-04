package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.PopupWindowUtil;
import com.ub.techexcel.bean.SoundtrackBean;

/**
 * Created by wang on 2017/9/18.
 */

public class NoteOperatorPopup implements View.OnClickListener {


    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    LinearLayout notedelete;
    LinearLayout noterename;
    LinearLayout noteedit;
    LinearLayout noteshare;


    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void delete();
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
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.noteoperatorpopup, null);

        notedelete = view.findViewById(R.id.notedelete);
        noterename = view.findViewById(R.id.noterename);
        noteedit = view.findViewById(R.id.noteedit);
        noteshare = view.findViewById(R.id.noteshare);

        notedelete.setOnClickListener(this);
        noterename.setOnClickListener(this);
        noteedit.setOnClickListener(this);
        noteshare.setOnClickListener(this);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.anination3);

    }

    private NoteDetail noteDetail;

    @SuppressLint("NewApi")
    public void StartPop(View v, NoteDetail noteDetail) {
        if (mPopupWindow != null) {
            this.noteDetail = noteDetail;
            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view, 50);
            int height = mContext.getResources().getDisplayMetrics().heightPixels;
            int xOff = 20; // 可以自己调整偏移
            windowPos[0] -= xOff;
            mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);

        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notedelete:
                dismiss();
                deleteNote(noteDetail);
                break;
            case R.id.noterename:
                break;
            case R.id.noteedit:
                break;
            case R.id.noteshare:
                break;
            default:
                break;
        }
    }

    private void deleteNote(NoteDetail noteDetail) {

        String url = AppConfig.URL_PUBLIC + "DocumentNote/RemoveNote?linkIDs=" + noteDetail.getLinkID();
        ServiceInterfaceTools.getinstance().removeNote(url, ServiceInterfaceTools.REMOVENOTE, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                int code = (int) object;
                if (code == 0) {
                    mFavoritePoPListener.delete();
                }

            }
        });
    }


}
